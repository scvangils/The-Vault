package com.example.thevault.klant.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.LoggerFactory;

import org.slf4j.Logger;

import java.util.Objects;

public class LoginDto {

    private String gebruikersnaam;
    private String wachtwoord;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(LoginDto.class);

    //TODO JavaDoc
    //TODO Verwijderen?
    public LoginDto() {
        super();
        gebruikersnaam = "testKlant";
        wachtwoord = "testWW";
        logger.info("New LoginDto noArgs");
    }

    //TODO JavaDoc
    public LoginDto(String gebruikersnaam, String wachtwoord) {
        this.gebruikersnaam = gebruikersnaam;
        this.wachtwoord = wachtwoord;
        logger.info("new loginDto {} " + gebruikersnaam, wachtwoord);
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

    @Override
    public String toString() {
        return "LoginDto{" +
                "gebruikersnaam='" + gebruikersnaam + '\'' +
                ", wachtwoord='" + wachtwoord + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoginDto loginDto = (LoginDto) o;
        return gebruikersnaam.equals(loginDto.gebruikersnaam) && wachtwoord.equals(loginDto.wachtwoord);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gebruikersnaam, wachtwoord);
    }
}
