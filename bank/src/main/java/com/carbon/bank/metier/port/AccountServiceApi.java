package com.carbon.bank.metier.port;

import com.carbon.bank.metier.exception.AccountNonExistentException;
import com.carbon.bank.metier.exception.NegativeAmountException;
import com.carbon.bank.metier.pojo.TransactionBO;
import com.carbon.bank.metier.service.Formatter;

import java.math.BigDecimal;
import java.util.UUID;

public interface AccountServiceApi {

    TransactionBO deposit(UUID accountId, BigDecimal amount)
            throws NegativeAmountException, AccountNonExistentException;

    TransactionBO withdraw(UUID accountId, BigDecimal amount)
            throws NegativeAmountException, AccountNonExistentException;

    String printStatement(UUID accountId, Formatter formatter);

}
