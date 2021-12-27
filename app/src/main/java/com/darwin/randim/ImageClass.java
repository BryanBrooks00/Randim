package com.darwin.randim;

import android.app.WallpaperManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class ImageClass {

    private static final String TAG = "MY_LOG";

    protected Bitmap getBitmap(String url){
        Bitmap bitmap = DownloadImage(url);
        return bitmap;
    }

    private InputStream OpenHttpConnection(String urlString)
            throws IOException {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }

    private Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);

            setWallpaper(bitmap);
            in.close();
        } catch (IOException e1) {


            e1.printStackTrace();
        }
        return bitmap;
    }

    public void setWallpaper(Bitmap bitmap){
        WallpaperManager wManager;

        try {
            //bitmap = BitmapFactory.decodeFile(null);
            Context context = MainActivity.getContext();
            wManager = WallpaperManager.getInstance(context);
            wManager.setBitmap(bitmap);
            Log.i(TAG, "WALLPAPER SET");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
