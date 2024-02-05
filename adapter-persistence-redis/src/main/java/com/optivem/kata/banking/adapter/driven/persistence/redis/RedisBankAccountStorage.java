package com.optivem.kata.banking.adapter.driven.persistence.redis;

import com.optivem.kata.banking.adapter.driven.base.ProfileNames;
import com.optivem.kata.banking.adapter.driven.persistence.redis.internal.BankAccountModel;
import com.optivem.kata.banking.adapter.driven.persistence.redis.internal.RedisBankAccountDataAccessor;
import com.optivem.kata.banking.adapter.driven.persistence.redis.mapper.BankAccountMapper;
import com.optivem.kata.banking.core.ports.driven.BankAccountDto;
import com.optivem.kata.banking.core.ports.driven.BankAccountStorage;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.function.Consumer;

@Component("RedisBankAccountStorage")
@Profile(ProfileNames.ADAPTER_PERSISTENCE_REDIS)
public class RedisBankAccountStorage implements BankAccountStorage {

    private final RedisBankAccountDataAccessor dataAccessor;
    private final BankAccountMapper bankAccountMapper;

    public RedisBankAccountStorage(RedisBankAccountDataAccessor dataAccessor, BankAccountMapper bankAccountMapper) {
        this.dataAccessor = dataAccessor;
        this.bankAccountMapper = bankAccountMapper;
    }

    @Override
    public Optional<BankAccountDto> find(String accountNumber) {
        var bankAccountModel = findByAccountNumber(accountNumber);

        if (bankAccountModel.isEmpty()) {
            return Optional.empty();
        }

        var bankAccountDto = bankAccountMapper.toDto(bankAccountModel.get());
        return Optional.of(bankAccountDto);
    }

    @Override
    public void add(BankAccountDto bankAccountDto) {
        var bankAccountModel = bankAccountMapper.toModel(bankAccountDto);
        dataAccessor.save(bankAccountModel);
    }

    @Override
    public void update(BankAccountDto bankAccountDto) {
        var bankAccountModelOptional = findByAccountNumber(bankAccountDto.getAccountNumber());
        bankAccountModelOptional.ifPresent(updateTheModelWithInformationFrom(bankAccountDto));
    }

    private Optional<BankAccountModel> findByAccountNumber(String accountNumber) {
        return dataAccessor.findByAccountNumber(accountNumber);
    }

    private Consumer<BankAccountModel> updateTheModelWithInformationFrom(BankAccountDto bankAccountDto) {
        return bankAccountModel -> {
            var updatedBankAccountModel = bankAccountMapper.toModel(bankAccountDto, bankAccountModel);
            dataAccessor.save(updatedBankAccountModel);
        };
    }
}
