// Created by carme
// Creation date 01/12/2021

package com.example.thevault.handelingen;

import com.example.thevault.financieel.*;
import com.example.thevault.klant.*;
import net.minidev.json.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Deze klasse zorgt ervoor dat de incomplete objecten uit de DAO's volledig gemaakt kunnen worden
 * door de DAO's hier met elkaar te combineren
 */

@Repository
public class RootRepositoryKlant implements ApplicationListener<ContextRefreshedEvent> {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(RootRepositoryKlant.class);

    private final KlantDAO klantDAO;
    private final RekeningDAO rekeningDAO;
    private final AssetDAO assetDAO;
    private final CryptomuntDAO cryptomuntDAO;
    private final AdresDAO adresDAO;
    private final String KOPER = "Koper";
    private final String VERKOPER = "Verkoper";

    /**
     * Constructor voor RootRepositoryHandelingen
     * In de constructor worden alle DAOs geinjecteerd die de RootRepositoryHandelingen nodig heeft
     * @param klantDAO
     * @param rekeningDAO
     * @param assetDAO
     * @param cryptomuntDAO
     * @param adresDAO
     */
    @Autowired
    public RootRepositoryKlant(KlantDAO klantDAO, RekeningDAO rekeningDAO, AssetDAO assetDAO, CryptomuntDAO cryptomuntDAO,
                               AdresDAO adresDAO) {
        super();
        this.rekeningDAO = rekeningDAO;
        this.klantDAO = klantDAO;
        this.assetDAO = assetDAO;
        this.cryptomuntDAO = cryptomuntDAO;
        this.adresDAO = adresDAO;
        logger.info("New RootRepositoryHandelingen");
    }



    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }

    /**
     * author: Steven van Gils
     * Deze methode slaat de gegevens van een klant op in de database
     * op basis van een klant-object, de gebruikerId gaat van 0 naar de juiste
     *
     * @param klant het klant-object op basis van bij registratie ingevoerde gegevens
     * @return het klant-object met de juiste gebruikerId
     */
    public Klant slaKlantOp(Klant klant){
        klant.setAdres(adresDAO.slaAdresOp(klant.getAdres()));
        return klantDAO.slaKlantOp(klant);
    }

    /**
     * author: Steven van Gils
     * Deze methode zorgt ervoor dat een nieuw adres van een klant kan worden opgeslagen
     *
     * @param klant De betreffende klant
     * @param adres Het nieuwe adres
     * @return
     */
    public Adres slaNieuwAdresVanBestaandeKlantOp(Klant klant, Adres adres){
        adresDAO.slaAdresOp(adres);
        klant.setAdres(adres);
        klantDAO.updateKlant(klant);
        return adres;
    }

    /**
     * author: Steven van Gils
     * Deze methode zoekt of er in de database al een klant bestaat met deze gebruikersnaam
     * en maakt eventueel een klant-object aan op nasis van de teruggestuurde gegevens
     * Hier in de repository worden portefeuille en rekening toegevoegd
     *
     * @param gebruikersnaam gebruikersnaam van een (mogelijke) klant die uniek moet zijn
     * @return klant-object op basis van gegevens uit de database of null indien gebruikersnaam niet gevonden is
     */
    public Klant vindKlantByGebruikersnaam(String gebruikersnaam){
        Klant klant = klantDAO.vindKlantByGebruikersnaam(gebruikersnaam);
        maakKlantCompleet(klant);
        return klant;
    }

    void maakKlantCompleet(Klant klant) {
        if(klant != null){
            klant.setRekening(rekeningDAO.vindRekeningVanGebruiker(klant));
            klant.setAdres(adresDAO.getAdresByKlant(klant));
            klant.setPortefeuille(vulPortefeuilleKlant(klant));
         //   klant.setTransacties(geefTransactiesVanGebruiker(klant));
         //   klant.setTriggerKoperList(vindTriggersByGebruiker(klant, KOPER));
         //   klant.setTriggerVerkoperList(vindTriggersByGebruiker(klant, VERKOPER));

        }
    }

    /**
     * author: Steven van Gils
     * Deze methode zoekt of er in de database al een klant bestaat met deze gebruikerId
     * en maakt eventueel een klant-object aan op basis van de teruggestuurde gegevens
     * Hier in de repository worden portefeuille en rekening toegevoegd
     *
     * @param gebruikerId gebruikerId van een (mogelijke) klant die uniek moet zijn
     * @return klant-object op basis van gegevens uit de database of null indien gebruikerId niet gevonden is
     */
    public Klant vindKlantById(int gebruikerId){
        Klant klant = klantDAO.vindKlantById(gebruikerId);
        maakKlantCompleet(klant);
        return klant;
    }

    /**
     * Author: Ju-Sen Cheung
     * Deze methode slaat de gegevens van een rekening op in de database via de methode in de rekeningDAO.
     * @param rekening is de rekening die is aangemaakt bij een nieuwe gebruiker.
     * @return de rekening behorende bij de nieuwe gebruiker.
     */
    public Rekening slaRekeningOp(Rekening rekening){
        return rekeningDAO.slaRekeningOp(rekening);
    }

    /**
     * Author: Carmen
     * Dit betreft het vullen van de portefeuille met alle cryptomunten die er in zitten. Voor iedere asset
     * wordt alle informatie over de bijbehorende cryptomunt opgevraagd en meegegeven
     * @param gebruiker de klant die informatie opvraagt over de cryptomunt
     * @return List</Asset> een lijst van alle Assets (cryptomunten + hoeveelheden) in het bezit van de klant
     */
    public List<Asset> vulPortefeuilleKlant(Gebruiker gebruiker){
        List<Asset> portefeuille = assetDAO.geefAlleAssets(gebruiker);
        if(portefeuille.size() != 0){
        for (Asset asset: portefeuille) {
            Cryptomunt cryptomunt = cryptomuntDAO.geefCryptomunt(asset.getCryptomunt().getId());
            asset.setCryptomunt(cryptomunt);
        }
        gebruiker.setPortefeuille(portefeuille);
        }
        return portefeuille;
    }

    /**
     * Author: Carmen
     * Dit betreft het vinden van een specifieke cryptomunt die in de portefeuille zit
     * @param gebruiker de gebruiker die informatie opvraagt over de cryptomunt
     * @param cryptomunt cryptomunt waarover informatie wordt opgevraagd
     * @return Asset de asset (cryptomunt + aantal) waarover informatie is opgevraagd
     */
    public Asset geefAssetVanGebruiker(Gebruiker gebruiker, Cryptomunt cryptomunt){
        return assetDAO.geefAssetGebruiker(gebruiker, cryptomunt).orElseThrow(AssetNotExistsException::new);
    }
}
