// Created by carme
// Creation date 12/12/2021

package com.example.thevault.financieel.support;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Lookup;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Asset bestaat niet.")
public class AssetNotExistsException extends RuntimeException{

    String message;

    /**
     * Author: Carmen
     * Geeft een waarschuwing dat de opgevraagde asset niet bestaat
     */
    public AssetNotExistsException() {
        super();
        message = "Asset bestaat niet.";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
