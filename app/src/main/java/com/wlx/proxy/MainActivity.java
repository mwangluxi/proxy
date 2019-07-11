package com.wlx.proxy;

import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.i("wang","activity:"+getApplication());
        Log.i("wang","activity:"+getApplicationContext());
        Log.i("wang","activity:"+getApplicationInfo().className);

        startService(new Intent(this, MyService.class));

        Intent intent = new Intent("com.dongnao.broadcast.test");
        intent.setComponent(new ComponentName(getPackageName(), MyBroadCastReciver.class.getName
                ()));
        sendBroadcast(intent);

        getContentResolver().delete(Uri.parse("content://com.wlx.proxy.MyProvider"), null,
                null);
    }
}
