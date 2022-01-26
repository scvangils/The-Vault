package com.example.thevault.handelingen.repository;

import com.example.thevault.financieel.model.Cryptomunt;
import com.example.thevault.handelingen.model.CryptoWaarde;

import java.time.LocalDate;
import java.util.List;

public interface CryptoWaardeDAO {



    public List<CryptoWaarde> getCryptoWaardeByCryptomunt(Cryptomunt cryptomunt);

    public CryptoWaarde getCryptoWaardeByCryptomuntAndDate(Cryptomunt cryptomunt, LocalDate datum);

    public CryptoWaarde slaCryptoWaardeOp(CryptoWaarde cryptoWaarde);




    }
