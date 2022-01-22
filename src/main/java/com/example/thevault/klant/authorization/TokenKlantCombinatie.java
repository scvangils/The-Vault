// Created by E.S. Olthof
// 20210712
package com.example.thevault.klant.authorization;

import com.example.thevault.klant.Klant;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class TokenKlantCombinatie {

    private UUID key;
    private Klant klant;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    //TODO JavaDoc
    public TokenKlantCombinatie(UUID key, Klant klant) {
        super();
        this.key = key;
        this.klant = klant;
        logger.info("New TokenKlantCombinatie");
    }

    public UUID getKey() {
        return key;
    }

    public void setKey(UUID key) {
        this.key = key;
    }

    public Klant getKlant() {
        return klant;
    }

    public void setKlant(Klant klant) {
        this.klant = klant;
    }

    @Override
    public String toString() {
        return "TokenKlantCombinatie{" +
                "key=" + key +
                ", klant=" + klant +
                '}';
    }
}
