package com.example.playground;

import java.lang.ref.WeakReference;

class GarbageCollectionJava {

    public static void main(String[] args) {
        new GarbageCollectionJava().run();
    }

    private static class Repository {

        private WeakReference<Runnable> listener;

        public void setListener(Runnable listener) {
            this.listener = new WeakReference<>(listener);
        }

        public void doSg() {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    Runnable listener = Repository.this.listener.get();
                    if (listener != null) {
                        listener.run();
                    } else {
                        LoggerKt.log("missing listener");
                    }
                }
            }).start();
        }
    }

    private static class DisposableClass {

        private Repository repository;

        DisposableClass(Repository repository) {
            this.repository = repository;
        }

        public void callRepository() {
            repository.setListener(new Runnable() {
                @Override
                public void run() {
                    LoggerKt.log("task finished");
                }
            });
            repository.doSg();
            LoggerKt.log("method called");
        }

        @Override
        public void finalize() {
            LoggerKt.log("finalized");
        }

    }

    private Repository repository = new Repository();


    private void run() {
        new DisposableClass(repository).callRepository();
        System.gc();
    }

}
