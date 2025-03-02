package com.grig.mytraining;
import com.jakewharton.threetenabp.AndroidThreeTen;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        AndroidThreeTen.init(this);

    }

    public static Context getAppContext() {
        return context;
    }
}