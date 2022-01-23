package com.darwin.randim;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.util.concurrent.TimeUnit;

public class BackgroundService extends Service {
    private static final String SEARCH_METHOD = "flickr.photos.search";
    private static final String TAG = "BackgroundService.class";
    public static long INTERVAL_MS = TimeUnit.MINUTES.toMillis(1);

    public static Intent newIntent(Context context) {
        return new Intent(context, BackgroundService.class);
    }

    protected void startJob() {
        Thread thread = new Thread() {
            public void run() {

                Log.i(TAG, "Service was started");
                if (!isNetworkAvailableAndConnected()) {
                    Log.i(TAG, "Network is not available ");
                }
                String tag = Preferences.getLastTag(MainActivity.getContext());
                if (tag == null) {
                    Log.i(TAG, "NULL Preferences = " + tag);
                } else {
                    new Connection().buildUrl(SEARCH_METHOD, tag);
                    Log.i(TAG, "Preferences = " + tag);
                }
            }
        };
        thread.start();
    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    public static void setServiceAlarm(Context context, boolean isOn) {
        INTERVAL_MS = Long.parseLong(Preferences.getLastTime(context)) * 1000 * 60;
        Intent i = BackgroundService.newIntent(context);
        PendingIntent pi = PendingIntent.getService(context, 0, i, 0);
        AlarmManager alarmManager = (AlarmManager)
                context.getSystemService(Context.ALARM_SERVICE);
        if (isOn) {
            alarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
                    System.currentTimeMillis(), INTERVAL_MS, pi);
            Log.i(TAG, "AlarmManager set repeating");
        } else {
            alarmManager.cancel(pi);
            pi.cancel();
            Log.i(TAG, "AlarmManager was canceled");
        }
    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startJob();
        return BackgroundService.START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(R.drawable.notification_icon)
                    .setContentTitle(getResources().getString(R.string.app_name))
                    .setContentText(getResources().getString(R.string.notification)).build();

            startForeground(1, notification);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i(TAG, "onTaskRemoved");
        setServiceAlarm(getApplicationContext(), true);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service was stop");
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
