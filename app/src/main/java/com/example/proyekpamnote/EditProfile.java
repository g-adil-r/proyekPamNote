package com.example.proyekpamnote;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
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

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_IMAGE_PICK = 1;
    EditText etNama, etUsername, etOldPassword, etNewPassword;
    ImageView btnBackEdit,btnUpdateImage;
    Button btnUpdateProfile;
    String imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        btnBackEdit = findViewById(R.id.btnBackEdit);
        btnUpdateProfile = findViewById(R.id.btnLogout);
        etNama = findViewById(R.id.etNama);
        etUsername = findViewById(R.id.etUsername);
        etOldPassword = findViewById(R.id.etOldPassword);
        etNewPassword = findViewById(R.id.etNewPassword);

        btnUpdateImage = findViewById(R.id.profileImage);

        btnBackEdit.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        btnUpdateImage.setOnClickListener(this);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, HomePage.class));
            finish();
        }

        String uid = user.getUid();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("profiles").child(uid);

        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize a DataSnapshot object with the retrieved data
//                DataSnapshot snapshot = dataSnapshot;
                if (dataSnapshot.exists()) {
                    String nama = dataSnapshot.child("nama").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    imageUrl = dataSnapshot.child("downloadUrl").getValue(String.class);
                    loadImage();

                    // Update the EditText fields with the retrieved values
                    etNama.setText(nama);
                    etUsername.setText(username);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        etOldPassword.setTransformationMethod(new PasswordTransformationMethod());
        etNewPassword.setTransformationMethod(new PasswordTransformationMethod());

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackEdit:
                back();
                break;
            case R.id.profileImage:
                updateImage();
                break;
            case R.id.btnLogout:
                updateProfile();
                break;
        }
    }
    private void back() {
        finish();
    }
    private void loadImage() {
        // Retrieve the download URL from SharedPreferences
//        SharedPreferences preferences = getSharedPreferences("ImagePrefs", MODE_PRIVATE);
        // Declaring executor to parse the URL
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Once the executor parses the URL
        // and receives the image, handler will load it
        // in the ImageView
        Handler handler = new Handler(Looper.getMainLooper());
        // Only for Background process (can take time depending on the Internet speed)
        executor.execute(() -> {
            // Image URL
            // Tries to get the image and post it in the ImageView
            // with the help of Handler
            try {
                // Only for making changes in UI
                handler.post(() -> {
                    // btnUpdateImage.setImageBitmap(image[0]);

                    Glide.with(getApplicationContext())
                            .load(imageUrl)
                            .override(150, 150)
                            .error(R.drawable.profile)
                            .into(btnUpdateImage);
                });
            }
            // If the URL does not point to
            // an image or any other kind of failure
            catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private void updateImage() {
        // Create an intent to open the image gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            uploadSelectedImage(selectedImageUri);
        }
    }

    private void uploadSelectedImage(Uri imageUri) {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        Log.d("ImageUri", imageUri.toString());
        // Create a storage reference to the cloud storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // Generate a unique ID for the uploaded file
//        String fileId = UUID.randomUUID().toString();
        // Create a storage reference with the unique ID as the filename
        StorageReference imageRef = storageRef.child("images/" + uid + ".jpg");

        // Upload the image file to Firebase Storage
        imageRef.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get the download URL of the uploaded image
                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri downloadUri) {
                                // Handle the download URL of the uploaded image here
                                String imageUrl = downloadUri.toString();

                                DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference().child("profiles").child(uid);
                                updateRef.child("downloadUrl").setValue(imageUrl);

                                Log.d("downloadUri", imageUrl);

                                loadImage();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                Toast.makeText(EditProfile.this, "Failed to get download URL", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(EditProfile.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateProfile() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        String uid = user.getUid();
        String nama = (String) etNama.getText().toString();
        String username = (String) etUsername.getText().toString();
        String oldPassword = (String) etOldPassword.getText().toString();
        String newPassword = (String) etNewPassword.getText().toString();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("profiles").child(uid);

        if (!oldPassword.isEmpty()) {
            databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        AuthCredential credential = EmailAuthProvider
                                .getCredential(user.getEmail(), oldPassword);

                        user.reauthenticate(credential).addOnSuccessListener(unused -> {
                            user.updatePassword(newPassword).addOnSuccessListener(unused1 -> {
                                Toast.makeText(EditProfile.this, "Berhasil", Toast.LENGTH_SHORT).show();
                            }).addOnFailureListener(e -> {
                                Toast.makeText(EditProfile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                            });
                        }).addOnFailureListener(e -> {
                            Toast.makeText(EditProfile.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            finish();
        }

        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference().child("profiles").child(uid);

        updateRef.child("nama").setValue(nama);
        updateRef.child("username").setValue(username).addOnSuccessListener(unused -> {
//            finish();
        });

//        updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (dataSnapshot.exists()) {
//                    String nama = dataSnapshot.child("nama").getValue(String.class);
//                    String username = dataSnapshot.child("username").getValue(String.class);
////                  String newPassword = dataSnapshot.child("password").getValue(String.class);
//
//                    // Update the EditText fields with the retrieved values
//                    etNama.setText(nama);
//                    etUsername.setText(username);
////                  etNewPassword.setText(password);
//
//                    finish();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Toast.makeText(EditProfile.this, "Failed to Read Data", Toast.LENGTH_SHORT).show();
//            }
//
//        });

    }
    private boolean validatePass() {
        boolean result = true;
        if (TextUtils.isEmpty(etNewPassword.getText().toString())) {
            etNama.setError("Required");
            result = false;
        } else {
            etNama.setError(null);
        }
        return result;
    }
}