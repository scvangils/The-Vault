// Created by carme
// Creation date 17/01/2022

package com.example.thevault.handelingen.controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Author: Carmen
 * Beschrijving: er wordt een object aangemaakt met gegevens benodigd voor het vullen van de transactiepagina
 */

public class TransactiePaginaDto {

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(TransactiePaginaDto.class);

    private String klantnaam;
    private double rekeningsaldo;
    private String iban;
    private String cryptoNaam;
    private double cryptoDagkoers;
    private double cryptoAantal;
    private double bankfee;

    /**
     * No-args constructor voor transactiePaginaDto
     */
    public TransactiePaginaDto() {
        super();
        logger.info("New no-args TransactiePaginaDto");
    }

    public void setKlantnaam(String klantnaam) {
        this.klantnaam = klantnaam;
    }

    public void setRekeningsaldo(double rekeningsaldo) {
        this.rekeningsaldo = rekeningsaldo;
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public void setCryptoNaam(String cryptoNaam) {
        this.cryptoNaam = cryptoNaam;
    }

    public void setCryptoDagkoers(double cryptoDagkoers) {
        this.cryptoDagkoers = cryptoDagkoers;
    }

    public void setCryptoAantal(double cryptoAantal) {
        this.cryptoAantal = cryptoAantal;
    }

    public void setBankfee(double bankfee) {
        this.bankfee = bankfee;
    }

    public String getKlantnaam() {
        return klantnaam;
    }

    public double getRekeningsaldo() {
        return rekeningsaldo;
    }

    public String getCryptoNaam() {
        return cryptoNaam;
    }

    public double getCryptoDagkoers() {
        return cryptoDagkoers;
    }

    public double getCryptoAantal() {
        return cryptoAantal;
    }

    public double getBankfee() {
        return bankfee;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TransactiePaginaDto that = (TransactiePaginaDto) o;
        return Double.compare(that.rekeningsaldo, rekeningsaldo) == 0 && Double.compare(that.cryptoDagkoers, cryptoDagkoers) == 0 && Double.compare(that.cryptoAantal, cryptoAantal) == 0 && Double.compare(that.bankfee, bankfee) == 0 && klantnaam.equals(that.klantnaam) && iban.equals(that.iban) && cryptoNaam.equals(that.cryptoNaam);
    }

    @Override
    public int hashCode() {
        return Objects.hash(klantnaam, rekeningsaldo, iban, cryptoNaam, cryptoDagkoers, cryptoAantal, bankfee);
    }

    @Override
    public String toString() {
        return "TransactiePaginaDto{" +
                "klantnaam='" + klantnaam + '\'' +
                ", rekeningsaldo=" + rekeningsaldo +
                ", iban='" + iban + '\'' +
                ", cryptoNaam='" + cryptoNaam + '\'' +
                ", cryptoDagkoers=" + cryptoDagkoers +
                ", cryptoAantal=" + cryptoAantal +
                ", bankfee=" + bankfee +
                '}';
    }
}
