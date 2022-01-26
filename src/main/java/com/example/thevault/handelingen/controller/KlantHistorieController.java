// Created by S.C. van Gils
// Creation date 20-1-2022

package com.example.thevault.handelingen.controller;

import com.example.thevault.handelingen.service.TransactieService;
import com.example.thevault.handelingen.service.TriggerService;
import com.example.thevault.BasisApiController;
import com.example.thevault.klant.service.LoginService;
import com.example.thevault.klant.service.RegistrationService;
import com.example.thevault.klant.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Deze klasse beheert de afwikkeling van http-requests die te maken hebbben
 * met de overzichtspagina van de uitgevoerde transacties en geplaatste triggers van een klant
 */

@Controller
public class KlantHistorieController extends BasisApiController {

    private final Logger logger = LoggerFactory.getLogger(KlantHistorieController.class);
    private final TriggerService triggerService;

    /**
     * Constructor voor KlantHistorieController voor dependency injection van Spring Boot
     *
     * @param registrationService de service class voor registratie
     * @param authorizationService de service class voor autorisatie
     * @param loginService de service class voor login
     * @param transactieService de service class voor transactie
     */
    public KlantHistorieController(RegistrationService registrationService,
                                   AuthorizationService authorizationService, LoginService loginService, TransactieService transactieService,
                                    TriggerService triggerService) {
        super(registrationService, authorizationService, loginService, transactieService);
        this.triggerService = triggerService;
        logger.info("New KlantHistorieController");
    }
    @PostMapping("/klantHistorie")
    public ResponseEntity<HistorieDto> klantHistorieHandler(@RequestBody String gebruikersNaam){
        return ResponseEntity.ok().body(new HistorieDto(loginService.vindKlantByGebruikersnaam(gebruikersNaam)));
    }
}
