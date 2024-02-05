package com.optivem.kata.banking.core;

import com.optivem.kata.banking.core.internal.cleanarch.acl.BankAccountRepositoryImpl;
import com.optivem.kata.banking.core.internal.cleanarch.acl.EventPublisherImpl;
import com.optivem.kata.banking.core.internal.cleanarch.domain.scoring.DefaultScoreCalculator;
import com.optivem.kata.banking.core.internal.cleanarch.usecases.DepositFundsUseCase;
import com.optivem.kata.banking.core.internal.cleanarch.usecases.OpenAccountUseCase;
import com.optivem.kata.banking.core.internal.cleanarch.usecases.ViewAccountUseCase;
import com.optivem.kata.banking.core.ports.driven.*;
import com.optivem.kata.banking.core.ports.driver.accounts.depositfunds.DepositFundsRequest;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountRequest;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountResponse;
import com.optivem.kata.banking.core.ports.driver.accounts.viewaccount.ViewAccountRequest;
import com.optivem.kata.banking.core.ports.driver.accounts.viewaccount.ViewAccountResponse;

public class Facade {

    private final DepositFundsUseCase depositFundsUseCase;
    private final OpenAccountUseCase openAccountUseCase;
    private final ViewAccountUseCase viewAccountUseCase;

    public Facade(NationalIdentityGateway nationalIdentityGateway, CustomerGateway customerGateway, AccountIdGenerator accountIdGenerator, AccountNumberGenerator accountNumberGenerator, DateTimeService dateTimeService, BankAccountStorage bankAccountStorage, EventBus eventBus) {
        var scoreCalculator = DefaultScoreCalculator.create(dateTimeService);
        var bankAccountRepository = new BankAccountRepositoryImpl(bankAccountStorage, accountIdGenerator, accountNumberGenerator);
        var eventPublisher = new EventPublisherImpl(eventBus);

        this.depositFundsUseCase = new DepositFundsUseCase(bankAccountRepository, dateTimeService, eventPublisher);
        this.openAccountUseCase = new OpenAccountUseCase(nationalIdentityGateway, customerGateway, bankAccountRepository, dateTimeService, eventPublisher);
        this.viewAccountUseCase = new ViewAccountUseCase(bankAccountRepository, scoreCalculator);
    }

    public void execute(DepositFundsRequest request) {
        depositFundsUseCase.handle(request);
    }

    public OpenAccountResponse execute(OpenAccountRequest request) {
        return openAccountUseCase.handle(request);
    }

    public ViewAccountResponse execute(ViewAccountRequest request) {
        return viewAccountUseCase.handle(request);
    }


}
