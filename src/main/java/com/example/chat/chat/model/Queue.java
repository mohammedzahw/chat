package com.example.chat.chat.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
public class Queue {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String name;
    private String exchange;

    private String routingKey;

    

    public Queue(String name, String exchange, String routingKey) {
        this.name = name;
        this.exchange = exchange;
        this.routingKey = routingKey;
    }

}
