// Created by carme
// Creation date 30/11/2021

package com.example.thevault.financieel;

import com.example.thevault.controller.rest_api_controller.Gebruiker;
import net.minidev.json.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Author: Ju-Sen Cheung
 * Beschrijving: Het model voor Rekening welke is opgebouwd uit een iban, saldo en een bijgbehorende gebruiker.
 */

public class Rekening {

    private String iban;
    private double saldo;
    private Gebruiker gebruiker;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(Rekening.class);

    /**
    * De no-args constructor voor Rekening
    */
    public Rekening() {
        super();
        logger.info("Lege rekening, no args constructor");
    }

    /**
     * De all-args constructor voor Rekening
     * @param iban uniek iban-nummer die elke rekening heeft
     * @param saldo het bedrag wat op de rekening staat om met cryptomunten te kunnen handelen
     */
    public Rekening(String iban, double saldo) {
        this.gebruiker = null;
        this.iban = iban;
        this.saldo = saldo;
        logger.info("Nieuwe rekening " + this + " gemaakt");
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    public double getSaldo() {
        return saldo;
    }

    public void setSaldo(double saldo) {
        this.saldo = saldo;
    }

    public Gebruiker getGebruiker() {
        return gebruiker;
    }

    public void setGebruiker(Gebruiker gebruiker) {
        this.gebruiker = gebruiker;
    }

    @Override
    public String toString() {
        return "Rekening{" +
                ", iban='" + iban + '\'' +
                ", saldo=" + saldo +
                '}';
    }

    /**
     * Equals methode voor Rekening
     * @param o het object waarmee de Rekening wordt vergeleken
     * @return boolean: is het vergeleken object gelijk aan de Rekening of niet
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rekening rekening = (Rekening) o;
        return Double.compare(rekening.saldo, saldo) == 0 && iban.equals(rekening.iban) && gebruiker.getGebruikersnaam().equals(rekening.gebruiker.getGebruikersnaam());
    }

    /**
     * Hashcode-methode voor Rekening, waarbij een hashcode wordt gegenereerd op basis van iban, saldo en gebruiker
     * @return int de hashcode voor deze Rekening
     */
    @Override
    public int hashCode() {
        return Objects.hash(iban, saldo, gebruiker);
    }


}
