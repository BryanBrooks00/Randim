package com.darwin.randim;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

public class BackgroundService extends IntentService {
    private static final String API_KEY = "e11de7c416d222e8b12dbf5a735cadc6";
    private  static final  String SEARCH = "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=" + API_KEY + "&format=json&nojsoncallback=1&text=";
    private static final String SEARCH_METHOD = "flickr.photos.search";


    private static final String TAG = "MY_LOG";
    NotificationManager notificationManager;
    Intent intent;
    PendingIntent pendingIntent;
    final String channelId = "CHANNEL_ID";
    final int id = 1;
    int i = 0;

    public static Intent newIntent(Context context) {
        return new Intent(context, BackgroundService.class);
    }
    public BackgroundService() {
        super(TAG);
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        if (!isNetworkAvailableAndConnected()) {
            return;
        }
        Log.i(TAG, "Received an intent: " + intent);
        String tag = Preferences.getLastTag(this);
        if (tag == null) {
           Log.i(TAG, "Preferences = " + tag);
        } else {
            new Connection().buildUrl(SEARCH_METHOD, tag);
            Log.i(TAG, "Preferences = " + tag);
        }


    }

    private boolean isNetworkAvailableAndConnected() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        boolean isNetworkAvailable = cm.getActiveNetworkInfo() != null;
        boolean isNetworkConnected = isNetworkAvailable &&
                cm.getActiveNetworkInfo().isConnected();
        return isNetworkConnected;
    }

    @Override
    public void onDestroy() {
        Log.i(TAG, "Service was stop");
    }
}


