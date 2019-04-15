package com.example.yjo.coxld;

import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import com.example.yjo.coxld.Adapter.CustomAdapter;
import com.example.yjo.coxld.Helper.HttpDataHandler;
import com.example.yjo.coxld.model.ChatModelAI;
import com.example.yjo.coxld.model.SimSimiModel;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SimSimi extends AppCompatActivity {

    ListView listView;
    EditText editText;
    List<ChatModelAI>list_chat = new ArrayList<>();
    FloatingActionButton btn_send_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sim_simi);

        listView = (ListView)findViewById(R.id.list_of_message);//심심이
        editText = (EditText)findViewById(R.id.user_message);//유저 입력
        btn_send_message = (FloatingActionButton)findViewById(R.id.fab_send);

        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                ChatModelAI model = new ChatModelAI(text,true);//사용자 메세지 보내기
                list_chat.add(model);
                new SimSimAPI().execute(list_chat);
                //사용자 메세지 입력후 제거
                editText.setText("");
            }
        });
    }

    private class SimSimAPI extends AsyncTask<List<ChatModelAI>,Void,String>{

        String stream = null;
        List<ChatModelAI>models;
        String text = editText.getText().toString();


        @Override
        protected String doInBackground(List<ChatModelAI>... lists) {//ic = 언어
            String url = String.format("http://sandbox.api.simsimi.com/request.p?key=ef2328b0-d982-4b52-962f-318606280352&lc=ko&ft=1.0&text=hi",getString(R.string.simsimi_api),text);
            models = lists[0];
            HttpDataHandler httpDataHandler = new HttpDataHandler();
            stream = httpDataHandler.GetHTTPData(url);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            SimSimiModel response = gson.fromJson(s,SimSimiModel.class);
            ChatModelAI chatModelAI = new ChatModelAI(response.getResponse(),false);//심심이로 부터 응답 받기
            models.add(chatModelAI);
            CustomAdapter adapter = new CustomAdapter(models,getApplicationContext());
            listView.setAdapter(adapter);
        }
    }
}