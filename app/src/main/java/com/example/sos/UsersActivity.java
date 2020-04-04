package com.example.sos;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuView;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class UsersActivity extends AppCompatActivity {

    private Toolbar mToolbar;

    private RecyclerView mUsersList;

    private DatabaseReference mUsersDatabase;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users);

        /// Code from video mToolbar = (Toolbar) findViewById(R.id.user_appBar);
        ///                 mUsersList = (RecyclerView) findViewById(R.id.users_list);

        mToolbar = (Toolbar) findViewById(R.id.users_appBar);
        setSupportActionBar(mToolbar);

        getSupportActionBar().setTitle("All Users");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mUsersList = (RecyclerView) findViewById(R.id.users_list);
        mUsersList.setHasFixedSize(true);
        mUsersList.setLayoutManager(new LinearLayoutManager(this));

    }

    protected RecyclerView.Adapter newAdapter() {
        FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(mUsersDatabase,Users.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @Override
            public TeamHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                return new TeamHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.team_card_view, parent, false));
            }

            @Override
            protected void onBindViewHolder(TeamHolder holder, int position, Team model) {
                teamItems.add(model);
                holder.bind(model);
            }

            @Override
            public void onDataChanged() {
            }
        };

    public class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;


        public UsersViewHolder(View itemView) {
            super(itemView);

            mView = itemView;
        }

            public void setName(String name){

            }
    }

}
