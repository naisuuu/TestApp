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
 import com.iceteck.silicompressorr.SiliCompressor;
 import com.squareup.picasso.Picasso;
 import com.theartofdev.edmodo.cropper.CropImage;
 import com.theartofdev.edmodo.cropper.CropImageView;

 import java.io.ByteArrayOutputStream;
 import java.io.File;
 import java.io.IOException;
 import java.util.HashMap;
 import java.util.Map;
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
                    Picasso.get().load(image).placeholder(R.drawable.default_avatar).into(mDisplayImage);
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
                     .setMinCropWindowSize(500, 500)
                     .start(this);
             //Toast.makeText(SettingsActivity.this,imageUri,Toast.LENGTH_LONG).show(); Displays chosen data
         }
         if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) { //makes sure result is taken from created cropactivity
             CropImage.ActivityResult result = CropImage.getActivityResult(data); //stores result as result

             if (resultCode == RESULT_OK) {

                 mProgressDialog = new ProgressDialog(SettingsActivity.this);
                 mProgressDialog.setTitle("Uploading Image…");
                 mProgressDialog.setMessage("Please wait while we upload and process the image.");
                 mProgressDialog.setCanceledOnTouchOutside(false);
                 mProgressDialog.show();

                 Uri resultUri = result.getUri();

                 File thumb_filepath = new File(resultUri.getPath());
                 String thumb_filePath = thumb_filepath.toString();

                 Bitmap thumb_bitmap = null;
                 try {
                     thumb_bitmap = SiliCompressor.with(SettingsActivity.this).getCompressBitmap(thumb_filePath);
                 } catch (IOException e) {
                     e.printStackTrace();
                     Toast.makeText(SettingsActivity.this, "Reached here", Toast.LENGTH_LONG).show();
                 }

                 ByteArrayOutputStream baos = new ByteArrayOutputStream();
                 thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                 final byte[] thumb_byte = baos.toByteArray();

                 String currentUserId = mCurrentUser.getUid();//Save image as userId instead to prevent loads of images
                 StorageReference filepath = mImageStorage.child("profile_images").child(currentUserId + ".jpg");
                 final StorageReference thumb_FilePath = mImageStorage.child("profile_images").child(currentUserId + ".jpg");


                 filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) { //task returns a download url for image

                         if (task.isSuccessful()) {
                             final String download_url = task.getResult().getStorage().getDownloadUrl().toString();

                             UploadTask uploadTask = thumb_FilePath.putBytes(thumb_byte);
                             uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                 @Override
                                 public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                     String thumb_downloadUrl = thumb_task.getResult().getStorage().getDownloadUrl().toString();

                                     if (thumb_task.isSuccessful()) {

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
                         } else {
                             Toast.makeText(SettingsActivity.this, "Error Uploading", Toast.LENGTH_LONG).show();
                         }

                         // Too long and unnecessary
                         /*if(task.isSuccessful()){
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
                                         String download_url = downloadUri.toString();
                                         mUserDatabase.child("image").setValue(download_url).addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if(task.isSuccessful()){
                                                     mProgressDialog.dismiss();
                                                     Toast.makeText(SettingsActivity.this,"Success",Toast.LENGTH_LONG).show();
                                                 }
                                             }
                                         });
                                         File thumb_filepath = new File(downloadUri.getPath());
                                         Bitmap compressedImageFile = Compressor.compress(this, thumb_filepath);
                                     }
                                 }
                             });*/
                         Toast.makeText(SettingsActivity.this, "Working", Toast.LENGTH_LONG).show();
                     }
                 });
             } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                 Exception error = result.getError();
                 Toast.makeText(SettingsActivity.this, error.toString(), Toast.LENGTH_LONG).show();
             }
         }
     }

     //Random String Generator
     public static String random(){
         Random generator = new Random();
         StringBuilder randomStringBuilder = new StringBuilder();
         int randomLength = generator.nextInt(20); //determines length of name
         char tempChar;
         for (int i = 0; i < randomLength; i++){
             tempChar = (char)(generator.nextInt(96)+32);
             randomStringBuilder.append(tempChar);
         }
         return randomStringBuilder.toString();
     }
 }
