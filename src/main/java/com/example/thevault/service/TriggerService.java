// Created by S.C. van Gils
// Creation date 24-12-2021

package com.example.thevault.service;

import com.example.thevault.database.mapping.repository.RootRepository;
import com.example.thevault.domain.model.*;
import com.example.thevault.support.exceptions.*;
import org.apache.commons.math3.util.Precision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Deze klasse zorgt ervoor dat Triggers goed verwerkt kunnen worden
 * en eventueel tot geslaagde transacties kunnen leiden
 */

@Service
public class TriggerService implements ApplicationListener<ContextRefreshedEvent> {

    private final RootRepository rootRepository;
    private final TransactieService transactieService;
    public final double DEEL_TRANSACTION_FEE_KOPER = 0.5;
    public final String KOPER = "Koper";
    public final String VERKOPER = "Verkoper";

    private final Logger logger = LoggerFactory.getLogger(TriggerService.class);

    /**
     * Constructor voor TriggerService met dependency injection
     *
     * @param rootRepository De class die de DAO's bundelt
     * @param transactieService De class die Transacties afhandelt
     */
    public TriggerService(RootRepository rootRepository, TransactieService transactieService) {
        super();
        this.rootRepository = rootRepository;
        this.transactieService = transactieService;
        logger.info("New TriggerService");
    }

    /**
     * Deze methode kijkt of een Trigger van de subclass TriggerKoper
     * of van de subclass TriggerVerkoper is.
     *
     * @param trigger de betreffende trigger
     * @return true als de subclass TriggerKoper is
     */
    public static boolean isKoper(Trigger trigger){
        return (trigger instanceof TriggerKoper);
    }

    /**
     * Deze methode kijkt of een door een klant gezette trigger mogelijk is.
     * Er wordt gekeken of er een match gevonden kan worden om een transactie mee aan te gaan.
     * Zo niet, dan wordt geprobeerd de trigger op te slaan.
     *
     * @param trigger De betreffende trigger
     * @return Een geslaagde transactie of null indien deze niet heeft plaatsgevonden
     */
    public Transactie probeerTransactieMetTrigger(Trigger trigger, LocalDateTime datumTijd){
        // TODO eerst saldo en/of asset in javascript al nakijken
        checkTriggerExceptions(trigger);
        maakTriggerTabelUpToDate(trigger);
        Transactie transactie = null;
        if(triggerMogelijkQuaFondsen(trigger)) {
            transactie = handelTriggerAf(trigger, datumTijd);
        }
        return transactie;
    }

    private void checkTriggerExceptions(Trigger trigger) {
        checkAantalPositief(trigger);
        checkPrijsPositief(trigger);
    }

    private void maakTriggerTabelUpToDate(Trigger trigger) {
        if(isKoper(trigger)){
            verwijderOnuitvoerbareVerkoopTriggers();
        }
        else verwijderAlleOnuitvoerbareKoopTriggers();
    }

    private Transactie handelTriggerAf(Trigger trigger, LocalDateTime datumTijd) {
        Trigger triggerAnderePartij = vindMatch(trigger);
        if (triggerAnderePartij == null) {
            slaTriggerOp(trigger);
        } else {
            return bepaalTransactiePartijen(datumTijd, trigger, triggerAnderePartij);
        }
        return null;
    }

    private Transactie bepaalTransactiePartijen(LocalDateTime datumTijd, Trigger trigger, Trigger triggerAnderePartij) {
        Transactie transactie;
        if(isKoper(trigger)) {
            transactie = sluitTransactieAfMetKlant(datumTijd, trigger, triggerAnderePartij) ;
        }
        else transactie = sluitTransactieAfMetKlant(datumTijd, triggerAnderePartij, trigger);
        checkVerwijderTrigger(triggerAnderePartij);
        return transactie;
    }

    private void checkVerwijderTrigger(Trigger triggerAnderePartij) {
        int gelukt = verwijderTrigger(triggerAnderePartij);
        if(gelukt == 0){
            logger.warn("***** De trigger van de tegenpartij is niet verwijderd ******");
        }
    }
    /**
     * Maakt een transactie op basis van het bod en de vraagprijs van twee partijen.
     *
     * @param triggerKoper de gewenste transactie van de koper
     * @param triggerVerkoper de gewenste transactie van de verkoper
     * @return De uiteindelijke transactie
     */
    public Transactie sluitTransactieAfMetKlant(LocalDateTime datumTijd, Trigger triggerKoper, Trigger triggerVerkoper){
                return transactieService.sluitTransactie(datumTijd, triggerKoper, triggerVerkoper);
            }
    /**
     * Deze methode kijkt na of iemand de gewenste trigger kan inzetten.
     * Bij een triggerKoper wordt het saldo nagekeken en vergeleken met de mogelijke kosten van de uitstaande triggers
     * en de toe te voegen trigger.
     * Er wordt een exception gegooid indien niet mogelijk.
     * Bij een triggerVerkoper wordt het aantal van de betreffende cryptomunt in zijn portefeuille vergeleken
     * met de uitstaande triggers en de toe te voegen trigger.
     * Er wordt een exception gegooid indien niet mogelijk.
     * Ook wordt gekeken naar het minder waarschijnlijke geval dat het saldo van een verkoper plus de opbrengsten van een
     * verkoop niet genoeg zouden zijn voor de transactie
     *
     * @param trigger de na te kijken trigger
     * @return true of een exception
     */
    public boolean triggerMogelijkQuaFondsen(Trigger trigger){

        if(isKoper(trigger)){
            return schaduwSaldoExceptionHandler(trigger);
        }
        else {
        verkoperSaldoEdgeCaseHandler(trigger);
        }
        return schaduwAssetExceptionHandler(trigger);
    }


    private void verkoperSaldoEdgeCaseHandler(Trigger trigger){
        double edgeCaseSaldo = rootRepository.vraagSaldoOpVanGebruiker(trigger.getGebruiker());
        edgeCaseSaldo += trigger.getAantal() * trigger.getTriggerPrijs();
        edgeCaseSaldo -= (1 - DEEL_TRANSACTION_FEE_KOPER) * Bank.getInstance().getFee();
        if(edgeCaseSaldo < 0){
            throw new BalanceTooLowException();
        }
    }

    private boolean schaduwSaldoExceptionHandler(Trigger trigger) {
        if(checkSchaduwSaldo(trigger)){
        return checkSchaduwSaldo(trigger);
        }
        else throw new SchaduwSaldoException();
    }

    private boolean schaduwAssetExceptionHandler(Trigger trigger) {
        if( checkSchaduwAsset(trigger)) {
            return checkSchaduwAsset(trigger);
        }
        else throw new SchaduwAssetException(String.format("Het aantal %s zou te laag kunnen worden door de uitstaande triggers",
                trigger.getCryptomunt().getName()));
    }

    private boolean checkSchaduwSaldo(Trigger trigger) {
        return Precision.compareTo(schaduwSaldo(trigger.getGebruiker()),
                (trigger.getTriggerPrijs() * trigger.getAantal() + Bank.getInstance().getFee() * DEEL_TRANSACTION_FEE_KOPER), 1) >= 0;
    }

    /**
     * Deze methode berekent het saldo dat een gebruiker zou hebben als al zijn uitstaande kooptriggers worden uitgevoerd
     *
     * @param gebruiker De potentiële koper
     * @return Het saldo dat hij zou hebben als al zijn triggers worden uitgevoerd
     */
    public double schaduwSaldo(Gebruiker gebruiker){
        double schaduwSaldo = rootRepository.vraagSaldoOpVanGebruiker(gebruiker);
        List<Trigger> triggerListKoper = vindTriggersByGebruiker(gebruiker, KOPER);
        for(Trigger trigger: triggerListKoper){
            schaduwSaldo -= trigger.getTriggerPrijs() * trigger.getAantal() + DEEL_TRANSACTION_FEE_KOPER * Bank.getInstance().getFee();
        }
        return schaduwSaldo;
    }

    private boolean checkSchaduwAsset(Trigger trigger) {
        return Precision.compareTo(schaduwAantalAsset(trigger.getGebruiker(), trigger.getCryptomunt()),
                trigger.getAantal(), 1) >= 0;
    }

    /**
     * Deze methode berekent hoeveel een gebruiker nog van een cryptomunt zou hebben als al
     * zijn verkooptriggers worden uitgevoerd.
     *
     * @param gebruiker De potentiële verkoper
     * @param cryptomunt De cryptomunt voor die mogelijke verkopen
     * @return Het aantal van de cryptomunt dat hij zou hebben als al zijn triggers worden uitgevoerd
     */
    public double schaduwAantalAsset(Gebruiker gebruiker, Cryptomunt cryptomunt){
        Asset gebruikerAsset = rootRepository.geefAssetVanGebruiker(gebruiker, cryptomunt);
        checkTriggerAssetInPortefeuille(cryptomunt, gebruikerAsset);
        double schaduwAantal = gebruikerAsset.getAantal();
        List<Trigger> triggerListVerkoper = vindTriggersByGebruikerByCryptomunt(gebruiker, cryptomunt);
        for(Trigger trigger: triggerListVerkoper){
            schaduwAantal -= trigger.getAantal();
        }
        return schaduwAantal;
    }

    /**
     * Deze methode bekijkt of iemand een cryptomunt wel al in zijn bezit heeft voor een verkooptrigger
     * en gooit een exception als dit niet zo is.
     *
     * @param cryptomunt De betreffende cryptomunt
     * @param asset De asset die niet null mag zijn
     */
    private void checkTriggerAssetInPortefeuille(Cryptomunt cryptomunt, Asset asset) {
        if(asset == null){
            throw new TriggerAssetNietInPortefeuilleException(
                    String.format("Je hebt geen %s om een verkoopreservering mee te maken", cryptomunt.getName()));
        }
    }

    /**
     * Deze methode slaat een trigger op in de database met de huidige datum
     * en voegt de door de database gegenereerde id toe aan de trigger
     * Afhankelijk van het type trigger wordt hij in de triggerKoper- of
     * in de triggerVerkopertabel opgeslagen.
     *
     * @param trigger de betreffende trigger
     * @return de trigger met de gegenereerde id
     */
    public Trigger slaTriggerOp(Trigger trigger){

       return rootRepository.slaTriggerOp(trigger);
    }
    /** Deze methode zoekt voor een triggerKoper in de triggerVerkoperTabel een match
     * om een transactie mee aan te gaan.
     * Gegeven meerdere matches, eerste het grootste verschil tussen vraag en aanbod,
     * dan de langst staande trigger.
     *
     * @param trigger De betreffende trigger
     * @return De meest geschikte match of null indien geen match
     */
    public Trigger vindMatch(Trigger trigger){
        return rootRepository.vindMatch(trigger);
    }

    /**
     * Deze methode verwijdert een trigger op basis van zijn id.
     *
     * @param trigger De te verwijderen trigger
     * @return Een 0 indien gefaald of niet gevonden, een 1 indien geslaagd
     */
    public int verwijderTrigger(Trigger trigger){
        return rootRepository.verwijderTrigger(trigger);
    }

    /**
     * Geeft alle triggers van een bepaald type aanwezig in de database
     *
     * @param koperOfVerkoper Geeft aan welke tabel gebruikt moet worden
     * @return een List van Triggers, geheel bestaand uit een enkele subklasse
     */
    public List<Trigger> vindAlleTriggers(String koperOfVerkoper){
        return rootRepository.vindAlleTriggers(koperOfVerkoper);
    }

    /**
     * Geeft alle triggers van een bepaald type aanwezig in de database van een bepaalde gebruiker
     *
     * @param gebruiker De betreffende gebruiker
     * @param koperOfVerkoper Geeft aan welke tabel gebruikt moet worden
     * @return een List van Triggers, geheel bestaand uit een enkele subklasse
     */
    public List<Trigger> vindTriggersByGebruiker(Gebruiker gebruiker, String koperOfVerkoper){
        return rootRepository.vindTriggersByGebruiker(gebruiker, koperOfVerkoper);
    }

    private List<Trigger> vindTriggersByGebruikerByCryptomunt(Gebruiker gebruiker, Cryptomunt cryptomunt){
        List<Trigger> triggerList = vindTriggersByGebruiker(gebruiker, VERKOPER);
        List<Trigger> triggerListVoorCryptomunt = new ArrayList<>();
                for(Trigger trigger: triggerList ){
                    if(trigger.getCryptomunt().equals(cryptomunt)){
                        triggerListVoorCryptomunt.add(trigger);
                    }
                }
                return triggerListVoorCryptomunt;
    }

    /**
     * Deze methode bekijkt of het gewenste aantal van een cryptomunt wel positief is.
     * Zo niet, wordt er een exception gegooid.
     *
     * @param trigger de betreffende trigger
     */
    public static void checkAantalPositief(Trigger trigger){
        if(trigger.getAantal() <= 0){
            throw new TriggerAantalNietPositiefException();
        }
    }
    /**
     * Deze methode bekijkt of de gewenste prijs van een cryptomunt wel positief is.
     * Zo niet, wordt er een exception gegooid.
     *
     * @param trigger de betreffende trigger
     */
    public static void checkPrijsPositief(Trigger trigger){
        if(trigger.getTriggerPrijs() <= 0){
           throw new TriggerPrijsNietPositiefException();
        }
    }

    /**
     * Deze methode zorgt ervoor dat er geen onuitvoerbare triggers meer in de tabel triggerKoper staan
     */
    private void verwijderAlleOnuitvoerbareKoopTriggers(){
        List<Trigger> triggerKoperList = vindAlleTriggers(KOPER);
        for(Trigger trigger: triggerKoperList){
            if(schaduwSaldo(trigger.getGebruiker()) < 0){
                verwijderTrigger(trigger);
            }
        }
    }

    /**
     * Deze methode zorgt ervoor dat er geen onuitvoerbare triggers meer in de tabel triggerVerkoper staan
     */
    private void verwijderOnuitvoerbareVerkoopTriggers(){
        List<Trigger> triggerVerkoperList = vindAlleTriggers(VERKOPER);
            for (Trigger trigger : triggerVerkoperList) {
                onhaalbareTriggerVerkoperHandler(trigger);
            }

    }

    private void onhaalbareTriggerVerkoperHandler(Trigger trigger) {
        try {
            if (schaduwAantalAsset(trigger.getGebruiker(), trigger.getCryptomunt()) < 0) {
                verwijderTrigger(trigger);
            }
        }
        catch (AssetNotExistsException assetNotExistsException){
            logger.warn("**** Een altijd onhaalbare triggerVerkoper is in de database beland *****");
            verwijderTrigger(trigger);
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {

    }

}
