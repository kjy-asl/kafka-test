# test2 — Kafka 기반 MSA 학습 샘플

이 저장소는 **Order Service → Kafka → Inventory Service** 흐름을 가장 작은 단위로 묶어둔 연습용 프로젝트입니다. 각 모듈은 Spring Boot 기반으로 분리되어 있고, `common-events` 모듈이 공통 메시지 스키마를 제공합니다.

## 요구 사항

- Java 21 JDK (로컬 Gradle 작업용)
- Docker 24+ 및 Docker Compose v2 (컨테이너 실행용)
- (선택) `curl` 또는 Postman과 같은 HTTP 클라이언트

## 모듈 구조

| 모듈 | 설명 |
| --- | --- |
| `common-events` | 재사용 가능한 이벤트 모델(`OrderCreatedEvent`)과 토픽 상수 |
| `order-service` | REST API(`/api/orders`)로 주문을 접수하여 Kafka 토픽(`orders.created`)에 발행 |
| `inventory-service` | Kafka Consumer. 주문 이벤트를 구독하고 재고 시스템이 해야 할 일을 log로 대체 |

## 컨테이너 기반 실행 (권장 가상환경)

모든 서비스와 Kafka를 Docker Compose로 한꺼번에 띄울 수 있습니다.

```bash
docker compose up --build -d
```

- `order-service` 컨테이너는 8081 포트, `inventory-service` 컨테이너는 8082 포트를 호스트에 노출합니다.
- 내부 통신은 `msa-net` 브리지 네트워크에서 `kafka:9092` 주소를 사용합니다.
- 로그 확인: `docker compose logs -f order-service inventory-service`
- 종료: `docker compose down`

### 시나리오 테스트

Kafka와 서비스 컨테이너가 모두 `healthy` 상태가 되면 다음 명령으로 주문 이벤트를 발행할 수 있습니다.

```bash
curl -X POST http://localhost:8081/api/orders \
     -H 'Content-Type: application/json' \
     -d '{"productCode":"BOOK-001","quantity":2}'
```

`inventory-service` 로그에서 해당 이벤트를 수신했는지 확인하세요.

## 로컬 JVM 실행 (참고)

컨테이너 대신 JVM으로 직접 실행하고 싶다면 아래 순서를 따르면 됩니다.

1. **Kafka 인프라만 실행**
   ```bash
   docker compose up -d kafka
   ```
2. **애플리케이션 실행** (별도 터미널)
   ```bash
   ./gradlew :order-service:bootRun
   ./gradlew :inventory-service:bootRun
   ```
3. **테스트 호출**은 컨테이너 방식과 동일합니다.

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

- Docker Compose 기반 Kafka + Spring Boot 컨테이너 파이프라인
- Kafka Producer/Consumer 수동 설정(JsonSerializer/Deserializer)
- 레코드(Record) 기반 이벤트 모델링과 Bean Validation
- 서비스 간 코드 공유를 위한 multi-module Gradle 구성

## 다음 단계 아이디어

- Inventory 서비스에 RDB(or Redis)를 붙여 재고 차감 로직 구현
- API Gateway 혹은 Config Server 추가
- 주문/재고 이벤트에 tracing (OpenTelemetry) 붙이기
- Contract Test (Spring Cloud Contract)로 이벤트 스키마 안정성 검사
