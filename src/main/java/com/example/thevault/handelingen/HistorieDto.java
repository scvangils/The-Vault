// Created by S.C. van Gils
// Creation date 5-1-2022

/**
 * Deze klasse zorgt ervoor dat de voor een klant noodzakelijke informatie van zijn eerdere transacties naar de front-end kan
 */

package com.example.thevault.handelingen;

import com.example.thevault.controller.rest_api_controller.Gebruiker;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class HistorieDto {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(HistorieDto.class);

    private List<Transactie> transactieLijstGekocht;
    private List<Transactie> transactieLijstVerkocht;
    private List<Trigger> koopTriggers;
    private List<Trigger> verkoopTriggers;

    //TODO JavaDoc
    public HistorieDto() {
        super();
        logger.info("New HistorieDto");
    }

    //TODO JavaDoc
    public HistorieDto(Gebruiker klant){
        this.transactieLijstGekocht = maakTransactieLijstGekocht(klant);
        this.transactieLijstVerkocht = maakTransactieLijstVerkocht(klant);
        this.koopTriggers = klant.getTriggerKoperList();
        this.verkoopTriggers = klant.getTriggerVerkoperList();
    }



    private List<Transactie> maakTransactieLijstGekocht(Gebruiker klant) {
        List<Transactie> transactieLijstGekocht = new ArrayList<>();
        for(Transactie transactie: klant.getTransacties()){
            if(transactie.getKoper().getGebruikerId() == klant.getGebruikerId()){
                transactieLijstGekocht.add(transactie);
            }
        }
        return transactieLijstGekocht;
    }

    public List<Transactie> getTransactieLijstGekocht() {
        return transactieLijstGekocht;
    }

    public void setTransactieLijstGekocht(List<Transactie> transactieLijstGekocht) {
        this.transactieLijstGekocht = transactieLijstGekocht;
    }

    private List<Transactie> maakTransactieLijstVerkocht(Gebruiker klant) {
        List<Transactie> transactieLijstVerkocht = new ArrayList<>();
        for(Transactie transactie: klant.getTransacties()){
            if(transactie.getVerkoper().getGebruikerId() == klant.getGebruikerId()){
                transactieLijstVerkocht.add(transactie);
            }
        }
        return transactieLijstVerkocht;
    }

    public List<Transactie> getTransactieLijstVerkocht() {
        return transactieLijstVerkocht;
    }

    public void setTransactieLijstVerkocht(List<Transactie> transactieLijstVerkocht) {
        this.transactieLijstVerkocht = transactieLijstVerkocht;
    }

    public List<Trigger> getKoopTriggers() {
        return koopTriggers;
    }

    public void setKoopTriggers(List<Trigger> koopTriggers) {
        this.koopTriggers = koopTriggers;
    }

    public List<Trigger> getVerkoopTriggers() {
        return verkoopTriggers;
    }

    public void setVerkoopTriggers(List<Trigger> verkoopTriggers) {
        this.verkoopTriggers = verkoopTriggers;
    }
}
