package com.example.chat.chat.config;

import java.util.List;

import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.chat.registration.Service.LocalUserService;

@Configuration
public class ConfigureRabbitMq {

    private final LocalUserService localUserService;

    public ConfigureRabbitMq(LocalUserService localUserService) {
        this.localUserService = localUserService;

    }

    @Bean
    public List<String> queueList() {
        return localUserService.getQueuesByUserId();

    }

    @Bean
    public SimpleRabbitListenerContainerFactory container(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(new Jackson2JsonMessageConverter());
        return factory;
    }

}