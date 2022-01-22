// Created by S.C. van Gils
// Creation date 19-1-2022

package com.example.thevault.handelingen;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Gewenst aantal cryptomunt moet positief zijn")
public class TriggerAantalNietPositiefException extends RuntimeException{}
