package com.example.foodrecipes;


import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class AppExecutor {
    private static AppExecutor instance;

    public static AppExecutor getInstant() {
        if (instance == null)
            instance = new AppExecutor();
        return instance;
    }

    // Do Operation On The Cache(Insert Update Delete Retrive)
    private final Executor mDiskIO = Executors.newSingleThreadExecutor();

    // Do Operation On The MainThread
    private final Executor mMainThreadExecutor = new MainThreadExecutor();


    public Executor diskIO(){
        return mDiskIO;
    }

    public Executor mainThread(){
        return mMainThreadExecutor;
    }

    private static class MainThreadExecutor implements Executor{

        private Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        //Looper Is Class For Post Things to the Main thread
        // (if we have task on background thread and we want to post it in the main thread)

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}