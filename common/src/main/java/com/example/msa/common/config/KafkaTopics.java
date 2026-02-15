package com.example.msa.common.config;

/**
 * 서비스들이 공통으로 사용하는 Kafka 토픽 이름 모음입니다. 단일 관리 지점을 두면
 * 이벤트 스트림에 참여하는 서비스가 많아져도 리팩터링이 훨씬 안전해집니다.
 */
public final class KafkaTopics {

    public static final String ORDER_CREATED_V1 = "order.created.v1";
    public static final String EVENT_QUALIFIED_V1 = "event.qualified.v1";
    public static final String COUPON_ISSUED_V1 = "coupon.issued.v1";

    private KafkaTopics() {
    }
}
