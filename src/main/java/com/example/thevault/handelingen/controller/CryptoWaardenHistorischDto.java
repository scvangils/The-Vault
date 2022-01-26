// Created by S.C. van Gils
// Creation date 26-12-2021

package com.example.thevault.handelingen.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoWaardenHistorischDto {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(CryptoWaardenHistorischDto.class);

    private String[] datum;
    private double[] waarde;

    //TODO JavaDoc
    //TODO Verwijderen?
    public CryptoWaardenHistorischDto() {
        super();
        logger.info("New CryptoWaardenHistorischDto");
    }

    //TODO JavaDoc
    public CryptoWaardenHistorischDto(String[] datum, double[] waarde){
        this.datum = datum;
        this.waarde = waarde;
    }

    public String[] getDatum() {
        return datum;
    }

    public void setDatum(String[] datum) {
        this.datum = datum;
    }

    public double[] getWaarde() {
        return waarde;
    }

    public void setWaarde(double[] waarde) {
        this.waarde = waarde;
    }
}
