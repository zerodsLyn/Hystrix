package com.netflix.hystrix;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.subjects.ReplaySubject;

public class HystrixCachedObservable<R> {
    protected final Subscription originalSubscription;

    /**
     * 对传入的Observable进行封装
     */
    protected final Observable<R> cachedObservable;

    private volatile int outstandingSubscriptions = 0;

    protected HystrixCachedObservable(final Observable<R> originalObservable) {
        // 使用 ReplaySubject 向传入的 Observable 发起订阅
        // 通过 ReplaySubject 能够重放执行结果 实现缓存的效果
        ReplaySubject<R> replaySubject = ReplaySubject.create();

        // 对于传入的originalObservable进行订阅后的Subscription，便于后面取消订阅
        this.originalSubscription = originalObservable.subscribe(replaySubject);

        this.cachedObservable = replaySubject
                .doOnUnsubscribe(new Action0() {
                    @Override
                    public void call() {
                        outstandingSubscriptions--;
                        if (outstandingSubscriptions == 0) {
                            originalSubscription.unsubscribe();
                        }
                    }
                })
                .doOnSubscribe(new Action0() {
                    @Override
                    public void call() {
                        outstandingSubscriptions++;
                    }
                });
    }

    public static <R> HystrixCachedObservable<R> from(Observable<R> o, AbstractCommand<R> originalCommand) {
        return new HystrixCommandResponseFromCache<R>(o, originalCommand);
    }

    public static <R> HystrixCachedObservable<R> from(Observable<R> o) {
        return new HystrixCachedObservable<R>(o);
    }

    public Observable<R> toObservable() {
        return cachedObservable;
    }

    public void unsubscribe() {
        originalSubscription.unsubscribe();
    }
}
