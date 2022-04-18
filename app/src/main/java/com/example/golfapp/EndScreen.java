package com.example.golfapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

/**
 * This class is used to display an end screen to the user once a round of golf is completed
 * This is to inform them that all their data has been saved and allow them to return back to
 * the home screen
 */

public class EndScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end_screen);
        Button homeBtn = findViewById(R.id.homeButton);
        // adds listener to home button to launch home screen
        homeBtn.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), HomeActivity.class)));
    }
}