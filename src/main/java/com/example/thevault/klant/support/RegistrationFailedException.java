// Created by S.C. van Gils
// Creation date 1-12-2021

package com.example.thevault.klant.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Username already registered.")
public class RegistrationFailedException extends RuntimeException {}

//TODO Waar is de method?

