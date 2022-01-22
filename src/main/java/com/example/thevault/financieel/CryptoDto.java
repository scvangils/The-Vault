//Wibul 220220113
//DTO moet alle crypto's doorgeven met de recente koers

//hoe zorg ik ervoor dat alle crypto's in de DTO komen?

package com.example.thevault.financieel;

import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CryptoDto {
    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(CryptoDto.class);

    private String name;
    private String afkorting;
    private double prijs;
    private double aantal;

    //TODO JavaDoc
    //TODO Verwijderen?
    public CryptoDto(){
        super();
        logger.info("new cryptoDTO no args");
    }

    //TODO JavaDoc
    public CryptoDto(String name, String afkorting, double prijs, double aantal) {
        this.name = name;
        this.afkorting = afkorting;
        this.prijs = prijs;
        this.aantal = aantal;
    }

    public Logger getLogger() {
        return logger;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAfkorting() {
        return afkorting;
    }

    public void setAfkorting(String afkorting) {
        this.afkorting = afkorting;
    }

    public double getAantal() {
        return aantal;
    }

    public void setAantal(double aantal) {
        this.aantal = aantal;
    }

    public double getPrijs() {
        return prijs;
    }

    public void setPrijs(double prijs) {
        this.prijs = prijs;
    }

}
