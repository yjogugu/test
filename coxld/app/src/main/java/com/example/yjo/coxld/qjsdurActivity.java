package com.example.yjo.coxld;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.model.Model;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class qjsdurActivity extends AppCompatActivity {

    Button btTranslate;
    EditText etSource;
    TextView tvResult;
    TextView tvResult1;
    EditText etSource1;
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qjsdur);

        etSource = (EditText) findViewById(R.id.et_source);
        tvResult = (TextView) findViewById(R.id.tv_result);
        btTranslate = (Button) findViewById(R.id.bt_translate);
        etSource1 = (EditText)findViewById(R.id.et_source1);
        tvResult1 = (TextView)findViewById(R.id.tv_result1);
        button = (Button)findViewById(R.id.qjsdur);
        final TextView tv = (TextView)findViewById(R.id.textView1);
        Spinner s = (Spinner)findViewById(R.id.spinner1);
        final TextView tv2 = (TextView)findViewById(R.id.text2);
        s.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(final AdapterView<?> parent, View view, final int position, long id) {
                final int a=position;

                    btTranslate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(etSource.getText().toString().length() == 0) {
                                Toast.makeText(qjsdurActivity.this, "번역할 내용을 입력하세요.", Toast.LENGTH_SHORT).show();
                                etSource.requestFocus();
                                return;
                            }
                            /*if(0) {
                                NaverTranslateTask asyncTask = new NaverTranslateTask();
                                String sText = etSource.getText().toString();
                                asyncTask.execute(sText);
                            }
                            else if(equals(a)){
                                NaverTranslateTask1 asyncTask1 = new NaverTranslateTask1();
                                String sText1 = etSource1.getText().toString();
                                asyncTask1.execute(sText1);
                            }*/

                        }
                    });

                    //position=0;




                    tv2.setText("호");
                   /* button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NaverTranslateTask1 asyncTask1 = new NaverTranslateTask1();
                            String sText1 = etSource1.getText().toString();
                            asyncTask1.execute(sText1);
                        }
                    });*/
                    //position=0;



                tv.setText("position : " + parent.getItemAtPosition(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });



    }



    public class NaverTranslateTask  extends AsyncTask<String, Void, String> {

        public String resultText;
        String clientId = "WOmVDHhfdjYl1xGQpzV7";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "YEliDNt0eT";//애플리케이션 클라이언트 시크릿값";
        String sourceLang = "en";
        String sourceLang1 = "ja";
        String sourceLang2 = "zh-CN";
        String targetLang = "ko";



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {
            //네이버제공 예제 복사해 넣자.
            //Log.d("AsyncTask:", "1.Background");

            try {
                //String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
                String text = URLEncoder.encode(etSource.getText().toString(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
                String postParams1 = "source="+sourceLang1+"&target="+targetLang+"&text=" + text;
                String postParams2 = "source="+sourceLang2+"&target="+targetLang+"&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                //System.out.println(response.toString());
                //tvResult.setText(response.toString());
                return response.toString();

            } catch (Exception e) {
                //System.out.println(e);
                return null;
            }

        }


        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //결과를 tvResult 뷰에 집어 넣는다.
            //Gson을 사용할 것이다.

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s.toString())
                    //원하는 데이터 까지 찾아 들어간다.
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            //안드로이드 객체에 담기
            TranslatedItem items = gson.fromJson(rootObj.toString(), TranslatedItem.class);
            //Log.d("AsyncTask:", "2.PostExecute");
            tvResult.setText(items.getTranslatedText());
        }


    }
    public class TranslatedItem {
        String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }
    }


    public class NaverTranslateTask1  extends AsyncTask<String, Void, String> {

        public String resultText;
        String clientId = "WOmVDHhfdjYl1xGQpzV7";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "YEliDNt0eT";//애플리케이션 클라이언트 시크릿값";
        String sourceLang = "ko";
        String targetLang = "en";


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        //AsyncTask 메인처리
        @Override
        protected String doInBackground(String... strings) {
            //네이버제공 예제 복사해 넣자.
            //Log.d("AsyncTask:", "1.Background");

            try {
                //String text = URLEncoder.encode("만나서 반갑습니다.", "UTF-8");
                String text = URLEncoder.encode(etSource1.getText().toString(), "UTF-8");
                String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
                URL url = new URL(apiURL);
                HttpURLConnection con = (HttpURLConnection)url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("X-Naver-Client-Id", clientId);
                con.setRequestProperty("X-Naver-Client-Secret", clientSecret);
                // post request
                String postParams = "source="+sourceLang+"&target="+targetLang+"&text=" + text;
                con.setDoOutput(true);
                DataOutputStream wr = new DataOutputStream(con.getOutputStream());
                wr.writeBytes(postParams);
                wr.flush();
                wr.close();
                int responseCode = con.getResponseCode();
                BufferedReader br;
                if(responseCode==200) { // 정상 호출
                    br = new BufferedReader(new InputStreamReader(con.getInputStream()));
                } else {  // 에러 발생
                    br = new BufferedReader(new InputStreamReader(con.getErrorStream()));
                }
                String inputLine;
                StringBuffer response = new StringBuffer();
                while ((inputLine = br.readLine()) != null) {
                    response.append(inputLine);
                }
                br.close();
                //System.out.println(response.toString());
                //tvResult.setText(response.toString());
                return response.toString();

            } catch (Exception e) {
                //System.out.println(e);
                return null;
            }

        }


        //번역된 결과를 받아서 처리
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            //결과를 tvResult 뷰에 집어 넣는다.
            //Gson을 사용할 것이다.

            Gson gson = new GsonBuilder().create();
            JsonParser parser = new JsonParser();
            JsonElement rootObj = parser.parse(s.toString())
                    //원하는 데이터 까지 찾아 들어간다.
                    .getAsJsonObject().get("message")
                    .getAsJsonObject().get("result");
            //안드로이드 객체에 담기
            TranslatedItem1 items = gson.fromJson(rootObj.toString(), TranslatedItem1.class);
            //Log.d("AsyncTask:", "2.PostExecute");
            //tvResult1.setText(items.getTranslatedText());
            etSource1.setText(items.getTranslatedText());

        }


    }
    private class TranslatedItem1 {
        String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }
    }





}




