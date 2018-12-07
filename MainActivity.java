package com.cuhksz.learning;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = "cuhk-MainActivity";
    private String packageName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Get list of activities
        PackageInfo packageInfo =
                getPackageManager().getPackageArchiveInfo(getPackageResourcePath(),
                        PackageManager.GET_ACTIVITIES);

        this.packageName = packageInfo.packageName;

        ArrayList<String> names = new ArrayList<>();

        if (packageInfo != null) {
//            for (int i=0; i<packageInfo.activities.length; i++) {
            for (ActivityInfo activityInfo: packageInfo.activities) {
                String name = activityInfo.name;
                names.add(getActivityName(name));
                Log.i(TAG, name);
            }
        }

        // Populate the spinner with list of activity names
        Spinner spinner = (Spinner) findViewById(R.id.activity_list);
//        ArrayAdapter<String> adapter =
//                new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, names);
        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(this, R.layout.spinner_layout, R.id.spinnerTarget, names);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.spinner_layout);
        spinner.setAdapter(adapter);

        Log.i(TAG, "Done getting list of activities");

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume()");
    }

    private String getActivityName(String name) {
        String[] parts = name.split("\\.");
        String title = parts[parts.length-1];
        return title;
    }

    public void onButtonClick(View view) {
        Spinner spinner = (Spinner) findViewById(R.id.activity_list);
        String name = this.packageName + "." + spinner.getSelectedItem().toString();

        // Passing the class name to the selected activity
        Intent intent = new Intent(name);
        String[] parts = name.split("\\.");
        String title = parts[parts.length-1];
        intent.putExtra("title", title);

        // Using bundle to pass data
        Bundle bundle = new Bundle();
        bundle.putString("other", "Title from bundle");
        intent.putExtras(bundle);

        // startActivity(intent);
        startActivityForResult(intent, 99);
        Toast.makeText(this, "onButtonClick()", Toast.LENGTH_LONG).show();
    }


    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 99 && resultCode == RESULT_OK && data != null){
            TextView textView = (TextView) findViewById(R.id.main_title);
            textView.setText(data.getStringExtra("data"));
        }
    }
}
