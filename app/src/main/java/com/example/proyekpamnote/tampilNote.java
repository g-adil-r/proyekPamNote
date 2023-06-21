package com.example.proyekpamnote;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class tampilNote extends AppCompatActivity implements View.OnClickListener{
    ImageView btAddNote, imgProf;
    FirebaseUser user;
    ProgressBar progressBar;
    RecyclerView noteView;
    DatabaseReference dbNote;
    NoteAdapter noteAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tampil_note);

        user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            startActivity(new Intent(this, HomePage.class));
            finish();
        }

        noteView = findViewById(R.id.note_rec_view);
        btAddNote = findViewById(R.id.bt_add_note);
        progressBar = findViewById(R.id.progressBar);
        imgProf =  findViewById(R.id.imgProf);

        imgProf.setOnClickListener(this);

        dbNote = FirebaseDatabase.getInstance().getReference()
                .child("notes")
                .child(user.getUid());

        dbNote.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(tampilNote.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        FirebaseRecyclerOptions<Note> options
                = new FirebaseRecyclerOptions.Builder<Note>()
                .setQuery(dbNote,Note.class)
                .build();

        noteAdapter = new NoteAdapter(options);
        noteView.setLayoutManager(new LinearLayoutManager(this));
        noteView.setAdapter(noteAdapter);
        noteView.setItemAnimator(null);

        btAddNote.setOnClickListener(v ->
                startActivity(new Intent(getApplicationContext(), CreateNote.class))
        );

        // Retrieve the download URL from SharedPreferences
        SharedPreferences preferences = getSharedPreferences("MyPrefs", MODE_PRIVATE);
        String imageUrl = preferences.getString("imageUrl", null);

        // Load the image using Glide
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .override(60, 60)
                .error(R.drawable.profile)
                .into(imgProf);
    }

    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.imgProf:
                profile();
                break;
    }
    }

        private void profile() {
            try {
                startActivity(new Intent(tampilNote.this, ViewProfile.class));
            } catch (Exception e) {
                Toast.makeText(tampilNote.this, "Gagal masuk edit profile", Toast.LENGTH_SHORT).show();
            }
        }
    }