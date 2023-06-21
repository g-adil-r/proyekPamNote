package com.example.proyekpamnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class ViewProfile extends AppCompatActivity implements View.OnClickListener {

    Button btnLogout, btnProfile, btnReturn;
    TextView tvNama, tvEmail;
    ImageView profileImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_profile);

        btnLogout = findViewById(R.id.btnLogout);
        btnProfile = findViewById(R.id.btnProfile);
        btnReturn = findViewById(R.id.btnReturn);
        tvNama = findViewById(R.id.tvNama);
        tvEmail = findViewById(R.id.tvEmail);
        profileImage = findViewById(R.id.profileImage);

        btnLogout.setOnClickListener(this);
        btnProfile.setOnClickListener(this);
        btnReturn.setOnClickListener(this);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, HomePage.class));
            finish();
        }

        String uid = user.getUid();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("notes").child(uid);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize a DataSnapshot object with the retrieved data
                DataSnapshot snapshot = dataSnapshot;
                if (dataSnapshot.exists()) {
                    String username = dataSnapshot.child("username").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    // Update the EditText fields with the retrieved values
                    tvNama.setText(username);
                    tvEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Retrieve the download URL from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("ImagePrefs", MODE_PRIVATE);
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
                String imageUrl = preferences.getString("imageUrl", null);
//                Log.d("imageUrl", imageUrl);
                // Tries to get the image and post it in the ImageView
                // with the help of Handler
                try {
                    InputStream in = new java.net.URL(imageUrl).openStream();
                    image[0] = BitmapFactory.decodeStream(in);
                    // Only for making changes in UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
//                      btnUpdateImage.setImageBitmap(image[0]);

                            Glide.with(getApplicationContext())
                                    .load(imageUrl)
                                    .override(100, 100)
                                    .into(profileImage);
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

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.btnLogout:
                        logout();
                        break;
                    case R.id.btnProfile:
                        profile();
                        break;
                    case R.id.btnReturn:
                        back();
                        break;
                }
            }


            private void back() {
                finish();
            }

            private void profile() {
                try {
                    startActivity(new Intent(ViewProfile.this, EditProfile.class));
                } catch (Exception e) {
                    Toast.makeText(ViewProfile.this, "Gagal masuk edit profile", Toast.LENGTH_SHORT).show();
                }
            }

            private void logout() {

                FirebaseAuth user = FirebaseAuth.getInstance();
                user.signOut();
                finishAffinity();
                startActivity(new Intent(ViewProfile.this, HomePage.class));

            }

}





