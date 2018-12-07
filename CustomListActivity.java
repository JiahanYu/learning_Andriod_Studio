package com.cuhksz.learning;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class CustomListActivity extends AppCompatActivity {

    String[] colors = {
            "Yellow", "Red", "Blue", "LightBlue", "Black", "Grey",
            "Orange", "Purple", "Brown", "White", "Green", "LightGreen",
            "Red", "Pink", "Chocolate"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        setTitle(getIntent().getStringExtra("title"));

        ArrayAdapter<String> stringList = new ArrayAdapter<String>(this,
                R.layout.custom_list, R.id.userid, colors);

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(stringList);
//        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
//        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CustomListActivity.this, "You have selected " + colors[position], Toast.LENGTH_SHORT).show();
                Intent data = new Intent();
                data.putExtra("data",colors[position].toString());
                setResult(RESULT_OK, data);
                finish();
            }
        });
    }
}
