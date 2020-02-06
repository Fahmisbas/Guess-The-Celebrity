package com.fahmisbas.guessthecelebrity;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ImageView img_celeb;
    Button btn_0, btn_1, btn_2, btn_3;
    String html;
    Bitmap celebImage;
    ArrayList<String> celebNames = new ArrayList<String>();
    ArrayList<String> bitmapURLs = new ArrayList<String>();
    String[] answer = new String[4];
    int chosenCeleb, correctAnswerPos;


    public void chooseAnswer(View view) {
        if (view.getTag().toString().equals(String.valueOf(correctAnswerPos))) {
            Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Incorrect", Toast.LENGTH_SHORT).show();
        }

        setQuestion();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        img_celeb = findViewById(R.id.img_celeb);
        btn_0 = findViewById(R.id.btn_0);
        btn_1 = findViewById(R.id.btn_1);
        btn_2 = findViewById(R.id.btn_2);
        btn_3 = findViewById(R.id.btn_3);

        GetHtml getHtml = new GetHtml();

        try {
            html = getHtml.execute("http://www.posh24.se/kandisar").get();
            filter();
        } catch (Exception e) {
            e.printStackTrace();
        }

        setQuestion();


    }

    private void filter() {
        Pattern p = Pattern.compile("alt=\"(.*?)\"");
        Matcher m = p.matcher(html);

        while (m.find()) {
            celebNames.add(m.group(1));
        }

        p = Pattern.compile("img src=\"(.*?)\"");
        m = p.matcher(html);

        while (m.find()) {
            bitmapURLs.add(m.group(1));
        }
    }

    private void setQuestion() {

        try {
            Random random = new Random();
            CelebImageData imageData = new CelebImageData();
            chosenCeleb = random.nextInt(celebNames.size());
            celebImage = imageData.execute(bitmapURLs.get(chosenCeleb)).get();
            img_celeb.setImageBitmap(celebImage);

            correctAnswerPos = random.nextInt(4);
            for (int i = 0; i < 4; i++) {
                if (i == correctAnswerPos) {
                    answer[i] = celebNames.get(chosenCeleb);
                } else {
                    int incorrectAnswerPos = random.nextInt(celebNames.size());
                    while (incorrectAnswerPos == chosenCeleb) {
                        incorrectAnswerPos = random.nextInt(celebNames.size());
                    }
                    answer[i] = celebNames.get(incorrectAnswerPos);
                }
            }

            btn_0.setText(String.valueOf(answer[0]));
            btn_1.setText(String.valueOf(answer[1]));
            btn_2.setText(String.valueOf(answer[2]));
            btn_3.setText(String.valueOf(answer[3]));

            Log.i("names", Arrays.toString(answer));

        } catch (Exception e) {
            e.printStackTrace();
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

    public class GetHtml extends AsyncTask<String, Void, String> {

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
