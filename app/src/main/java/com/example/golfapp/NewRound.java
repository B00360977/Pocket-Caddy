package com.example.golfapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.Toast;

public class NewRound extends AppCompatActivity {

    ImageButton addLocation;
    Button startRound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_round);

        String[] arraySpinner = new String[] {
                "Bishopbriggs Golf Club", "Cadder Golf Club"
        };
        Spinner clubChoice = (Spinner) findViewById(R.id.select_location);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        clubChoice.setAdapter(adapter);

        addLocation = findViewById(R.id.addLocation);
        startRound = findViewById(R.id.button);

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddLocationClick(view);
            }
        });

        startRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String club = clubChoice.getSelectedItem().toString().trim();
                GlobalVariables.getInstance().setClubLocation(club);
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }

    public void onAddLocationClick(View view) {
    
        AlertDialog.Builder dialogBuilder;
        AlertDialog dialog;
        dialogBuilder = new AlertDialog.Builder(this);
        final View addLocationPopupView = getLayoutInflater().inflate(R.layout.add_location_popup, null);
        dialogBuilder.setView(addLocationPopupView);

        dialog = dialogBuilder.create();
        dialog.show();
        dialog.getWindow().setBackgroundDrawable((new ColorDrawable(Color.TRANSPARENT)));


        Button cancelBtn = addLocationPopupView.findViewById(R.id.cancel_button);
        Button saveBtn = addLocationPopupView.findViewById(R.id.save_button);

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //update DB with new location
                dialog.dismiss();
                //refresh dropdown list
            }
        });


    }
}