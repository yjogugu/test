package com.example.yjo.coxld.fragment;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.yjo.coxld.R;
import com.example.yjo.coxld.WeaActivity;
import com.example.yjo.coxld.Weather;
import com.example.yjo.coxld.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Map;

public class AccountFragment extends Fragment {
    private static final int PICK_FROM_ALBUM = 10;
    private Uri imageuri;
    private ImageView imageView;
    private String myUid1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
    public RequestManager requestManager;
    public Intent intent ;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragement_acount,container,false);
        final EditText editText = view.findViewById(R.id.commentDialog_edittext);
        final EditText editText_name = view.findViewById(R.id.editText_name);
        Button button_maps = view.findViewById(R.id.acount_maps);
        Button button_ok = view.findViewById(R.id.button_ok);
        requestManager = Glide.with(this);
        imageView = view.findViewById(R.id.imageView_comment);

        button_maps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),WeaActivity.class);
                startActivity(intent);
            }
        });

        FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot  snapshot : dataSnapshot.getChildren()){
                    User string1 = snapshot.getValue(User.class);
                    if(string1.uid.equals(myUid1)){
                        editText.setText(string1.comment);
                        editText_name.setText(string1.user_name);
                        requestManager.load(string1.profieImageUrl).apply(new RequestOptions().circleCrop())
                                .into(imageView);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });


        button_ok.setOnClickListener(new View.OnClickListener() {//다이얼로그 확인 버튼
            @Override
            public void onClick(View v) {
                final Map<String,Object> stringObjectMap = new HashMap<>();
                stringObjectMap.put("comment",editText.getText().toString());
                stringObjectMap.put("user_name",editText_name.getText().toString());
                final StorageReference profileimageref = FirebaseStorage.getInstance().getReference().child("users_images");


                FirebaseDatabase.getInstance().getReference().child("users")
                        .child(myUid1).updateChildren(stringObjectMap);//상태메세지 바뀌는곳

            }
        });


        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode==Activity.RESULT_OK){
            imageView.setImageURI(data.getData());//가운데 뷰를 바꿈
            imageuri=data.getData(); //이미지 경로원본
        }
    }
}
