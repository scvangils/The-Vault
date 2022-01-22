// Created by E.S. Olthof
// Creation date 12/15/2021

package com.example.thevault.database.mapping.dao;

import com.example.thevault.domain.model.*;

import java.sql.Timestamp;
import java.util.List;

public interface TransactieDAO {

    Transactie slaTransactieOp(Transactie transactie);

    List<Transactie> geefTransactiesVanGebruiker(Gebruiker gebruiker);

    List<Transactie> geefAlleTransacties();

    List<Transactie> geefTransactiesVanGebruikerInPeriode(Gebruiker gebruiker, Timestamp startDatum, Timestamp eindDatum);

    List<Transactie> geefAlleTransactiesInPeriode(Timestamp startDatum, Timestamp eindDatum);

    List<Transactie> geefTransactiesVanGebruikerMetCryptomunt(Gebruiker gebruiker, Cryptomunt cryptomunt);

    Transactie verwijderTransactie(Transactie transactie);

}
