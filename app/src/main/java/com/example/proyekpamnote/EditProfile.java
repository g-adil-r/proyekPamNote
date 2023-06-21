package com.example.proyekpamnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{

    private static final int REQUEST_IMAGE_PICK = 1;
    EditText etNama, etUsername,etEmail, etPassword;
    ImageView btnBackEdit,btnUpdateImage;
    Button btnUpdateProfile;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        btnBackEdit = findViewById(R.id.btnBackEdit);
        btnUpdateProfile = findViewById(R.id.btnLogout);
        etNama = findViewById(R.id.etNama);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdateImage = findViewById(R.id.profileImage);

        btnBackEdit.setOnClickListener(this);
        btnUpdateProfile.setOnClickListener(this);
        etNama.setOnClickListener(this);
        etUsername.setOnClickListener(this);
        etEmail.setOnClickListener(this);
        etPassword.setOnClickListener(this);
        btnUpdateImage.setOnClickListener(this);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        String uid = acct.getId();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("notes").child(uid);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize a DataSnapshot object with the retrieved data
                DataSnapshot snapshot = dataSnapshot;
                if (dataSnapshot.exists()) {
                    String nama = dataSnapshot.child("nama").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);

                    // Update the EditText fields with the retrieved values
                    etNama.setText(nama);
                    etUsername.setText(username);
                    etEmail.setText(email);
                    etPassword.setText(password);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        etPassword.setTransformationMethod(new PasswordTransformationMethod());

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
    private void updateImage() {
        // Create an intent to open the image gallery
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            uploadSelectedImage(selectedImageUri);
        }
    }

    private void uploadSelectedImage(Uri imageUri) {
        Log.d("ImageUri", imageUri.toString());
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Create a storage reference to the cloud storage
        StorageReference storageRef = FirebaseStorage.getInstance().getReference();
        // Generate a unique ID for the uploaded file
        String fileId = UUID.randomUUID().toString();
        // Create a storage reference with the unique ID as the filename
        StorageReference imageRef = storageRef.child("images/" + fileId);

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
                                Log.d("downloadUri", imageUrl);

                                    // Store the download URL in SharedPreferences
                                    SharedPreferences preferences = getSharedPreferences("ImagePrefs", MODE_PRIVATE);
                                    SharedPreferences.Editor editor = preferences.edit();
                                    editor.putString("imageUrl", imageUrl);
                                    editor.apply();

                                // Declaring executor to parse the URL
                                ExecutorService executor = Executors.newSingleThreadExecutor();

                                // Once the executor parses the URL
                                // and receives the image, handler will load it
                                // in the ImageView
                                Handler handler = new Handler(Looper.getMainLooper());

                                // Initializing the image
                                final Bitmap[] image = {null};

                                // Only for Background process (can take time depending on the Internet speed)
                                executor.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Image URL
                                        String imageURL = downloadUri.toString();
                                        // Tries to get the image and post it in the ImageView
                                        // with the help of Handler
                                        try {
                                            InputStream in = new java.net.URL(imageURL).openStream();
                                            image[0] = BitmapFactory.decodeStream(in);
                                            // Only for making changes in UI
                                            handler.post(new Runnable() {
                                                @Override
                                                public void run() {
//                                                    btnUpdateImage.setImageBitmap(image[0]);

                                                    Glide.with(getApplicationContext())
                                                            .load(imageURL)
                                                            .override(100, 100)
                                                            .into(btnUpdateImage);
                                                }
                                            });
                                        }
                                        // If the URL does not point to
                                        // an image or any other kind of failure
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                });
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

        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);

        String uid = acct.getId();
        String nama = (String) etNama.getText().toString();
        String username = (String) etUsername.getText().toString();
        String email = (String) etEmail.getText().toString();
        String password = (String) etPassword.getText().toString();

        DatabaseReference updateRef = FirebaseDatabase.getInstance().getReference().child("notes").child(uid);

        updateRef.child("nama").setValue(nama);
        updateRef.child("username").setValue(username);
        updateRef.child("email").setValue(email);
        updateRef.child("password").setValue(password);

        updateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    String nama = dataSnapshot.child("nama").getValue(String.class);
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    String password = dataSnapshot.child("password").getValue(String.class);

                    // Update the EditText fields with the retrieved values
                    etNama.setText(nama);
                    etUsername.setText(username);
                    etEmail.setText(email);
                    etPassword.setText(password);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(EditProfile.this, "Failed to Read Data", Toast.LENGTH_SHORT).show();
            }

        });

    }
//    private boolean validateForm() {
//        boolean result = true;
//        if (TextUtils.isEmpty(etNama.getText().toString())) {
//            etNama.setError("Required");
//            result = false;
//        } else {
//            etNama.setError(null);
//        }
//        if (TextUtils.isEmpty(etUsername.getText().toString())) {
//            etUsername.setError("Required");
//            result = false;
//        } else {
//            etUsername.setError(null);
//        }
//        if (TextUtils.isEmpty(etEmail.getText().toString())) {
//            etEmail.setError("Required");
//            result = false;
//        } else {
//            etEmail.setError(null);
//        }
//        if (TextUtils.isEmpty(etPassword.getText().toString())) {
//            etPassword.setError("Required");
//            result = false;
//        } else {
//            etPassword.setError(null);
//        }
//        return result;
//    }
}