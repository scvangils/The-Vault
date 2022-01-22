// Created by Elise Olthof
// Creation date 12-12-2021

package com.example.thevault.klant;

import com.example.thevault.handelingen.RootRepository;
import com.example.thevault.financieel.RekeningService;
import com.example.thevault.klant.authorization.AuthorizationService;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class LoginService {

    private RootRepository rootRepository;
    private AuthorizationService authorizationService;
    private RekeningService rekeningService;

    //TODO Verwijderen? Worden nergens gebruikt
    private UUID opaakToken;
    private String jwtToken;

    private final Logger logger = LoggerFactory.getLogger(LoginService.class);

    //TODO JavaDoc
    @Autowired
    public LoginService(RootRepository rootRepository, AuthorizationService authorizationService, RekeningService rekeningService) {
        super();
        this.rootRepository = rootRepository;
        this.authorizationService = authorizationService;
        this.rekeningService = rekeningService;
        logger.info("New LoginService......");
    }

    /**
     * Wim 20211207
     * @return Klant als combinatie gebruikersnaam en wachtwoord correct is, anders geef foutmelding
     */
    public Klant valideerLogin (LoginDto loginDto){
        Klant klant = vindKlantByGebruikersnaam(loginDto.getGebruikersnaam());
        if(klant != null) {

            String encodedWachtwoord = klant.getWachtwoord();
            String wachtwoord = new String(Base64.decodeBase64(encodedWachtwoord));

            if (!BCryptWachtwoordHash.verifyHash(loginDto.getWachtwoord(), wachtwoord)) {
                klant = null;
            }
        }
        return klant;
    }

    /**
     * Deze methode zoekt of er in de database al een klant bestaat met deze gebruikersnaam
     * en maakt eventueel een klant-object aan op nasis van de teruggestuurde gegevens
     *
     *
     * @param gebruikersnaam gebruikersnaam van een (mogelijke) klant die uniek moet zijn
     * @return klant-object op basis van gegevens uit de database of null indien gebruikersnaam niet gevonden is
     */
    public Klant vindKlantByGebruikersnaam(String gebruikersnaam){
        return rootRepository.vindKlantByGebruikersnaam(gebruikersnaam);
    }
}