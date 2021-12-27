package com.darwin.randim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    SharedPreferences sp;
    SharedPreferences.Editor editor;
    private static Context instance;
    String index;


    private static final String TAG = "MY_LOG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;

        Button btn_start = findViewById(R.id.btn_start);

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getUserData();
            }
        });

    }

    public void getUserData(){
        EditText tags_et = findViewById(R.id.tags_et);
        EditText time_et = findViewById(R.id.time_et);
        String tag = tags_et.getText().toString();
        String time = time_et.getText().toString();
        if (tag.equals("") || time.equals("")){
            makeToast(getResources().getString(R.string.no_text));
        }
        //savePreferences("time", time);
        savePreferences(tag, time);
        index = "0";
        Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);


    }

    public void makeToast(String text){
        Toast.makeText(this, text,
                Toast.LENGTH_LONG).show();
    }

    public void savePreferences(String tag, String time){
        Preferences.setLastTag(this, tag);
        Preferences.setLastTime(this, time);
        Preferences.setLastPhotoIndex(this, "0");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity destroyed");
    }

    public static Context getContext(){
        return instance;
        // or return instance.getApplicationContext();
    }
}