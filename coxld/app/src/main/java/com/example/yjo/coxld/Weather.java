package com.example.yjo.coxld;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class Weather extends AppCompatActivity {

    TextView t1_temp,t2_city,t3_description,t4_date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        t1_temp = (TextView)findViewById(R.id.weather_textview);
        t2_city = (TextView)findViewById(R.id.weather_textview2);
        t3_description = (TextView)findViewById(R.id.weather_textview3);
        t4_date = (TextView)findViewById(R.id.weather_textview4);

        find_weather();
    }

    public void find_weather(){

        String uri ="";
    }
}
