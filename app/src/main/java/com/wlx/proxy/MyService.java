package com.wlx.proxy;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;



public class MyService extends Service {


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("wang", "service:" + getApplication());
        Log.i("wang", "service:" + getApplicationContext());
        Log.i("wang", "service:" + getApplicationInfo().className);
    }
}
