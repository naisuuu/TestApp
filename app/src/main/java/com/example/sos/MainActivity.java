package com.example.sos;


import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    private DatabaseReference mUserDatabase;

    private ImageView mItemImage;
    private TextView mItemName, mItemStatus;
    private static final String TAG = "MainActivity";
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;
    private CardStackView cardStackView;
    Button btnLogOut;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Toolbar mToolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        cardStackView = findViewById(R.id.card_stack_view);
        manager = new CardStackLayoutManager(this, new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);
            }

            @Override
            public void onCardSwiped(Direction direction) {
                Log.d(TAG, "onCardSwiped: p=" + manager.getTopPosition() + " d=" + direction);

                switch (direction) {
                    case Right:
                        Toast.makeText(MainActivity.this, "Right", Toast.LENGTH_LONG).show();
                        break;
                    case Left:
                        Toast.makeText(MainActivity.this, "Left", Toast.LENGTH_LONG).show();
                        break;
                    case Top:
                        Toast.makeText(MainActivity.this, "Top", Toast.LENGTH_LONG).show();
                        break;
                    case Bottom:
                        Toast.makeText(MainActivity.this, "Bottom", Toast.LENGTH_LONG).show();
                        break;
                }

                //Paginating
                if (manager.getTopPosition() == adapter.getItemCount()) {
                    paginate();
                }

            }

            @Override
            public void onCardRewound() {
                Log.d(TAG, "onCardRewound: " + manager.getTopPosition());
            }

            @Override
            public void onCardCanceled() {
                Log.d(TAG, "onCardCanceled: " + manager.getTopPosition());
            }

            @Override
            public void onCardAppeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }

            @Override
            public void onCardDisappeared(View view, int position) {
                TextView tv = view.findViewById(R.id.item_name);
                Log.d(TAG, "onCardAppeared: " + position + ", name: " + tv.getText());
            }
        });
        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter(addList());
        cardStackView.setLayoutManager(manager);
        cardStackView.setItemAnimator(new DefaultItemAnimator());


        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("SOS");


    }

    @Override
    protected void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<ItemModel> options =
                new FirebaseRecyclerOptions.Builder<ItemModel>()
                        .setQuery(mUserDatabase, ItemModel.class)
                        .setLifecycleOwner(this)
                        .build();


        FirebaseRecyclerAdapter<ItemModel, MainActivity.UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ItemModel, MainActivity.UsersViewHolder>(options) {
            @NonNull
            @Override
            public MainActivity.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new MainActivity.UsersViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_card, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull MainActivity.UsersViewHolder holder, int position, @NonNull ItemModel model) {
                //We want to passe the name of the user it will get that name and than will stored in layout (user_single_layout.xml -> display_name)
                holder.setName(model.getName());
                holder.setUserStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image(), getApplicationContext());

                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Intent profileIntent = new Intent(MainActivity.this, ProfileActivity.class);
                        //take on comment the line above because a error comes from it
                        //profileIntent.putExtra("user_id", user_id);
                        //Log.d(user_id, "onClick: ");
                        //startActivity(profileIntent);
                    }
                });
            }

        };
        cardStackView.setAdapter(firebaseRecyclerAdapter);
    }

    private List<ItemModel> addList() {
        final List<ItemModel> carditems = new ArrayList<>();
        mUserDatabase.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                ItemModel object = dataSnapshot.getValue(ItemModel.class);
                carditems.add(object);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return carditems;
    } //not used

    private void paginate() {
        List<ItemModel> old = adapter.getItems();
        List<ItemModel> fresh = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, fresh);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setItems(fresh);
        result.dispatchUpdatesTo(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        super.onOptionsItemSelected(item);

        if(item.getItemId() == R.id.main_logout_btn){
            sendToStart();
        }

        if (item.getItemId() == R.id.main_settings_btn) {

            Intent settingsIntent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settingsIntent);
        }
/*
        if(item.getItemId()==R.id.main_all_btn){

            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }
*/
        if(item.getItemId()==R.id.main_map_btn){

            Intent settingsIntent = new Intent( MainActivity.this, GoogleMapAPI.class);
            startActivity(settingsIntent);

        }
        if(item.getItemId()==R.id.main_chat_btn) {

            Intent settingsIntent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(settingsIntent);
        }


        return true;
    }

    private void sendToStart() {
        FirebaseAuth.getInstance().signOut();
        Intent I = new Intent(MainActivity.this, ActivityLogin.class);
        startActivity(I);
    }


    // End of Card Swipe View


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder { //Don't forget "static "

        //We need a view then be used by firebase adapter
        View mView;


        public UsersViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.item_name);
            userNameView.setText(name);
        }

        public void setUserStatus(String userStatus) {
            TextView userStatusView = mView.findViewById(R.id.item_status);
            userStatusView.setText(userStatus);
        }

        public void setUserImage(String thumb_image, Context context) {
            ImageView userImageView = mView.findViewById(R.id.item_image);
            Picasso.get().load(thumb_image).placeholder(R.drawable.default_avatar).into(userImageView);
        }
    }
}
