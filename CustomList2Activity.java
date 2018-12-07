package com.cuhksz.learning;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class CustomList2Activity extends AppCompatActivity {

    String[] userid = {
            "Yellow", "Red", "Blue", "LightBlue", "Black", "Grey",
            "Orange", "Purple", "Brown", "White", "Green", "LightGreen",
            "Red", "Pink", "Chocolate"
    };

    String[] msg = {
            "where were you?", "I am in school", "Blue", "LightBlue", "Black", "Grey",
            "Orange", "Purple", "Brown", "White", "Green", "LightGreen",
            "Red", "Pink", "Chocolate"
    };


    int[] img = {
            R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5,
            R.drawable.sample_1, R.drawable.sample_2, R.drawable.sample_3, R.drawable.sample_4, R.drawable.sample_5
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_list);

        setTitle(getIntent().getStringExtra("title"));

        ArrayAdapter<String> stringList = new ArrayAdapter<String>(this,-1, userid) {
            @NonNull
            @Override
            public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

                Log.v("cuhksz:", "getView():" + position);

                View view;
                if (convertView == null)
                {
                    LayoutInflater inflater = (LayoutInflater)
                            CustomList2Activity.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    view = inflater.inflate(R.layout.custom_list2, null);

                }
                else
                {
                    view = convertView;
                }

                ((TextView) view.findViewById(R.id.userid)).setText(userid[position]);
                ((TextView) view.findViewById(R.id.message)).setText(msg[position]);
                ImageView imageView = (ImageView) view.findViewById(R.id.userPhoto);
                imageView.setImageDrawable(getResources().getDrawable(img[position]));

                return view;
            }
        };

        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(stringList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(CustomList2Activity.this, "You have selected " + userid[position], Toast.LENGTH_SHORT).show();
            }
        });
    }
}
