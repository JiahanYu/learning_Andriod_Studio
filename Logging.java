package com.cuhksz.learning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class Logging extends AppCompatActivity {

    private final static String TAG = "cuhk-logging";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logging);
        Log.i(TAG, "onCreate()");
        // Extracting data directly using intent
        String title = getIntent().getStringExtra("title");

        // Extracting data using bundle
        Bundle bundle = getIntent().getExtras();

        Log.i(TAG, "title via intent: " + title);
        Log.i(TAG, "title via bundle: " + bundle.getString("title"));

        setTitle(title);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart()");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart()");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onCreate()");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "onPause()");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy()");
    }
}
