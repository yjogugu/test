package com.example.yjo.coxld.chat;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.yjo.coxld.Main2Activity;
import com.example.yjo.coxld.R;
import com.example.yjo.coxld.model.ChatModel;
import com.example.yjo.coxld.model.User;
import com.example.yjo.coxld.qjsdurActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

public class MassgeActivity extends AppCompatActivity {

    private String destinationUid;
    private Button button;
    private EditText editText;
    public TextView textView_messge;
    private String uid;
    private String chatRoomUid;

    private RecyclerView recyclerView;
    private Button translator;

    private DatabaseReference databaseReference;
    private ValueEventListener valueEventListener;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("    HH:mm");
    int peopleCount = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_massge);
        textView_messge=(TextView)findViewById(R.id.message_textview);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();//채팅을 요구하는 아이디 즉 단말기에 로그인된 UID
        destinationUid = getIntent().getStringExtra("destinationUid");//채팅을 당하는 아이디
        button = (Button)findViewById(R.id.messageActivity_button);
        editText = (EditText)findViewById(R.id.messageActivity_edittext);
        translator = (Button)findViewById(R.id.translator_massge);

        translator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NaverTranslateTask naverTranslateTask = new NaverTranslateTask();
                String sText = editText.getText().toString();
                naverTranslateTask.execute(sText);
            }
        });

        recyclerView=(RecyclerView)findViewById(R.id.messageActivity_recyclerview);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatModel chatModel = new ChatModel();
                chatModel.users.put(uid,true);
                chatModel.users.put(destinationUid,true);
                if(chatRoomUid == null){
                    button.setEnabled(false);
                    FirebaseDatabase.getInstance().getReference().child("chatrooms")
                            .push().setValue(chatModel).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            checkrool(); // 데이터 중복 확인
                        }
                    });

                }
                else{
                    ChatModel.Comment comment = new  ChatModel.Comment();
                    comment.uid = uid;
                    comment.message = editText.getText().toString();//메세지를 보내는부분
                    comment.timestamp = ServerValue .TIMESTAMP;
                    String sText = editText.getText().toString();
                    FirebaseDatabase.getInstance().getReference().child("chatrooms")
                            .child(chatRoomUid).child("comments").push().setValue(comment).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            editText.setText("");//메세지 보낸후 글자 없애기
                        }
                    });

                }

            }
        });
        checkrool();

    }


    void checkrool(){
        FirebaseDatabase.getInstance().getReference().child("chatrooms")
                .orderByChild("users/"+uid).equalTo(true)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot item : dataSnapshot.getChildren()){
                            ChatModel chatModel = item.getValue(ChatModel.class);//채팅방안에 유저를 받아오는 부분
                                if (chatModel.users.containsKey(destinationUid)){
                                    chatRoomUid = item.getKey();//방아이디를 가져온다
                                    button.setEnabled(true);
                                    recyclerView.setLayoutManager(new LinearLayoutManager(MassgeActivity.this));
                                    recyclerView.setAdapter(new RecyclerViewAdapter());

                                }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });//채팅방 중복체크
    }

    class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<ChatModel.Comment>comments;
        User users;



        public RecyclerViewAdapter() {
            comments = new ArrayList<>();

            FirebaseDatabase.getInstance().getReference().child("users").child(destinationUid).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users = dataSnapshot.getValue(User.class);
                    getMessgeList();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });


        }

        void getMessgeList(){
             databaseReference = FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid)
                    .child("comments");
                valueEventListener =databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    comments.clear();//데이터가 추가될때마다 모든 채팅의대한 내용을 다 보내준다
                    Map<String,Object> readUsersMap = new HashMap<>();
                    for(DataSnapshot item : dataSnapshot.getChildren()){
                        String key = item.getKey();
                        ChatModel.Comment comment_origin = item.getValue(ChatModel.Comment.class);
                        ChatModel.Comment comment_motify = item.getValue(ChatModel.Comment.class);
                        comment_motify.readUsers.put(uid,true);

                        readUsersMap.put(key,comment_motify);
                        comments.add(comment_origin);
                    }
                    if(comments.size()==0){
                        return;
                    }
                    if(!comments.get(comments.size()-1).readUsers.containsKey(uid)) {

                        FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("comments")
                                .updateChildren(readUsersMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                notifyDataSetChanged();
                                recyclerView.scrollToPosition(comments.size() - 1);
                            }
                        });
                    }
                    else{
                        //메세지 갱신
                        notifyDataSetChanged();//갱신

                        recyclerView.scrollToPosition(comments.size()-1);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view =LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_messge,viewGroup,false);

            return new MessageViewHoder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
            MessageViewHoder messageViewHoder = ((MessageViewHoder)viewHolder);
            //내가 보낸 메세지
            if(comments.get(i).uid.equals(uid)){
                messageViewHoder.textView_message.setText(comments.get(i).message);
                messageViewHoder.textView_message.setBackgroundResource(R.drawable.bule);
                messageViewHoder.linearLayout_destination.setVisibility(View.INVISIBLE);
                messageViewHoder.textView_message.setTextSize(25);
                messageViewHoder.linearLayout_main.setGravity(Gravity.RIGHT);
                setReadCounter(i,messageViewHoder.textView_left);
            }
            //상대방이 보낸 메세지
            else{

                Glide.with(viewHolder.itemView.getContext())
                        .load(users.profieImageUrl)
                        .apply(new RequestOptions().circleCrop())
                        .into(messageViewHoder.imageView_profile);

                messageViewHoder.textView_name.setText(users.user_name);
                messageViewHoder.linearLayout_destination.setVisibility(View.VISIBLE);
                messageViewHoder.textView_message.setBackgroundResource(R.drawable.yellow);
                messageViewHoder.textView_message.setText(comments.get(i).message);
                messageViewHoder.textView_message.setTextSize(25);
                messageViewHoder.linearLayout_main.setGravity(Gravity.LEFT);
                setReadCounter(i,messageViewHoder.textView_right);
            }
            long unixTime = (long) comments.get(i).timestamp;
            Date data = new Date(unixTime);
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
            String time = simpleDateFormat.format(data);
            messageViewHoder.textView_tiemstamp.setText(time);

        }

        void setReadCounter(final int position , final TextView textView){//읽음 표시부분
            if(peopleCount==0) {
                FirebaseDatabase.getInstance().getReference().child("chatrooms").child(chatRoomUid).child("users")
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                Map<String, Boolean> uesrs = (Map<String, Boolean>) dataSnapshot.getValue();
                                peopleCount = uesrs.size();
                                int count = peopleCount - comments.get(position).readUsers.size();
                                if (count > 0) {
                                    textView.setVisibility(View.VISIBLE);
                                    textView.setText(String.valueOf(count));

                                } else {
                                    textView.setVisibility(View.INVISIBLE);
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
            }
            else{
                int count = peopleCount - comments.get(position).readUsers.size();
                if (count > 0) {
                    textView.setVisibility(View.VISIBLE);
                    textView.setText(String.valueOf(count));

                } else {
                    textView.setVisibility(View.INVISIBLE);
                }
            }
        }

        @Override
        public int getItemCount() {

            return comments.size();
        }

        private class MessageViewHoder extends RecyclerView.ViewHolder {
            public TextView textView_message;
            public TextView textView_name;
            public ImageView imageView_profile;
            public LinearLayout linearLayout_destination;
            public LinearLayout linearLayout_main;
            public TextView textView_left;
            public TextView textView_right;
            public TextView textView_tiemstamp;
            public MessageViewHoder(View view) {
                super(view);
                textView_message = (TextView)view.findViewById(R.id.message_textview);
                textView_name = (TextView)view.findViewById(R.id.messageitem_textview_name);
                imageView_profile = (ImageView)view.findViewById(R.id.message_image_profile);
                linearLayout_destination = (LinearLayout)view.findViewById(R.id.messageitem_linearlayout_destination);
                linearLayout_main = (LinearLayout)view.findViewById(R.id.messageItmem_linearlayout_main);
                textView_left=(TextView)view.findViewById(R.id.messageitem_textview_readcoun_left);
                textView_right=(TextView)view.findViewById(R.id.messageitem_textview_readcoun_right);
                textView_tiemstamp=(TextView)view.findViewById(R.id.message_time_textview);
            }
        }
    }
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
        databaseReference.removeEventListener(valueEventListener);
        Intent intent = new Intent(MassgeActivity.this,Main2Activity.class);
        ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(getApplicationContext(),R.anim.fromlft,R.anim.fromtorit);
        overridePendingTransition(R.anim.fromlft, R.anim.fromtorit);
        startActivity(intent,activityOptions.toBundle());
        finish();

    }
    public class NaverTranslateTask  extends AsyncTask<String, Void, String> {

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
                String text = URLEncoder.encode(editText.getText().toString(), "UTF-8");
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
            TranslatedItem item = gson.fromJson(rootObj.toString(),MassgeActivity.TranslatedItem.class);
            //Log.d("AsyncTask:", "2.PostExecute");
            editText.setText(item.getTranslatedText());
        }


    }
    public class TranslatedItem {
        String translatedText;

        public String getTranslatedText() {
            return translatedText;
        }
    }




}
