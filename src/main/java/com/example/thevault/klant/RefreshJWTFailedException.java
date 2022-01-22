// Created by carme
// Creation date 01/12/2021
// aanpassing door Wim 20211207

package com.example.thevault.klant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Je moet opnieuw inloggen.")
public class RefreshJWTFailedException extends RuntimeException{}

//TODO Waar is de method?
