package xyz.whatson.android.services;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.JobIntentService;
import android.support.v4.app.NotificationManagerCompat;

import xyz.whatson.android.R;
import xyz.whatson.android.activities.MyEventsActivity;
import xyz.whatson.android.activities.detail.ViewEventActivity;

//needs to be JobIntentService to allow it to run when app is in background or closed
public class MyNewIntentService extends JobIntentService {
    private static final int NOTIFICATION_ID = 3;
    public static final int JOB_ID = 123;

    String CHANNEL_ID = "channel_id";

    public MyNewIntentService() {

    }

    public static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, MyNewIntentService.class, JOB_ID, work);
    }


    //this function adapted from
    // https://developer.android.com/training/notify-user/channels#java
    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            String name = "channel name";
            String description = "channel description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    protected void onHandleWork(Intent intent) {



        createNotificationChannel();
        Notification.Builder builder;

        //splitting it out because newer than API 26 Notification.Builder needs channel ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder = new Notification.Builder(this, CHANNEL_ID);
        } else {
            builder = new Notification.Builder(this);
        }




        Intent notifyIntent = new Intent(this, MyEventsActivity.class);  //open up "my events" feed

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 2, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentTitle("You have an event starting in 15 minutes!");
        builder.setContentText("Click to see your events feed");

        builder.setSmallIcon(R.drawable.ic_notif_icon);
        builder.setShowWhen(true);
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);



        //to be able to launch your activity from the notification
        builder.setContentIntent(pendingIntent);
        Notification notificationCompat = builder.build();
        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
        managerCompat.notify(NOTIFICATION_ID, notificationCompat);
    }
}