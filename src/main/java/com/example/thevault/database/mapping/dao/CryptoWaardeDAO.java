package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.CryptoWaarde;
import com.example.thevault.domain.model.Cryptomunt;

import java.time.LocalDate;
import java.util.List;

public interface CryptoWaardeDAO {



    public List<CryptoWaarde> getCryptoWaardeByCryptomunt(Cryptomunt cryptomunt);

    public CryptoWaarde getCryptoWaardeByCryptomuntAndDate(Cryptomunt cryptomunt, LocalDate datum);

    public CryptoWaarde slaCryptoWaardeOp(CryptoWaarde cryptoWaarde);




    }
