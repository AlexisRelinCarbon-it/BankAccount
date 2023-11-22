package com.carbon.bank.metier.exception;

public class AccountNonExistentException extends Exception {
    public AccountNonExistentException(String message) {
        super(message);
    }
}
