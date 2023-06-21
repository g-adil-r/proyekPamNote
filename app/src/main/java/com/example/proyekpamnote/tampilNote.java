package com.example.proyekpamnote;

import android.content.Intent;
import android.content.SharedPreferences;
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

        // Store the download URL in SharedPreferences
        SharedPreferences preferences = getSharedPreferences("ImagePrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString("imageUrl", user.getUid());
        editor.apply();
        // Retrieve the download URL from SharedPreferences
//        SharedPreferences preferences = getSharedPreferences("ImagePrefs", MODE_PRIVATE);
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
                // Tries to get the image and post it in the ImageView
                // with the help of Handler
                try {
                    InputStream in = new java.net.URL(imageUrl).openStream();
                    image[0] = BitmapFactory.decodeStream(in);
                    // Only for making changes in UI
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
// btnUpdateImage.setImageBitmap(image[0]);

                            Glide.with(getApplicationContext())
                                    .load(imageUrl)
                                    .override(60, 60)
                                    .into(imgProf);
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