package com.example.sos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    public EditText emailId, passwd;
    Button btnSignUp;
    TextView signIn;
    FirebaseAuth firebaseAuth;
    private ProgressDialog mRegProgress;

    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.ETemail);
        passwd = findViewById(R.id.ETpassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        signIn = findViewById(R.id.TVSignIn);
        mRegProgress = new ProgressDialog(this);
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String emailID = emailId.getText().toString();
                String paswd = passwd.getText().toString();

                if (emailID.isEmpty() && paswd.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Fields Empty!", Toast.LENGTH_SHORT).show();
                } else if (!(emailID.isEmpty() && paswd.isEmpty())) {
                    mRegProgress.setTitle("Registering User...");
                    mRegProgress.setMessage("Please wait while we create your account");
                    mRegProgress.setCanceledOnTouchOutside(false);
                    mRegProgress.show();
                    registerUser(emailID, paswd);
                } else {
                    Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(MainActivity.this, ActivityLogin.class);
                I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(I);
            }
        });
    }
    public void registerUser(String email, String password){
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener() {
            @Override
            public void onComplete(@NonNull Task task) {

                if (!task.isSuccessful()) {


                    FirebaseUser current_user= FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);

                    HashMap<String, String> userMap = new HashMap<>();
                    userMap.put("name", display_name);
                    userMap.put("status", "Hi there i'm using SoS chat app");
                    userMap.put("image", "default");
                    userMap.put("thumb image", "default");

                    mDatabase.setValue(userMap);


                    /*
                    mRegProgress.hide();

                    Toast.makeText(MainActivity.this.getApplicationContext(),
                            "Signup unsuccessful: " + task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                            */

                } else {
                    mRegProgress.setMessage("Successfully created account!");
                    mRegProgress.dismiss();
                    startActivity(new Intent(MainActivity.this, ActivityLogin.class));
                }
            }
        });
    }
}
