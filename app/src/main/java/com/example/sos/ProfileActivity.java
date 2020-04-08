package com.example.sos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class ProfileActivity extends AppCompatActivity {

    private ImageView mProfileImage;
    private TextView mProfileName, mProfileStatus, mProfileFriendsCount;
    private Button mProfileSendReqBtn, mProfileDeclineBtn;

    private DatabaseReference mUsersDatabase;

    private DatabaseReference mFriendReqDatabase;

    private DatabaseReference mNotifcationDatabase;

    private DatabaseReference mFriendDatabase;

    private ProgressDialog mProgressDialog;

    private FirebaseUser mCurrent_user;

    private String mCurrent_state;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        final String user_id = getIntent().getStringExtra("user_id");

        mUsersDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabase = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mNotifcationDatabase = FirebaseDatabase.getInstance().getReference().child("notifications");
        mCurrent_user = FirebaseAuth.getInstance().getCurrentUser();

        mProfileImage = findViewById(R.id.profile_image);
        mProfileName = findViewById(R.id.profile_displayName);
        mProfileFriendsCount = findViewById(R.id.profile_totalFriends);
        mProfileStatus = findViewById(R.id.profile_status);
        mProfileSendReqBtn = findViewById(R.id.profile_send_req_btn);
        mProfileDeclineBtn = findViewById(R.id.profile_decline_btn);

        mCurrent_state = "not_friends";

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Loading User Data");
        mProgressDialog.setMessage("please wait while we load user data.");
        mProgressDialog.setCanceledOnTouchOutside(false);
        mProgressDialog.show();


        mUsersDatabase.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String display_name = dataSnapshot.child("name").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();


                mProfileName.setText(display_name);
                mProfileStatus.setText(status);

                //come back to if needs to be fixed
                Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mProfileImage);

                //------------------FRIENDS LIST / REQUEST FEATURE ------------------------------
                mFriendReqDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();
                            if (req_type.equals("received")) {


                                mCurrent_state = "req_received";
                                mProfileSendReqBtn.setText("Accept Friend Request");
                                mProfileDeclineBtn.setVisibility(View.VISIBLE);
                                mProfileDeclineBtn.setEnabled(true);


                            } else if (req_type.equals("sent")) {
                                mCurrent_state = "req_sent";
                                mProfileSendReqBtn.setText("Cancel Friend Request");
                                mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                mProfileDeclineBtn.setEnabled(false);
                            }
                        } else {
                            mFriendDatabase.child(mCurrent_user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_state = "friends";
                                        mProfileSendReqBtn.setText("Unfriend this Person");

                                        mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                        mProfileDeclineBtn.setEnabled(false);
                                    }
                                    mProgressDialog.dismiss();

                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    mProgressDialog.dismiss();
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
        mProfileSendReqBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mProfileSendReqBtn.setEnabled(false);


                // -------------NOT FRIEND STATE------

                if (mCurrent_state.equals("not_friends")) {

                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).child("request_type")
                            .setValue("sent").addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            HashMap<String, String> notificationData = new HashMap<>();
                            notificationData.put("from", mCurrent_user.getUid());
                            notificationData.put("type", "request");



                            mNotifcationDatabase.child(user_id).push().setValue(notificationData).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mCurrent_state = "not_Friends";
                                    mProfileSendReqBtn.setText("Sent Freind Request");

                                    mProfileDeclineBtn.setVisibility(View.INVISIBLE);
                                    mProfileDeclineBtn.setEnabled(false);
                                }
                            });






                            if (task.isSuccessful()) {

                                mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).child("request_type").setValue("received").addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {


                                        mProfileSendReqBtn.setEnabled(true);
                                        mCurrent_state = "req_sent";
                                        mProfileSendReqBtn.setText("Cancel Friend Request");



                                        //Toast.makeText(ProfileActivity.this, "Request Sent Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                });

                            } else {

                                Toast.makeText(ProfileActivity.this, "Failed Sending Request", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                //------------------CANCEL REQUEST STATE -----------------------------------------------------------------------------------------

                if (mCurrent_state.equals("req_sent")) {
                    mFriendReqDatabase.child(mCurrent_user.getUid()).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            mFriendReqDatabase.child(user_id).child(mCurrent_user.getUid()).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {





                                }
                            });
                        }
                    });
                }
            }
        });
    }
}