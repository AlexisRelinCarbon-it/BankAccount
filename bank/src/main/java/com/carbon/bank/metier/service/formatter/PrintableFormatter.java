package com.carbon.bank.metier.service.formatter;

import com.carbon.bank.metier.pojo.TransactionBO;
import com.carbon.bank.metier.service.Formatter;

import java.util.Comparator;
import java.util.List;

public class PrintableFormatter implements Formatter {

    @Override
    public String format(List<TransactionBO> transactions) {

        StringBuilder content = new StringBuilder();

        content.append("""
                %-36s | %-10s | %-10s | %-10s |
                """
                .formatted("AccountId", "Date", "Type", "Amount (€)"));

        transactions = transactions.stream()
                .sorted(Comparator.comparing(TransactionBO::Date).reversed())
                .toList();

        for (TransactionBO transaction : transactions) {
            content.append("""
                    %-36s | %-10s | %-10s | %-10s |
                    """
                    .formatted(transaction.accountId(), transaction.Date(),
                            transaction.type(), transaction.amount()));
        }

        content.append("Balance (€) = ").append(transactions.getFirst().balance());

        return content.toString();
    }
}
