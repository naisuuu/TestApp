 package com.example.sos;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

 public class SettingsActivity extends AppCompatActivity {
    private DatabaseReference mUserDatabase;
    private FirebaseUser mCurrentUser;

    private CircleImageView mDisplayImage;
    private TextView mName;
    private TextView mStatus;

    private Button mStatusBtn;
    private Button mImageBtn;

    private static final int GALLERY_PICK = 1;

    //Storage
    private StorageReference mImageStorage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        mDisplayImage = findViewById(R.id.settings_image);
        mName = findViewById(R.id.settings_displayname);
        mStatus = findViewById(R.id.settings_status);
        mStatusBtn = findViewById(R.id.settings_status_btn);
        mImageBtn = findViewById(R.id.setttings_image_btn);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        String current_uid = mCurrentUser.getUid();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid);

        //Storage
        mImageStorage = FirebaseStorage.getInstance().getReference();

        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue().toString();
                String image = dataSnapshot.child("image").getValue().toString();
                String status = dataSnapshot.child("status").getValue().toString();
                String thumb_image = dataSnapshot.child("thumb_image").getValue().toString();

                mName.setText(name);
                mStatus.setText(status);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        mStatusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String status_value = mStatus.getText().toString();
                Intent status_intent = new Intent(SettingsActivity.this, StatusActivity.class);
                status_intent.putExtra("status_value",status_value);
                startActivity(status_intent);
            }
        });

        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .start(SettingsActivity.this);

                       //Can pick between above or below for image picker

               /* Intent galleryIntent = new Intent();
                galleryIntent.setType("image/");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(galleryIntent, "SELECT IMAGE"),GALLERY_PICK); //opens the documents app
                */
            }
        });
    }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if(requestCode == GALLERY_PICK && resultCode == RESULT_OK){

             Uri imageUri = data.getData();

             CropImage.activity(imageUri)
                     .setAspectRatio(1,1)
                     .start(this);
             //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show(); Displays chosen data
         }
         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { //makes sure result is taken from created cropactivity
             CropImage.ActivityResult result = CropImage.getActivityResult(data); //stores result as result

             if (resultCode == RESULT_OK) {

                 Uri resultUri = result.getUri(); //gives us uri of cropped image        this below makes a random string and adds jpg extension to it
                 StorageReference filepath = mImageStorage.child("profile_images").child(random()+".jpg");
                 filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                         if(task.isSuccessful()){
                             Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_LONG).show();
                         } else {
                             Toast.makeText(SettingsActivity.this, "Not working", Toast.LENGTH_LONG).show();
                         }
                     }
                 });

             } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                 Exception error = result.getError();
             }
         }
     }

     //Random String Generator
     public static String random(){
         Random generator = new Random();
         StringBuilder randomStringBuilder = new StringBuilder();
         int randomLength = generator.nextInt(10);
         char tempChar;
         for (int i = 0; i < randomLength; i++){
             tempChar = (char)(generator.nextInt(96)+32);
             randomStringBuilder.append(tempChar);
         }
         return randomStringBuilder.toString();
     }
 }
