// Created by S.C. van Gils
// Creation date 3-12-2021

/**
 * Deze klasse zorgt ervoor dat alleen de relevante informatie terug bij de klant komt
 * en dat er niet onnodige en eventueel gevoelige informatie wordt verstuurd naar de frontend.
 */

package com.example.thevault.financieel;

import com.example.thevault.klant.Klant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class RegistrationDto {
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(RegistrationDto.class);

    private String naam;
    private String gebruikersnaam;
    private String iban;

    private final String WELKOM = "Welkom, %s, bij The Vault";
    private final String LET_OP = "Onthoud je gebruikersnaam, %s, goed!";
    private final String IBAN = "Dit is je IBAN: %s.";

    //TODO JavaDoc
    //TODO Verwijderen?
    public RegistrationDto() {
        super();
        logger.info("New RegistrationDto no args");
    }

    //TODO JavaDoc
    public RegistrationDto(Klant klant){
        this.naam = String.format(WELKOM, klant.getNaam());
        this.gebruikersnaam = String.format(LET_OP, klant.getGebruikersnaam());
        this.iban = String.format(IBAN, klant.getRekening().getIban());
    }

    public String getNaam() {
        return naam;
    }

    public void setNaam(String naam) {
        this.naam = naam;
    }

    public String getGebruikersnaam() {
        return gebruikersnaam;
    }

    public void setGebruikersnaam(String gebruikersnaam) {
        this.gebruikersnaam = gebruikersnaam;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    @Override
    public String toString() {
        return "RegistrationDto{" +
                "naam='" + naam + '\'' +
                ", gebruikersnaam='" + gebruikersnaam + '\'' +
                ", iban='" + iban + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RegistrationDto)) return false;
        RegistrationDto registrationDto = (RegistrationDto) o;
        return naam.equals(registrationDto.naam) && gebruikersnaam.equals(registrationDto.gebruikersnaam) && iban.equals(registrationDto.iban) ;
    }

    @Override
    public int hashCode() {
        return Objects.hash(naam, gebruikersnaam, iban);
    }
}
