package com.carbon.bank.metier.port;

import com.carbon.bank.metier.pojo.TransactionBO;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository {

    TransactionBO saveTransaction(TransactionBO transaction);

    Optional<TransactionBO> findLastTransaction(UUID accountId);

    List<TransactionBO> getHistory(UUID accountId);

}
