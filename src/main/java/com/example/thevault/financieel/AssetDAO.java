package com.example.thevault.financieel;

import com.example.thevault.controller.rest_api_controller.Cryptomunt;
import com.example.thevault.controller.rest_api_controller.Gebruiker;

import java.util.List;
import java.util.Optional;

/**
 * @Author: Carmen Rietdijk
 * Beschrijving: De interface voor de DAO voor Asset, waar methodes in staan voor CRUD van de Asset van de klant,
 * alsmede de methode om de portefeuille van de klant te vullen met Assets
 */

public interface AssetDAO {

    /**
     * Dit betreft het toevoegen van een cryptomunt die nog niet in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt
     * @param asset de cryptomunt en het aantal dat de klant aanschaft
     * @return Asset de asset die de klant heeft toegevoegd
     */
    public Asset voegNieuwAssetToeAanPortefeuille(Asset asset);

    /**
     * Dit betreft het verwijderen van een cryptomunt uit de portefeuille
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's verkoopt
     * @param asset de asset die uit de portefeuille wordt verwijderd
     * @return String bericht dat de cryptomunt uit de portefeuille is verwijderd
     */
    public Asset verwijderAssetUitPortefeuille(Asset asset);

    /**
     * Dit betreft het updaten van een cryptomunt die al in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt of verkoopt
     * @param gebruiker de asset waarin de klant handelt
     * @param cryptomunt
     * @param aantal
     * @return Asset de asset na de update
     */
    public Asset updateAsset(Gebruiker gebruiker, Cryptomunt cryptomunt, double aantal);

    /**
     * Dit betreft het vinden van een cryptomunt die in de portefeuille zit
     * @param gebruiker gebruiker die informatie opvraagt over de cryptomunt
     * @param cryptomunt cryptomunt waarover informatie wordt opgevraagd
     * @return Asset de asset (cryptomunt + aantal) waarover informatie is opgevraagd
     */
    public Optional<Asset> geefAssetGebruiker(Gebruiker gebruiker, Cryptomunt cryptomunt);

    /**
     * Dit betreft het vinden van alle cryptomunten die in de portefeuille zitten
     * @param gebruiker klant die informatie opvraagt over de cryptomunten
     * @return List</Asset> een lijst van alle Assets in het bezit van de klant
     */
    public List<Asset> geefAlleAssets(Gebruiker gebruiker);

    public double geefAantalCryptoInEigendom(Gebruiker gebruiker, Cryptomunt cryptomunt);
}
