package com.example.proyekpamnote;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class tampilNote extends AppCompatActivity {
    ImageView btAddNote;
    FirebaseUser user;
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

        btAddNote = findViewById(R.id.bt_add_note);

        dbNote = FirebaseDatabase.getInstance().getReference()
                .child("notes")
                .child(user.getUid());

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
}