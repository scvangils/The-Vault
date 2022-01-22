package com.example.thevault.service;

import com.example.thevault.controller.rest_api_controller.RootRepository;
import com.example.thevault.financieel.Asset;
import com.example.thevault.controller.rest_api_controller.Cryptomunt;
import com.example.thevault.klant.Klant;
import com.example.thevault.financieel.AssetDAO;
import com.example.thevault.financieel.AssetService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class AssetServiceIntegratieTestDatabase {

    private AssetService assetService;
    @Autowired
    private RootRepository rootRepository;
    private AssetDAO assetDAO;
    private Klant testKlant;
    private Asset testAsset1;
    private Cryptomunt testCryptomunt1;

    @BeforeEach
    void setUp() {
        assetDAO = Mockito.mock(AssetDAO.class);
        assetService = new AssetService(rootRepository);
        testCryptomunt1 = new Cryptomunt(1, null, null);
        testAsset1 = new Asset(testCryptomunt1, 0.2);
        testKlant = new Klant("Huub", "PWHuub", "Huub",
                0, null);
        testKlant.setGebruikerId(1);
    }

    @Test
    void wijzigAssetGebruiker() {
        Mockito.when(assetDAO.updateAsset(testKlant, testCryptomunt1, 0.5)).thenReturn(testAsset1);
        Asset expected = testAsset1;
        Asset actual = assetService.wijzigAssetGebruiker(testKlant, testCryptomunt1, -4.0);
        System.out.println(expected);
        System.out.println(actual);
        assertThat(actual).as("Test wijzigen van asset van testklant AANTAL").isNotNull().
                extracting(Asset::getAantal).isEqualTo(expected.getAantal());
        assertThat(actual).as("Test wijzigen van asset van testklant CryptoID").isNotNull().
                extracting(Asset::getCryptomunt).extracting(Cryptomunt::getId).isEqualTo(expected.getCryptomunt().
                        getId());
    }
}