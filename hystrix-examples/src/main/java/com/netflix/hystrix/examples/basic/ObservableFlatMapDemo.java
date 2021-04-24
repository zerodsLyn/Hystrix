package com.netflix.hystrix.examples.basic;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @description:
 * @author: gengchao
 * @create: 2021-04-23
 **/
public class ObservableFlatMapDemo {
    public static void main(String[] args) {
        List<Course> xCourses = new ArrayList<Course>();
        xCourses.add(new Course("英语"));
        xCourses.add(new Course("数学"));

        List<Course> yCourses = new ArrayList<Course>();
        xCourses.add(new Course("语文"));
        xCourses.add(new Course("自然"));

        Student[] students = new Student[] {
            new Student("xx", xCourses),
            new Student("yy", yCourses)
        };

        Subscriber<Course> subscriber = new Subscriber<Course>() {
            @Override
            public void onStart() {
                System.out.println("started");
            }

            @Override
            public void onCompleted() {
                System.out.println("completed");
            }

            @Override
            public void onError(Throwable e) {
                e.printStackTrace();
            }

            @Override
            public void onNext(Course course) {
                System.out.println(course.getName());
            }
        };


        Observable.from(students)
            .flatMap(new Func1<Student, Observable<Course>>() {
                @Override
                public Observable<Course> call(Student student) {
                    return Observable.from(student.getCourses());
                }
            })
            .subscribe(subscriber);
    }

    private static class Course {
        private String name;

        public Course(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    private static class Student {
        private String name;

        private List<Course> courses;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }

        public Student(String name, List<Course> courses) {
            this.name = name;
            this.courses = courses;
        }
    }
}


