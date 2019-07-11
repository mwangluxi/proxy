package com.wlx.proxy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class MyBroadCastReciver extends BroadcastReceiver {


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("wang", "reciver:" + context);
        Log.i("wang","reciver:" + context.getApplicationContext());
        Log.i("wang","reciver:" + context.getApplicationInfo().className);

    }
}
