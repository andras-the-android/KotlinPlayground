package com.example.playground;

public class GenericsJava {


    private class Parent {}

    private class Basic extends Parent {}

    private class Child extends Basic {}

    private class ClassOut<T extends Basic> {

        private T value;

        T getT() {
            return value;
        }

        // this is illegal in kotlin
        void setT(T value) {
            this.value = value;
        }
    }

    // compile error: Parent is not a subclass of Basic
    // ClassOut<Parent> classOut = new ClassOut<>();

    // using wildcard to make ClassOut<Basic> and ClassOut<Child> covariant
    private ClassOut<? extends Basic> classOut = new ClassOut<Child>();

    private void testClassOut() {
        ClassOut<Child> classOutChild = new ClassOut<>();
        classOutChild.setT(new Child());
        classOutChild.getT();

        ClassOut<? extends Basic> classOutBasic = classOutChild;
        // compile error: can't call setter because of the extends wildcard
        // classOutBasic.setT(new Basic());
        classOutBasic.getT();
    }

    public static void main(String[] args) {
        new GenericsJava().testClassOut();
        LoggerKt.log("finished");
    }

}
