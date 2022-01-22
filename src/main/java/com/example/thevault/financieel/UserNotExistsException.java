package com.example.thevault.financieel;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Gebruiker bestaat niet.")
public class UserNotExistsException extends RuntimeException {

    //TODO JavaDoc
    public UserNotExistsException() {
        super();
    }

}
