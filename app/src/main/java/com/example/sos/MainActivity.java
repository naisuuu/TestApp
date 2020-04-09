package com.example.sos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DiffUtil;

import com.google.firebase.auth.FirebaseAuth;
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

    private static final String TAG = "MainActivity";
    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    Button btnLogOut;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private Toolbar mToolbar;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        CardStackView cardStackView = findViewById(R.id.card_stack_view);
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
                if (manager.getTopPosition() == adapter.getItemCount() - 5) {
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
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());


        mToolbar = findViewById(R.id.main_page_toolbar);
        setSupportActionBar(mToolbar);
        Objects.requireNonNull(getSupportActionBar()).setTitle("TESTT");
    }

    private void paginate() {
        List<ItemModel> old = adapter.getItems();
        List<ItemModel> fresh = new ArrayList<>(addList());
        CardStackCallback callback = new CardStackCallback(old, fresh);
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(callback);
        adapter.setItems(fresh);
        result.dispatchUpdatesTo(adapter);
    }


    private List<ItemModel> addList() {
        List<ItemModel> items = new ArrayList<>();
        items.add(new ItemModel(R.drawable.default_avatar, "Alison", "50", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "CJ", "20", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "David", "41", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "Jake", "35", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "Jessica", "60", "Dublin"));

        items.add(new ItemModel(R.drawable.default_avatar, "Alison", "50", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "CJ", "20", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "David", "41", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "Jake", "35", "Dublin"));
        items.add(new ItemModel(R.drawable.default_avatar, "Jessica", "60", "Dublin"));
        return items;
    }

    private void sendToStart(){
        FirebaseAuth.getInstance().signOut();
        Intent I = new Intent(MainActivity.this, ActivityLogin.class);
        startActivity(I);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
       super.onCreateOptionsMenu(menu);
       getMenuInflater().inflate(R.menu.main_menu, menu);
       return true;
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

        if(item.getItemId()==R.id.main_all_btn){

            Intent settingsIntent = new Intent(MainActivity.this, UsersActivity.class);
            startActivity(settingsIntent);
        }



        return true;
    }
}
