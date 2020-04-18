package com.example.sos;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
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
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class CardsFragment extends Fragment {
    private CardStackView mUsersList;

    private DatabaseReference mFriendsDatabase;
    private DatabaseReference mUsersDatabase;

    private FirebaseAuth mAuth;

    private String mCurrent_user_id;

    private View mMainView;

    public CardsFragment() {
        // Required empty public constructor
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_cards, container, false);

        mUsersList = mMainView.findViewById(R.id.card_stack_view);
        mAuth = FirebaseAuth.getInstance();

        mCurrent_user_id = mAuth.getCurrentUser().getUid();

        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends").child(mCurrent_user_id);
        mFriendsDatabase.keepSynced(true);
        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        mUsersDatabase.keepSynced(true);

        CardStackLayoutManager manager = new CardStackLayoutManager(getContext());
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
        mUsersList.setLayoutManager(manager);

        // Inflate the layout for this fragment
        return mMainView;
    }

    @Override
    public void onStart() {
        super.onStart();


        FirebaseRecyclerOptions<ItemModel> options =
                new FirebaseRecyclerOptions.Builder<ItemModel>()
                        .setQuery(mUsersDatabase, ItemModel.class)
                        .setLifecycleOwner(this)
                        .build();


        FirebaseRecyclerAdapter<ItemModel, UsersViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<ItemModel, CardsFragment.UsersViewHolder>(options) {
            @NonNull
            @Override
            public CardsFragment.UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new CardsFragment.UsersViewHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_card, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull CardsFragment.UsersViewHolder holder, int position, @NonNull ItemModel model) {
                //We want to pass the name of the user it will get that name and than will stored in layout (user_single_layout.xml -> display_name)
                holder.setName(model.getName());
                holder.setUserStatus(model.getStatus());
                holder.setUserImage(model.getThumb_image(), getActivity().getApplicationContext());
                holder.setUserInstrument(model.getInstrument());
                holder.setUserGenre(model.getGenre());


                final String user_id = getRef(position).getKey();

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent profileIntent = new Intent(getContext(), ProfileActivity.class);
                        profileIntent.putExtra("user_id", user_id);
                        Log.d(user_id, "onClick: ");
                        startActivity(profileIntent);
                    }
                });
            }

        };
        mUsersList.setAdapter(firebaseRecyclerAdapter);
    }

    private List<ItemModel> addList() {
        final List<ItemModel> carditems = new ArrayList<>();
        mUsersDatabase.addChildEventListener(new ChildEventListener() {
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

        public void setUserInstrument(String instrument) {
            TextView userInstrumentView = mView.findViewById(R.id.item_instrument);
            userInstrumentView.setText(instrument);
        }

        public void setUserGenre(String genre) {
            TextView userGenreView = mView.findViewById(R.id.item_genre);
            userGenreView.setText(genre);
        }
    }

}
