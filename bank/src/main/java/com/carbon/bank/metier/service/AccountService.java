package com.carbon.bank.metier.service;

import com.carbon.bank.metier.exception.AccountNonExistentException;
import com.carbon.bank.metier.exception.NegativeAmountException;
import com.carbon.bank.metier.pojo.Operation;
import com.carbon.bank.metier.pojo.TransactionBO;
import com.carbon.bank.metier.port.AccountServiceApi;
import com.carbon.bank.metier.port.TransactionRepository;

import java.math.BigDecimal;
import java.util.UUID;

public class AccountService implements AccountServiceApi {

    private static final String NEGATIVE_AMOUNT_MESSAGE = "The amount can't be negative.";
    private static final String NEGATIVE_ACCOUNT_WITHDRAW_MESSAGE = "The account can't be negative for a withdrawal.";
    private static final String ACCOUNT_NON_EXISTENCE_MESSAGE = "The account do not exist.";

    private final TransactionRepository transactionRepository;
    private final TimeProvider timeProvider;

    public AccountService(TransactionRepository transactionRepository, TimeProvider timeProvider) {
        this.transactionRepository = transactionRepository;
        this.timeProvider = timeProvider;
    }

    @Override
    public TransactionBO deposit(UUID accountId, BigDecimal amount)
            throws NegativeAmountException, AccountNonExistentException {

        verifyAmountCompliance(amount);
        final var lastBalance = getLastBalanceFromAccountId(accountId);

        return transactionRepository.saveTransaction(
                new TransactionBO(accountId,
                        timeProvider.getCurrentDate(), Operation.DEPOSIT,
                        amount, lastBalance.add(amount)));
    }

    @Override
    public TransactionBO withdraw(UUID accountId, BigDecimal amount)
            throws NegativeAmountException, AccountNonExistentException {

        verifyAmountCompliance(amount);
        final var lastBalance = getLastBalanceFromAccountId(accountId);

        if (lastBalance.subtract(amount).compareTo(BigDecimal.ZERO) < 0)
            throw new NegativeAmountException(NEGATIVE_ACCOUNT_WITHDRAW_MESSAGE);

        return transactionRepository.saveTransaction(
                new TransactionBO(accountId,
                        timeProvider.getCurrentDate(), Operation.WITHDRAWAL,
                        amount, lastBalance.subtract(amount)));
    }

    @Override
    public String printStatement(UUID accountId, Formatter formatter) {
        return null;
    }

    private void verifyAmountCompliance(BigDecimal amount) throws NegativeAmountException {
        if (amount.signum() <= 0)
            throw new NegativeAmountException(NEGATIVE_AMOUNT_MESSAGE);
    }

    private BigDecimal getLastBalanceFromAccountId(UUID accountId) throws AccountNonExistentException {
        return transactionRepository.findLastTransaction(accountId)
                .orElseThrow(() -> new AccountNonExistentException(ACCOUNT_NON_EXISTENCE_MESSAGE))
                .balance();
    }
}

