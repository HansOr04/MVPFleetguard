package com.fleetguard.fleet.domain.model;

import com.fleetguard.fleet.domain.event.DomainEvent;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AggregateRootTest {

    static class TestAggregateRoot extends AggregateRoot {
    }

    static class TestDomainEvent implements DomainEvent {
        private final UUID id = UUID.randomUUID();

        public UUID getId() {
            return id;
        }
    }

    @Test
    void shouldStartWithNoDomainEvents() {
        TestAggregateRoot aggregate = new TestAggregateRoot();
        List<DomainEvent> events = aggregate.pullDomainEvents();
        assertTrue(events.isEmpty());
    }

    @Test
    void shouldAddDomainEvent() {
        TestAggregateRoot aggregate = new TestAggregateRoot();
        TestDomainEvent event = new TestDomainEvent();
        aggregate.addDomainEvent(event);
        List<DomainEvent> events = aggregate.pullDomainEvents();
        assertEquals(1, events.size());
        assertEquals(event, events.get(0));
    }

    @Test
    void shouldClearDomainEventsAfterPulling() {
        TestAggregateRoot aggregate = new TestAggregateRoot();
        aggregate.addDomainEvent(new TestDomainEvent());
        aggregate.addDomainEvent(new TestDomainEvent());
        List<DomainEvent> pulledEvents = aggregate.pullDomainEvents();
        List<DomainEvent> afterPull = aggregate.pullDomainEvents();
        assertEquals(2, pulledEvents.size());
        assertTrue(afterPull.isEmpty());
    }
}