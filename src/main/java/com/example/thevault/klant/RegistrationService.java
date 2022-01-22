// Created by S.C. van Gils
// Creation date 3-12-2021

package com.example.thevault.klant;

import com.example.thevault.financieel.RekeningService;
import com.example.thevault.support.exceptions.IncorrectFormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Deze class handelt de zaken af die met registreren te maken hebben
 * en combineert de functionaliteit van enkele andere services daarvoor
 */
@Service
public class RegistrationService {

    private final Logger logger = LoggerFactory.getLogger(RegistrationService.class);

    private final KlantService klantService;
    private final AdresService adresService;
    private final RekeningService rekeningService;

    /**
     * De constructor van de RegistrationService class voor Spring Boot
     * met dependency injection
     *
     * @param klantService De service-class die zich bezighoudt met klant-gerelateerde processen
     * @param adresService De service-class die zich bezighoudt met adres-gerelateerde processen
     * @param rekeningService De service-class die zich bezighoudt met rekening-gerelateerde processen
     */
    @Autowired
    public RegistrationService(KlantService klantService, AdresService adresService, RekeningService rekeningService) {
        super();
        this.klantService =  klantService;
        this.adresService = adresService;
        this.rekeningService = rekeningService;
        logger.info("New RegistrationService");
    }

    /**
     * Deze methode probeert een nieuwe klant te registreren.
     * Als de gegevens allemaal kloppen, wordt er een nieuwe rekening aangemaakt en
     * wordt deze vervolgens opgeslagen.
     *
     * @param klant een Klant-object is wordt aangemaakt op basis van ingevoerde gegevens
     * @return een DTO waar de relevante klantgegevens in staan als de klant succesvol is opgeslagen
     */
    public RegistrationDto registreerKlant(Klant klant){
      if(!adresService.postcodeOpmaak(klant.getAdres().getPostcode())){
          throw new IncorrectFormatException();
      }
        klant = klantService.registreerKlant(klant);
        klant.setRekening(rekeningService.creeerRekening(klant));
        System.out.println(klant.getRekening());
        rekeningService.slaRekeningOp(klant.getRekening());
        return new RegistrationDto(klant);
    }
}
