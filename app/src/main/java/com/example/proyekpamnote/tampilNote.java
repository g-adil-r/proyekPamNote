package com.example.proyekpamnote;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class tampilNote extends AppCompatActivity {
    ImageView btAddNote;
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
}