package xyz.whatson.android;

import android.app.Application;
import android.content.Context;

public class WhatsOn extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        WhatsOn.context = getApplicationContext();
    }

    public static Context getAppContext() {
        return WhatsOn.context;
    }
}
