package com.carbon.bank;

import com.carbon.bank.metier.exception.AccountNonExistentException;
import com.carbon.bank.metier.exception.NegativeAmountException;
import com.carbon.bank.metier.pojo.Operation;
import com.carbon.bank.metier.pojo.TransactionBO;
import com.carbon.bank.metier.port.TransactionRepository;
import com.carbon.bank.metier.service.AccountService;
import com.carbon.bank.metier.service.TimeProvider;
import com.carbon.bank.metier.service.formatter.PrintableFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private TransactionRepository transactionRepository;
    @Mock
    private TimeProvider timeProvider;

    @InjectMocks
    private PrintableFormatter formatter;
    @InjectMocks
    private AccountService accountService;

    private TransactionBO lastTransaction;
    private TransactionBO depositTransaction;
    private TransactionBO withdrawTransaction;
    private UUID accountId;
    private LocalDate localDate1;

    @BeforeEach
    void setUp() {

        final var UTC_ZONE_ID = ZoneId.of("UTC");
        final var clock1 = Clock.fixed(Instant.parse("2007-12-23T00:00:00Z"), UTC_ZONE_ID);
        final var clock2 = Clock.fixed(Instant.parse("2007-12-25T00:00:00Z"), UTC_ZONE_ID);
        final var clock3 = Clock.fixed(Instant.parse("2007-12-22T00:00:00Z"), UTC_ZONE_ID);
        localDate1 = LocalDate.now(clock1);
        final var localDate2 = LocalDate.now(clock2);
        final var localDate3 = LocalDate.now(clock3);

        accountId = UUID.fromString("afc4efb4-b30b-41a9-bd68-2373e54d2516");

        lastTransaction = new TransactionBO(accountId, localDate1,
                Operation.DEPOSIT, new BigDecimal(15), new BigDecimal(32));

        depositTransaction = new TransactionBO(accountId, localDate2,
                Operation.DEPOSIT, new BigDecimal(854), new BigDecimal(886));

        withdrawTransaction = new TransactionBO(accountId, localDate3,
                Operation.WITHDRAWAL, new BigDecimal(11), new BigDecimal(875));

    }

    @Test
    @DisplayName("Deposit - nominal operating")
    void deposit() throws AccountNonExistentException, NegativeAmountException {

        TransactionBO expectedTransaction = new TransactionBO(
                accountId, localDate1, Operation.DEPOSIT, new BigDecimal(854), new BigDecimal(886));

        when(timeProvider.getCurrentDate()).thenReturn(localDate1);
        when(transactionRepository.findLastTransaction(depositTransaction.accountId()))
                .thenReturn(Optional.of(lastTransaction));
        when(transactionRepository.saveTransaction(expectedTransaction)).thenReturn(expectedTransaction);

        assertEquals(expectedTransaction, accountService.deposit(accountId, new BigDecimal(854)));

        verify(timeProvider).getCurrentDate();
        verify(transactionRepository).findLastTransaction(depositTransaction.accountId());
        verify(transactionRepository).saveTransaction(expectedTransaction);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    @DisplayName("Withdraw - nominal operating")
    void withdraw() throws AccountNonExistentException, NegativeAmountException {

        TransactionBO expectedTransaction = new TransactionBO(
                accountId, localDate1, Operation.WITHDRAWAL, new BigDecimal(11), new BigDecimal(21));

        when(timeProvider.getCurrentDate()).thenReturn(localDate1);
        when(transactionRepository.findLastTransaction(withdrawTransaction.accountId()))
                .thenReturn(Optional.of(lastTransaction));
        when(transactionRepository.saveTransaction(expectedTransaction)).thenReturn(expectedTransaction);

        assertEquals(expectedTransaction, accountService.withdraw(accountId, new BigDecimal(11)));

        verify(timeProvider).getCurrentDate();
        verify(transactionRepository).findLastTransaction(depositTransaction.accountId());
        verify(transactionRepository).saveTransaction(expectedTransaction);
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    @DisplayName("Deposit / Withdraw - Positive Amount Exception")
    void negativeTransactionAmount() {

        Exception exceptionDeposit = assertThrows(NegativeAmountException.class, () -> {
            accountService.deposit(accountId, new BigDecimal(-150));
        });
        Exception exceptionWithdraw = assertThrows(NegativeAmountException.class, () -> {
            accountService.withdraw(accountId, new BigDecimal(-663));
        });

        String expectedMessage = "The amount can't be negative.";
        String actualMessageDeposit = exceptionDeposit.getMessage();
        String actualMessageWithdraw = exceptionWithdraw.getMessage();

        assertTrue(actualMessageDeposit.contains(expectedMessage));
        assertTrue(actualMessageWithdraw.contains(expectedMessage));

        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    @DisplayName("Deposit / Withdraw - Account Existence Exception")
    void accountDoNOtExist() {

        when(transactionRepository.findLastTransaction(withdrawTransaction.accountId()))
                .thenReturn(Optional.ofNullable(null));

        Exception exceptionDeposit = assertThrows(AccountNonExistentException.class, () -> {
            accountService.deposit(accountId, new BigDecimal(150));
        });
        Exception exceptionWithdraw = assertThrows(AccountNonExistentException.class, () -> {
            accountService.withdraw(accountId, new BigDecimal(23));
        });

        String expectedMessage = "The account do not exist.";
        String actualMessageDeposit = exceptionDeposit.getMessage();
        String actualMessageWithdraw = exceptionWithdraw.getMessage();

        assertTrue(actualMessageDeposit.contains(expectedMessage));
        assertTrue(actualMessageWithdraw.contains(expectedMessage));

        verify(transactionRepository, times(2))
                .findLastTransaction(depositTransaction.accountId());
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    @DisplayName("Withdraw - Positive Balance Exception")
    void negativeTransactionBalance() {

        when(transactionRepository.findLastTransaction(withdrawTransaction.accountId()))
                .thenReturn(Optional.of(lastTransaction));

        Exception exceptionWithdraw = assertThrows(NegativeAmountException.class, () -> {
            accountService.withdraw(accountId, new BigDecimal(33));
        });

        String expectedMessage = "The account can't be negative for a withdrawal.";
        String actualMessageWithdraw = exceptionWithdraw.getMessage();

        assertTrue(actualMessageWithdraw.contains(expectedMessage));

        verify(transactionRepository).findLastTransaction(depositTransaction.accountId());
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    @DisplayName("Print transactions with balance - reverse order")
    void printableFormatter(){
        List<TransactionBO> transactions =
                List.of(lastTransaction, depositTransaction, withdrawTransaction);

        String expected = """
                AccountId                            | Date       | Type       | Amount (€) |
                afc4efb4-b30b-41a9-bd68-2373e54d2516 | 2007-12-25 | DEPOSIT    | 854        |
                afc4efb4-b30b-41a9-bd68-2373e54d2516 | 2007-12-23 | DEPOSIT    | 15         |
                afc4efb4-b30b-41a9-bd68-2373e54d2516 | 2007-12-22 | WITHDRAWAL | 11         |
                Balance (€) = 886""";

        when(transactionRepository.getHistory(accountId)).thenReturn(transactions);

        assertEquals(expected, accountService.printStatement(accountId, formatter));

        verify(transactionRepository).getHistory(accountId);
        verifyNoMoreInteractions(transactionRepository);
    }

}