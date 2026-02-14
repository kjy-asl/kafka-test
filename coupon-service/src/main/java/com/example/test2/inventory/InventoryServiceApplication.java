package com.example.test2.inventory;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Kafka 이벤트를 구독해 재고 변화를 추적하는 서비스입니다. 현재는 로그만 남기지만, 구조적으로는
 * 추후 DB 연동이나 실제 재고 계산 로직을 추가할 수 있도록 여지를 남겨두었습니다.
 */
@SpringBootApplication
public class InventoryServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InventoryServiceApplication.class, args);
    }
}
