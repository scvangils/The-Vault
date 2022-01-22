// Created by S.C. van Gils
// Creation date 2-12-2021

package com.example.thevault.klant;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "BSN kan niet kloppen.")
public class IncorrectBSNException extends RuntimeException {}

//TODO Waar is de method?
