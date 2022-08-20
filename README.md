# [인프런] 재고시스템으로 알아보는 동시성이슈 해결방법

[Inflearn Link](https://www.inflearn.com/course/%EB%8F%99%EC%8B%9C%EC%84%B1%EC%9D%B4%EC%8A%88-%EC%9E%AC%EA%B3%A0%EC%8B%9C%EC%8A%A4%ED%85%9C)

## 환경

- Docker MySql - port: 4306, schema: stock_concurrency

## 재고 감소 로직 개선

> StockService

### [v1] 동시성이 고려되지 않은 기초적인 로직

- 메서드 레벨에 `@Transactional` 사용.
- 다중 스레드 구성시 정상적인 재고 감소가 일어나지 않습니다.

### [v2] syncronized

- 메서드 레벨에 `syncronized` 사용.
- 다중 스레드 구성시 정상적인 재고 감소가 일어납니다.
- 하지만 `syncronized`는 같은 프로세스에서만 동시성을 보장하여 분산 환경에 적합하지 않습니다.

### [v3] pessimistic lock

- 메서드 레벨에 `@Transactional`, DB 조회시 `@Lock(LockModeType.PESSIMISTIC_WRITE)` 사용.
    - 트랜잭션 시작시 Shared Lock 또는 Exclusive Lock을 걸게 됩니다.
- 동시성 관리의 주체가 DB 이기에 정상적인 재고 감소가 일어납니다.
- 충돌이 잦을 것으로 예상되거나, 동시성을 강력하게 지켜야될 때 적합합니다. ex) 계좌 잔고
- 하지만 속도가 느리고 데드락의 위험이 있습니다.

### [v4] optimistic lock

- 메서드 레벨에 `@Transactional`, DB 조회시 `@Lock(LockModeType.OPTIMISTIC)` 사용.
    - version을 통해 애플리케이션 레벨에서 동시성을 관리합니다.
- 동시성 관리의 주체가 DB 이기에 정상적인 재고 감소가 일어납니다.
- 하지만 추가 개발 구현이 필요합니다.
    - version 관리를 테이블 변경.
    - version 충돌시 재시도 로직 구현.
    - DB 트랜잭션을 활용하지 않기에 롤백 직접 구현.

### pessimistic lock vs optimistic lock

- 충돌이 적은 경우 optimistic lock 이 빠르지만, 충돌이 많다면 pessimistic lock 이 더 빠릅니다.
    - 따라서 업무 성격을 분석한 후 선택해야 합니다.
- named lock은 생략합니다.
