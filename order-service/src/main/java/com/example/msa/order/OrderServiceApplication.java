package com.example.msa.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 주문 서비스 진입점입니다. 모듈을 실행하면 주문을 접수해서 Kafka로 전달하는 간단한 REST API가 열리고,
 * 다른 서비스가 해당 이벤트를 토대로 후속 작업을 진행할 수 있습니다.
 */
@SpringBootApplication
@EnableScheduling
public class OrderServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderServiceApplication.class, args);
    }
}
