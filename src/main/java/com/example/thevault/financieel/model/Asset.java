// Created by carme
// Creation date 02/12/2021

package com.example.thevault.financieel.model;

import com.example.thevault.klant.model.Gebruiker;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Author: Carmen
 * Beschrijving: Het model voor Asset, bestaande uit cryptomunt en aantal, wat de bouwstenen zijn voor de portefeuille
 * van cryptomunten van de klant
 */

public class Asset {

    private Cryptomunt cryptomunt;
    private double aantal;
    @JsonBackReference
    private Gebruiker gebruiker;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(Asset.class);

    /**
     * De no-args constructor voor Asset
     */
    public Asset() {
        super();
        logger.info("New Asset, no args constructor");
    }

    /**
     * De constructor voor Asset om gegevens uit de database te halen
     * @param aantal het aantal van de cryptomunt die in deze Asset aanwezig is
     */
    public Asset(Cryptomunt cryptomunt, double aantal) {
        this(cryptomunt, aantal, null);
        logger.info("Asset " + this);
    }

    /**
     * De all-args constructor voor Asset
     * @param cryptomunt de cryptomunt waarvoor de Asset is aangemaakt
     * @param aantal het aantal van de cryptomunt die in deze Asset aanwezig is
     * @param gebruiker de klant die de Asset bezit
     */
    public Asset(Cryptomunt cryptomunt, double aantal, Gebruiker gebruiker){
        super();
        this.cryptomunt = cryptomunt;
        this.aantal = aantal;
        this.gebruiker = gebruiker;
        logger.info("Asset: " + this);
    }

    /**
     * Equals methode voor Asset
     * @param o het object waarmee de Asset wordt vergeleken
     * @return boolean: is het object gelijk aan de Asset of niet
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Asset asset = (Asset) o;
        return Double.compare(asset.aantal, aantal) == 0 && cryptomunt.equals(asset.cryptomunt) &&
                gebruiker.equals(asset.gebruiker);
    }

    /**
     * Hashcode methode voor Asset, waarbij een hashcode wordt gegenereerd op basis van cryptomunt en aantal
     * @return int de hashcode voor deze Asset
     */
    @Override
    public int hashCode() {
        return Objects.hash(cryptomunt, aantal, gebruiker);
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

    public Gebruiker getGebruiker() {
        return gebruiker;
    }

    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker;
    }

    @Override
    public String toString() {
        return "Asset{" +
                "cryptomunt=" + cryptomunt +
                ", aantal=" + aantal +
                                '}';
    }
}
