package com.example.sos;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

///Hey///
public class ActivityLogin extends AppCompatActivity {
    public EditText loginEmailId, logInpasswd;
    Button btnLogIn;
    TextView signup;
    FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private ProgressDialog mLogProgress;
    private DatabaseReference userDatabase;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        userDatabase = FirebaseDatabase.getInstance().getReference().child("Users");
        firebaseAuth = FirebaseAuth.getInstance();
        loginEmailId = findViewById(R.id.loginEmail);
        logInpasswd = findViewById(R.id.loginpaswd);
        btnLogIn = findViewById(R.id.btnLogIn);
        signup = findViewById(R.id.TVSignIn);
        mLogProgress = new ProgressDialog(this);
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Intent I = new Intent(ActivityLogin.this, MainActivity.class);
                    I.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(I);
                }
            }
        };
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(ActivityLogin.this, RegisterActivity.class);
                startActivity(I);
            }
        });
        btnLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = loginEmailId.getText().toString();
                String password = logInpasswd.getText().toString();

                if (!TextUtils.isEmpty(email) || !TextUtils.isEmpty(password)) {

                    mLogProgress.setTitle("Logging In");
                    mLogProgress.setMessage("Please wait while we check your credentials.");
                    mLogProgress.setCanceledOnTouchOutside(false);
                    mLogProgress.show();

                    loginUser(email, password);

                }

            }
        });
    }

    private void loginUser(String email, String password) {


        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    mLogProgress.dismiss();

                    final String current_user_id = firebaseAuth.getCurrentUser().getUid();
                    FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                        @Override
                        public void onComplete(@NonNull Task<InstanceIdResult> task) {
                            if (task.isSuccessful()) {
                                String token = task.getResult().getToken();
                                userDatabase.child(current_user_id).child("device_token").setValue(token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Intent mainIntent = new Intent(ActivityLogin.this, MainActivity.class);
                                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(mainIntent);
                                        finish();

                                    }
                                });
                            }
                        }
                    });
                } else {

                    mLogProgress.hide();

                    String task_result = task.getException().getMessage();

                    Toast.makeText(ActivityLogin.this, "Error : " + task_result, Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}
