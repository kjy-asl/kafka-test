-- 주문 서비스 초기 스키마
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_number VARCHAR(50) NOT NULL UNIQUE,
    member_id BIGINT NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    product_id VARCHAR(50) NOT NULL,
    quantity INT NOT NULL,
    unit_price DECIMAL(15,2) NOT NULL,
    CONSTRAINT fk_order_items_orders FOREIGN KEY (order_id) REFERENCES orders(id)
);

CREATE TABLE order_outbox (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id CHAR(36) NOT NULL,
    event_type VARCHAR(100) NOT NULL,
    payload LONGTEXT NOT NULL,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    sent_at TIMESTAMP NULL,
    UNIQUE KEY uk_order_outbox_event (event_id)
);
