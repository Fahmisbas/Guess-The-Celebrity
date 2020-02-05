package com.fahmisbas.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.widget.Button;
import android.widget.ImageView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView img_celeb;
    Button btn_0, btn_1, btn_2, btn_3;
    String celebData;
    Bitmap celebImages;
    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> imageLinks = new ArrayList<String>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_celeb = findViewById(R.id.img_celeb);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);

        CelebWebData celebWebData = new CelebWebData();
        CelebImageData imageData = new CelebImageData();
        try {
            celebData = celebWebData.execute("http://www.posh24.se/kandisar").get();
            filterNameImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void filterNameImage() {
        Pattern p = Pattern.compile("alt=\"(.*?)\"");
        Matcher m = p.matcher(celebData);

        while (m.find()) {
            names.add(m.group(1));
        }

        p = Pattern.compile("src=\"(.*?)\"");
        m = p.matcher(celebData);

        while (m.find()) {
            imageLinks.add(m.group(1));
        }
    }

    public class CelebImageData extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... urls) {

            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();
                InputStream in = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(in);

                return bitmap;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }


        }
    }

    public class CelebWebData extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while (data != -1) {
                    char current = (char) data;

                    result += current;
                    data = reader.read();
                }
                return result;
            } catch (Exception e) {
                e.printStackTrace();
                return "Failed";
            }
        }
    }
}
