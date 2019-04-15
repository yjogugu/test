package com.example.yjo.coxld;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.yjo.coxld.chat.MassgeActivity;
import com.example.yjo.coxld.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseDatabase database;
    public RequestManager mGlideRequestManager;
    private String myUid1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
    private List<User> uidlist = new ArrayList<>();
    private static final String TAG = "Main";
    List<User>users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        final Intent intent = getIntent();
        final String name =intent.getStringExtra("name");
        final String image =intent.getStringExtra("image");
        final String chat = intent.getStringExtra("destinationUid");
        final TextView textView =findViewById(R.id.textView_profile);
        final ImageView imageView = findViewById(R.id.imageView_profile);
        final ImageView imageView_reset = findViewById(R.id.imageView_reset);
        Button button = findViewById(R.id.button_chat);
        mGlideRequestManager = Glide.with(this);



        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot  snapshot : dataSnapshot.getChildren()){
                    //DataSnapshot snapshot1 : dataSnapshot.getChildren();
                    User string1 = snapshot.getValue(User.class);

                    uidlist.add(string1);
                        //storage.getReference().child("users_images")
                        textView.setText(name);
                        mGlideRequestManager.load(image).apply(new RequestOptions().circleCrop())
                                .into(imageView);



                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                users = new ArrayList<>();
                 Intent intent = new Intent(v.getContext(),MassgeActivity.class);
                    intent.putExtra("destinationUid",chat);
                ActivityOptions activityOptions = ActivityOptions.makeCustomAnimation(v.getContext(),R.anim.fromrit,R.anim.fromtoleft);
                    startActivity(intent,activityOptions.toBundle());

            }
        });
        imageView_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });




    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fromlft, R.anim.fromtorit);

    }

    void showDialog() {
        Intent intent = getIntent();
        final String name =intent.getStringExtra("name");
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater layoutInflater = getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.dialog_frand,null);
        final EditText editText = findViewById(R.id.textView_set);
        builder.setView(view).setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Toast.makeText(ProfileActivity.this, editText.getText().toString(), Toast.LENGTH_SHORT).show();
                Map<String,Object> stringObjectMap = new HashMap<>();
                stringObjectMap.put("user_name",editText.getText().toString());
                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(name).updateChildren(stringObjectMap);

            }
        }).setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.show();
    }
}
