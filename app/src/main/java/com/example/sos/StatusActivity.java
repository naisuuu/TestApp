package com.example.sos;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class StatusActivity extends AppCompatActivity {
    private Toolbar mToolbar;
    private TextInputEditText mStatus, mInstrument, mGenre;
    private Button mSavebtn;

    //Firebase
    private DatabaseReference mStatusDatabase;
    private FirebaseUser mCurrentUser;

    //Progress
    private ProgressDialog mProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status);

        //Firebase
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();

        mStatusDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        mToolbar = findViewById(R.id.status_appBar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setTitle("Account Status");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String status_value = getIntent().getStringExtra("status_value");
        String instruments_value = getIntent().getStringExtra("instruments_value");
        String genres_value = getIntent().getStringExtra("genres_value");
        //Progress
        mProgress = new ProgressDialog(this);

        mStatus = findViewById(R.id.status_input);
        mInstrument = findViewById(R.id.instruments_input);
        mGenre = findViewById(R.id.genre_input);


        mStatus.setText(status_value);
        mInstrument.setText(instruments_value);
        mGenre.setText(genres_value);

        mSavebtn = findViewById(R.id.status_savebutton);
        mSavebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Progress
                mProgress = new ProgressDialog(StatusActivity.this);
                mProgress.setTitle("Saving Changes");
                mProgress.setMessage("Please wait while the changes are saved");
                mProgress.show();

                           //Adds a delay so users can see dialog bar

                String status = mStatus.getText().toString();
                String instrument = mInstrument.getText().toString();
                String genre = mGenre.getText().toString();
                mStatusDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgress.dismiss();
                                }
                            }, 3000);
                        } else {
                            Toast.makeText(getApplicationContext(),"There was an error in saving changes", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                mStatusDatabase.child("instrument").setValue(instrument).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgress.dismiss();
                                }
                            }, 3000);
                        } else {
                            Toast.makeText(getApplicationContext(), "There was an error in saving changes", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                mStatusDatabase.child("genre").setValue(genre).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    mProgress.dismiss();
                                }
                            }, 3000);
                        } else {
                            Toast.makeText(getApplicationContext(), "There was an error in saving changes", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

    }
}
