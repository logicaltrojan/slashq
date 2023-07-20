package com.troy.slashq;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.reactivestreams.Processor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;


@Getter
@Slf4j
@RequiredArgsConstructor
public class CircularFifoQueuePipe<T> implements Processor<T, T> {


    private CircularFifoQueue<T> queue = new CircularFifoQueue<>(5);
    private Subscription subscription;
    private Subscriber<? super T> subscriber;

    @Override
    public void subscribe(Subscriber<? super T> s) {
        this.subscriber = s;
        s.onSubscribe(new Subscription() {
            @Override
            public void request(long n) {
                while (!queue.isEmpty() && n > 0) {
                    s.onNext(queue.poll());
                    n--;
                }
            }

            @Override
            public void cancel() {
            }
        });
    }

    @Override
    public void onSubscribe(Subscription s) {
        this.subscription = s;
        subscription.request(1);
    }

    @Override
    public void onNext(T t) {
        if (queue.isAtFullCapacity()) {
            //drop
            log.error("Dropped : {}", t);
        } else {
            queue.add(t);
        }
        subscription.request(1);
    }

    @Override
    public void onError(Throwable t) {
        subscription.request(1);
    }

    @Override
    public void onComplete() {

    }
}


