package com.example.account_service.Exception;

public class TransaccionNotFoundException extends RuntimeException {
    public TransaccionNotFoundException(String message) {
        super(message);
    }
}
