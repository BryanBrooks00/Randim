package com.darwin.randim;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

public class Connection {

    private static final String API_KEY = "e11de7c416d222e8b12dbf5a735cadc6";
    private static final Uri ENDPOINT = Uri
            .parse("https://api.flickr.com/services/rest/")
            .buildUpon()
            .appendQueryParameter("api_key", API_KEY)
            .appendQueryParameter("format", "json")
            .appendQueryParameter("nojsoncallback", "1")
            .appendQueryParameter("extras", "url_o")
            .build();

    private static final String TAG = "Connection.class";
    private String URL_INDEX = "url_o";
    int i = 0;


    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();

        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() + ": with " + urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }

    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }

    public String buildUrl(String method, String tag) {
        Uri.Builder uriBuilder = ENDPOINT.buildUpon()
                .appendQueryParameter("method", method);
        uriBuilder.appendQueryParameter("text", tag);
        Log.i(TAG, uriBuilder.build().toString());
        fetchItems(uriBuilder.build().toString());
        return uriBuilder.build().toString();
    }
    
    public List<GalleryItem> fetchItems(String str) {
        List<GalleryItem> items = new ArrayList<>();
        try {
            String url = Uri.parse(str)
                    .buildUpon()
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            parseItems(items, jsonBody, URL_INDEX);
        } catch (IOException exception) {
            Log.e(TAG, "Failed to load items", exception);
        } catch (JSONException je) {
            Log.e(TAG, "Failed to parse JSON", je);
        }
        return items;
    }

    private void parseItems(List<GalleryItem> items, JSONObject jsonBody, String URL_INDEX) throws IOException, JSONException {
        Context context = MainActivity.getContext();
        i = Integer.parseInt(Preferences.getLastPhotoIndex(context));
        JSONObject photosJsonObject = jsonBody.getJSONObject("photos");
        Log.i(TAG, "i = " + i);
        JSONArray photoJsonArray = photosJsonObject.getJSONArray("photo");
        JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
        GalleryItem item = new GalleryItem();
        item.setId(photoJsonObject.getString("id"));
        item.setCaption(photoJsonObject.getString("title"));
        if (!photoJsonObject.has(URL_INDEX)) {
            Log.i(TAG, "!photoJsonObject.has(" + URL_INDEX + ")");
            i++;
            Preferences.setLastPhotoIndex(context, i + "");
            parseItems(items, jsonBody, URL_INDEX);
        } else {
            item.setUrl(photoJsonObject.getString(URL_INDEX));
            items.add(item);
            Log.i(TAG, item.getUrl());
            i++;
            Preferences.setLastPhotoIndex(context, i + "");
            new ImageClass().getBitmap(item.getUrl());
        }
    }
    }
