// Created by S.C. van Gils
// Creation date 2-12-2021

package com.example.thevault.klant.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Gegevens zijn onjuist ingevoerd.")
    public class IncorrectFormatException extends RuntimeException {}

    //TODO Waar is de method?

