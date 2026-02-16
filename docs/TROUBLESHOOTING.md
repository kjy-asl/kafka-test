# Troubleshooting Guide

## Kafka 연결 실패
- 증상: `org.apache.kafka.common.errors.TimeoutException` 등으로 이벤트 발행/소비 실패
- 조치: `docker compose ps`로 `kafka` 상태 확인 → 필요시 `docker compose logs kafka` 로 오류 확인 후 재기동(`docker compose down -v && docker compose up -d`).

## Flyway 마이그레이션 오류
- 증상: `FlywayValidateException` 혹은 "table already exists" 오류
- 조치: DB 컨테이너를 재생성하거나 해당 DB를 초기화. `docker compose down -v` 후 다시 `up` 하면 초기 상태로 재구축.

## 포트 충돌
- 증상: 서비스가 `Address already in use` 로 부팅 실패
- 조치: 해당 포트를 사용 중인 프로세스를 종료하거나 `docker compose`의 포트를 변경. (예: member-service 8081 → 8181)

## Kafka UI 접속 불가
- 증상: <http://localhost:8080> 접속 안 됨
- 조치: `docker compose logs kafka-ui` 확인 후 컨테이너 재시작. UI가 up되기 전에 Kafka 헬스체크가 실패하면 재기동 필요.

## Correlation ID 누락
- 증상: 로그에 correlationId 가 비어있음
- 조치: 요청 헤더에 `X-Correlation-Id`를 전달하거나, gateway 측에서 헤더를 추가. 내부 필터가 없는 서비스라면 `CorrelationIdFilter` 등록 상태 확인.
