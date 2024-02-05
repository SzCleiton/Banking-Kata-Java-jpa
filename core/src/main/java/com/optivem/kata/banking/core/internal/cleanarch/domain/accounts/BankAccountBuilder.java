package com.optivem.kata.banking.core.internal.cleanarch.domain.accounts;

import java.time.LocalDate;

public class BankAccountBuilder {
    public static BankAccountBuilder bankAccount() {
        return new BankAccountBuilder();
    }
    private AccountId accountId;
    private AccountNumber accountNumber;
    private String nationalIdentityNumber;
    private AccountHolderName accountHolderName;
    private LocalDate openingDate;
    private Balance balance;

    public BankAccountBuilder withAccountId(AccountId accountId) {
        this.accountId = accountId;
        return this;
    }

    public BankAccountBuilder withAccountNumber(AccountNumber accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public BankAccountBuilder withNationalIdentityNumber(String nationalIdentityNumber) {
        this.nationalIdentityNumber = nationalIdentityNumber;
        return this;
    }

    public BankAccountBuilder withAccountHolderName(AccountHolderName accountHolderName) {
        this.accountHolderName = accountHolderName;
        return this;
    }

    public BankAccountBuilder withOpeningDate(LocalDate openingDate) {
        this.openingDate = openingDate;
        return this;
    }

    public BankAccountBuilder withBalance(Balance balance) {
        this.balance = balance;
        return this;
    }

    public BankAccount build() {
        return new BankAccount(accountId, accountNumber, nationalIdentityNumber, accountHolderName, openingDate, balance);
    }


}
