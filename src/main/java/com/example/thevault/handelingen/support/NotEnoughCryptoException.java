// Created by E.S. Olthof
// Creation date 12-12-2021

package com.example.thevault.handelingen.support;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Niet genoeg cryptomunten voor transactie.")
public class NotEnoughCryptoException extends RuntimeException {
    String message;

    //TODO JavaDoc
    public NotEnoughCryptoException() {
        super();
        message = "Niet genoeg cryptomunten voor transactie.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
