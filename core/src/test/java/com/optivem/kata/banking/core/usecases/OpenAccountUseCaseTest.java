package com.optivem.kata.banking.core.usecases;

import an.awesome.pipelinr.Command;
import com.optivem.kata.banking.adapter.driven.generation.fake.FakeAccountIdGenerator;
import com.optivem.kata.banking.adapter.driven.generation.fake.FakeAccountNumberGenerator;
import com.optivem.kata.banking.adapter.driven.messaging.fake.FakeEventBus;
import com.optivem.kata.banking.adapter.driven.microservice.fake.FakeCustomerGateway;
import com.optivem.kata.banking.adapter.driven.persistence.fake.FakeBankAccountStorage;
import com.optivem.kata.banking.adapter.driven.thirdparty.fake.FakeNationalIdentityGateway;
import com.optivem.kata.banking.adapter.driven.time.fake.FakeDateTimeService;
import com.optivem.kata.banking.core.common.builders.ports.driven.BankAccountDtoTestBuilder;
import com.optivem.kata.banking.core.common.factories.CleanArchUseCaseFactory;
import com.optivem.kata.banking.core.ports.driven.events.AccountOpenedDto;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountRequest;
import com.optivem.kata.banking.core.ports.driver.accounts.openaccount.OpenAccountResponse;
import com.optivem.kata.banking.core.ports.driver.exceptions.ValidationMessages;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.stream.Stream;

import static com.optivem.kata.banking.core.common.Verifications.verifyThat;
import static com.optivem.kata.banking.core.common.builders.requests.OpenAccountRequestBuilder.openAccountRequest;
import static com.optivem.kata.banking.core.common.data.MethodSources.NEGATIVE_INTEGERS;
import static com.optivem.kata.banking.core.common.data.MethodSources.NULL_EMPTY_WHITESPACE;

class OpenAccountUseCaseTest {
    private FakeNationalIdentityGateway nationalIdentityProvider;
    private FakeCustomerGateway customerProvider;
    private FakeBankAccountStorage storage;
    private FakeAccountIdGenerator accountIdGenerator;
    private FakeAccountNumberGenerator accountNumberGenerator;
    private FakeDateTimeService dateTimeService;
    private FakeEventBus eventBus;

    private Command.Handler<OpenAccountRequest, OpenAccountResponse> useCase;

    private static Stream<Arguments> should_open_account_given_valid_request() {
        return Stream.of(Arguments.of("ABC", "John", "Smith", 0, 3456, "GB41OMQP68570038161775", LocalDate.of(2022, 5, 20)),
                Arguments.of("DEF", "Mary", "McDonald", 50, 735353, "GB36BMFK75394735916876", LocalDate.of(2021, 6, 15)));
    }

    @BeforeEach
    void init() {
        this.nationalIdentityProvider = new FakeNationalIdentityGateway();
        this.customerProvider = new FakeCustomerGateway();
        this.storage = new FakeBankAccountStorage();
        this.accountIdGenerator = new FakeAccountIdGenerator();
        this.accountNumberGenerator = new FakeAccountNumberGenerator();
        this.dateTimeService = new FakeDateTimeService();
        this.eventBus = new FakeEventBus();

        // TODO: VC: Make configurable so that we can run same test twice
        // var useCaseFactory = new CrudUseCaseFactory();
        var useCaseFactory = new CleanArchUseCaseFactory();
        this.useCase = useCaseFactory.createOpenAccountHandler(nationalIdentityProvider, customerProvider, storage, accountIdGenerator, accountNumberGenerator, dateTimeService, eventBus);
    }

    @ParameterizedTest
    @MethodSource
    void should_open_account_given_valid_request(String nationalIdentityNumber, String firstName, String lastName, int initialBalance, long generatedAccountId, String generatedAccountNumber, LocalDate openingDate) {
        nationalIdentityProvider.setupExistent(nationalIdentityNumber);
        accountIdGenerator.setupNext(generatedAccountId);
        accountNumberGenerator.setupNext(generatedAccountNumber);
        dateTimeService.setupNow(LocalDateTime.of(openingDate, LocalTime.MIN));

        var request = openAccountRequest()
                .withNationalIdentityNumber(nationalIdentityNumber)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withBalance(initialBalance)
                .build();

        var expectedResponse = OpenAccountResponse.builder()
                .accountNumber(generatedAccountNumber)
                .build();

        var expectedBankAccount = BankAccountDtoTestBuilder.bankAccount()
                .withAccountId(generatedAccountId)
                .withAccountNumber(generatedAccountNumber)
                .withNationalIdentityNumber(nationalIdentityNumber)
                .withFirstName(firstName)
                .withLastName(lastName)
                .withOpeningDate(openingDate)
                .withBalance(initialBalance)
                .build();

        var expectedEvent = AccountOpenedDto.builder()
                .timestamp(LocalDateTime.of(openingDate, LocalTime.MIN))
                .accountId(generatedAccountId)
                .firstName(firstName)
                .lastName(lastName)
                .balance(initialBalance)
                .build();

        verifyThat(useCase).withRequest(request).shouldReturnResponse(expectedResponse);

        storage.shouldContain(expectedBankAccount);
        eventBus.shouldHavePublishedExactly(expectedEvent);
    }

    @ParameterizedTest
    @MethodSource(NULL_EMPTY_WHITESPACE)
    void should_throw_exception_given_empty_national_identity_number(String nationalIdentityNumber) {
        accountIdGenerator.setupNext(1001L);
        accountNumberGenerator.setupNext("1-0-0-1");
        dateTimeService.setupNow(LocalDateTime.of(LocalDate.of(2021, 6, 15), LocalTime.MIN));

        var request = openAccountRequest()
                .withNationalIdentityNumber(nationalIdentityNumber)
                .build();

        verifyThat(useCase).withRequest(request).shouldThrowValidationException(ValidationMessages.NATIONAL_IDENTITY_NUMBER_EMPTY);
    }

    @Test
    void should_throw_exception_given_nonexistent_national_identity_number() {
        accountIdGenerator.setupNext(1001L);
        accountNumberGenerator.setupNext("1-0-0-1");
        dateTimeService.setupNow(LocalDateTime.of(LocalDate.of(2021, 6, 15), LocalTime.MIN));

        var request = openAccountRequest()
                .build();

        verifyThat(useCase).withRequest(request).shouldThrowValidationException(ValidationMessages.NATIONAL_IDENTITY_NUMBER_NONEXISTENT);
    }

    @Test
    void should_throw_exception_given_blacklisted_national_identity_number() {
        var nationalIdentityNumber = "NAT_1001";
        nationalIdentityProvider.setupExistent(nationalIdentityNumber);
        customerProvider.setupBlacklisted(nationalIdentityNumber);
        accountIdGenerator.setupNext(1001L);
        accountNumberGenerator.setupNext("1-0-0-1");
        dateTimeService.setupNow(LocalDateTime.of(LocalDate.of(2021, 6, 15), LocalTime.MIN));

        var request = openAccountRequest()
                .withNationalIdentityNumber(nationalIdentityNumber)
                .build();

        verifyThat(useCase).withRequest(request).shouldThrowValidationException(ValidationMessages.NATIONAL_IDENTITY_NUMBER_BLACKLISTED);
    }

    @ParameterizedTest
    @MethodSource(NULL_EMPTY_WHITESPACE)
    void should_throw_exception_given_empty_first_name(String firstName) {
        var request = openAccountRequest()
                .withFirstName(firstName)
                .build();

        verifyThat(useCase).withRequest(request).shouldThrowValidationException(ValidationMessages.FIRST_NAME_EMPTY);
    }

    @ParameterizedTest
    @MethodSource(NULL_EMPTY_WHITESPACE)
    void should_throw_exception_given_empty_last_name(String lastName) {
        var request = openAccountRequest()
                .withLastName(lastName)
                .build();

        verifyThat(useCase).withRequest(request).shouldThrowValidationException(ValidationMessages.LAST_NAME_EMPTY);
    }

    @ParameterizedTest
    @MethodSource(NEGATIVE_INTEGERS)
    void should_throw_exception_given_negative_balance(int balance) {
        var request = openAccountRequest()
                .withBalance(balance)
                .build();

        verifyThat(useCase).withRequest(request).shouldThrowValidationException(ValidationMessages.BALANCE_NEGATIVE);
    }
}
