// Created by carme
// Creation date 30/11/2021

package com.example.thevault.klant;

import com.example.thevault.financieel.Asset;
import com.example.thevault.financieel.Rekening;
import com.example.thevault.handelingen.Transactie;
import com.example.thevault.handelingen.Trigger;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

/**
 * Deze class definieert wat een gebruiker aan informatie moet opgeven,
 * wat hij aan bezittingen heeft en wat zijn handelingen binnen de bank zijn geweest
 */

public abstract class Gebruiker {
    protected int gebruikerId;
    protected String gebruikersnaam;
    @JsonBackReference
    protected String wachtwoord;
    protected Rekening rekening;
    @JsonManagedReference
    protected List<Asset> portefeuille;
    protected List<Transactie> transacties;
    protected static int DEFAULT_GEBRUIKERID = 0;
    protected List<Trigger> triggerKoperList;
    protected List<Trigger> triggerVerkoperList;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(Gebruiker.class);

    /**
     * No-args constructor voor Gebruiker class
     */
    public Gebruiker() {
        super();
        logger.info("Lege Gebruiker, no args constructor");
    }

    /**
     * Constructor voor Gebruiker class voor die fields set
     * die geen complexe objecten zijn
     *
     * @param gebruikersnaam de gekozen gebruikersnaam van de gebruiker
     * @param wachtwoord het gekozen wachtwoord van de gebruiker
     */
    public Gebruiker(String gebruikersnaam, String wachtwoord){
        this.gebruikerId = DEFAULT_GEBRUIKERID;
        this.gebruikersnaam = gebruikersnaam;
        this.wachtwoord = wachtwoord;
        logger.info("Gebruiker " + this + "aangemaakt");
    }

    @Override
    public String toString() {
        return "Gebruiker{" +
                "gebruikerId=" + gebruikerId +
                ", gebruikersnaam='" + gebruikersnaam + '\'' +
                '}';
    }

    public int getGebruikerId() {
        return gebruikerId;
    }

    public void setGebruikerId(int gebruikerId) {
        this.gebruikerId = gebruikerId;
    }

    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public void setGebruikersnaam(String gebruikersnaam) {
        this.gebruikersnaam = gebruikersnaam;
    }

    public String getWachtwoord() {
        return wachtwoord;
    }

    public void setWachtwoord(String wachtwoord) {
        this.wachtwoord = wachtwoord;
    }

    public Rekening getRekening() {
        return rekening;
    }

    public void setRekening(Rekening rekening) {
        this.rekening = rekening;
    }

    public List<Asset> getPortefeuille() {
        return portefeuille;
    }

    public void setPortefeuille(List<Asset> portefeuille) {
        this.portefeuille = portefeuille;
    }

    public List<Transactie> getTransacties() {
        return transacties;
    }

    public void setTransacties(List<Transactie> transacties) {
        this.transacties = transacties;
    }

    public List<Trigger> getTriggerKoperList() {
        return triggerKoperList;
    }

    public void setTriggerKoperList(List<Trigger> triggerKoperList) {
        this.triggerKoperList = triggerKoperList;
    }

    public List<Trigger> getTriggerVerkoperList() {
        return triggerVerkoperList;
    }

    public void setTriggerVerkoperList(List<Trigger> triggerVerkoperList) {
        this.triggerVerkoperList = triggerVerkoperList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Gebruiker)) return false;
        Gebruiker gebruiker = (Gebruiker) o;
        return gebruikersnaam.equals(gebruiker.gebruikersnaam) && wachtwoord.equals(gebruiker.wachtwoord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gebruikerId, gebruikersnaam, wachtwoord);
    }
}
