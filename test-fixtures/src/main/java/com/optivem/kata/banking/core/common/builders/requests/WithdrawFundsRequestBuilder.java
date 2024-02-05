package com.optivem.kata.banking.core.common.builders.requests;

import com.optivem.kata.banking.core.ports.driver.accounts.withdrawfunds.WithdrawFundsRequest;

public class WithdrawFundsRequestBuilder {

    private static final String DEFAULT_ACCOUNT_NUMBER = "GB51BARC20031816295685";
    private static final int DEFAULT_AMOUNT = 300;

    public static WithdrawFundsRequestBuilder withdrawFundsRequest() {
        return new WithdrawFundsRequestBuilder();
    }

    private String accountNumber;
    private int amount;

    public WithdrawFundsRequestBuilder() {
        this.accountNumber = DEFAULT_ACCOUNT_NUMBER;
        this.amount = DEFAULT_AMOUNT;
    }

    public WithdrawFundsRequestBuilder withAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
        return this;
    }

    public WithdrawFundsRequestBuilder withAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public WithdrawFundsRequest build() {
        return WithdrawFundsRequest.builder()
                .accountNumber(accountNumber)
                .amount(amount)
                .build();
    }
}
