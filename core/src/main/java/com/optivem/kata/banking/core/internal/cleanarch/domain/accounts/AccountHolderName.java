package com.optivem.kata.banking.core.internal.cleanarch.domain.accounts;

import com.optivem.kata.banking.core.ports.driver.exceptions.ValidationMessages;

import static com.optivem.kata.banking.core.internal.cleanarch.domain.common.Guard.guard;

public record AccountHolderName(Text firstName, Text lastName) {

    public AccountHolderName {
        guard(firstName).againstNullOrWhitespace(ValidationMessages.FIRST_NAME_EMPTY);
        guard(lastName).againstNullOrWhitespace(ValidationMessages.LAST_NAME_EMPTY);
    }

    public static AccountHolderName of(String firstName, String lastName) {
        return new AccountHolderName(Text.of(firstName), Text.of(lastName));
    }

    public Text getFullName() {
        return firstName().addSpace().add(lastName());
    }

    @Override
    public String toString() {
        return getFullName().value();
    }
}
