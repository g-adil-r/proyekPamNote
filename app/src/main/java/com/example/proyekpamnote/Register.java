package com.example.proyekpamnote;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.nfc.Tag;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private EditText editEmail, editPassword, editNama, editUsername;
    private Button btnMasuk;
    private Button btnDaftar;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        editEmail = findViewById(R.id.et_email);
        editPassword = findViewById(R.id.et_password);
        btnDaftar = findViewById(R.id.btn_daftar);
        btnMasuk = findViewById(R.id.btn_masuk1);

        mAuth = FirebaseAuth.getInstance();

        btnDaftar.setOnClickListener(this);
        btnMasuk.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
    }


    public void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(Register.this, tampilNote.class);
            startActivity(intent);
        } else {
            Toast.makeText(Register.this, "Log in First", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_masuk1:
                login(editEmail.getText().toString(), editPassword.getText().toString());
                break;
            case R.id.btn_daftar:
                signUp(editEmail.getText().toString(), editPassword.getText().toString());
                break;
            }
        }

    private void signUp(String email, String password) {
        if (!validateForm()){
            return;
        }
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "createUserWithEmail:succes");
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    Toast.makeText(Register.this, user.toString(),Toast.LENGTH_SHORT).show();
                } else {
                    Log.d(TAG, "createUserWithEmail:failure", task.getException());
                    Toast.makeText(Register.this, task.getException().toString(), Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(editEmail.getText().toString())) {
            editEmail.setError("Required");
            result = false;
        } else {
            editEmail.setError(null);
        }
        if (TextUtils.isEmpty(editPassword.getText().toString())) {
            editPassword.setError("Required");
            result = false;
        }else {
            editPassword.setError(null);
        }
        return result;
    }

    public void login(String email, String password) {
        if (!validateForm()){
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Log.d(TAG, "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                    Toast.makeText(Register.this, user.toString(), Toast.LENGTH_SHORT).show();
                    updateUI(user);
                }else {
                    Log.d(TAG, "signInWithEmail:failure", task.getException());
                    Toast.makeText(Register.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    updateUI(null);
                }
            }
        });
    }

    private void login() {
    }
}

