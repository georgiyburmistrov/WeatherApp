package com.example.myapplication;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText userField;
    private Button mainButton;
    private TextView resultInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate() is done");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userField = findViewById(R.id.userField);
        mainButton = findViewById(R.id.mainButton);
        resultInfo = findViewById(R.id.resultInfo);

        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(userField.getText().toString().trim().equals("")) {
                    Toast.makeText(MainActivity.this, R.string.noInput, Toast.LENGTH_LONG).show();
                } else {
                    String city = userField.getText().toString().trim();

                    String key = "5c549b504cae54a0c1bf447603ba80bd";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetURLData().execute(url);
                }
            }
        });
    }

    private class GetURLData extends AsyncTask<String, String, String> {

        protected void onPreExecute(){
            super.onPreExecute();
            resultInfo.setText("Мы загружеам информацию " + "\uD83C\uDF10");
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d(TAG, "doInBackground() is done");
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while((line = reader.readLine()) != null){
                    buffer.append(line).append("\n");
                }
                return buffer.toString();

            } catch (IOException e) {
                throw new RuntimeException(e);
            } finally {
                if(connection != null){
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result){
            Log.d(TAG, "onPostExecute() is done");
            super.onPostExecute(result);

            try {
                JSONObject jsonObject = new JSONObject(result);
                String weather = jsonObject.getJSONArray("weather").getJSONObject(0).getString("description");
                weather = weather.substring(0, 1).toUpperCase() + weather.substring(1);
                resultInfo.setText(weather + "\n" +
                         "Температура воздуха: " + jsonObject.getJSONObject("main").getDouble("temp") + " \u00B0" + "C" + "\n" +
                        "Ощущается как: " + jsonObject.getJSONObject("main").getDouble("feels_like") + " \u00B0" + "C" + "\n");

            } catch (JSONException e) {
                throw new RuntimeException(e);
            }


        }
    }
}