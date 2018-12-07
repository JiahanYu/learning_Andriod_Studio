package com.cuhksz.learning;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudentActivity_final extends AppCompatActivity {

    final static String api_ip = "http://10.20.12.23:8080/";

    Map<Integer,Bitmap> studentPhotos;

    private class Student {
        public String ID;
        public String EName;
        public String CName;
        public String Pinyin;
        public String School;
        public String Major;
        public String SID;
        public String Shape;
        public ImageViewMasked imageView;

        Student(String ID, String CName, String EName, String Pinyin,
                String School, String Major, String SID, String Shape) {
            this.ID = ID;
            this.CName = CName;
            this.EName = EName;
            this.Pinyin = Pinyin;
            this.School = School;
            this.Major = Major;
            this.SID = SID;
            this.Shape = Shape;
        }
    }

    List<Student> studentList;

    private class studentAdapter extends ArrayAdapter<Student> {


        public studentAdapter(@NonNull Context context, @LayoutRes int resource, @NonNull List<Student> objects) {
            super(context, resource, objects);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            Log.v("cuhksz:getView()", ""+position);

            View view = convertView;
            if (view == null){
                LayoutInflater inflater = (LayoutInflater)
                        StudentActivity_final.this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                view = inflater.inflate(R.layout.custom_student, null);
            }

            view.setTag(position);
            ((TextView) view.findViewById(R.id.cname)).setText(studentList.get(position).CName);
            ((TextView) view.findViewById(R.id.ename)).setText(studentList.get(position).EName);
            ((TextView) view.findViewById(R.id.pinyin)).setText(studentList.get(position).Pinyin);
            String study = studentList.get(position).School + "/" + studentList.get(position).Major;
            ((TextView) view.findViewById(R.id.school)).setText(study);

            ImageViewMasked imageView = (ImageViewMasked) view.findViewById(R.id.photo);
            studentList.get(position).imageView = imageView;

            if (studentPhotos.containsKey(position))
                UpdateStudentPhoto(position, studentPhotos.get(position));
            else
                new DownloadImageTask(position, view).
                        execute(api_ip + "photo?name=" + studentList.get(position).SID);

            return view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);

        studentList = new ArrayList<Student>();
        studentPhotos = new HashMap<>();

        //new DownloadImageTask().execute("http://10.20.15.6:8080/photo?name=114020091.JPG");
        new DownloadJSONTask().execute(api_ip + "student");
}

    private void ConfigureListView() {
        ListView listView = (ListView) findViewById(R.id.listview);

        listView.setAdapter(new studentAdapter(this,-1,studentList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(StudentActivity_final.this, "You have selected " + studentList.get(position).CName,
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ParseStudentJSON(String data) {
        try {
            JSONObject jobj = new JSONObject(data);
            JSONArray students = jobj.getJSONArray("students");

            for (int i=0; i < students.length(); i++) {
                JSONObject student = students.getJSONObject(i);
                String id = student.getString("id");
                String ename = student.getString("ename");
                String cname = student.getString("cname");
                String pinyin = student.getString("pinyin");
                String school = student.getString("school");
                String sid = student.getString("sid");
                String shape = student.getString("shape");
                String major = student.getString("major");
                Student studentObject = new Student(id, cname, ename, pinyin, school, major, sid, shape);
                studentList.add(studentObject);
            }

            Log.i("cuhk:student count", String.valueOf(studentList.size()));

            ConfigureListView();

        } catch (Exception ex) {
            Log.e("cuhk:ex", ex.toString());
        }
    }

    private void UpdateStudentPhoto(int position, Bitmap photo)
    {
        ImageViewMasked img = (ImageViewMasked) studentList.get(position).imageView;

        String shape = "shape_" + studentList.get(position).Shape;
        int drawableID = getResources().getIdentifier(shape, "drawable", getPackageName());
        if (drawableID != 0 && drawableID != img.getMaskDrawableID())
            img.setMaskDrawableID(drawableID);

        img.setCurrentBitmap(photo);
//        if (studentPhotos.containsKey(position) == false)
//            studentPhotos.put(position, photo);
    }

    private InputStream OpenHttpConnection(String urlString) throws IOException{

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            return conn.getInputStream();
        }
        return null;
    }


    private String getJSON(String url) {
        try {
            InputStream conn = OpenHttpConnection(url);

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(conn));

            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            return response.toString();

        } catch (Exception ex) {
            Log.e("cuhk:ex", ex.toString());
        }
        return "";
    }

    private Bitmap getBitmap(String url) {
        Bitmap bitmap = null;
        try {
            InputStream conn = OpenHttpConnection(url);
            bitmap = BitmapFactory.decodeStream(conn);
            conn.close();
        } catch (Exception ex) {
            Log.e("cuhk:ex", ex.toString());
        }
        return  bitmap;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        int position;
        View view;

        DownloadImageTask(int position, View view) {
            this.position = position;
            this.view = view;
        }
        protected Bitmap doInBackground(String... urls) {
            return getBitmap(urls[0]);
        }
        protected void onPostExecute(Bitmap result) {
            int tag = (Integer) this.view.getTag();
            Log.v("cuhksz:onPostExecute()", tag+":"+position);
            if (tag == this.position) {
                UpdateStudentPhoto(position, result);
            }
        }
    }

    private class DownloadJSONTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return getJSON(urls[0]);
        }
        protected void onPostExecute(String result) {
            Log.v("cuhk:", result);
            ParseStudentJSON(result);
        }
    }

}
