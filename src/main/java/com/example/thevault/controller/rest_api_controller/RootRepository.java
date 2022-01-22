// Created by carme
// Creation date 01/12/2021

package com.example.thevault.controller.rest_api_controller;

import com.example.thevault.handelingen.*;
import com.example.thevault.financieel.Asset;
import com.example.thevault.klant.Adres;
import com.example.thevault.klant.AdresDAO;
import com.example.thevault.klant.Klant;
import com.example.thevault.klant.KlantDAO;
import com.example.thevault.financieel.AssetDAO;
import com.example.thevault.financieel.Rekening;
import com.example.thevault.financieel.RekeningDAO;
import com.example.thevault.support.exceptions.AssetNotExistsException;
import net.minidev.json.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

/**
 * Deze klasse zorgt ervoor dat de incomplete objecten uit de DAO's volledig gemaakt kunnen worden
 * door de DAO's hier met elkaar te combineren
 */

@Repository
public class RootRepository implements ApplicationListener<ContextRefreshedEvent> {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(RootRepository.class);

    private final KlantDAO klantDAO;
    private final RekeningDAO rekeningDAO;
    private final AssetDAO assetDAO;
    private final CryptomuntDAO cryptomuntDAO;
    private final CryptoWaardeDAO cryptoWaardeDAO;
    private final AdresDAO adresDAO;
    private final TransactieDAO transactieDAO;
    private final TriggerDAO triggerDAO;
    private final String KOPER = "Koper";
    private final String VERKOPER = "Verkoper";

    /**
     * Constructor voor RootRepository
     * In de constructor worden alle DAOs geinjecteerd die de RootRepository nodig heeft
     * @param klantDAO
     * @param rekeningDAO
     * @param assetDAO
     * @param cryptomuntDAO
     * @param cryptoWaardeDAO
     * @param adresDAO
     * @param transactieDAO
     * @param triggerDAO
     */
    @Autowired
    public RootRepository(KlantDAO klantDAO, RekeningDAO rekeningDAO, AssetDAO assetDAO, CryptomuntDAO cryptomuntDAO,
                          CryptoWaardeDAO cryptoWaardeDAO, AdresDAO adresDAO,
                          TransactieDAO transactieDAO, TriggerDAO triggerDAO) {
        super();
        this.rekeningDAO = rekeningDAO;
        this.klantDAO = klantDAO;
        this.assetDAO = assetDAO;
        this.cryptomuntDAO = cryptomuntDAO;
        this.cryptoWaardeDAO = cryptoWaardeDAO;
        this.adresDAO = adresDAO;
        this.transactieDAO = transactieDAO;
        this.triggerDAO = triggerDAO;
        logger.info("New RootRepository");
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
    private void maakKlantCompleet(Klant klant) {
        if(klant != null){
            klant.setRekening(vindRekeningVanGebruiker(klant));
            klant.setAdres(adresDAO.getAdresByKlant(klant));
            klant.setPortefeuille(vulPortefeuilleKlant(klant));
            klant.setTransacties(geefTransactiesVanGebruiker(klant));
            klant.setTriggerKoperList(vindTriggersByGebruiker(klant, KOPER));
            klant.setTriggerVerkoperList(vindTriggersByGebruiker(klant, VERKOPER));

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
    * Author: Ju-Sen Cheung
    * Methode die op gebruiker zoekt en de rekening teruggeeft
    * @param gebruiker kan zowel een klant als een bank zijn
    * @return rekening geeft een rekening object terug
    * */
    public Rekening vindRekeningVanGebruiker(Gebruiker gebruiker){
        return rekeningDAO.vindRekeningVanGebruiker(gebruiker);
    }

    /**
     * Author: Ju-Sen Cheung
     * Deze methode geeft het rekeningsaldo op van de gebruiker. Het saldo wordt via de methode in de rekeningDAO uit
     * de database gehaald.
     * @param gebruiker is de gebruiker van wie het rekeningsaldo wordt opgevraagd.
     * @return het rekeningsaldo behorende bij de gebruiker.
     */
    public double vraagSaldoOpVanGebruiker(Gebruiker gebruiker){
        return rekeningDAO.vraagSaldoOpVanGebruiker(gebruiker);
    }

    /**
     * Author: Ju-Sen Cheung
     * Deze methode wijzigt het rekeningsaldo van de klant in de database via de methode in de rekeningDAO.
     * @param gebruiker is de gebruiker van wie het rekeningsaldo wordt opgevraagd.
     * @param transactiebedrag is het bedrag waarmee het saldo van de rekening verhoogd of verlaagd moet worden.
     * @return rekening met geüpdatete saldo.
     */
    public Rekening wijzigSaldoVanGebruiker(Gebruiker gebruiker, double transactiebedrag){
        return rekeningDAO.wijzigSaldoVanGebruiker(gebruiker, transactiebedrag);
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

    /**
     * Author: Carmen
     * Dit betreft het toevoegen van een cryptomunt die nog niet in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt
     * @param asset de cryptomunt en het aantal dat de klant aanschaft
     * @return Asset de asset die de klant heeft toegevoegd
     */
    public Asset slaNieuwAssetVanKlantOp(Asset asset){
        return assetDAO.voegNieuwAssetToeAanPortefeuille(asset);
    }

    /**
     * Author: Carmen
     * Dit betreft het wijzigen van een cryptomunt die al in de portefeuille zit
     * Dit gebeurt via een 'transactie', waarbij een klant crypto's koopt of verkoopt
     * @param gebruiker de handelende partij
     * @param cryptomunt de munt waarin gehandeld wordt
     * @param aantal de hoeveelheid die verhandeld wordt
     * @return Asset de asset na de update, waarbij het nieuwe aantal wordt meegegeven
     */
    public Asset wijzigAssetVanKlant(Gebruiker gebruiker, Cryptomunt cryptomunt, double aantal){
        return assetDAO.updateAsset(gebruiker, cryptomunt , aantal);
    }

    /**
     * author: Steven van Gils
     * Deze methode haalt uit de database de waarde die de betreffende cryptomunt vandaag heeft
     * zodat huidige waarde van assets kan worden berekend
     *
     * @param cryptomunt de cryptomunt
     * @return CryptoWaarde-object om huidige waarde van asset te kunnen berekenen
     */
    //CryptoWaarde wordt eens per dag opgehaald om 00.00 uur
    public CryptoWaarde haalMeestRecenteCryptoWaarde(Cryptomunt cryptomunt){
        CryptoWaarde cryptoWaarde = cryptoWaardeDAO.getCryptoWaardeByCryptomuntAndDate(cryptomunt, LocalDate.now());
        cryptoWaarde.setCryptomunt(cryptomunt);
        return cryptoWaarde;
    }

    /**
     * author: Steven van Gils
     * Deze methode haalt de koers van een cryptomunt op een bepaalde dag op
     *
     * @param cryptomunt De betreffende cryptomunt
     * @param datum De datum waarop gezocht wordt
     * @return
     */
    public CryptoWaarde haalCryptoWaardeOpDatum(Cryptomunt cryptomunt, LocalDate datum){
        CryptoWaarde cryptoWaarde = cryptoWaardeDAO.getCryptoWaardeByCryptomuntAndDate(cryptomunt, datum);
        cryptoWaarde.setCryptomunt(cryptomunt);
        return cryptoWaarde;
    }
    /**
     * author: Steven van Gils/Wim Bultman
     * Deze methode slaat een cryptowaarde op in de database
     *
     * @param cryptoWaarde De betreffende cryptowaarde
     * @return
     */
    public CryptoWaarde slaCryptoWaardeOp(CryptoWaarde cryptoWaarde){
        return cryptoWaardeDAO.slaCryptoWaardeOp(cryptoWaarde);
    }

    /**
     * Deze methode haalt de hele historie van koersen van een cryptomunt op
     *
     * @param cryptomunt  De betreffende cryptomunt
     * @return
     */
    public List<CryptoWaarde> haalAlleCryptoWaardesVanCryptomunt(Cryptomunt cryptomunt){
        List<CryptoWaarde> cryptoWaardeList = cryptoWaardeDAO.getCryptoWaardeByCryptomunt(cryptomunt);
        for(CryptoWaarde cryptoWaarde: cryptoWaardeList){
            cryptoWaarde.setCryptomunt(cryptomunt);
        }
        return cryptoWaardeList;
    }

    /**
     *  Deze methode slaat een transactie op in de database
     *
     * @param transactie de betreffende transactie
     * @return de transactie met zijn nieuwe Id
     */
    public Transactie slaTransactieOp(Transactie transactie){
        return transactieDAO.slaTransactieOp(transactie);
    }

    /**
     * methode die alle transacties die bij een klant horen teruggeeft
     * hierbij worden eerst de koper, verkoper en cryptomunt op id uit de database
     * gehaald en toegevoegd aan het transactie object
     * Als de bank een partij is, wordt er geen gebruiker uit de database gehaald,
     * alleen een rekening
     *
     * @param gebruiker de klant waarvan alle transacties moeten worden opgezocht
     * @return lijst transacties van de klant
     */
    List<Transactie> geefTransactiesVanGebruiker(Gebruiker gebruiker){
        List<Transactie> transactiesVanKlant = transactieDAO.geefTransactiesVanGebruiker(gebruiker);
        for (Transactie transactie: transactiesVanKlant) {
            maakTransactieCompleet(transactie);
        }
        return transactiesVanKlant;
    }

    private void maakTransactieCompleet(Transactie transactie) {
        setKoperTransactie(transactie);
        setVerkoperTransactie(transactie);
        transactie.setCryptomunt(cryptomuntDAO.geefCryptomunt(transactie.getCryptomunt().getId()));
    }

    private void setVerkoperTransactie(Transactie transactie) {
        if(transactie.getVerkoper().getGebruikerId() != 0) {
            transactie.setVerkoper(klantDAO.vindKlantById(transactie.getVerkoper().getGebruikerId()));
            transactie.getVerkoper().setRekening(rekeningDAO.vindRekeningVanGebruiker(transactie.getVerkoper()));
        }
        else transactie.getVerkoper().setRekening(vindRekeningVanGebruiker(transactie.getVerkoper()));
    }

    private void setKoperTransactie(Transactie transactie) {
        if(transactie.getKoper().getGebruikerId() != 0){
            transactie.setKoper(klantDAO.vindKlantById(transactie.getKoper().getGebruikerId()));
            transactie.getKoper().setRekening(rekeningDAO.vindRekeningVanGebruiker(transactie.getKoper()));
        }
        else transactie.getKoper().setRekening(vindRekeningVanGebruiker(transactie.getKoper()));
    }


    /**
     * methode die alle transacties die bij een klant horen die in een bepaalde
     * periode hebben plaatsgevonden teruggeeft
     * @param gebruiker
     * @param startDatum
     * @param eindDatum de klant waarvan alle transacties moeten
     * worden opgezocht, en data vanaf en tot wanneer de transacties plaatsvonden
     * @return lijst transacties van de klant
     */
    List<Transactie> geefTransactiesVanGebruikerInPeriode(Gebruiker gebruiker, Timestamp startDatum, Timestamp eindDatum){
        List<Transactie> transactiesVanKlant =  transactieDAO.geefTransactiesVanGebruikerInPeriode(gebruiker, startDatum, eindDatum);
        for (Transactie transactie: transactiesVanKlant) {
            maakTransactieCompleet(transactie);
        }
        return transactiesVanKlant;
    }

    /**
     * methode die alle transacties binnen een bepaalde periode teruggeeft
     * @params startDatum en eindDatum periode
     * @return lijst transacties van die periode
     */
    List<Transactie> geefAlleTransactiesInPeriode(Timestamp startDatum, Timestamp eindDatum){
        return transactieDAO.geefAlleTransactiesInPeriode(startDatum, eindDatum);
    }

    /**
     * methode die alle transacties die bij een klant horen met een bepaalde cryptomunt
     * @params gebruiker cryptomunt
     * @return lijst transacties van de klant met de meegegeven cryptomunt
     */
    List<Transactie> geefTransactiesVanGebruikerMetCryptomunt(Gebruiker gebruiker, Cryptomunt cryptomunt){
        List<Transactie> transactieList = transactieDAO.geefTransactiesVanGebruikerMetCryptomunt(gebruiker, cryptomunt);
        for (Transactie transactie : transactieList) {
        maakTransactieCompleet(transactie);
        }
        return transactieList;
    }

    //TODO JavaDoc
    public double geefAssetVanGebruikerOrElseNull(Gebruiker gebruiker, Cryptomunt cryptomunt){
        return assetDAO.geefAantalCryptoInEigendom(gebruiker, cryptomunt);
    }
    //TODO JavaDoc
    public Cryptomunt geefCryptomuntByNaam(String cryptoNaam){
        return cryptomuntDAO.geefCryptomuntByNaam(cryptoNaam);
    }


    //TODO JavaDoc
    public Cryptomunt geefCryptomunt(int cryptomuntId){
        return cryptomuntDAO.geefCryptomunt(cryptomuntId);
    }

    //TODO JavaDoc
    public List<Cryptomunt> geefAlleCryptomunten(){
        return cryptomuntDAO.geefAlleCryptomunten();
    }

    /**
     * author: Steven van Gils
     * Deze methode slaat een trigger op in de database met de huidige datum
     * en voegt de door de database gegenereerde id toe aan de trigger
     * Afhankelijk van het type trigger wordt hij in de triggerKoper- of
     * in de triggerVerkopertabel opgeslagen.
     *
     * @param trigger de betreffende trigger
     * @return de trigger met de gegenereerde id
     */
    public Trigger slaTriggerOp(Trigger trigger){
        return triggerDAO.slaTriggerOp(trigger);
    }
    /**
     * author: Steven van Gils
     * Deze methode verwijdert een trigger op basis van zijn id.
     *
     * @param trigger de te verwijderen trigger
     * @return een 0 indien gefaald of niet gevonden, een 1 indien geslaagd
     */
    public int verwijderTrigger(Trigger trigger){
        return triggerDAO.verwijderTrigger(trigger);
    }

    /**
     * author: Steven van Gils
     * Deze methode zoekt voor een triggerKoper in de triggerVerkoperTabel een match
     * om een transactie mee aan te gaan.
     * Gegeven meerdere matches, eerste het grootste verschil tussen vraag en aanbod,
     * dan de langst staande trigger.
     *
     * @param trigger de betreffende trigger
     * @return de meest geschikte match of null indien geen match
     */
    public Trigger vindMatch(Trigger trigger){
        Trigger triggerMatch = triggerDAO.vindMatch(trigger);
        if(triggerMatch != null){
            maakTriggerCompleet(triggerMatch);
        }
        return triggerMatch;
    }

    private void maakTriggerCompleet(Trigger trigger) {
        trigger.setCryptomunt(geefCryptomunt(trigger.getCryptomunt().getId()));
        trigger.setGebruiker(klantDAO.vindKlantById(trigger.getGebruiker().getGebruikerId()));
    }

    /**
     * author: Steven van Gils
     * Geeft alle triggers van een bepaald type aanwezig in de database
     *
     * @param koperOfVerkoper Geeft aan welke tabel gebruikt moet worden
     * @return een List van Triggers, geheel bestaand uit een enkele subklasse
     */
    public List<Trigger> vindAlleTriggers(String koperOfVerkoper){
        List<Trigger> triggerList = triggerDAO.vindAlleTriggers(koperOfVerkoper);
        if(triggerList != null){
            for(Trigger trigger: triggerList){
                    maakTriggerCompleet(trigger);
            }
        }
        return triggerList;
    }

    /**
     * author: Steven van Gils
     * Geeft alle triggers van een bepaald type aanwezig in de database van een bepaalde gebruiker
     *
     * @param gebruiker De betreffende gebruiker
     * @param koperOfVerkoper Geeft aan welke tabel gebruikt moet worden
     * @return een List van Triggers, geheel bestaand uit een enkele subklasse
     */
    public List<Trigger> vindTriggersByGebruiker(Gebruiker gebruiker, String koperOfVerkoper){

        List<Trigger> triggerList = triggerDAO.vindTriggersByGebruiker(gebruiker, koperOfVerkoper);
        if(triggerList != null){
            for(Trigger trigger: triggerList){
                maakTriggerCompleet(trigger);
            }
        }
        return triggerList;
    }

    /**
     * Author: Carmen
     *
     * Verzamelt alle benodigde informatie voor het transactiescherm en geeft deze terug
     *
     * @param transactieStartDto Gebruikersnaam en cryptoid
     * @return TransactiePaginaDto alle informatie die nodig is voor het transactiescherm
     */
    public TransactiePaginaDto openTransactieScherm(TransactieStartDto transactieStartDto){
        TransactiePaginaDto transactiePaginaDto = new TransactiePaginaDto();
        Klant klant = vindKlantByGebruikersnaam(transactieStartDto.getGebruikersNaam());
        Rekening rekening = klant.getRekening();
        Cryptomunt cryptomunt = cryptomuntDAO.geefCryptomuntByNaam(transactieStartDto.getCryptoNaam());
        CryptoWaarde cryptoWaarde = haalMeestRecenteCryptoWaarde(cryptomunt);
        transactiePaginaDto.setKlantnaam(klant.getNaam());
        transactiePaginaDto.setRekeningsaldo(vraagSaldoOpVanGebruiker(klant));
        transactiePaginaDto.setIban(rekening.getIban());
        transactiePaginaDto.setCryptoNaam(cryptomunt.getName());
        transactiePaginaDto.setCryptoDagkoers(cryptoWaarde.getWaarde());
        transactiePaginaDto.setCryptoAantal(assetDAO.geefAantalCryptoInEigendom(klant,cryptomunt));
        transactiePaginaDto.setBankfee(Bank.getInstance().getFee());
        return transactiePaginaDto;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }
}
