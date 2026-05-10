package com.example.account_service.Exception;

public class FondosInsuficientesException extends RuntimeException {
    public FondosInsuficientesException(String message) {
        super(message);
    }
}
