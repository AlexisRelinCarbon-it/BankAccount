package com.carbon.bank.metier.service;

import com.carbon.bank.metier.exception.AccountNonExistentException;
import com.carbon.bank.metier.exception.NegativeAmountException;
import com.carbon.bank.metier.pojo.TransactionBO;
import com.carbon.bank.metier.port.AccountServiceApi;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountService implements AccountServiceApi {

    @Override
    public TransactionBO deposit(UUID accountId, BigDecimal amount) throws NegativeAmountException, AccountNonExistentException {
        return null;
    }

    @Override
    public TransactionBO withdraw(UUID accountId, BigDecimal amount) throws NegativeAmountException, AccountNonExistentException {
        return null;
    }

    @Override
    public String printStatement(UUID accountId, Formatter formatter) {
        return null;
    }
}
