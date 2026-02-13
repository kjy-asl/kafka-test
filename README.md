# test2 — Kafka 기반 MSA 학습 샘플

이 저장소는 **Order Service → Kafka → Inventory Service** 흐름을 가장 작은 단위로 묶어둔 연습용 프로젝트입니다. 각 모듈은 Spring Boot 기반으로 분리되어 있고, `common-events` 모듈이 공통 메시지 스키마를 제공합니다.

## 모듈 구조

| 모듈 | 설명 |
| --- | --- |
| `common-events` | 재사용 가능한 이벤트 모델(`OrderCreatedEvent`)과 토픽 상수 |
| `order-service` | REST API(`/api/orders`)로 주문을 접수하여 Kafka 토픽(`orders.created`)에 발행 |
| `inventory-service` | Kafka Consumer. 주문 이벤트를 구독하고 재고 시스템이 해야 할 일을 log로 대체 |

## 빠른 시작

1. **Kafka 인프라 실행**
   ```bash
   docker compose up -d
   ```
2. **각 서비스 실행** (별도 터미널)
   ```bash
   ./gradlew :order-service:bootRun
   ./gradlew :inventory-service:bootRun
   ```
3. **주문 이벤트 발행 테스트**
   ```bash
   curl -X POST http://localhost:8081/api/orders \
        -H 'Content-Type: application/json' \
        -d '{"productCode":"BOOK-001","quantity":2}'
   ```
   Inventory 서비스 로그에서 이벤트 수신을 확인할 수 있습니다.

## GitHub 푸시 가이드

이미 로컬에서 GitHub 인증을 마쳤다면 다음 명령으로 초기 커밋을 만들 수 있습니다.

```bash
cd /Users/kjy/IdeaProjects/test2
git init # (기존 저장소가 없다면)
git add .
git commit -m "feat: add kafka-based msa skeleton"
git remote add origin git@github.com:<YOUR_ACCOUNT>/kafka-test.git
git push -u origin main
```

## 학습 포인트

- Docker Compose 로컬 Kafka 클러스터 구성
- Kafka Producer/Consumer 수동 설정(JsonSerializer/Deserializer)
- 레코드(Record) 기반 이벤트 모델링과 Bean Validation
- 서비스 간 코드 공유를 위한 multi-module Gradle 구성

## 다음 단계 아이디어

- Inventory 서비스에 RDB(or Redis)를 붙여 재고 차감 로직 구현
- API Gateway 혹은 Config Server 추가
- 주문/재고 이벤트에 tracing (OpenTelemetry) 붙이기
- Contract Test (Spring Cloud Contract)로 이벤트 스키마 안정성 검사
