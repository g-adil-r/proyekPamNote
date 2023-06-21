package com.example.proyekpamnote;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class splashScreen extends AppCompatActivity {
    private static final int SPLASH_DURATION = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Handler untuk menunda navigasi ke activity utama
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Intent untuk mengarahkan ke activity utama
                Intent intent = new Intent(splashScreen.this, HomePage.class);
                startActivity(intent);
                finish(); // Menutup activity splash screen agar tidak dapat dikembalikan
            }
        }, SPLASH_DURATION);
    }
}