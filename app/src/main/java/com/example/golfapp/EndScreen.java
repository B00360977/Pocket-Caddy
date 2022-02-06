package com.example.golfapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class EndScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        Button homeBtn = findViewById(R.id.homeButton);
        homeBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeActivity.class)));

    }
}