// Created by E.S. Olthof
// Creation date 12-12-2021

package com.example.thevault.handelingen;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Saldo te laag.")
public class BalanceTooLowException extends RuntimeException {
    String message;

    //TODO JavaDoc
    public BalanceTooLowException() {
        super();
        message = "Saldo te laag.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}