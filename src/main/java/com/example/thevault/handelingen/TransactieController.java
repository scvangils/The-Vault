// Created by carme
// Creation date 17/01/2022

package com.example.thevault.handelingen;

import com.example.thevault.controller.rest_api_controller.BasisApiController;
import com.example.thevault.klant.LoginService;
import com.example.thevault.klant.RegistrationService;
import com.example.thevault.klant.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class TransactieController extends BasisApiController {

    private final Logger logger = LoggerFactory.getLogger(TransactieController.class);

    //TODO JavaDoc
    public TransactieController(RegistrationService registrationService,
                                AuthorizationService authorizationService, LoginService loginService, TransactieService transactieService) {
        super(registrationService, authorizationService, loginService, transactieService);
        logger.info("New TransactieController");
    }

    /**
     *  Deze methode verzorgt de gegevens die nodig zijn om een transactiepagina te kunnen aanmaken
     *  op basis van de klant en de gewenste cryptomunt waarin hij/zij wil handelen
     *  Als dit goed verloopt komt alle benodigde informatie terug en een
     *  HTTP-response 200 = OK
     * @param transactieStartDto een object dat wordt aangemaakt op basis van gebruikersnaam van de gebruiker
     *                           en cryptoid van de gekozen cryptomunt
     * @return TransactiePaginaDto waar alle relevante gegevens in staan voor de transactiepagina
     */
    @PostMapping("/transaction")
    public ResponseEntity<TransactiePaginaDto> transactieAanvraagHandler(@RequestBody TransactieStartDto transactieStartDto) {
        TransactiePaginaDto transactiePaginaDto = transactieService.openTransactiescherm(transactieStartDto);
        return ResponseEntity.ok().body(transactiePaginaDto);
    }
}
