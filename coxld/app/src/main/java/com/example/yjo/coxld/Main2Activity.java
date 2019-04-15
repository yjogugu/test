package com.example.yjo.coxld;

import android.preference.PreferenceFragment;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.yjo.coxld.fragment.AccountFragment;
import com.example.yjo.coxld.fragment.ChatFragment;
import com.example.yjo.coxld.fragment.peoplefragment;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new peoplefragment()).commit();
        BottomNavigationView bottomNavigationView =(BottomNavigationView)findViewById(R.id.main_bottomnaviget);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.action_people:

                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new peoplefragment()).commit();
                        return true;

                    case R.id.action_chat:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new ChatFragment()).commit();
                        return true;

                    case R.id.action_account:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame,new AccountFragment()).commit();
                        return true;
                }
                return false;
            }
        });


    }
}
