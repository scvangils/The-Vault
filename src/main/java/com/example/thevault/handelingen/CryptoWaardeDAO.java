package com.example.thevault.handelingen;

import com.example.thevault.controller.rest_api_controller.Cryptomunt;

import java.time.LocalDate;
import java.util.List;

public interface CryptoWaardeDAO {



    public List<CryptoWaarde> getCryptoWaardeByCryptomunt(Cryptomunt cryptomunt);

    public CryptoWaarde getCryptoWaardeByCryptomuntAndDate(Cryptomunt cryptomunt, LocalDate datum);

    public CryptoWaarde slaCryptoWaardeOp(CryptoWaarde cryptoWaarde);




    }
