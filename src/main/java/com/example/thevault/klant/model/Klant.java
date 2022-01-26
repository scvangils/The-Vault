// Created by carme
// Creation date 30/11/2021

package com.example.thevault.klant.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.Objects;

/**
 * deze class voegt klant-specifieke fields toe aan het Gebruiker-Model
 */

public class Klant extends Gebruiker {
    private String naam;
    private Adres adres;
    private long bsn;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    @JsonFormat(pattern="yyyy-MM-dd")
    private LocalDate geboortedatum;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(Klant.class);

    /**
     * No-args constructor voor Klant
     */
    public Klant(){
        super();
        logger.info("Lege klant, no args constructor");
    }

    /**
     * Constructor voor Klant-object voor fields die geen complex object zijn
     *
     * @param gebruikersnaam de gekozen gebruikersnaam
     * @param wachtwoord het gekozen wachtwoord
     * @param naam de naam van de klant
     * @param bsn Het BurgerServiceNummer van de Klant
     * @param geboortedatum de geboortedatum van de klant, die minimaal 18 jaar moet zijn
     */
    public Klant(String gebruikersnaam, String wachtwoord,
                 String naam, long bsn, LocalDate geboortedatum) {
        super(gebruikersnaam, wachtwoord);
        this.naam = naam;
        this.bsn = bsn;
        this.geboortedatum = geboortedatum;
        logger.info("New Klant, meest gebruikte constructor");
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public Adres getAdres() {
        return adres;
    }

    public void setAdres(Adres adres) {
        this.adres = adres;
    }

    public long getBsn() {
        return bsn;
    }

    public void setBsn(long bsn) {
        this.bsn = bsn;
    }

    public LocalDate getGeboortedatum() {
        return geboortedatum;
    }

    public void setGeboortedatum(LocalDate geboortedatum) {
        this.geboortedatum = geboortedatum;
    }

    @Override
    public String toString() {
        return  "Klant{" + super.toString() +
                ", naam='" + naam + '\'' +
                ", adres=" + adres +
                ", BSN=" + bsn +
                ", geboortedatum=" + geboortedatum +
                ", rekening=" + rekening +
                ", portefeuille=" + portefeuille +
                ", transacties=" + transacties +
                ", koopTriggers=" + triggerKoperList +
                ", verkoopTriggers=" + triggerVerkoperList +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Klant)) return false;
        if (!super.equals(o)) return false;
        Klant klant = (Klant) o;
        return bsn == klant.bsn && naam.equals(klant.naam) && geboortedatum.equals(klant.geboortedatum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), naam, adres, bsn, rekening, geboortedatum);
    }
}
