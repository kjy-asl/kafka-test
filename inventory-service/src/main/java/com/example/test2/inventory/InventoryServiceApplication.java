package com.example.test2.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Inventory service keeps track of product stock levels by reacting to Kafka events. For now it
 * simply logs the events but the structure leaves room for you to add database logic later.
 */
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
