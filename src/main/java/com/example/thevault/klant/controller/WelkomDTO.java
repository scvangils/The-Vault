// Created by E.S. Olthof
// 20211012

package com.example.thevault.klant.controller;

import com.example.thevault.financieel.controller.CryptoDto;
import com.example.thevault.klant.model.Klant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class WelkomDTO {
    private double saldo;
    private List<CryptoDto> portefeuille;
    private String iban;


    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(LoginDto.class);

    //TODO JavaDoc
    //TODO Verwijderen?
    public WelkomDTO() {
        super();
        logger.info("New WelkomDTO, no args constructor");
    }

    //TODO JavaDoc
    //TODO StackOverflowError wegwerken
    public WelkomDTO(Klant klant, List<CryptoDto> portefeuille){
        super();
        this.saldo = klant.getRekening().getSaldo();
        this.portefeuille = portefeuille;
        this.iban = klant.getRekening().getIban();
        logger.info("New WelkomDTO: " + this);
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public List<CryptoDto> getPortefeuille() {
        return portefeuille;
    }

    public void setPortefeuille(List<CryptoDto> portefeuille) {
        this.portefeuille = portefeuille;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
}
