# Kafka 기반 MSA 이벤트-쿠폰 학습 프로젝트

주문 생성 → Kafka 이벤트 발행 → 이벤트 조건 평가 → 쿠폰 발급 → 회원 귀속까지의 흐름을 학습용으로 구현한 멀티 모듈 Gradle 프로젝트입니다.

## 1. 시스템 구성

### 모듈
| 모듈 | 설명 |
| --- | --- |
| `common` | 이벤트 DTO, Kafka 토픽 상수, Correlation ID 필터/헬퍼 |
| `order-service` | 주문 생성 API + Outbox → `order.created.v1` 발행 |
| `event-service` | 주문 이벤트 소비, 조건 평가(생일/상품/금액) → `event.qualified.v1` 발행 |
| `coupon-service` | 자격 이벤트 소비, 쿠폰 발급 멱등 처리 → `coupon.issued.v1` 발행 |
| `member-service` | 쿠폰 발급 이벤트 소비, `member_coupon` 업데이트 |

### 인프라
- Kafka(KRaft) + Kafka UI
- 서비스별 MySQL 4개 (member/order/event/coupon)
- 모든 서비스를 한 번에 띄우는 docker-compose 제공

## 2. 실행 방법

### Docker Compose (권장)
```bash
docker compose up -d --build
```
- Kafka UI: <http://localhost:8080>
- member/order/event/coupon 서비스: 각각 8081/8082/8083/8084
- 종료: `docker compose down -v`

### 로컬 JVM 실행
1. Kafka만 실행: `docker compose up -d kafka`
2. 서비스 부팅:
   ```bash
   ./gradlew :member-service:bootRun &
   ./gradlew :order-service:bootRun &
   ./gradlew :event-service:bootRun &
   ./gradlew :coupon-service:bootRun &
   ```
3. MySQL은 로컬에 띄우거나 docker-compose DB를 재사용

## 3. 시나리오 예시
1. 회원/이벤트 시드 생성 (예: POST `/members`, `/events/seed`) *(추가 구현 예정)*
2. 주문 생성:
   ```bash
   curl -X POST http://localhost:8082/api/orders \
        -H 'Content-Type: application/json' \
        -d '{
              "memberId": 1,
              "items": [
                {"productId":"BOOK-001","quantity":2,"unitPrice":10000}
              ]
            }'
   ```
3. 이벤트/쿠폰/회원 서비스 로그에서 이벤트 전파 과정을 확인

## 4. 테스트 준비
- 추후 Testcontainers 기반 E2E 테스트 추가 예정

## 5. 트러블슈팅
- Kafka 연결 오류 → docker-compose에서 kafka 컨테이너 상태 확인 (`docker compose logs kafka`)
- Flyway 실패 → 각 서비스 DB 초기화 후 재실행 (`docker compose down -v && docker compose up -d`)

## 6. TODO
- member/event API 시드 엔드포인트 구현
- 통합 테스트 추가(Testcontainers)
- README에 더 상세한 이벤트 JSON 예시 및 트러블슈팅 확장
