package com.example.proyekpamnote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class CreateNote extends AppCompatActivity implements View.OnClickListener {

    ImageView btnBackCreate;
    TextView btnSelesai;
    EditText etJudul, etDeskripsi;
    Note note;
    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, HomePage.class));
            finish();
        }

        btnBackCreate = findViewById(R.id.btnBackEdit);
        btnSelesai = findViewById(R.id.btnSelesai);
        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);

        btnBackCreate.setOnClickListener(this);
        btnSelesai.setOnClickListener(this);
        etJudul.setOnClickListener(this);
        etDeskripsi.setOnClickListener(this);

        note = new Note();
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this, gso);
        dataRef = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnBackEdit:
                back();
                break;
            case R.id.btnSelesai:
                done();
                break;
        }
    }

    private void back() {
        finish();
    }
    private void done() {
        if (!validateForm()) {
            return;
        }

        // GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser acct = FirebaseAuth.getInstance().getCurrentUser();

        String title = etJudul.getText().toString();
        String desc = etDeskripsi.getText().toString();
        String personUID = acct.getUid();

        String key = dataRef.child("notes").child(personUID).push().getKey();

        Note noteData = new Note(key, title, desc);

        dataRef.child("notes").child(personUID).child(key).setValue(noteData).addOnSuccessListener(CreateNote.this, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Toast.makeText(CreateNote.this, "Note Created", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(CreateNote.this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(CreateNote.this, "Failed to Create Note", Toast.LENGTH_SHORT).show();
            }
        });
        finish();
    }
    private boolean validateForm() {
        boolean result = true;
        if (TextUtils.isEmpty(etJudul.getText().toString())) {
            etJudul.setError("Required");
            result = false;
        } else {
            etJudul.setError(null);
        }
        if (TextUtils.isEmpty(etDeskripsi.getText().toString())) {
            etDeskripsi.setError("Required");
            result = false;
        } else {
            etDeskripsi.setError(null);
        }
        return result;
    }
}