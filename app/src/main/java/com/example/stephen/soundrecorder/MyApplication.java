package com.example.stephen.soundrecorder;

import android.app.Application;
import android.content.Context;

/**
 * Created by stephen on 17-8-9.
 */

public class MyApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext(){
        return context;
    }
}
