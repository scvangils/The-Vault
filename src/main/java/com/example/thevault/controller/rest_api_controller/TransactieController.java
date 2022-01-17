// Created by carme
// Creation date 17/01/2022

package com.example.thevault.controller.rest_api_controller;

import com.example.thevault.domain.model.Cryptomunt;
import com.example.thevault.domain.transfer.CryptoWaardenHistorischDto;
import com.example.thevault.domain.transfer.TransactiePaginaDto;
import com.example.thevault.domain.transfer.TransactieStartDto;
import com.example.thevault.service.CryptoHistorischService;
import com.example.thevault.service.LoginService;
import com.example.thevault.service.RegistrationService;
import com.example.thevault.service.TransactieService;
import com.example.thevault.support.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Controller
public class TransactieController extends BasisApiController {

    private final Logger logger = LoggerFactory.getLogger(TransactieController.class);

    public TransactieController(RegistrationService registrationService,
                                AuthorizationService authorizationService, LoginService loginService, TransactieService transactieService) {
        super(registrationService, authorizationService, loginService, transactieService);
        logger.info("New TransactieController");
    }

    //TODO check: Is accesstoken nog geldig?
    @PostMapping("/transaction")
    public ResponseEntity<TransactiePaginaDto> transactieAanvraagHandler(@RequestBody TransactieStartDto transactieStartDto) {
        TransactiePaginaDto transactiePaginaDto = transactieService.openTransactiescherm(transactieStartDto);
        return ResponseEntity.ok().body(transactiePaginaDto);
    }
}
