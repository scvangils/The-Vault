package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.Cryptomunt;

import java.util.List;

/**
 * @Author: Carmen Rietdijk
 * Beschrijving: De interface voor de DAO voor Cryptomunt, waar methodes in komen voor CRUD van de Cryptomunten
 * Op dit moment alleen 'getCryptomunt' ten behoeve van het gebruik van Assets in de RootRepo
 */

public interface CryptomuntDAO {

    /**
     * Dit betreft het vinden van een specifieke cryptomunt
     * @param cryptomuntId cryptomuntidentifier waarover informatie wordt opgevraagd
     * @return Cryptomunt de cryptomunt waarover informatie is opgevraagd
     */
    public Cryptomunt geefCryptomunt(int cryptomuntId);
    public List<Cryptomunt> geefAlleCryptomunten();
    public Cryptomunt geefCryptomuntByNaam(String cryptoNaam);
}
