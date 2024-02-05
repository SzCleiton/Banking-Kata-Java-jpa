package com.optivem.kata.banking.core.internal.cleanarch.acl;

import com.optivem.kata.banking.core.internal.cleanarch.domain.accounts.FundsDeposited;
import com.optivem.kata.banking.core.ports.driven.events.FundsDepositedDto;

public class FundsDepositedConverter {
    public static FundsDepositedDto fromEvent(FundsDeposited event) {
        return FundsDepositedDto.builder()
                .timestamp(event.getTimestamp())
                .accountNumber(event.getAccountNumber().value().toString())
                .amount(event.getAmount().value().value())
                .build();
    }
}
