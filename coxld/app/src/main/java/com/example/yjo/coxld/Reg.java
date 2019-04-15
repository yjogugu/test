package com.example.yjo.coxld;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.yjo.coxld.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class Reg extends AppCompatActivity {
    private static final int PICK_FROM_ALBUM = 10;
    private EditText email;
    private EditText name;
    private EditText password;
    private Button Regster;
    private ImageView profile;
    private Uri imageuri;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reg);
        email = (EditText)findViewById(R.id.editText_email);
        password = (EditText)findViewById(R.id.editText_password);
        name = (EditText)findViewById(R.id.editText_name);
        Regster = (Button)findViewById(R.id.reg_button);
        profile = (ImageView)findViewById(R.id.imageView_reg);
        progressBar=(ProgressBar)findViewById(R.id.progressBar2);
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,PICK_FROM_ALBUM);
            }
        });

        Regster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                if(email.getText().toString() == null || name.getText().toString()==null||password.getText().toString()==null||imageuri==null){
                    progressBar.setVisibility(View.INVISIBLE);
                    if(imageuri==null){
                        Toast.makeText(Reg.this, "사진을 넣어주세요", Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                    return;
                }

                else{
                    Toast.makeText(Reg.this, "회원가입이 되었습니다", Toast.LENGTH_SHORT).show();
                }
                FirebaseAuth.getInstance().createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
                        .addOnCompleteListener(Reg.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                final String uid = task.getResult().getUser().getUid();
                                final StorageReference profileimageref = FirebaseStorage.getInstance().getReference().child("users_images").child(uid);
                                profileimageref.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                        Task<Uri> uriTask = profileimageref.getDownloadUrl();
                                        while(!uriTask.isSuccessful());
                                        Uri down = uriTask.getResult();
                                        String imageUri = String.valueOf(down);
                                        User User = new User();
                                        User.user_name = name.getText().toString();
                                        User.profieImageUrl = imageUri;
                                        User.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                        FirebaseDatabase.getInstance().getReference().child("users")
                                                .child(uid).setValue(User);
                                        Intent intent = new Intent(Reg.this,LoginActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });

                            }
                        });
            }
        });


    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode==RESULT_OK){
            profile.setImageURI(data.getData());//가운데 뷰를 바꿈
            imageuri=data.getData(); //이미지 경로원본
        }
    }
}
  /*  FirebaseRemoteConfig mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();


        profile = (ImageView) findViewById(R.id.imageView_reg);
                profile.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent,PICK_FROM_ALBUM);
        }
        });
        email =(EditText)findViewById(R.id.editText_email);
        name =(EditText)findViewById(R.id.editText_name);
        password =(EditText)findViewById(R.id.editText_password);
        Regster=(Button)findViewById(R.id.reg_button);

        Regster.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
        if(email.getText().toString() == null || name.getText().toString() ==null && password.getText().toString()==null||imageuri==null){
        if (email.getText().toString()== null) {
        Toast.makeText(Reg.this, "아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
        return;
        }
        else if(password.getText().toString()== null){
        Toast.makeText(Reg.this, "비밀번호를 입력해주세요", Toast.LENGTH_SHORT).show();
        return;
        }
        else if(imageuri == null){
        Toast.makeText(Reg.this, "이미지를 넣어주세요", Toast.LENGTH_SHORT).show();
        return;
        }
        }
        else{
        Toast.makeText(Reg.this, "회원가입 되었습니다", Toast.LENGTH_SHORT).show();
        FirebaseAuth.getInstance();
        }

        FirebaseAuth.getInstance()
        .createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString())
        .addOnCompleteListener(Reg.this, new OnCompleteListener<AuthResult>() {
@Override
public void onComplete(@NonNull Task<AuthResult> task) {
final String uid = task.getResult().getUser().getUid();
final StorageReference profileimageref = FirebaseStorage.getInstance().getReference().child("users_images").child(uid);

        profileimageref.putFile(imageuri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {

@Override
public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
        //String imageUri = profileimageref.getDownloadUrl().toString();
        Task<Uri> uriTask = profileimageref.getDownloadUrl();
        while(!uriTask.isSuccessful());
        Uri down = uriTask.getResult();
        String imageUri = String.valueOf(down);
        User User = new User();
        User.user_name = name.getText().toString();
        User.profieImageUrl = imageUri;
        User.uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseDatabase.getInstance().getReference().child("users").child(uid).setValue(User)
        .addOnSuccessListener(new OnSuccessListener<Void>() {
@Override
public void onSuccess(Void aVoid) {
        Reg.this.finish();
        }
        });
        }
        });


        }
        });
        }
        });

        }

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == PICK_FROM_ALBUM && resultCode==RESULT_OK){
        profile.setImageURI(data.getData());//가운데 뷰를 바꿈
        imageuri=data.getData(); //이미지 경로원본
        }
        }*/
