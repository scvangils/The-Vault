// Created by S.C. van Gils
// Creation date 19-1-2022

package com.example.thevault.support.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_ACCEPTABLE, reason = "Saldo wordt te laag als alle triggers worden uitgevoerd.")
public class SchaduwSaldoException extends RuntimeException{}
