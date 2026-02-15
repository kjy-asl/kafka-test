-- 이벤트 서비스 초기 스키마
CREATE TABLE events (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    template_id VARCHAR(50) NOT NULL,
    start_date DATE NOT NULL,
    end_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE event_conditions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NOT NULL,
    condition_type VARCHAR(50) NOT NULL,
    condition_value VARCHAR(100) NOT NULL,
    CONSTRAINT fk_event_conditions_events FOREIGN KEY (event_id) REFERENCES events(id)
);

CREATE TABLE processed_event (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    event_id BIGINT NULL,
    event_type VARCHAR(100) NOT NULL,
    event_key VARCHAR(100) NOT NULL,
    processed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_processed_event (event_type, event_key)
);
