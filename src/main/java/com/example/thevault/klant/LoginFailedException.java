// Created by carme
// Creation date 01/12/2021
// aanpassing door Wim 20211207

package com.example.thevault.klant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Deze combinatie van gegevens is niet bekend.")
public class LoginFailedException extends RuntimeException{

    //TODO Wat moet er gebeuren met onderstaande code?
//    private final Logger logger = LoggerFactory.getLogger(LoginFailedException.class);
//
//    public LoginFailedException() {
//        super();
//        logger.info("New LoginFailedException");
//    }
}
