package com.example.proyekpamnote;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class HomePage extends AppCompatActivity {
    private Button btnmasuk, btndaftar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);
        btnmasuk = findViewById(R.id.btn_masuk);
        btndaftar = findViewById(R.id.btn_daftar);

        btnmasuk.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
        });
        btndaftar.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Register.class));
        });
    }
}