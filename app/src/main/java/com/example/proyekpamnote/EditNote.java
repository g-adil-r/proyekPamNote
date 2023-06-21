package com.example.proyekpamnote;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EditNote extends AppCompatActivity implements View.OnClickListener {
    String noteKey;
    ImageView btnBackCreate;
    TextView btnSelesai;
    EditText etJudul, etDeskripsi;
    private DatabaseReference dataRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_note);

        btnBackCreate = findViewById(R.id.btnBackEdit);
        btnSelesai = findViewById(R.id.btnSelesai);
        etJudul = findViewById(R.id.etJudul);
        etDeskripsi = findViewById(R.id.etDeskripsi);

        Intent i = getIntent();
        noteKey = i.getStringExtra("key");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, HomePage.class));
            finish();
        }

        dataRef = FirebaseDatabase.getInstance()
                .getReference()
                .child("notes")
                .child(user.getUid());

        dataRef.child(noteKey).get().addOnSuccessListener(dataSnapshot -> {
            etJudul.setText(dataSnapshot.child("title").getValue().toString());
            etDeskripsi.setText(dataSnapshot.child("description").getValue().toString());
        });

        btnBackCreate.setOnClickListener(this);
        btnSelesai.setOnClickListener(this);
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

//        GoogleSignInAccount acct = GoogleSignIn.getLastSignedInAccount(this);
        FirebaseUser acct = FirebaseAuth.getInstance().getCurrentUser();

        String title = etJudul.getText().toString();
        String desc = etDeskripsi.getText().toString();

        Log.d("Create",title);
        Log.d("Create", desc);

        Note newNote = new Note(noteKey,title,desc);
        dataRef.child(noteKey).setValue(newNote).addOnSuccessListener(unused -> {
            Toast.makeText(this, "Berhasil update note", Toast.LENGTH_SHORT).show();
            finish();
        }).addOnFailureListener(e -> {
            Toast.makeText(this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        });
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