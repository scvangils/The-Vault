package com.example.thevault.financieel;

import com.example.thevault.handelingen.RootRepository;
import com.example.thevault.klant.Gebruiker;
import com.example.thevault.klant.Klant;
import com.example.thevault.support.exceptions.UserNotExistsException;
import net.minidev.json.annotate.JsonIgnore;
import org.iban4j.CountryCode;
import org.iban4j.Iban;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Author Ju-Sen Cheung m.u.v. methode creeerIban
 * Servicelaag voor rekening waarin een rekening aangemaakt kan worden en informatie over een rekening opgevraagd of
 * aangepast kan worden.
 */

@Service
public class RekeningService {

    private RootRepository rootRepository;

    @JsonIgnore
    private final Logger logger = LoggerFactory.getLogger(RekeningService.class);

    private final Double STARTSALDO = 1000.0;

     /**
     * Hier is sprake van Dependency Injection. Dee rekeningService wordt hier aangemaakt en deze maakt gebruik van de
     * rootrepository. Deze wordt als het ware geïnjecteerd.
     * @param rootRepository de repository waar de methodes kunnen worden aangeroepen die in deze klasse worden gebruikt.
     */
    @Autowired
    public RekeningService(RootRepository rootRepository) {
        super();
        this.rootRepository = rootRepository;
        logger.info("Nieuwe RekeningService.");
    }

    /**
     * Author Wim Bultman
     * creeert IBAN, mbv org.iban4j.Iban library, zie https://github.com/arturmkrtchyan/iban4j
     */
    public static Iban creeerIban(){
        Iban iban = new Iban.Builder().countryCode(CountryCode.NL).bankCode("TVLT").buildRandom();
        return iban;
    }

    /**
     * Deze methode zorgt ervoor dat er een rekening wordt aangemaakt voor een nieuwe klant met een juiste iban en een
     * vast startsaldo.
     * @param klant is de klant die meegegeven wordt waarvoor de rekening wordt aangemaakt.
     * @return de aangemaakte rekening voor de nieuwe gebruiker wordt teruggegeven met een iban en startsaldo van € 1.000,-.
     */
    public Rekening creeerRekening(Klant klant) {
        String iban = creeerIban().toString();
        Rekening rekening = new Rekening(iban, STARTSALDO);
        klant.setRekening(rekening);
        rekening.setGebruiker(klant);
        return rekening;
    }

    /**
     * Deze methode zorgt ervoor dat een aangemaakte rekening wordt opgeslagen.
     * @param rekening is de meegegeven rekening welke opgeslagen moet worden.
     * @return de aangemaakte rekening wordt doorgegeven aan de RootRepository voor het verdere opslagproces via de DAO
     * in de database.
     */
    public Rekening slaRekeningOp(Rekening rekening){
        return rootRepository.slaRekeningOp(rekening);
    }

     /**
     * Met deze methode kan je met een opgegeven gebruiker de rekening opvragen als de gebruiker voorkomt in de database.
     * @param gebruiker is de meegegeven gebruiker voor wie de rekening wordt opgevraagd.
     * @throws UserNotExistsException als er geen gebruiker wordt meegegeven of als de gebruikersnaam niet bestaat,
     * wordt er een exceptie gegooid.
     * @return de opgevraagde rekening wordt teruggegeven.
     */
    public Rekening vindRekening(Gebruiker gebruiker) throws UserNotExistsException {
        if (gebruiker == null){
            throw new UserNotExistsException();
        }
        if (rootRepository.vindKlantByGebruikersnaam(gebruiker.getGebruikersnaam()) == null ){
            throw new UserNotExistsException();
        }
        return rootRepository.vindRekeningVanGebruiker(gebruiker);
    }

    /**
     * Met deze methode kan je het rekeningsaldo van de gebruiker opvragen als de gerbuiker voorkomt in de database.
     * @param gebruiker is de meegegeven gebruiker voor wie het rekeningsaldo wordt opgevraagd.
     * @throws UserNotExistsException als er geen gebruiker wordt meegegeven of als de gebruikersnaam niet bestaat,
     * wordt er een exceptie gegooid.
     * @return het saldo van de opgevraagde rekening wordt teruggegeven.
     */
    public double vraagSaldoOp(Gebruiker gebruiker) throws UserNotExistsException{
        if (gebruiker == null){
            throw new UserNotExistsException();
        }
        if (rootRepository.vindKlantByGebruikersnaam(gebruiker.getGebruikersnaam()) == null ){
            throw new UserNotExistsException();
        }
        return vindRekening(gebruiker).getSaldo();
    }

    /**
     * Met deze methode kan je het rekeningsaldo van de gebruiker wijzigen als er een tarnsactie heeft plaatsgevonden.
     * @param gebruiker is de meegegeven gebruiker voor wie het rekeningsaldo wordt opgevraagd.
     * @param transactiebedrag is het bedrag van de transactie.
     * @throws UserNotExistsException als er geen gebruiker wordt meegegeven of als de gebruikersnaam niet bestaat,
     * wordt er een exceptie gegooid.
     * @return het saldo van de opgevraagde rekening wordt gewijzigd na de transactie.
     */

    public Rekening wijzigSaldo(Gebruiker gebruiker, double transactiebedrag) {
        return rootRepository.wijzigSaldoVanGebruiker(gebruiker, transactiebedrag);
    }
}
