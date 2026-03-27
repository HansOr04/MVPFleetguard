package com.fleetguard.rulesalerts.infrastructure.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "fleetguard.exchange";
    public static final String MILEAGE_ROUTING_KEY = "mileage.registered";
    public static final String MILEAGE_QUEUE = "mileage.registered.queue";

    @Bean
    public TopicExchange fleetguardExchange() {
        return new TopicExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue mileageRegisteredQueue() {
        return new Queue(MILEAGE_QUEUE, true);
    }

    @Bean
    public Binding mileageRegisteredBinding(Queue mileageRegisteredQueue, TopicExchange fleetguardExchange) {
        return BindingBuilder.bind(mileageRegisteredQueue)
                .to(fleetguardExchange)
                .with(MILEAGE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
