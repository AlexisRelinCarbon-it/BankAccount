package com.carbon.bank.metier.pojo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record TransactionBO(UUID accountId,
                            LocalDate Date,
                            Operation type,
                            BigDecimal amount,
                            BigDecimal balance) {
}
