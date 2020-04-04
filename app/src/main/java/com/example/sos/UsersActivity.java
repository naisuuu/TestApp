package com.example.sos;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;


public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        /// Code from video mToolbar = (Toolbar) findViewById(R.id.user_appBar);
        ///                 mUsersList = (RecyclerView) findViewById(R.id.users_list);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setSupportActionBar(mToolbar);


        mUsersList = findViewById(R.id.users_list);

    }
}
