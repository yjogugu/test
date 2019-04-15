package com.example.yjo.coxld.fragment;


import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.example.yjo.coxld.ProfileActivity;
import com.example.yjo.coxld.R;
import com.example.yjo.coxld.SimSimi;
import com.example.yjo.coxld.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class peoplefragment extends Fragment {
    //FirebaseDatabase.getInstance().getReference().child("chatrooms")
    //          .orderByChild("users/"+uid).equalTo(true)
    private List<User> uidlist = new ArrayList<>();
    //User users = new User();
    private String myUid1 = FirebaseAuth.getInstance().getCurrentUser().getUid();
    private DatabaseReference mdatabase;
    private LinearLayout linearLayout;
    private ImageView imageView_my;
    private TextView textView_my;
    private FirebaseDatabase database;
    private FirebaseStorage storage;
    public RequestManager mGlide;
    private TextView textView_comment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.peoplefragment, container, false);
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recylerview);
        linearLayout = view.findViewById(R.id.linearLayout_simsimi);
        mGlide = Glide.with(peoplefragment.this);
        textView_my = view.findViewById(R.id.frienditem_textview_my);
        imageView_my = view.findViewById(R.id.frienditem_image_my);
        textView_comment = view.findViewById(R.id.editText_comment);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(),SimSimi.class);
                startActivity(intent);
            }
        });
        database.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot  snapshot : dataSnapshot.getChildren()){
                    User string1 = snapshot.getValue(User.class);
                    uidlist.add(string1);
                    uidlist.clear();
                    if (string1.uid.equals(myUid1)) {
                        textView_my.setText(string1.user_name);
                        mGlide.load(string1.profieImageUrl).apply(new RequestOptions().circleCrop())
                                .into(imageView_my);
                        textView_comment.setText(string1.comment);
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        recyclerView.setLayoutManager(new LinearLayoutManager(inflater.getContext()));
        recyclerView.setAdapter(new PeopleFragmentRecyclerViewAdapter());
        return view;
    }



    class PeopleFragmentRecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        List<User>users;
        public PeopleFragmentRecyclerViewAdapter (){
            users = new ArrayList<>();
            final String myUid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            //child(myUid).child("친구목록").
            FirebaseDatabase.getInstance().getReference().child("users").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    users.clear();//
                    for(DataSnapshot snapshot : dataSnapshot.getChildren()){
                        User users1 = snapshot.getValue(User.class);
                        if(users1.uid.equals(myUid)){
                            continue;
                        }
                        users.add(users1);
                    }
                    notifyDataSetChanged();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_frand,viewGroup,false);
            return new CustomViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
            Glide.with(viewHolder.itemView.getContext())
                    .load(users.get(i).profieImageUrl)
                    .apply(new RequestOptions().circleCrop())
                    .into(((CustomViewHolder)viewHolder).imageView);

            ((CustomViewHolder)viewHolder).textView.setText(users.get(i).user_name);
            viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),ProfileActivity.class);
                    intent.putExtra("name",users.get(i).user_name);
                    intent.putExtra("image",users.get(i).profieImageUrl);
                    intent.putExtra("destinationUid",users.get(i).uid);
                    ActivityOptions activityOptions =ActivityOptions.makeCustomAnimation(v.getContext(),R.anim.fromrit,R.anim.fromtoleft);

                    startActivity(intent,activityOptions.toBundle());

                }
            });
            if(users.get(i).comment !=null) {
                ((CustomViewHolder) viewHolder).textView_comment.setText(users.get(i).comment);
            }
            else{
                return;
            }

        }

        @Override
        public int getItemCount() {
            return users.size();
        }

        private class CustomViewHolder extends RecyclerView.ViewHolder {
            public ImageView imageView;
            public TextView textView;
            public TextView textView_comment;
            public CustomViewHolder(View view) {
                super(view);
                imageView = view.findViewById(R.id.frienditem_image);
                textView = view.findViewById(R.id.frienditem_textview);
                textView_comment = view.findViewById(R.id.frienditem_textview_comment);
            }
        }
    }
}



