package com.example.proyekpamnote;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
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

import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class tampilNote extends AppCompatActivity implements View.OnClickListener{
    ImageView btAddNote, imgProf;
    FirebaseUser user;
    ProgressBar progressBar;
    RecyclerView noteView;
    DatabaseReference dbNote;
    NoteAdapter noteAdapter;
    String imageUrl;
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

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference profiles = FirebaseDatabase.getInstance().getReference().child("profiles").child(uid);

        profiles.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                // Initialize a DataSnapshot object with the retrieved data
//                DataSnapshot snapshot = dataSnapshot;
                if (dataSnapshot.exists()) {
                    imageUrl = dataSnapshot.child("downloadUrl").getValue(String.class);
                    loadImage();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void loadImage() {
        // Retrieve the download URL from SharedPreferences
//        SharedPreferences preferences = getSharedPreferences("ImagePrefs", MODE_PRIVATE);
        // Declaring executor to parse the URL
        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Once the executor parses the URL
        // and receives the image, handler will load it
        // in the ImageView
        Handler handler = new Handler(Looper.getMainLooper());
        // Only for Background process (can take time depending on the Internet speed)
        executor.execute(() -> {
            // Image URL
            // Tries to get the image and post it in the ImageView
            // with the help of Handler
            try {
                // Only for making changes in UI
                handler.post(() -> {
                    // btnUpdateImage.setImageBitmap(image[0]);

                    Glide.with(getApplicationContext())
                            .load(imageUrl)
                            .override(60, 60)
                            .error(R.drawable.profile)
                            .into(imgProf);
                });
            }
            // If the URL does not point to
            // an image or any other kind of failure
            catch (Exception e) {
                e.printStackTrace();
            }
        });
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
                Log.d("profile", e.getLocalizedMessage());
                Toast.makeText(tampilNote.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }