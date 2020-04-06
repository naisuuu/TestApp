 package com.example.sos;

 import android.app.ProgressDialog;
 import android.content.Intent;
 import android.graphics.Bitmap;
 import android.net.Uri;
 import android.os.Bundle;
 import android.view.View;
 import android.widget.Button;
 import android.widget.TextView;
 import android.widget.Toast;

 import androidx.annotation.NonNull;
 import androidx.annotation.Nullable;
 import androidx.appcompat.app.AppCompatActivity;

 import com.google.android.gms.tasks.Continuation;
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
 import com.google.firebase.storage.StorageReference;
 import com.google.firebase.storage.UploadTask;
 import com.squareup.picasso.Picasso;
 import com.theartofdev.edmodo.cropper.CropImage;
 import com.theartofdev.edmodo.cropper.CropImageView;

 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.util.HashMap;
 import java.util.Map;

 import de.hdodenhof.circleimageview.CircleImageView;
 import id.zelory.compressor.Compressor;


 public class SettingsActivity extends AppCompatActivity {
     public static final int CAMERA_REQUEST = 2;
     private static final int GALLERY_PICK = 1;
     private DatabaseReference mUserDatabase;
     private FirebaseUser mCurrentUser;
     private CircleImageView mDisplayImage;
     private TextView mName;
     private TextView mStatus;
     private Button mStatusBtn;
     private Button mImageBtn;
     private ProgressDialog mProgressDialog;

     //Storage
     private StorageReference mImageStorage;

     Uri pickedImageUri;

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
                 if (!image.equals("default")) {
                     Picasso.get().load(image).into(mDisplayImage);
                 }

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
                 status_intent.putExtra("status_value", status_value);
                 startActivity(status_intent);
             }
         });

         mImageBtn.setOnClickListener(new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 CropImage.activity()
                         .setGuidelines(CropImageView.Guidelines.ON)
                         .start(SettingsActivity.this);
             }
         });
     }

     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
         super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == GALLERY_PICK || requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {

             Uri imageUri = data.getData();

             CropImage.activity(imageUri)
                     .setAspectRatio(1, 1)
                     .setMinCropWindowSize(500, 500)
                     .start(this);
             //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show(); Displays chosen data
         } else {
             Uri imageUri = data.getData();

             CropImage.activity(imageUri)
                     .setAspectRatio(1, 1)
                     .setMinCropWindowSize(500, 500)
                     .start(this);
             //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show(); Displays chosen data
             Toast.makeText(SettingsActivity.this, "Reached here", Toast.LENGTH_LONG).show();
         }
         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { //makes sure result is taken from created cropactivity
             CropImage.ActivityResult result = CropImage.getActivityResult(data); //stores result as result

             if (resultCode == RESULT_OK) {

                 mProgressDialog = new ProgressDialog(SettingsActivity.this);
                 mProgressDialog.setTitle("Uploading Image…");
                 mProgressDialog.setMessage("Please wait while we upload and process the image.");
                 mProgressDialog.setCanceledOnTouchOutside(false);
                 mProgressDialog.show();

                 final Uri resultUri = result.getUri();

                 File thumb_filepath = new File(resultUri.getPath());
                 Bitmap thumb_bitmap = new Compressor(this)
                         .setMaxHeight(200)
                         .setMaxWidth(200)
                         .setQuality(75)
                         .compressToBitmap(thumb_filepath);


                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                 final byte[] thumb_byte = baos.toByteArray();

                 String currentUserId = mCurrentUser.getUid();//Save image as userId instead to prevent loads of images
                 final StorageReference filepath = mImageStorage.child("profile_images").child(currentUserId + ".jpg");
                 final StorageReference thumb_FilePath = mImageStorage.child("profile_images").child("thumbs").child(currentUserId + ".jpg");


                 filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {


                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //task returns a download url for image
                         if (task.isSuccessful()) {
                             Task<Uri> urlTask = filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                 @Override
                                 public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                     if (!task.isSuccessful()) {
                                         throw task.getException();
                                     }
                                     // Continue with the task to get the download URL
                                     return filepath.getDownloadUrl();
                                 }
                             }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                 @Override
                                 public void onComplete(@NonNull Task<Uri> task) {
                                     if (task.isSuccessful()) {
                                         Uri downloadUri = task.getResult();
                                         final String download_url = downloadUri.toString();

                                         UploadTask uploadTask = thumb_FilePath.putBytes(thumb_byte);
                                         uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                             @Override
                                             public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                                                 //Uri thumb_downloadUrl = thumb_task.getResult();
                                                 if (thumb_task.isSuccessful()) {
                                                     Task<Uri> urlTask = filepath.putFile(resultUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                                         @Override
                                                         public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                                             if (!task.isSuccessful()) {
                                                                 throw task.getException();
                                                             }
                                                             // Continue with the task to get the download URL
                                                             return filepath.getDownloadUrl();
                                                         }
                                                     }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                                         @Override
                                                         public void onComplete(@NonNull Task<Uri> task) {
                                                             if (task.isSuccessful()) {
                                                                 Uri downloadUri = task.getResult();
                                                                 final String thumb_downloadUrl = downloadUri.toString();

                                                                 Map update_hashMap = new HashMap();
                                                                 update_hashMap.put("image", download_url);
                                                                 update_hashMap.put("thumb_image", thumb_downloadUrl);

                                                                 mUserDatabase.updateChildren(update_hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                     @Override
                                                                     public void onComplete(@NonNull Task<Void> task) {
                                                                         if (task.isSuccessful()) {
                                                                             mProgressDialog.dismiss();
                                                                             Toast.makeText(SettingsActivity.this, "Success Uploading", Toast.LENGTH_LONG).show();
                                                                         }
                                                                     }
                                                                 });
                                                             } else {
                                                                 Toast.makeText(SettingsActivity.this, "Error Uploading Thumbnail", Toast.LENGTH_LONG).show();
                                                                 mProgressDialog.dismiss();
                                                             }
                                                         }
                                                     });
                                                 }
                                             }
                                         });
                                     }
                                 }
                             });
                         }
                     }
                 });
             } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                 Exception error = result.getError();
                 Toast.makeText(SettingsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
             }
         }
     }
 }
