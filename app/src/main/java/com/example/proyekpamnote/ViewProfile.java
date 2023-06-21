package com.example.proyekpamnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
        String uid = user.getUid();

        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference().child("notes").child(uid);

        databaseRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize a DataSnapshot object with the retrieved data
                DataSnapshot snapshot = dataSnapshot;
                if (dataSnapshot.exists()) {
                    String nama = dataSnapshot.child("nama").getValue(String.class);
                    String email = dataSnapshot.child("email").getValue(String.class);
                    // Update the EditText fields with the retrieved values
                    tvNama.setText(nama);
                    tvEmail.setText(email);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        // Retrieve the download URL from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageUrl = preferences.getString("imageUrl", null);

        // Load the image using Glide
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .override(100, 100)
                .into(profileImage);
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
                finish();
                startActivity(new Intent(ViewProfile.this, Login.class));

            }

}





