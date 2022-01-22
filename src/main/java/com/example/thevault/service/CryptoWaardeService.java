package com.example.thevault.service;

import com.example.thevault.database.mapping.repository.RootRepository;
import com.example.thevault.domain.model.CryptoWaarde;
import com.example.thevault.domain.model.Cryptomunt;
import com.example.thevault.support.api.CryptoAPI;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class CryptoWaardeService {
    private RootRepository rootRepository;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(CryptoWaardeService.class);

    private static final String DEFAULT_ID = "defaultId";
    private static final String CRON_ELKE_DAG_OM_MIDDERNACHT = "0 0 0 * * *";
    private static final String CRON_NEDERLANDSE_TIJDZONE = "Europe/Paris";

    //TODO JavaDoc
    public CryptoWaardeService(RootRepository rootRepository){
        super();
        this.rootRepository = rootRepository;
    }

    /**
     * Deze methode slaat een cryptowaarde op in de database
     *
     * @param cryptoWaarde De betreffende cryptowaarde
     */
    public void slaCryptoWaardeOp(CryptoWaarde cryptoWaarde){
        rootRepository.slaCryptoWaardeOp(cryptoWaarde);
    }

    /**
     * Deze methode haalt uit de database de waarde die de betreffende cryptomunt vandaag heeft
     * zodat huidige waarde van assets kan worden berekend
     *
     * @param cryptomunt de cryptomunt
     * @return CryptoWaarde-object om huidige waarde van asset te kunnen berekenen
     */
    public CryptoWaarde vindMeestRecenteCryptoWaarde(Cryptomunt cryptomunt){
        return rootRepository.haalMeestRecenteCryptoWaarde(cryptomunt);
    }

    /**
     * Deze methode haalt de koers van een cryptomunt op een bepaalde dag op
     *
     * @param cryptomunt De betreffende cryptomunt
     * @param datum De datum waarop gezocht wordt
     * @return Een cryptoWaarde-object met de gezochte informatie of null indien niet aanwezig voor die datum
     */
    public CryptoWaarde vindCryptoWaardeOpDatum(Cryptomunt cryptomunt, LocalDate datum){
        return rootRepository.haalCryptoWaardeOpDatum(cryptomunt, datum);
    }

    /**
     * Deze methode roept een API aan die koersinformatie van cryptomunten biedt.
     * De huidige koers wordt vervolgens opgeslagen voor alle verhandelbare cryptomunten
     */
    @Scheduled(cron = CRON_ELKE_DAG_OM_MIDDERNACHT, zone = CRON_NEDERLANDSE_TIJDZONE)
    public void haalCryptoWaardes(){
        for (int i = 0; i < cryptoLijst().size(); i++) {
            double cryptoDagwaarde = CryptoAPI.cryptoDagwaarde(cryptoLijst().get(i));
            LocalDate datum = LocalDate.now();
            CryptoWaarde cryptoWaarde = new CryptoWaarde(DEFAULT_ID, cryptoLijst().get(i), cryptoDagwaarde,datum);
            System.out.println(cryptoWaarde.getCryptomunt().getName() + cryptoWaarde.getWaarde());
            slaCryptoWaardeOp(cryptoWaarde);
        }
    }

    /**
     * Deze methode haalt uit de database alle meest recente koersen van de verhandelbare cryptomunten
     *
     * @return Een List van CryptoWaarde-objecten met de koersinformatie
     */
    public List<CryptoWaarde> haalMeestRecenteCryptoWaardes(){
        List<Cryptomunt>  cryptomuntList = CryptoWaardeService.cryptoLijst();
        List<CryptoWaarde> cryptoWaardeList = new ArrayList<>();
        for(Cryptomunt cryptomunt: cryptomuntList){
            cryptoWaardeList.add(vindMeestRecenteCryptoWaarde(cryptomunt));
        }
        return cryptoWaardeList;
    }

    //TODO JavaDoc
    //TODO juiste plek voor aanmaken arraylist?
    public static ArrayList<Cryptomunt> cryptoLijst(){
        Cryptomunt bitcoin = new Cryptomunt(1, "bitcoin", "BTC");
        Cryptomunt ethereum = new Cryptomunt(1027, "ethereum", "ETH");
        Cryptomunt solana = new Cryptomunt(5426, "solana", "SOL");
        Cryptomunt binance = new Cryptomunt(1839, "binance-coin", "BNB");
        Cryptomunt cardano = new Cryptomunt(2010, "cardano", "ADA");
        Cryptomunt xrp = new Cryptomunt(52, "xrp", "XRP");
        Cryptomunt avalanche = new Cryptomunt(5805, "avalanche", "AVAX");
        Cryptomunt polkadot = new Cryptomunt(6636, "polkadot", "DOT");
        Cryptomunt terra = new Cryptomunt(4172, "terra", "LUNA");
        Cryptomunt dogecoin = new Cryptomunt(74, "dogecoin", "DOGE");
        Cryptomunt polygon = new Cryptomunt(3890, "polygon", "MATIC");
        Cryptomunt litecoin = new Cryptomunt(2, "litecoin", "LTC");
        Cryptomunt terrausd = new Cryptomunt(7129, "terrausd", "UST");
        Cryptomunt algorand = new Cryptomunt(4030, "algorand", "ALGO");
        Cryptomunt tron = new Cryptomunt(1958, "tron", "TRX");
        Cryptomunt bitcoin_cash = new Cryptomunt(1831, "bitcoin-cash", "BCH");
        Cryptomunt stellar = new Cryptomunt(512, "stellar", "XLM");
        Cryptomunt elrond = new Cryptomunt(6892, "elrond", "EGLD");
        Cryptomunt vechain = new Cryptomunt(3077, "vechain", "VET");
        Cryptomunt filecoin = new Cryptomunt(2280, "filecoin", "FIL");


        List<Cryptomunt> list = Arrays.asList(bitcoin, ethereum, solana, binance, cardano, xrp, avalanche, polkadot, terra, dogecoin,
                polygon, litecoin, terrausd, algorand, tron, bitcoin_cash, stellar, elrond, vechain, filecoin);

        return new ArrayList<>(list);
    }
}


