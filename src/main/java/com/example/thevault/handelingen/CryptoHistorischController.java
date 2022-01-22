// Created by S.C. van Gils
// Creation date 26-12-2021

package com.example.thevault.handelingen;

import com.example.thevault.klant.BasisApiController;
import com.example.thevault.financieel.Cryptomunt;
import com.example.thevault.klant.LoginService;
import com.example.thevault.klant.RegistrationService;
import com.example.thevault.klant.authorization.AuthorizationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class CryptoHistorischController extends BasisApiController {

    private final Logger logger = LoggerFactory.getLogger(CryptoHistorischController.class);

    private final CryptoHistorischService cryptoHistorischService;

    //TODO JavaDoc
    public CryptoHistorischController(RegistrationService registrationService,
                                      AuthorizationService authorizationService, LoginService loginService, TransactieService transactieService,
                                      CryptoHistorischService cryptoHistorischService) {
        super(registrationService, authorizationService, loginService, transactieService);
        this.cryptoHistorischService = cryptoHistorischService;
        logger.info("New CryptoHistorischController");
    }

    //TODO JavaDoc
    @PostMapping("/cryptoGrafiek")
    public ResponseEntity<CryptoWaardenHistorischDto> cryptoWaardeArrayHandler(@RequestBody String cryptoNaam){

        CryptoWaardenHistorischDto cryptoArrays = cryptoHistorischService.maakCryptoWaardeArray(cryptoHistorischService.getCryptoMuntOpNaam(cryptoNaam));
        return ResponseEntity.ok().body(cryptoArrays);
    }

    //TODO JavaDoc
    @GetMapping("/cryptoLijst")
    public ResponseEntity<Cryptomunt[]> getCryptomunten(){
        return ResponseEntity.ok().body(cryptoHistorischService.maakCryptoMuntArray());
    }
}
