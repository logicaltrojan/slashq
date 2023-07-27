


# Slashq 

- Message processing demo inspired by below conference
- Specific implementation can be different
- slash 23 (https://toss.im/slash-23/session-detail/B1-7)

![img.png](img.png)

## Feature

- Message Listening will be handled by netty io thread (default cpu)
- Message processing will be handled by dedicated single thread

## Not implemented

- Data Store with async -> can be implemente through reactive redis template

## Performance

- 비동기는 단일 요청의 입장에서는 어떤것도 빠르게 만들지는 않는다. Async never makes something faster...
- 다수의 request에서 평균 wait시간이 감속하는 효과
- Kafka가 성능이 느린건 I/O가 ... 아무리 kafka read/write이 효율이 좋다고 하지만 file i/o의 한계인듯

### Why

- why single thread executor
  - sequence matters
- why policy is drop last
  - urgency matters

### Try?

- Sequence matters -> always single thread?
  - parallel and sort ?
- parallel 처리 후 sort 한다면 .. nlgn
- sort can be O(1) if it has range, and we have (timestamp)
  - Map<TimeStamp, Record>
- caveat
  - timestamp can have multiple records
  - But many threads will access in parallel to update record
  - *ConcurrentHashMap* is disaster in performance

### Tips

- TheadPoolExecutor -> core 수 설정하더라도 executor task가 들어오지 않으면 spawn 하지 않음
- Spring에서 제공하는 TaskExecutor가 아닌, jdk Executor를 사용할 경우 아래와 같이 warm up 코드가 존재하는 것이 좋음

```java
this.threadPoolExecutor.execute(()->log.info("Thread ready :{}",Thread.currentThread().getName()));

```