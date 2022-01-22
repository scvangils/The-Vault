package com.example.thevault.service;

import com.example.thevault.database.mapping.dao.*;
import com.example.thevault.database.mapping.repository.RootRepository;
import com.example.thevault.domain.model.Asset;
import com.example.thevault.domain.model.Cryptomunt;
import com.example.thevault.domain.model.Klant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.assertj.core.api.Assertions.assertThat;

class AssetServiceIntegratieTestRootRepository {

    private AssetService assetService;
    private RootRepository rootRepository;
    private KlantDAO klantDAO;
    private RekeningDAO rekeningDAO;
    private AssetDAO assetDAO;
    private CryptomuntDAO cryptomuntDAO;
    private CryptoWaardeDAO cryptoWaardeDAO;
    private AdresDAO adresDAO;
    private TransactieDAO transactieDAO;
    private TriggerDAO triggerDAO;
    private Klant testKlant;
    private Asset testAsset1;
    private Asset testAsset4;
    private Cryptomunt testCryptomunt1;

    public AssetServiceIntegratieTestRootRepository(){
        super();
        klantDAO = Mockito.mock(KlantDAO.class);
        rekeningDAO = Mockito.mock(RekeningDAO.class);
        assetDAO = Mockito.mock(AssetDAO.class);
        cryptomuntDAO = Mockito.mock(CryptomuntDAO.class);
        cryptoWaardeDAO = Mockito.mock(CryptoWaardeDAO.class);
        adresDAO = Mockito.mock(AdresDAO.class);
        transactieDAO = Mockito.mock(TransactieDAO.class);
        triggerDAO = Mockito.mock(TriggerDAO.class);
        this.rootRepository = new RootRepository(klantDAO, rekeningDAO, assetDAO, cryptomuntDAO, cryptoWaardeDAO,
                adresDAO, transactieDAO, triggerDAO);
        this.assetService = new AssetService(rootRepository);
    }

    @BeforeEach
    void setUp() {
        testCryptomunt1 = new Cryptomunt(1, "CarmenCrypto", "CCR" );
        testAsset1 = new Asset(testCryptomunt1, 5.1);
        testAsset4 = new Asset(testCryptomunt1, 5.6);
        testKlant = new Klant("Huub", "PWHuub", "Huub",
                0, null);
        testKlant.setGebruikerId(1);
    }

    @Test
    void wijzigAssetGebruiker() {
        Mockito.when(assetDAO.updateAsset(testKlant, testCryptomunt1, 0.5)).thenReturn(testAsset4);
        Asset expected = testAsset4;
        Asset actual = assetService.wijzigAssetGebruiker(testKlant, testCryptomunt1, -4.0);
        System.out.println(expected);
        System.out.println(actual);
//        assertThat(actual).as("Test wijzigen van asset van testklant").isNotNull().isEqualTo(expected);
    }
}