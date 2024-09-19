package com.example.chat.chat.service;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

import org.springframework.stereotype.Service;

import com.example.chat.chat.dto.MessageDto;
import com.example.chat.chat.model.Queue;
import com.example.chat.chat.rabbitmq.ProduceMessageService;
import com.example.chat.chat.repository.QueueRepository;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class QueueService {

    private final QueueRepository queueRepository;

    private final ProduceMessageService produceMessageService;

    public Queue createQueue(String name, String exchange, String routingKey) throws IOException, TimeoutException {
        // Create a connection factory
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost"); // Set your RabbitMQ server host
        factory.setUsername("guest"); // Set your RabbitMQ username
        factory.setPassword("guest"); // Set your RabbitMQ password

        // Create a connection
        try (Connection connection = factory.newConnection()) {
            // Create a channel
            try (Channel channel = connection.createChannel()) {
                // Declare a direct exchange
                String exchangeType = "direct";
                boolean durable = true;
                channel.exchangeDeclare(exchange, exchangeType, durable);

                // Declare a queue
                boolean queueDurable = true;
                boolean exclusive = false;
                boolean autoDelete = false;
                channel.queueDeclare(name, queueDurable, exclusive, autoDelete, null);

                // Bind the queue to the exchange with a routing key
                channel.queueBind(name, exchange, routingKey);
                log.info("Queue created successfully with name: {}", name);
            } catch (Exception e) {
                // log.error("Error creating queue: {}", e.getMessage());
                throw e;

            }
        } catch (IOException | TimeoutException e) {
            // log.error("Error creating queue: {}", e.getMessage());
            throw e;
        }

        Queue queue = new Queue(name, exchange, routingKey);
        queueRepository.save(queue);
        // System.out.println("queue : " + queue);
        return queue;
    }

    /***************************************************************************************************** */

    public void deleteQueueFromRabbitMq(String name) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        factory.setUsername("guest");
        factory.setPassword("guest");
        try (Connection connection = factory.newConnection()) {
            try (Channel channel = connection.createChannel()) {
                channel.queueDelete(name);
                log.info("Queue deleted successfully with name: {}", name);
            }
        } catch (IOException | TimeoutException e) {
            // log.error("Error deleting queue: {}", e.getMessage());
            throw e;
        }
    }

    /********************************************************************************************************** */
    public void deleteQueue(Integer Id) {

        queueRepository.deleteFromListenersById(Id);
        queueRepository.deleteById(Id);
        
    }
    /********************************************************************************************************** */
    public Queue getQueue(String name) {
        return queueRepository.findByName(name).orElse(null);

    }

    /********************************************************************************************************** */

    public void sendMessage(Queue queue, MessageDto message, String messageType) throws IOException, TimeoutException {

        produceMessageService.produceMessage(queue, message, messageType);

    }

    /********************************************************************************************************** */
}
