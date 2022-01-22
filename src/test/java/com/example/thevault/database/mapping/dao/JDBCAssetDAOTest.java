package com.example.thevault.database.mapping.dao;

import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.klant.Gebruiker;
import com.example.thevault.financieel.Asset;
import com.example.thevault.klant.Klant;
import com.example.thevault.financieel.AssetDAO;
import com.example.thevault.handelingen.NotEnoughCryptoException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

@SpringBootTest
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class JDBCAssetDAOTest {

    private AssetDAO testAssetDAO;
    private Gebruiker testKlant1;
    private Gebruiker testKlant2;
    private Gebruiker testKlant3;
    private Cryptomunt testCryptomunt1;
    private Cryptomunt testCryptomunt2;
    private Cryptomunt testCryptomunt3;
    private Cryptomunt testCryptomunt4;
    private Asset testAsset1;
    private Asset testAsset2;
    private Asset testAsset3;
    private Asset testAsset4;
    private List<Asset> testAsset5;
    private Asset testAsset6;
    private Asset testAsset7;
    private Asset testAsset8;

    @Autowired
    public JDBCAssetDAOTest(AssetDAO testAssetDAO) {
        super();
        this.testAssetDAO = testAssetDAO;
    }

    @Test
    public void setupTest(){
        assertThat(testAssetDAO).isNotNull();
    }

    @BeforeEach
    void setup() {
        testKlant1 = new Klant("Jolien", "BeterWachtwoord",
                "Jolien",
                987654321, LocalDate.parse("1985-10-14"));
        testKlant2 = new Klant("Carmen", "GoedWachtwoord",
                "Carmen",
                123456789, LocalDate.parse("1985-12-30"));
        testKlant3 = new Klant("Pimmeh", "BesteWachtwoord",
                "Pim", 246813579, LocalDate.parse("1987-02-26"));
        testKlant1.setGebruikerId(2);
        testKlant2.setGebruikerId(1);
        testKlant3.setGebruikerId(3);
        testCryptomunt1 = new Cryptomunt(1, "Bitcoin", "BCN");
        testCryptomunt2 = new Cryptomunt(1, null, null);
        testCryptomunt3 = new Cryptomunt(2, null, null);
        testCryptomunt4 = new Cryptomunt(3, "Elrond", "LOTR");
        testAsset1 = new Asset(testCryptomunt1, 4.2, testKlant2);
        testAsset2 = new Asset(testCryptomunt1, 5.3, testKlant2);
        testAsset3 = new Asset(testCryptomunt2, 4.2, testKlant2);
        testAsset4 = new Asset(testCryptomunt3, 3.5, testKlant2);
        testAsset5 = new ArrayList<>();
        testAsset5.add(testAsset3);
        testAsset5.add(testAsset4);
        testAsset6 = new Asset(testCryptomunt1, 100, testKlant2);
        testAsset7 = new Asset(testCryptomunt1, 3.1, testKlant2);
        testAsset8 = new Asset(testCryptomunt4, 9.0, testKlant3);
    }

    @Test
    void geefAanwezigAsset() {
        Optional<Asset> actual = testAssetDAO.geefAssetGebruiker(testKlant2, testCryptomunt1);
        Asset actualUitgepakt = actual.orElse(testAsset6);
        Asset expected = testAsset3;
        Asset unexpected = testAsset6;
        assertThat(actualUitgepakt.getAantal()).as("Test geef 1 asset van gebruiker: aantal").isNotNull().
                isEqualTo(expected.getAantal()).isNotEqualTo(unexpected.getAantal());
    }

    @Test
    void geefNietAanwezigAsset() {
        Optional<Asset> actual = testAssetDAO.geefAssetGebruiker(testKlant3, testCryptomunt2);
        assertThat(actual).as("Test geef niet bestaand asset van gebruiker").isEmpty();
    }

    @Test
    void geefAlleAssets() {
        List<Asset> actual = testAssetDAO.geefAlleAssets(testKlant2);
        List<Asset> expected = testAsset5;
        assertThat(actual.get(0).getGebruiker().getGebruikerId()).
                as("Test ophalen alle assets van gebruiker: gebruiker 1").isNotNull().
                isEqualTo(expected.get(0).getGebruiker().getGebruikerId());
        assertThat(actual.get(1).getGebruiker().getGebruikerId()).
                as("Test ophalen alle assets van gebruiker: gebruiker 2").isNotNull().
                isEqualTo(expected.get(1).getGebruiker().getGebruikerId());
        assertThat(actual.get(0).getCryptomunt().getId()).
                as("Test ophalen alle assets van gebruiker: crypto 1").isNotNull().
                isEqualTo(expected.get(0).getCryptomunt().getId());
        assertThat(actual.get(1).getCryptomunt().getId()).
                as("Test ophalen alle assets van gebruiker: crypto 2").isNotNull().
                isEqualTo(expected.get(1).getCryptomunt().getId());
        assertThat(actual.get(0).getAantal()).as("Test ophalen alle assets van gebruiker: aantal 1").
                isNotNull().isEqualTo(expected.get(0).getAantal());
        assertThat(actual.get(1).getAantal()).as("Test ophalen alle assets van gebruiker: aantal 1").
                isNotNull().isEqualTo(expected.get(1).getAantal());
    }

    @Test
    void geefAlleAssetsLeeg() {
        List<Asset> actual = testAssetDAO.geefAlleAssets(testKlant3);
        assertThat(actual).as("Test ophalen alle assets van gebruiker is leeg").isNullOrEmpty();
    }

    @Test
    void voegNieuwAssetToeAanPortefeuille() {
        Asset actual = testAssetDAO.voegNieuwAssetToeAanPortefeuille(testAsset8);
        Asset expected = testAsset8;
        assertThat(actual).as("Test nieuwe asset toevoegen").isNotNull().isEqualTo(expected).extracting(Asset::getGebruiker).
                extracting(Gebruiker::getGebruikersnaam).isEqualTo("Pimmeh");
    }

    @Test
    void verwijderAssetUitPortefeuille() {
        Asset actual = testAssetDAO.verwijderAssetUitPortefeuille(testAsset1);
        Asset expected = testAsset1;
        assertThat(actual).as("Verwijder asset uit portefeuille test deel 1").isNotNull().isEqualTo(expected).
                hasNoNullFieldsOrPropertiesExcept("Cryptomunt", "Aantal", "Gebruiker");
        Optional<Asset> actual2 = testAssetDAO.geefAssetGebruiker(testKlant2, testCryptomunt1);
        assertThat(actual2).as("Verwijder asset uit portefeuille test deel 2").isEmpty();
    }

    @Test
    void updateAssetAankoop() {
        Asset actual = testAssetDAO.updateAsset(testKlant2, testCryptomunt1, 1.1);
        Asset expected = testAsset2;
        System.out.println(actual);
        System.out.println(expected);
        assertThat(actual.getAantal()).isEqualTo(expected.getAantal());
    }

    @Test
    void updateAssetVerkoop() {
        Asset actual = testAssetDAO.updateAsset(testKlant2, testCryptomunt1, -1.1);
        Asset expected = testAsset7;
        assertThat(actual.getAantal()).isEqualTo(expected.getAantal());
    }

    @Test
    void updateAssetVerkoopTekortAanCrypto() {
        try{
            testAssetDAO.updateAsset(testKlant2, testCryptomunt1, -10);
            fail();
        } catch (NotEnoughCryptoException notEnoughCryptoException) {
            System.out.println("Not Enough Crypto exception werkt");
        }
    }
}