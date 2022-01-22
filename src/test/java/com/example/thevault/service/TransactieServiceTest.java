// Created by E.S. Olthof
// Creation date 15-12-2020

package com.example.thevault.service;

import com.example.thevault.controller.rest_api_controller.Cryptomunt;
import com.example.thevault.financieel.*;
import com.example.thevault.handelingen.CryptoWaardeService;
import com.example.thevault.handelingen.Transactie;
import com.example.thevault.handelingen.TransactieService;
import com.example.thevault.klant.KlantService;
import com.example.thevault.handelingen.JDBCTransactieDAO;
import com.example.thevault.controller.rest_api_controller.RootRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import com.example.thevault.klant.Klant;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

class TransactieServiceTest {

    private static double bankFee = 1.5;
    private static Klant testKlant1;
    private static Klant testKlant2;
    private static Klant testKlantBank;
    private static Transactie testTransactie1;
    private static Transactie actualTransactie;
    private static Transactie testTransactie2;
    private static Transactie testTransactie3;
    private static Transactie actualTransactie1;
    private static Transactie excpectedTransactie1;
    private static Cryptomunt testCryptomunt1;
    private static Cryptomunt testCryptomunt2;
    private static Cryptomunt testCryptomunt3;
    private static Rekening testRekening1;
    private static Rekening testRekening11;
    private static Rekening testRekening2;
    private static Rekening testRekening22;
    private static Rekening testRekening3;
    private static KlantService klantService;
    private static RekeningService rekeningService;
    private static TransactieService transactieService;
    private static RootRepository mockRootRepository;
    private static JDBCTransactieDAO transactieDAO;
    private static JDBCRekeningDAO jdbcRekeningDAO;
    private static CryptoWaardeService cryptoWaardeService;
    private static AssetService assetService;

    private static Asset testAsset1;
    private static Asset testAsset2;
    private static Asset testAsset3;
    private static Asset testAsset4;

    @BeforeEach
    void setUp() {
        testKlant1 = new Klant( "Henknr1", "fdsaljkl", "Hello", 1890393, LocalDate.of(1991, 1, 12));
        testKlant2 = new Klant( "HarryBeste", "210jklf", "", 101212, LocalDate.of(1991, 1, 12));
        testKlantBank = new Klant( "TVLT", "831hgtr", "TVlT", 1001021, LocalDate.of(1990, 5, 10));

        testCryptomunt1 = new Cryptomunt(1, "CarmenCrypto", "CCR" );
        testCryptomunt2 = new Cryptomunt(2, "DigiCrypto", "DIG");
        testCryptomunt3 = new Cryptomunt(3, "Coyne", "COY");
        testAsset1 = new Asset(testCryptomunt1, 5.1);
        testAsset2 = new Asset(testCryptomunt2, 2.4);
        testAsset3 = new Asset(testCryptomunt3, 3.6);
        testAsset4 = new Asset(testCryptomunt1, 3.6);
        testAsset1.setGebruiker(testKlant1);
        testAsset4.setGebruiker(testKlant2);

        excpectedTransactie1 = new Transactie(LocalDateTime.now(), testKlant1, testCryptomunt1, 1050
                , 1.6, testKlant2);
        testTransactie2 = new Transactie(LocalDateTime.now(), testKlant2, testCryptomunt2, 9000
                , 1, testKlant1);
        testTransactie3 = new Transactie(LocalDateTime.now(), testKlant1, testCryptomunt3, 10000
                , 1, testKlant2);

        testRekening1 = new Rekening("NL20RABO9876543", 10000.0);
        testRekening11 =new Rekening("NL20RABO9876543", 9000.0);
        testRekening2 = new Rekening("INGB0001234567NL", 20000.0);
        testRekening22 = new Rekening("INGB0001234567NL", 21000.0);
        testRekening3 = new Rekening("INGB0001234567NL", 29000.0);

        mockRootRepository = Mockito.mock(RootRepository.class);
        rekeningService = new RekeningService(mockRootRepository);
        klantService = new KlantService(mockRootRepository);
        assetService = new AssetService(mockRootRepository);
        cryptoWaardeService = new CryptoWaardeService(mockRootRepository);
        transactieService = new TransactieService(mockRootRepository, klantService, rekeningService, assetService);

        testKlant1.setRekening(testRekening1);
        testKlant2.setRekening(testRekening2);
        testKlantBank.setRekening(testRekening3);

        List<Asset> testPortefeuille1 = new ArrayList<>();
        testPortefeuille1.add(testAsset1);
        testKlant1.setPortefeuille(testPortefeuille1);

        List<Asset> testPortefeuille2 = new ArrayList<>();
        testPortefeuille2.add(testAsset4);
        testPortefeuille2.add(testAsset2);
        testPortefeuille2.add(testAsset3);
        testKlant2.setPortefeuille(testPortefeuille2);
    }

/*    @Test
    void sluitTransactie() {

        double bedragVerkoperTestTransactie1 = -(((1.6 * (1000 + 1100) / 2.0) ) - (bankFee/2.0));
        double bedragKoperTestTransactie1 = ((1.6 * (1000 + 1100) / 2.0) ) + (bankFee/2.0);
        testRekening11.setSaldo(testRekening1.getSaldo() + bedragKoperTestTransactie1);
        testRekening22.setSaldo(testRekening2.getSaldo() + bedragVerkoperTestTransactie1);
        Mockito.when(rekeningService.wijzigSaldo(testKlant1, bedragKoperTestTransactie1))
                .thenReturn(testRekening11);
        Mockito.when(rekeningService.wijzigSaldo(testKlant2, bedragVerkoperTestTransactie1))
                .thenReturn(testRekening22);
        Mockito.when(mockRootRepository.slaRekeningOp(testRekening1))
                .thenReturn(testRekening1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant1, testCryptomunt1)).thenReturn(testAsset1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant2, testCryptomunt1)).thenReturn(testAsset4);
        Mockito.when(mockRootRepository.wijzigAssetVanKlant(testAsset1)).thenReturn(testAsset1);
        Mockito.when(mockRootRepository.wijzigAssetVanKlant(testAsset4)).thenReturn(testAsset4);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant1)).thenReturn(testRekening1);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant2)).thenReturn(testRekening2);
        Mockito.when(transactieService.slaTransactieOp(actualTransactie1)).thenReturn(actualTransactie1);
        Mockito.when(transactieService.slaTransactieOp(actualTransactie)).thenReturn(actualTransactie);
        Transactie actualTransactie = transactieService.sluitTransactie(testKlant1, testCryptomunt1,
                1000, 1100, 1.6, testKlant2);
        System.out.println("EXPECTED TRANSACTIE: " + excpectedTransactie1);
        System.out.println("ACTUAL TRANSACTIE :" + actualTransactie);

        assertThat(actualTransactie.getKoper().getGebruikersnaam()).isEqualTo(excpectedTransactie1.getKoper().getGebruikersnaam());
        assertThat(actualTransactie.getTransactieId()).isEqualTo(excpectedTransactie1.getTransactieId());
        assertThat(actualTransactie.getPrijs()).isEqualTo(excpectedTransactie1.getPrijs());
        assertThat(actualTransactie.getAantal()).isEqualTo(excpectedTransactie1.getAantal());
        assertThat( actualTransactie.getMomentTransactie()).isEqualToIgnoringSeconds(excpectedTransactie1.getMomentTransactie());
    }*/

/*    @Test
    void notEnoughCryptoExceptionHandler(){
        Mockito.when(rekeningService.wijzigSaldo(testKlant1, (testKlant1.getRekening().getSaldo() - 1000)))
                .thenReturn(testRekening11);
        Mockito.when(rekeningService.wijzigSaldo(testKlant2, (testKlant2.getRekening().getSaldo() + 1000)))
                .thenReturn(testRekening22);
        Mockito.when(mockRootRepository.slaRekeningOp(testRekening1))
                .thenReturn(testRekening1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant1, testCryptomunt1)).thenReturn(testAsset1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant2, testCryptomunt1)).thenReturn(testAsset4);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant1)).thenReturn(testRekening1);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant2)).thenReturn(testRekening2);

        try{
            transactieService.sluitTransactie(testKlant1, testCryptomunt1,
                    1000, 1100, 8.6, testKlant2);
            fail("Moet een NotEnoughCryptoException gooien");
        } catch (NotEnoughCryptoException exception) {
            System.out.println("Test geslaagd, execption: " + exception);
        }
    }*/


/*    @Test
    void saldoTooLowExceptionHandler() {
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant1)).thenReturn(testRekening1);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant2)).thenReturn(testRekening2);

        // zorg dat de prijs van de crypto (24500.0) hoger is dan het saldo van de koper (=20000.0)
        double vraagprijs = 40000;
        double bod = 9000;

        try {
            transactieService.sluitTransactie(testKlant1, testCryptomunt1,
                    vraagprijs, bod, 1.3, testKlant2);
            fail("Moet een BalanceTooLowException gooien");
        } catch (BalanceTooLowException exception) {
            System.out.println("Test geslaagd, exception: " + exception);
            System.out.println("saldo koper: " + testKlant2.getRekening().getSaldo());
            System.out.println("prijs crypto: " + (vraagprijs+bod)/2);
        }
    }*/

/*    @Test
    void checkRekeningGebruikersNaTransactie() {
        double vraagPrijs = 1000.0;
        double bod = 1100.0;
        double transactieBedragKoper = 1.6 * ((vraagPrijs + bod)/2.0) + (bankFee/2.0);
        System.out.println(transactieBedragKoper);
        double transactieBedragVerkoper = (1.6 * (vraagPrijs + bod)/2.0) - (bankFee/2.0);
        System.out.println(transactieBedragVerkoper);
        testRekening1.setSaldo(transactieBedragVerkoper);
        System.out.println("TESTREK1: "+ testRekening1.getSaldo());
        testRekening2.setSaldo(transactieBedragKoper);
        Mockito.when(rekeningService.wijzigSaldo(testKlant1, - transactieBedragVerkoper)).thenReturn(testRekening1);
        Mockito.when(rekeningService.wijzigSaldo(testKlant2, transactieBedragKoper))
                .thenReturn(testRekening2);
        Mockito.when(mockRootRepository.slaRekeningOp(testRekening1))
                .thenReturn(testRekening1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant1, testCryptomunt1)).thenReturn(testAsset1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant2, testCryptomunt1)).thenReturn(testAsset4);
        Mockito.when(mockRootRepository.wijzigAssetVanKlant(testAsset1)).thenReturn(testAsset1);
        Mockito.when(mockRootRepository.wijzigAssetVanKlant(testAsset4)).thenReturn(testAsset4);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant1)).thenReturn(testRekening1);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant2)).thenReturn(testRekening2);
        Mockito.when(transactieService.slaTransactieOp(actualTransactie1)).thenReturn(actualTransactie1);
        Mockito.when(transactieService.slaTransactieOp(actualTransactie)).thenReturn(actualTransactie);

        double saldoVoorTransactie = testKlant1.getRekening().getSaldo();
        System.out.println("BEFORE VERKOPER: " + saldoVoorTransactie);
        System.out.println("BEFORE KOPER: " + testKlant2.getRekening().getSaldo());
        Transactie actualTransactie = transactieService.sluitTransactie(testKlant1, testCryptomunt1,
                vraagPrijs, bod, 1.6, testKlant2);
        double saldoNaTransactie = testKlant1.getRekening().getSaldo();
        System.out.println("AFTER: " + saldoNaTransactie);

        assertThat(saldoVoorTransactie).isNotEqualTo(saldoNaTransactie);
    }*/

   /* public void slaAlleAspectenVanTransactieOp(Transactie transactie,
                                               double bedragKoper, double bedragVerkoper) {
        slaTransactieOp(transactie);
        transactie.getKoper().setRekening(rekeningService.wijzigSaldo(transactie.getKoper(), -bedragKoper));
        transactie.getVerkoper().setRekening(rekeningService.wijzigSaldo(transactie.getVerkoper(), bedragVerkoper));
        rekeningService.wijzigSaldo(Bank.getInstance(), transactie.getBankFee());
        assetService.wijzigAssetGebruiker(transactie.getKoper(), transactie.getCryptomunt(), -transactie.getAantal());
        assetService.wijzigAssetGebruiker(transactie.getVerkoper(), transactie.getCryptomunt(), transactie.getAantal());

    }*/

    @Test
    void slaAlleAspectenVanTransactieOp() {

    }

    @Test
    void SlaTransactieOp() {
        Transactie expectedTransactie = new Transactie (LocalDateTime.of(2022, 1, 12, 14, 45, 0), testKlant1, testCryptomunt1, 55, 2.3, testKlant2);
        Mockito.when(mockRootRepository.slaTransactieOp(expectedTransactie)).thenReturn(expectedTransactie);
        Transactie actualTransactie = transactieService.slaTransactieOp(expectedTransactie);



/*
        Mockito.when(rekeningService.wijzigSaldo(testKlant1, bedragKoperTestTransactie1))
                .thenReturn(testRekening11);
        Mockito.when(rekeningService.wijzigSaldo(testKlant2, bedragVerkoperTestTransactie1))
                .thenReturn(testRekening22);
        Mockito.when(mockRootRepository.slaRekeningOp(testRekening1))
                .thenReturn(testRekening1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant1, testCryptomunt1)).thenReturn(testAsset1);
        Mockito.when(mockRootRepository.geefAssetVanGebruiker(testKlant2, testCryptomunt1)).thenReturn(testAsset4);
        Mockito.when(mockRootRepository.wijzigAssetVanKlant(testAsset1)).thenReturn(testAsset1);
        Mockito.when(mockRootRepository.wijzigAssetVanKlant(testAsset4)).thenReturn(testAsset4);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant1)).thenReturn(testRekening1);
        Mockito.when(mockRootRepository.vindRekeningVanGebruiker(testKlant2)).thenReturn(testRekening2);
        Mockito.when(transactieService.slaTransactieOp(actualTransactie1)).thenReturn(actualTransactie1);
        Mockito.when(transactieService.slaTransactieOp(actualTransactie)).thenReturn(actualTransactie);
        Transactie actualTransactie = transactieService.sluitTransactie(testKlant1, testCryptomunt1,
                1000, 1100, 1.6, testKlant2);*/

        assertThat(expectedTransactie.getCryptomunt().getId()).isEqualTo(actualTransactie.getCryptomunt().getId());
    }
}