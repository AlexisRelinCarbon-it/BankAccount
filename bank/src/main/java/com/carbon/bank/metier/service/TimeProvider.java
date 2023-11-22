package com.carbon.bank.metier.service;

import java.time.LocalDate;
import java.time.ZoneOffset;

public interface TimeProvider {

    default LocalDate getCurrentDate() {
        return LocalDate.now(ZoneOffset.UTC);
    }
}
