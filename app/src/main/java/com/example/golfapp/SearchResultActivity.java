package com.example.golfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class SearchResultActivity extends AppCompatActivity {

    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            startDate = extras.getString("startDate");
            endDate = extras.getString("endDate");
        }



    }
}