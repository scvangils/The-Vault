package com.example.thevault.service;

import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.handelingen.RootRepository;
import com.example.thevault.financieel.Asset;
import com.example.thevault.financieel.AssetDto;
import com.example.thevault.handelingen.CryptoWaarde;
import com.example.thevault.klant.Klant;
import com.example.thevault.financieel.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class AssetServiceTest {

    public static Klant testKlant;
    public static Asset testAsset1;
    public static Asset testAsset2;
    public static Asset testAsset3;
    public static Asset testAsset4;
    public static Asset testAsset5;
    public static AssetDto testAssetDto1;
    public static AssetDto testAssetDto2;
    public static AssetDto testAssetDto3;
    public static AssetDto testAssetDto4;
    public static RootRepository rootRepository;
    public static AssetService assetService;
    public static List<Asset> portefeuille;
    public static List<AssetDto> portefeuilleDto;
    public static Cryptomunt testCryptomunt1;
    public static Cryptomunt testCryptomunt2;
    public static Cryptomunt testCryptomunt3;
    public static CryptoWaarde testCryptoWaarde1;
    public static CryptoWaarde testCryptoWaarde2;
    public static CryptoWaarde testCryptoWaarde3;
    public static List<Cryptomunt> testLijstCrypto;

    @BeforeEach
    void setUp() {
        testKlant = new Klant();
        testCryptomunt1 = new Cryptomunt(1, "CarmenCrypto", "CCR" );
        testCryptoWaarde1 = new CryptoWaarde("20211214CCR", testCryptomunt1, 100.0, LocalDate.now());
        testCryptomunt2 = new Cryptomunt(2, "DigiCrypto", "DIG");
        testCryptoWaarde2 = new CryptoWaarde("20211214DIG", testCryptomunt2, 75.0, LocalDate.now());
        testCryptomunt3 = new Cryptomunt(3, "Coyne", "COY");
        testCryptoWaarde3 = new CryptoWaarde("20211214COY", testCryptomunt3, 125.0, LocalDate.now());
        testAsset1 = new Asset(testCryptomunt1, 5.1);
        testAsset2 = new Asset(testCryptomunt2, 2.4);
        testAsset3 = new Asset(testCryptomunt3, 3.6);
        testAsset4 = new Asset(testCryptomunt1, 0.5);
        testAsset5 = new Asset(testCryptomunt1, 5.6);
        testAssetDto1 = new AssetDto(testAsset1, testCryptoWaarde1);
        testAssetDto2 = new AssetDto(testAsset2, testCryptoWaarde2);
        testAssetDto3 = new AssetDto(testAsset3, testCryptoWaarde3);
        testAssetDto4 = new AssetDto(testAsset4, testCryptoWaarde1);
        rootRepository = Mockito.mock(RootRepository.class);
        assetService = new AssetService(rootRepository);
        portefeuille = new ArrayList<>();
        portefeuille.add(testAsset1);
        portefeuille.add(testAsset2);
        portefeuille.add(testAsset3);
        portefeuilleDto = new ArrayList<>();
        portefeuilleDto.add(testAssetDto1);
        portefeuilleDto.add(testAssetDto2);
        portefeuilleDto.add(testAssetDto3);
        testLijstCrypto = new ArrayList<>();
        testLijstCrypto.add(testCryptomunt1);
        testLijstCrypto.add(testCryptomunt2);
        testLijstCrypto.add(testCryptomunt3);
        testKlant = new Klant("Huub", "PWHuub", "Huub",
                0, null);
    }

    @Test
    void geefCryptomunt() {
        Mockito.when(rootRepository.geefAssetVanGebruiker(testKlant, testCryptomunt1)).thenReturn(testAsset1);
        AssetDto expected = new AssetDto(testAsset1, testCryptoWaarde1);
        AssetDto actual = assetService.geefCryptomunt(testKlant, testCryptomunt1, testCryptoWaarde1);
        assertThat(actual).as("Test geef specifieke AssetDto van testklant").isNotNull().isEqualTo(expected).
                isIn(portefeuilleDto).isNotEqualTo(testAssetDto2).isNotSameAs(testAssetDto3);
    }

    @Test
    void slaNieuwAssetOp() {
        Mockito.when(rootRepository.slaNieuwAssetVanKlantOp(testAsset2)).thenReturn(testAsset2);
        Asset expected = testAsset2;
        Asset actual = assetService.slaNieuwAssetOp(testAsset2);
        assertThat(actual).as("Test sla asset van testklant op").isNotNull().isEqualTo(expected).
                isIn(portefeuille).isNotEqualTo(testAsset3).isNotSameAs(testAsset4);
    }

    @Test
    void wijzigAssetGebruiker() {
        Mockito.when(rootRepository.vulPortefeuilleKlant(testKlant)).thenReturn(portefeuille);
        Mockito.when(rootRepository.wijzigAssetVanKlant(testKlant, testCryptomunt1, 0.5)).thenReturn(testAsset5);
        Asset expected = testAsset5;
        Asset actual = assetService.wijzigAssetGebruiker(testKlant, testCryptomunt1, 0.5);
        assertThat(actual).as("Test wijzigen van asset van testklant").isNotNull().isEqualTo(expected).
                isNotIn(portefeuille);
    }

    @Test
    void vulPortefeuilleVanGebruiker() {
        Mockito.when(rootRepository.vulPortefeuilleKlant(testKlant)).thenReturn(portefeuille);
        List<Asset> expected = portefeuille;
        List<Asset> actual = assetService.vulPortefeuilleVanGebruiker(testKlant);
        assertThat(actual).as("Test vullen portefeuille gebruiker").isNotNull().isEqualTo(expected)
                .extracting(Asset::getAantal).contains(5.1).contains(2.4).contains(3.6);
    }

    @Test
    void geefAlleCryptomunten() {
        Mockito.when(rootRepository.geefAlleCryptomunten()).thenReturn(testLijstCrypto);
        List<Cryptomunt> expected = testLijstCrypto;
        List<Cryptomunt> actual = assetService.geefAlleCryptomunten();
        assertThat(actual).as("Test geef alle cryptomunten").isNotNull().isNotEmpty().isEqualTo(expected).
                extracting(Cryptomunt::getName).contains("CarmenCrypto").doesNotContain("Elrond");
    }
}