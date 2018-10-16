package xyz.whatson.android.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyReceiver extends BroadcastReceiver {

    public MyReceiver () {

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        MyNewIntentService.enqueueWork(context, new Intent());
    }
}

