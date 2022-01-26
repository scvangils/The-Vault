// Created by carme
// Creation date 17/01/2022

package com.example.thevault.handelingen.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Author: Carmen
 * Beschrijving: er wordt een object aangemaakt met gegevens benodigd voor het opvragen van de gegevens nodig voor
 * het vullen van de transactiepagina
 */

public class TransactieStartDto {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(TransactieStartDto.class);

    private String gebruikersNaam;
    private String cryptoNaam;

    /**
     * All-args constructor voor transactieStartDto
     * @param gebruikersNaam
     * @param cryptoNaam
     */
    public TransactieStartDto(String gebruikersNaam, String cryptoNaam) {
        super();
        this.gebruikersNaam = gebruikersNaam;
        this.cryptoNaam = cryptoNaam;
        logger.info("New all-args TransactieStartDto");
    }

    public String getGebruikersNaam() {
        return gebruikersNaam;
    }



    public void setGebruikersNaam(String gebruikersNaam) {
        this.gebruikersNaam = gebruikersNaam;
    }


    public String getCryptoNaam() {
        return cryptoNaam;
    }

    public void setCryptoNaam(String cryptoNaam) {
        this.cryptoNaam = cryptoNaam;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactieStartDto that = (TransactieStartDto) o;
        return cryptoNaam.equals(that.cryptoNaam) && gebruikersNaam.equals(that.gebruikersNaam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(gebruikersNaam, cryptoNaam);
    }
}
