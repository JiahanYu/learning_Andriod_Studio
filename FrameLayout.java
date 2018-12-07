package com.cuhksz.learning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class FrameLayout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame_layout);

        String title = getIntent().getStringExtra("title");
        setTitle(title);
    }

    public void onButtonClick(View view) {
        Intent intent = new Intent();
        intent.putExtra("data", "FrameLayout");
        setResult(RESULT_OK, intent);
        finish();
    }
}
