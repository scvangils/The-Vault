// Created by S.C. van Gils
// Creation date 19-1-2022

package com.example.thevault.support.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE)
public class SchaduwAssetException extends RuntimeException{

    public SchaduwAssetException(String message){
        super(message);
    }
}
