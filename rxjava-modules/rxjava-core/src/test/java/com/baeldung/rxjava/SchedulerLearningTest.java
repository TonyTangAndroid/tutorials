package com.baeldung.rxjava;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.schedulers.Schedulers;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://akarnokd.blogspot.com/2017/11/when-multiple-subscribeons-do-have.html">Blog</a>
 */
public class SchedulerLearningTest {

    @Test
    public void justHasNoSubscriptionSideEffects() {
        Observable.just(Thread.currentThread().getName())
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .blockingSubscribe(System.out::println);
//        main
    }

    @Test
    public void deferHasSubscriptionSideEffects() {
        Observable.defer(()-> Observable.just(Thread.currentThread().getName()))
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .blockingSubscribe(System.out::println);
//        RxCachedThreadScheduler-1
    }

    @Test
    public void hasSubscriptionSideEffects() {
        Observable.create(SchedulerLearningTest::source)
                .subscribeOn(Schedulers.io())
                .subscribeOn(Schedulers.computation())
                .blockingSubscribe(System.out::println);
//        0: RxCachedThreadScheduler-1
//        1: RxCachedThreadScheduler-1
//        2: RxCachedThreadScheduler-1
//        3: RxCachedThreadScheduler-1
//        4: RxCachedThreadScheduler-1
//        5: RxCachedThreadScheduler-1
//        6: RxCachedThreadScheduler-1
//        7: RxCachedThreadScheduler-1
//        8: RxCachedThreadScheduler-1
//        9: RxCachedThreadScheduler-1
    }

    @Test
    public void singleSubscribeOn() {
        List<String> strings = Observable.create(SchedulerLearningTest::source)
                .subscribeOn(Schedulers.io())
                .collect(SchedulerLearningTest::init, List::add)
                .blockingGet();
        strings.forEach(System.out::println);
//        main
//        0: RxCachedThreadScheduler-1
//        1: RxCachedThreadScheduler-1
//        2: RxCachedThreadScheduler-1
//        3: RxCachedThreadScheduler-1
//        4: RxCachedThreadScheduler-1
//        5: RxCachedThreadScheduler-1
//        6: RxCachedThreadScheduler-1
//        7: RxCachedThreadScheduler-1
//        8: RxCachedThreadScheduler-1
//        9: RxCachedThreadScheduler-1

    }

    @Test
    public void multipleSubscribeOn() {
        List<String> strings =
                Observable.create(SchedulerLearningTest::source)
                        .subscribeOn(Schedulers.io())
                        .collect(SchedulerLearningTest::init, List::add)
                        .subscribeOn(Schedulers.computation())
                        .blockingGet();
        strings.forEach(System.out::println);
//        RxComputationThreadPool-1
//        0: RxCachedThreadScheduler-1
//        1: RxCachedThreadScheduler-1
//        2: RxCachedThreadScheduler-1
//        3: RxCachedThreadScheduler-1
//        4: RxCachedThreadScheduler-1
//        5: RxCachedThreadScheduler-1
//        6: RxCachedThreadScheduler-1
//        7: RxCachedThreadScheduler-1
//        8: RxCachedThreadScheduler-1
//        9: RxCachedThreadScheduler-1
    }

    private static void source(ObservableEmitter<String> emitter) {
        for (int i = 0; i < 10; i++) {
            emitter.onNext(i + ": " + Thread.currentThread().getName());
        }
        emitter.onComplete();
    }

    private static List<String> init() {
        List<String> list = new ArrayList<>();
        list.add(Thread.currentThread().getName());
        return list;
    }

}
