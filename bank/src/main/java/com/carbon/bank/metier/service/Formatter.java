package com.carbon.bank.metier.service;

import com.carbon.bank.metier.pojo.TransactionBO;

import java.util.List;

public interface Formatter {

    String format(List<TransactionBO> transactions);

}
