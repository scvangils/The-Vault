package com.example.thevault.service;

import com.example.thevault.controller.rest_api_controller.RootRepository;
import com.example.thevault.financieel.Asset;
import com.example.thevault.handelingen.CryptoWaarde;
import com.example.thevault.controller.rest_api_controller.Cryptomunt;
import com.example.thevault.klant.Klant;
import com.example.thevault.financieel.AssetDto;
import com.example.thevault.klant.KlantService;
import com.example.thevault.support.BSNvalidator;
import com.example.thevault.support.exceptions.AgeTooLowException;
import com.example.thevault.support.exceptions.IncorrectBSNException;
import com.example.thevault.support.exceptions.PasswordNotSuitableException;
import com.example.thevault.support.hashing.BCryptWachtwoordHash;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;


class KlantServiceTest {


    private static Klant testKlant;
    private static Klant andereKlant;
    private static KlantService klantService;
    private static RootRepository mockRootRepository;
    public static List<Asset> portefeuille;
    public static List<AssetDto> portefeuilleDto;
    public static Cryptomunt testCryptomunt1;
    public static Cryptomunt testCryptomunt2;
    public static Cryptomunt testCryptomunt3;
    public static Asset testAsset1;
    public static Asset testAsset2;
    public static Asset testAsset3;
    public static Asset testAsset4;
    public static AssetDto testAssetDto1;
    public static AssetDto testAssetDto2;
    public static AssetDto testAssetDto3;
    public static AssetDto testAssetDto4;
    public static CryptoWaarde testCryptoWaarde1;
    public static CryptoWaarde testCryptoWaarde2;
    public static CryptoWaarde testCryptoWaarde3;


    @BeforeEach
    void setUp() {
        mockRootRepository = Mockito.mock(RootRepository.class);
        String gehashtWachtwoord = BCryptWachtwoordHash.hashWachtwoord("testWW");
        testKlant = new Klant("testKlant", gehashtWachtwoord, // klant is exact 18 vandaag
                "Jan", BSNvalidator.TESTBSN_VAN_RIVG,
                LocalDate.now().minusYears(KlantService.VOLWASSEN_LEEFTIJD));
         klantService = new KlantService(mockRootRepository);
        andereKlant = new Klant("andereKlant", gehashtWachtwoord, // klant is exact 18 vandaag
                "Jan", BSNvalidator.TESTBSN_VAN_RIVG,
                LocalDate.now().minusYears(KlantService.VOLWASSEN_LEEFTIJD));
        /**
         * @Author Carmen
         * Hieronder alle noodzakelijke input voor de test van portefeuille DTOs
         */
        testCryptomunt1 = new Cryptomunt(1, "CarmenCrypto", "CCR" );
        testCryptoWaarde1 = new CryptoWaarde("20211214CCR", testCryptomunt1, 100.0, LocalDate.now());
        testCryptomunt2 = new Cryptomunt(2, "DigiCrypto", "DIG");
        testCryptoWaarde2 = new CryptoWaarde("20211214DIG", testCryptomunt1, 75.0, LocalDate.now());
        testCryptomunt3 = new Cryptomunt(3, "Coyne", "COY");
        testCryptoWaarde3 = new CryptoWaarde("20211214COY", testCryptomunt1, 125.0, LocalDate.now());
        testAsset1 = new Asset(testCryptomunt1, 5.1);
        testAsset2 = new Asset(testCryptomunt2, 2.4);
        testAsset3 = new Asset(testCryptomunt3, 3.6);
        testAsset4 = new Asset(testCryptomunt1, 0.5);
        testAssetDto1 = new AssetDto(testAsset1, testCryptoWaarde1);
        testAssetDto2 = new AssetDto(testAsset2, testCryptoWaarde2);
        testAssetDto3 = new AssetDto(testAsset3, testCryptoWaarde3);
        testAssetDto4 = new AssetDto(testAsset4, testCryptoWaarde1);
        portefeuille = new ArrayList<>();
        portefeuille.add(testAsset1);
        portefeuille.add(testAsset2);
        portefeuille.add(testAsset3);
        portefeuilleDto = new ArrayList<>();
        portefeuilleDto.add(testAssetDto1);
        portefeuilleDto.add(testAssetDto2);
        portefeuilleDto.add(testAssetDto3);
    }



    @Test
    void vindKlantByUsername() {
        Mockito.when(mockRootRepository.vindKlantByGebruikersnaam("testKlant"))
                .thenReturn(testKlant);
        Klant expected = testKlant;
        Klant actual = klantService.vindKlantByGebruikersnaam(testKlant.getGebruikersnaam());
        assertThat(actual).isNotNull().isEqualTo(expected);
        actual = klantService.vindKlantByGebruikersnaam(andereKlant.getGebruikersnaam());
        assertThat(actual).isNull();
    }

    //TODO test geefNuttigePortefeuille aanpassen, wijziging door WIBUL 20220114
//    /**
//     * @Author Carmen
//     * Omdat ik de methode heb verplaatst van AssetService naar KlantService moest ik ook de test verplaatsen
//     */
//    @Test
//    void geefNuttigePortefeuille() {
//     //   Mockito.when(mockRootRepository.vulPortefeuilleKlant(testKlant)).thenReturn(portefeuille);
//        testKlant.setPortefeuille(portefeuille);
//        Mockito.when(mockRootRepository.haalMeestRecenteCryptoWaarde(testAsset1.getCryptomunt())).thenReturn(testCryptoWaarde1);
//        Mockito.when(mockRootRepository.haalMeestRecenteCryptoWaarde(testAsset2.getCryptomunt())).thenReturn(testCryptoWaarde2);
//        Mockito.when(mockRootRepository.haalMeestRecenteCryptoWaarde(testAsset3.getCryptomunt())).thenReturn(testCryptoWaarde3);
//        List<AssetDto> expected = portefeuilleDto;
//        List<AssetDto> actual = klantService.geefNuttigePortefeuille(testKlant);
//        assertThat(actual).as("Test geef inhoud portefeuilleDto van testklant").isNotNull().isEqualTo(expected).
//                contains(testAssetDto1, atIndex(0)).contains(testAssetDto2, atIndex(1)).contains(testAssetDto3, atIndex(2)).
//                doesNotContain(testAssetDto4).hasSize(3).extracting(AssetDto::getName).
//                contains("CarmenCrypto", "DigiCrypto", "Coyne").doesNotContain("BitCoin");
//    }

    @Test
    void registreerKlant() {
        Mockito.when(mockRootRepository.slaKlantOp(testKlant)).thenReturn(testKlant);
        Klant expected = testKlant;
        Klant actual = klantService.registreerKlant(testKlant);
        assertThat(actual).as("Juiste klantgegevens zorgt voor goede registratie").isNotNull().isEqualTo(expected);
        testKlant.setGeboortedatum(testKlant.getGeboortedatum().plusDays(1)); // nu te jong om rekening te openen
        assertThatThrownBy(() -> klantService.registreerKlant(testKlant)).as("AgeTooLowException getest")
                .isInstanceOf(AgeTooLowException.class);
        testKlant.setGeboortedatum(testKlant.getGeboortedatum().minusDays(1));
        testKlant.setBsn(testKlant.getBsn() + 1);
        assertThatThrownBy(() -> klantService.registreerKlant(testKlant)).as("IncorrectBSNException getest")
                .isInstanceOf(IncorrectBSNException.class);
        testKlant.setBsn(testKlant.getBsn() - 1);
        testKlant.setWachtwoord("WwMet Spatie");
        assertThat(klantService.checkWachtwoordFormat(testKlant)).isFalse();
        assertThatThrownBy(() -> klantService.registreerKlant(testKlant)).as("Wachtwoord met spatie niet toegestaan Exception getest")
                .isInstanceOf(PasswordNotSuitableException.class);
        testKlant.setWachtwoord("Ww<8Kar");
        assertThat(klantService.checkWachtwoordLengte(testKlant)).isFalse();
        assertThatThrownBy(() -> klantService.registreerKlant(testKlant)).as("Wachtwoord minder dan 8 karakters niet toegestaan Exception getest")
                .isInstanceOf(PasswordNotSuitableException.class);

    }
    @Test
    void checkVolwassen(){
        assertThat(klantService.checkVolwassen(testKlant)).isTrue();
        testKlant.setGeboortedatum(testKlant.getGeboortedatum().plusDays(1)); // nu te jong om rekening te openen
        assertThat(klantService.checkVolwassen(testKlant)).isFalse();
        testKlant.setGeboortedatum(testKlant.getGeboortedatum().minusDays(4));
        assertThat(klantService.checkVolwassen(testKlant)).isTrue();

    }
    @Test
    void checkWachtwoordFormat(){
        testKlant.setWachtwoord("WwMet Spatie");
        assertThat(klantService.checkWachtwoordFormat(testKlant)).isFalse();
        testKlant.setWachtwoord("WwZonderSpatie");
        assertThat(klantService.checkWachtwoordFormat(testKlant)).isTrue();


    }
    @Test
    void checkWachtwoordLengte(){
        testKlant.setWachtwoord("Ww<8Kar");
        assertThat(klantService.checkWachtwoordLengte(testKlant)).isFalse();
        testKlant.setWachtwoord("8KarInWw");
        assertThat(klantService.checkWachtwoordLengte(testKlant)).isTrue();
    }

}