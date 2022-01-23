package com.darwin.randim;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private static Context instance;
    String index = "0";
    private static final String TAG = "MainActivity.class";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn_start = (Button) findViewById(R.id.btn_start);
        Button btn_stop =  (Button) findViewById(R.id.btn_stop);
        instance = this;
        checkState();

        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmptyText()) {
                    makeToast(getResources().getString(R.string.no_text));
                }else{
                    Preferences.clearPreferences(getContext(), "1");
                    getUserData();
                    checkState();
                    //makeToast(getResources().getString(R.string.tip));
                }
            }
        });


        btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService(new Intent(getContext(), BackgroundService.class));
                new BackgroundService().setServiceAlarm(getApplicationContext(), false);
                Preferences.clearPreferences(getContext(), "0");
                checkState();
            }
        });
    }

    public void getUserData() {
        EditText tags_et = findViewById(R.id.tags_et);
        EditText time_et = findViewById(R.id.time_et);
        String tag = tags_et.getText().toString();
        String time = time_et.getText().toString().replace(".", "");
            savePreferences(tag, time);
            Intent intent = new Intent(getApplicationContext(), BackgroundService.class);
            getApplicationContext().startService(intent);
        BackgroundService.setServiceAlarm(getApplicationContext(), true);
    }

    public void savePreferences(String tag, String time){
        Preferences.setLastTag(this, tag);
        Preferences.setLastTime(this, time);
        Preferences.setLastPhotoIndex(this, index);
    }

    public void checkState(){
        Button btn_start =  (Button) findViewById(R.id.btn_start);
        Button btn_stop = (Button) findViewById(R.id.btn_stop);
        EditText tags_et = findViewById(R.id.tags_et);
        EditText time_et = findViewById(R.id.time_et);
        index = Preferences.getStateIndex(this);

        if (Preferences.getStateIndex(this) == null || index.equals("0")) {
            btn_start.setVisibility(View.VISIBLE);
            btn_stop.setVisibility(View.INVISIBLE);
            tags_et.setText("");
            time_et.setText("");
        } else {
            btn_start.setVisibility(View.INVISIBLE);
            btn_stop.setVisibility(View.VISIBLE);
            String tag = Preferences.getLastTag(getContext());
            String time = Preferences.getLastTime(getContext());
            tags_et.setText(tag);
            time_et.setText(time);

        }
            Log.i(TAG, "STATE INDEX = " + index );
    }

    public void makeToast(String text){
        Toast.makeText(this, text,
                Toast.LENGTH_LONG).show();
    }

    public Boolean isEmptyText(){
        EditText tags_et = findViewById(R.id.tags_et);
        EditText time_et = findViewById(R.id.time_et);
        String tag = tags_et.getText().toString();
        String time = time_et.getText().toString();
        return tag.equals("") || time.equals("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "MainActivity destroyed");
    }

    public static Context getContext(){
        return instance;
    }
}
