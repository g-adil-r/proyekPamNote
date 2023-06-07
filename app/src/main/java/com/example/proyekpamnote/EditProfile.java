package com.example.proyekpamnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.proyekpamnote.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditProfile extends AppCompatActivity implements View.OnClickListener{

    Button btnBackEdit, btnUpdateProfile;
    EditText etNama, etUsername,etEmail, etPassword;
    ImageButton btnUpdateImage;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);

        btnBackEdit = findViewById(R.id.btnBackEdit);
        btnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        etNama = findViewById(R.id.etNama);
        etUsername = findViewById(R.id.etUsername);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnUpdateImage = findViewById(R.id.btnUpdateImage);

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
            case R.id.btnUpdateImage:
                updateImage();
                break;
            case R.id.btnUpdateProfile:
                updateProfile();
                break;
        }
    }
    private void back() {
        finish();
    }
    private void updateImage() {
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