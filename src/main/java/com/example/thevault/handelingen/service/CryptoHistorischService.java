// Created by S.C. van Gils
// Creation date 26-12-2021

package com.example.thevault.handelingen.service;

import com.example.thevault.financieel.model.Cryptomunt;
import com.example.thevault.handelingen.controller.CryptoWaardenHistorischDto;
import com.example.thevault.handelingen.model.CryptoWaarde;
import com.example.thevault.handelingen.repository.RootRepositoryHandelingen;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Deze class zorgt ervoor dat de juiste data naar de front-end kan gaan
 * voor de grafische weergave van de historische koersen van de verschillende cryptomunten
 */

@Service
public class CryptoHistorischService{

    private final Logger logger = LoggerFactory.getLogger(CryptoHistorischService.class);

    private final RootRepositoryHandelingen rootRepositoryHandelingen;

    /**
     * constructor voor CryptoHistorischService voor Spring Boot
     * met dependency injection
     *
     * @param rootRepositoryHandelingen  De class die als facade voor de database dient
     */
    public CryptoHistorischService(RootRepositoryHandelingen rootRepositoryHandelingen) {
        super();
        this.rootRepositoryHandelingen = rootRepositoryHandelingen;
        logger.info("New CryptoHistorischService");
    }

    /**
     * Deze methode combineert de koersen en de data waarop die koersen waren
     * van een specifieke cryptomunt in een object dat twee arrays bevat
     *
     * @param cryptomunt De betreffende cryptomunt
     * @return een Dto die een array van waarden en een array van datum-strings bevat
     */
    public CryptoWaardenHistorischDto maakCryptoWaardeArray(Cryptomunt cryptomunt){
        List<CryptoWaarde> cryptoWaardeList = rootRepositoryHandelingen.haalAlleCryptoWaardesVanCryptomunt(cryptomunt);
        String[] datum = new String[cryptoWaardeList.size()];
        double[] waarde = new double[cryptoWaardeList.size()];
        for (int i = 0; i < cryptoWaardeList.size(); i++) {
            datum[i] = cryptoWaardeList.get(i).getDatum().toString();
            waarde[i] = cryptoWaardeList.get(i).getWaarde();
        }
        return new CryptoWaardenHistorischDto(datum, waarde);
    }

    /**
     * Deze methode maakt een array van cryptomunten die ingelezen kan worden in de front-end
     * als input voor html-elementen
     *
     * @return een array van alle gebruikte cryptomunten
     */
    public Cryptomunt[] maakCryptoMuntArray(){
        return rootRepositoryHandelingen.geefAlleCryptomunten().toArray(Cryptomunt[]::new);
    }

    /**
     * Deze methode geeft een Cryptomunt-object terug op basis van de naam van de munt
     * Deze wordt gehaald uit de tabel in de database
     *
     * @param naam de naam van de gezochte cryptomunt
     * @return het corresponderende cryptomunt object of null indien niet aanwezig in de database
     */
    public Cryptomunt getCryptoMuntOpNaam(String naam){
       return rootRepositoryHandelingen.geefAlleCryptomunten().stream()
               .filter(cryptomunt -> cryptomunt.getName().equals(naam)).findFirst().orElse(null);
    }

}