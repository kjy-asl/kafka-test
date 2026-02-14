-- 쿠폰 서비스 초기 스키마
CREATE TABLE coupon_template (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_code VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    discount_amount DECIMAL(15,2) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE coupons (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    template_id BIGINT NOT NULL,
    member_id BIGINT NOT NULL,
    correlation_id VARCHAR(50) NOT NULL,
    issued_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status VARCHAR(20) NOT NULL,
    UNIQUE KEY uk_coupon_unique (member_id, template_id, correlation_id),
    CONSTRAINT fk_coupons_template FOREIGN KEY (template_id) REFERENCES coupon_template(id)
);

CREATE TABLE processed_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_type VARCHAR(100) NOT NULL,
    event_key VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_coupon_processed (event_type, event_key)
);
