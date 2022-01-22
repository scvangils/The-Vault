// Created by carme
// Creation date 30/11/2021

package com.example.thevault.controller.rest_api_controller;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * Author: Carmen
 */

public class Cryptomunt {

    private int Id;
    private String name;
    private String symbol;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(Cryptomunt.class);

    public Cryptomunt() {
        super();
        logger.info("New Cryptomunt, no args constructor");
    }

    /**
     * Author: Carmen
     * Constructor voor het opvragen van kale informatie van cryptomunt uit de database voor Asset
     * @param cryptomuntId de identifier van de cryptomunt
     */
    public Cryptomunt(int cryptomuntId){
        this(cryptomuntId, null, null);
    }

    /**
     * Author: Carmen
     * All-args constructor voor cryptomunt
     * @param cryptomuntId de identifier van de cryptomunt
     * @param name Naam van de cryptomunt
     * @param afkorting afkorting van de cryptomunt
     */
    public Cryptomunt (int cryptomuntId, String name, String afkorting){
        this.Id = cryptomuntId;
        this.name = name;
        this.symbol = afkorting;
        logger.info("Cryptomunt:" + this);
    }

    @Override
    public String toString() {
        return "Cryptomunt{" +
                "Id=" + Id +
                ", name='" + name + '\'' +
                ", symbol='" + symbol + '\'' +
                '}';
    }

    public int getId() {
        return Id;
    }

    public void setId(int cryptomuntId) {
        this.Id = cryptomuntId;
    }

    public String getName() {
        return name;
    }

    public void setName(String naam) {
        this.name = naam;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cryptomunt that = (Cryptomunt) o;
        return Id == that.Id && name.equals(that.name) && Objects.equals(symbol, that.symbol);
    }

    @Override
    public int hashCode() {
        return Objects.hash(Id, name, symbol);
    }
}
