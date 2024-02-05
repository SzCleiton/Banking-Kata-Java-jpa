package com.optivem.kata.banking.core.common.factories;

import an.awesome.pipelinr.Command;
import com.optivem.kata.banking.adapter.driven.generation.fake.FakeAccountIdGenerator;
import com.optivem.kata.banking.adapter.driven.generation.fake.FakeAccountNumberGenerator;
import com.optivem.kata.banking.core.ports.driven.*;
import com.optivem.kata.banking.core.ports.driver.VoidResponse;
import com.optivem.kata.banking.core.ports.driver.accounts.depositfunds.DepositFundsRequest;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountRequest;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountResponse;
import com.optivem.kata.banking.core.ports.driver.accounts.withdrawfunds.WithdrawFundsRequest;

public interface UseCaseFactory {

    Command.Handler<OpenAccountRequest, OpenAccountResponse> createOpenAccountHandler(NationalIdentityGateway nationalIdentityGateway, CustomerGateway customerGateway, BankAccountStorage bankAccountStorage,
                                                                                      AccountIdGenerator accountIdGenerator, AccountNumberGenerator accountNumberGenerator, DateTimeService dateTimeService, EventBus eventBus);

    Command.Handler<DepositFundsRequest, VoidResponse> createDepositFundsHandler(BankAccountStorage bankAccountStorage, AccountIdGenerator accountIdGenerator, AccountNumberGenerator accountNumberGenerator, DateTimeService dateTimeService, EventBus eventBus);

    Command.Handler<WithdrawFundsRequest, VoidResponse> createWithdrawFundsHandler(BankAccountStorage bankAccountStorage, AccountIdGenerator accountIdGenerator, AccountNumberGenerator accountNumberGenerator, DateTimeService dateTimeService, EventBus eventBus);
}
