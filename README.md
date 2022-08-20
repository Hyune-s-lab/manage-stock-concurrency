# [인프런] 재고시스템으로 알아보는 동시성이슈 해결방법

[Inflearn Link](https://www.inflearn.com/course/%EB%8F%99%EC%8B%9C%EC%84%B1%EC%9D%B4%EC%8A%88-%EC%9E%AC%EA%B3%A0%EC%8B%9C%EC%8A%A4%ED%85%9C)

## 환경

- Docker MySql - port: 4306, schema: stock_concurrency
- Docker Redis - port: 6379

## 테스트

![image](https://user-images.githubusercontent.com/55722186/185759295-1841e37c-9936-4c9b-a4b0-7ee3ea986a98.png)

- 테스트 케이스에서는 version 충돌이 많기에 optimistic lock 이 느립니다.

## 재고 감소 로직 기본

> StockService

### [v1] 동시성이 고려되지 않은 기초적인 로직

- 메서드 레벨에 `@Transactional` 사용.

### [v2] syncronized

- 메서드 레벨에 `syncronized` 사용.
- 하지만 `syncronized` 는 같은 프로세스에서만 동시성을 보장하여 분산 환경에 적합하지 않습니다.

### [v3] pessimistic lock

- 메서드 레벨에 `@Transactional`, DB 조회시 `@Lock(LockModeType.PESSIMISTIC_WRITE)` 사용.
    - 트랜잭션 시작시 Shared Lock 또는 Exclusive Lock 을 걸게 됩니다.
- 충돌이 잦을 것으로 예상되거나, 동시성을 강력하게 지켜야될 때 적합합니다. ex) 계좌 잔고
- 하지만 속도가 느리고 데드락의 위험이 있습니다.

### [v4] optimistic lock

- 메서드 레벨에 `@Transactional`, DB 조회시 `@Lock(LockModeType.OPTIMISTIC)` 사용.
    - version 을 통해 애플리케이션 레벨에서 동시성을 관리합니다.
- 하지만 추가 개발 구현이 필요합니다.
    - version 관리를 테이블 변경.
    - version 충돌시 재시도 로직 구현.
    - DB 트랜잭션을 활용하지 않기에 롤백 직접 구현.

### pessimistic lock vs optimistic lock

- 충돌이 적은 경우 optimistic lock 이 빠르지만, 충돌이 많다면 pessimistic lock 이 더 빠릅니다.
    - 따라서 업무 성격을 분석한 후 선택해야 합니다.
- named lock 은 생략합니다.

## 재고 감소 with 분산 락 (distributed lock)

### [v5] lettuce lock

- spin lock 방식을 활용하여 분산 락 획득.
- spin lock 은 sleep time 이 작을 수록 redis 에 더 큰 부하를 줍니다.
- lucette 는 Spring Data Redis 에서 인터페이스를 제공하기에 별도의 학습이 필요하지 않습니다.

### [v6] redisson lock

- pub-sub 방식을 활용하여 분산 락 획득.
- redis를 직접 제어하지 않고 별도의 인터페이스를 제공 합니다.
- leaseTime을 잘못 잡으면 작업 도중 lock 이 해제될 수 위험이 있습니다. - `IllegalMonitorStateException`

### lettuce lock vs redisson lock

- lucette 는 Spring Data Redis 에서 인터페이스를 제공하기에 별도의 학습이 필요하지 않습니다.
    - 하지만 redisson 은 별도의 인터페이스이기에 학습이 필요 합니다.
- lock 획득 실패 시 재시도가 필요하지 않은 경우는 luttuce 를, 재시도가 필요한 경우는 redisson 활용을 권장합니다.

## 참고 자료

- https://hyperconnect.github.io/2019/11/15/redis-distributed-lock-1.html
- https://devroach.tistory.com/83
    - redisson lock 사용시 leaseTime의 중요성 
