// Created by S.C. van Gils
// Creation date 7-12-2021

package com.example.thevault.klant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class AgeTooLowException extends RuntimeException{

    //TODO JavaDoc
    public AgeTooLowException(String message){
        super(message);
    }
}
