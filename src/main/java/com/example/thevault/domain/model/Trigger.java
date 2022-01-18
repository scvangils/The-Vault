// Created by S.C. van Gils
// Creation date 20-12-2021

package com.example.thevault.domain.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Een trigger is een waarde die een actie inzet:
 * In dit geval een maximale koopprijs of een minimale verkoopprijs van een specifieke cryptomunt
 * met een bepaalde gevraagde of aangeboden hoeveelheid
 */

public abstract class Trigger {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(Trigger.class);

    protected int triggerId;
    private int DEFAULT_TRIGGER_ID = 0;
    @JsonBackReference
    protected Gebruiker gebruiker;
    protected  Cryptomunt cryptomunt;
    protected  double triggerPrijs;
    protected  double aantal;
    protected LocalDate datum;



    public Trigger() {
        super();
        logger.info("New Trigger");
    }
    public Trigger(Gebruiker gebruiker, Cryptomunt cryptomunt, double triggerPrijs, double aantal){
        super();
        this.triggerId = DEFAULT_TRIGGER_ID;
        this.gebruiker = gebruiker;
        this.cryptomunt = cryptomunt;
        this.triggerPrijs = triggerPrijs;
        this.aantal = aantal;

        logger.info("New Trigger, all-args constructor");
    }
    public Trigger(int triggerId, double triggerPrijs, double aantal, LocalDate datum) {
        this(null, null, triggerPrijs,aantal);
        this.triggerId = triggerId;
        this.datum = datum;
        logger.info("New Trigger, RowMapper constructor");
    }

    public int getTriggerId() {
        return triggerId;
    }

    public void setTriggerId(int triggerId) {
        this.triggerId = triggerId;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public void setDatum(LocalDate datum) {
        this.datum = datum;
    }

    public Gebruiker getGebruiker() {
        return gebruiker;
    }

    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker;
    }

    public double getTriggerPrijs() {
        return triggerPrijs;
    }

    public void setTriggerPrijs(double triggerPrijs) {
        this.triggerPrijs = triggerPrijs;
    }

    public Cryptomunt getCryptomunt() {
        return cryptomunt;
    }

    public void setCryptomunt(Cryptomunt cryptomunt) {
        this.cryptomunt = cryptomunt;
    }

    public double getAantal() {
        return aantal;
    }

    public void setAantal(double aantal) {
        this.aantal = aantal;
    }


    @Override
    public String toString() {
        return "Trigger{" +
                "triggerId=" + triggerId +
                ", gebruiker=" + gebruiker +
                ", cryptomunt=" + cryptomunt +
                ", triggerPrijs=" + triggerPrijs +
                ", aantal=" + aantal +
                ", datum=" + datum +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Trigger)) return false;
        Trigger trigger = (Trigger) o;
        return triggerId == trigger.triggerId && Objects.equals(gebruiker, trigger.gebruiker)
                && Objects.equals(cryptomunt, trigger.cryptomunt) && Objects.equals(datum, trigger.datum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(triggerId, gebruiker, cryptomunt, datum);
    }
}
