// Created by S.C. van Gils
// Creation date 12-1-2022

package com.example.thevault.handelingen.model;

import com.example.thevault.financieel.model.Cryptomunt;
import com.example.thevault.klant.model.Gebruiker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;

/**
 * Een trigger is een waarde die een actie inzet:
 * In dit geval een maximale koopprijs of een minimale verkoopprijs van een specifieke cryptomunt
 * met een bepaalde gevraagde of aangeboden hoeveelheid.
 * Deze subclass behelst een mogelijke aankoop.
 */
public class TriggerKoper extends Trigger{

    private final Logger logger = LoggerFactory.getLogger(TriggerKoper.class);

    /**
     * no-args constructor voor TriggerKoper class
     */
    public TriggerKoper() {
        super();
        logger.info("New TriggerKoper");
    }

    /**
     * constructor voor de TriggerKoper class met parameters die geset worden voor de trigger de database in gaat
     *
     * @param gebruiker de maker van de trigger
     * @param cryptomunt de gewenste cryptomunt
     * @param triggerPrijs de gewenste prijs
     * @param aantal het gewenste aantal
     */
    public TriggerKoper(Gebruiker gebruiker, Cryptomunt cryptomunt, double triggerPrijs, double aantal) {
        super(gebruiker, cryptomunt, triggerPrijs, aantal);
        logger.info("New TriggerKoperVeelGebruikt");
    }

    /**
     * Constructor voor TriggerKoper die fields set die geen complexe objecten zijn
     *
     * @param triggerId de id in de database
     * @param triggerPrijs de gewenste prijs
     * @param aantal het gewenste aantal
     * @param datum de datum waarop de trigger is aangemaakt
     */
    public TriggerKoper(int triggerId, double triggerPrijs, double aantal, LocalDate datum) {
        super(triggerId, triggerPrijs, aantal, datum);
        logger.info("New TriggerKoperRowMapper");
    }

    @Override
    public String toString() {
        return "TriggerKoper:" + super.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof TriggerKoper)) return false;
        return super.equals(o);
    }
}
