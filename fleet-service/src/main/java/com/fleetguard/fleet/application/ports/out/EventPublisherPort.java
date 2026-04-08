package com.fleetguard.fleet.application.ports.out;

public interface EventPublisherPort {

    void publish(Object event);
}