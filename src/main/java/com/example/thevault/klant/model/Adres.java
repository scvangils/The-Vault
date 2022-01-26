// Created by carme
// Creation date 30/11/2021

package com.example.thevault.klant.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Deze class maakt van een adres een object
 */

public class Adres {
    private int adresId;
    private static int DEFAULT_ADRES_ID = 0;
    private String straatnaam;
    private String toevoeging;
    private int huisnummer;
    private String postcode;
    private String plaatsnaam;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(Adres.class);

    /**
     * No-args constructor voor Adres-class
     */
    public Adres(){
        super();
        logger.info("Leeg adres, no args constructor");
    }

    /**
     * All-args constructor voor Adres-class
     *
     * @param straatnaam de straatnaam
     * @param huisnummer het huisnummer
     * @param toevoeging de mogelijke toevoeging van het huisnummer
     * @param postcode de postcode
     * @param plaatsnaam de plaatsnaam
     */
    public Adres(String straatnaam, int huisnummer, String toevoeging, String postcode, String plaatsnaam) {
        super();
        this.adresId = DEFAULT_ADRES_ID;
        logger.info("New Adres");
        this.straatnaam = straatnaam;
        this.huisnummer = huisnummer;
        this.toevoeging = toevoeging;
        this.postcode = postcode;
        this.plaatsnaam = plaatsnaam;
    }

    @Override
    public String toString() {
        return "Adres{" +
                "adresId='" + adresId + '\'' +
                "straatnaam='" + straatnaam + '\'' +
                ", toevoeging='" + toevoeging + '\'' +
                ", huisnummer=" + huisnummer +
                ", postcode='" + postcode + '\'' +
                ", plaatsnaam='" + plaatsnaam + '\'' +
                '}';
    }

    public int getAdresId() {
        return adresId;
    }

    public void setAdresId(int adresId) {
        this.adresId = adresId;
    }

    public String getStraatnaam() {
        return straatnaam;
    }

    public void setStraatnaam(String straatnaam) {
        this.straatnaam = straatnaam;
    }

    public String getToevoeging() {
        return toevoeging;
    }

    public void setToevoeging(String toevoeging) {
        this.toevoeging = toevoeging;
    }

    public int getHuisnummer() {
        return huisnummer;
    }

    public void setHuisnummer(int huisnummer) {
        this.huisnummer = huisnummer;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public String getPlaatsnaam() {
        return plaatsnaam;
    }

    public void setPlaatsnaam(String plaatsnaam) {
        this.plaatsnaam = plaatsnaam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Adres adres = (Adres) o;
        return huisnummer == adres.huisnummer && Objects.equals(toevoeging, adres.toevoeging) && postcode.equals(adres.postcode);
    }

    @Override
    public int hashCode() {
        return Objects.hash(straatnaam, toevoeging, huisnummer, postcode, plaatsnaam);
    }
}
