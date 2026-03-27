package com.fleetguard.rulesalerts.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

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
    public Binding mileageRegisteredBinding(Queue mileageRegisteredQueue,
                                            TopicExchange fleetguardExchange) {
        return BindingBuilder.bind(mileageRegisteredQueue)
                .to(fleetguardExchange)
                .with(MILEAGE_ROUTING_KEY);
    }

    @Bean
    public MessageConverter jackson2JsonMessageConverter() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());                     // ✅ soporte LocalDateTime
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);  // ✅ formato ISO string

        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(mapper);

        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setIdClassMapping(Map.of(
                "com.fleetguard.fleet.domain.event.MileageRegistered",
                com.fleetguard.rulesalerts.infrastructure.messaging.event.MileageRegisteredEvent.class
        ));
        typeMapper.setTypePrecedence(DefaultJackson2JavaTypeMapper.TypePrecedence.TYPE_ID);
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
                                         MessageConverter jackson2JsonMessageConverter) {
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        template.setMessageConverter(jackson2JsonMessageConverter);
        return template;
    }
}