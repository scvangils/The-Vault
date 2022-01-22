// Created by carme
// Creation date 01/12/2021

package com.example.thevault.controller.rest_api_controller;

import com.example.thevault.klant.LoginService;
import com.example.thevault.klant.RegistrationService;
import com.example.thevault.handelingen.TransactieService;
import com.example.thevault.klant.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RestController;

@RestController
public abstract class BasisApiController {

    private final Logger logger = LoggerFactory.getLogger(BasisApiController.class);

    protected AuthorizationService authorizationService;
    protected RegistrationService registrationService;
    protected LoginService loginService;
    protected TransactieService transactieService;

    //TODO JavaDoc
    public BasisApiController(RegistrationService registrationService, AuthorizationService authorizationService, LoginService loginService, TransactieService transactieService) {
        super();
        this.registrationService = registrationService;
        this.authorizationService = authorizationService;
        this.loginService = loginService;
        this.transactieService = transactieService;
        logger.info("New BasisApiController");
    }

}
