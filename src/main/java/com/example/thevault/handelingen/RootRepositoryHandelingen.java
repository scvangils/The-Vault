// Created by carme
// Creation date 01/12/2021

package com.example.thevault.handelingen;

import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.financieel.CryptomuntDAO;
import com.example.thevault.klant.Gebruiker;
import com.example.thevault.klant.Klant;
import com.example.thevault.klant.KlantDAO;
import com.example.thevault.financieel.AssetDAO;
import com.example.thevault.financieel.Rekening;
import com.example.thevault.financieel.RekeningDAO;
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
public class RootRepositoryHandelingen implements ApplicationListener<ContextRefreshedEvent> {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(RootRepositoryHandelingen.class);
    public final RootRepositoryFinancieel rootRepositoryFinancieel;

    private final KlantDAO klantDAO;
    private final RekeningDAO rekeningDAO;
    private final AssetDAO assetDAO;
    private final CryptomuntDAO cryptomuntDAO;
    private final CryptoWaardeDAO cryptoWaardeDAO;
    private final TransactieDAO transactieDAO;
    private final TriggerDAO triggerDAO;
    private final String KOPER = "Koper";
    private final String VERKOPER = "Verkoper";

    /**
     * Constructor voor RootRepositoryHandelingen
     * In de constructor worden alle DAOs geinjecteerd die de RootRepositoryHandelingen nodig heeft
     * @param klantDAO
     * @param rekeningDAO
     * @param assetDAO
     * @param cryptomuntDAO
     * @param cryptoWaardeDAO
     * @param transactieDAO
     * @param triggerDAO
     */
    @Autowired
    public RootRepositoryHandelingen(KlantDAO klantDAO, RekeningDAO rekeningDAO, AssetDAO assetDAO, CryptomuntDAO cryptomuntDAO,
                                     CryptoWaardeDAO cryptoWaardeDAO,
                                     TransactieDAO transactieDAO, TriggerDAO triggerDAO, RootRepositoryFinancieel rootRepositoryFinancieel) {
        super();
        this.rekeningDAO = rekeningDAO;
        this.klantDAO = klantDAO;
        this.assetDAO = assetDAO;
        this.cryptomuntDAO = cryptomuntDAO;
        this.cryptoWaardeDAO = cryptoWaardeDAO;
        this.transactieDAO = transactieDAO;
        this.triggerDAO = triggerDAO;
        this.rootRepositoryFinancieel = rootRepositoryFinancieel;
        logger.info("New RootRepositoryHandelingen");
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
        else transactie.getVerkoper().setRekening(rekeningDAO.vindRekeningVanGebruiker(transactie.getVerkoper()));
    }

    private void setKoperTransactie(Transactie transactie) {
        if(transactie.getKoper().getGebruikerId() != 0){
            transactie.setKoper(klantDAO.vindKlantById(transactie.getKoper().getGebruikerId()));
            transactie.getKoper().setRekening(rekeningDAO.vindRekeningVanGebruiker(transactie.getKoper()));
        }
        else transactie.getKoper().setRekening(rekeningDAO.vindRekeningVanGebruiker(transactie.getKoper()));
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
        trigger.setCryptomunt(rootRepositoryFinancieel.geefCryptomunt(trigger.getCryptomunt().getId()));
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
        Klant klant = klantDAO.vindKlantByGebruikersnaam(transactieStartDto.getGebruikersNaam());
        Rekening rekening = klant.getRekening();
        Cryptomunt cryptomunt = cryptomuntDAO.geefCryptomuntByNaam(transactieStartDto.getCryptoNaam());
        CryptoWaarde cryptoWaarde = haalMeestRecenteCryptoWaarde(cryptomunt);
        transactiePaginaDto.setKlantnaam(klant.getNaam());
        transactiePaginaDto.setRekeningsaldo(rootRepositoryFinancieel.vraagSaldoOpVanGebruiker(klant));
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
