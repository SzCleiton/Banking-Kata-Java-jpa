package com.optivem.kata.banking.core.internal.cleanarch.domain.common.events;

import java.time.LocalDateTime;

public interface DomainEvent {
    LocalDateTime getTimestamp();
}
