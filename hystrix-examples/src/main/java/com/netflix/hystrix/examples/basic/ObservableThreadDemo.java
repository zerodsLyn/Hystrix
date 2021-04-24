package com.netflix.hystrix.examples.basic;

import rx.Observable;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

import java.io.IOException;

/**
 * @description:
 * @author: gengchao
 * @create: 2021-04-23
 **/
public class ObservableThreadDemo {
    public static void main(String[] args) throws IOException {

        demo3();

    }

    private static void demo1() {
        Observable.just("long", "longer", "longest")
            .doOnNext(new Action1<String>() {
                @Override
                public void call(String c) {
                    System.out.println("processing item on thread " + Thread.currentThread().getName());
                }
            })
            .map(new Func1<String, Object>() {
                @Override
                public Object call(String s) {
                    return s.length();
                }
            })
            .subscribe(new Action1<Object>() {
                @Override
                public void call(Object length) {
                    System.out.println("item length " + length + " on thread " + Thread.currentThread().getName());
                }
            });
    }

    private static void demo2() throws IOException {
        Observable.just("long", "longer", "longest")
            .doOnNext(new Action1<String>() {
                @Override
                public void call(String c) {
                    System.out.println("processing item on thread " + Thread.currentThread().getName());
                }
            })
            .subscribeOn(Schedulers.newThread())
            .map(new Func1<String, Object>() {
                @Override
                public Object call(String s) {
                    return s.length();
                }
            })
            .subscribe(new Action1<Object>() {
                @Override
                public void call(Object length) {
                    System.out.println("item length " + length + " on thread " + Thread.currentThread().getName());
                }
            });

            System.in.read();
    }

    private static void demo3() throws IOException {
        Observable.just("long", "longer", "longest")
            .doOnNext(new Action1<String>() {
                @Override
                public void call(String c) {
                    System.out.println("processing item on thread " + Thread.currentThread().getName());
                }
            })
            // observable
            .subscribeOn(Schedulers.computation())
            .map(new Func1<String, Object>() {
                @Override
                public Object call(String s) {
                    return s.length();
                }
            })
            // 让observable在特定的线程上调用observer的接口
            .observeOn(Schedulers.newThread())
            .subscribe(new Action1<Object>() {
                @Override
                public void call(Object length) {
                    System.out.println("item length " + length + " on thread " + Thread.currentThread().getName());
                }
            });

        System.in.read();
    }
}
