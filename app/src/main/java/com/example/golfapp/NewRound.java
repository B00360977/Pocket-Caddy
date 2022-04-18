package com.example.golfapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import java.sql.PreparedStatement;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

/**
 * This class handles the start of any new round. Here a user can select which club they are playing
 * and this app will add this to the database
 */

public class NewRound extends AppCompatActivity {

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
        ImageButton addLocation = findViewById(R.id.addLocation);
        Button startRound = findViewById(R.id.button);

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
                addNewRound(club);
                startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            }
        });
    }

    // inserts the new round into tbl.Round in the database
    private void addNewRound(String location) {

        String courseID = null;
        ResultSet resultSet = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(System.currentTimeMillis());
        String currentDate = simpleDateFormat.format(date);
        int roundID = 0;

        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.connectionClass();
            if (connection != null) {
                String query = "SELECT dbo.[tbl.Courses].courseID \n" +
                        "FROM dbo.[tbl.Courses]\n" +
                        "WHERE dbo.[tbl.Courses].courseName = '"+ location +"'";

                Statement statement = connection.createStatement();
                resultSet =  statement.executeQuery(query);
                System.out.println(resultSet);
            } else {
                String connectionResult = "Check Connection";
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        while (true) {
            try {
                assert resultSet != null;
                if (!resultSet.next()) break;
                courseID  = resultSet.getString(1);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }

        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.connectionClass();
            if (connection != null) {
                String[] returnID = { "roundID" };
                String query = "INSERT INTO dbo.[tbl.Round](dateOfMatch, courseID)\n" +
                        "VALUES ('" + currentDate + "', " + courseID + ")";

                PreparedStatement statement = connection.prepareStatement(query, returnID);
                int affectedRows = statement.executeUpdate();
                if (affectedRows == 0) {
                    throw new SQLException("Insert Failed. No rows updated");
                }
                try (ResultSet resultSet1 = statement.getGeneratedKeys()) {
                    if (resultSet1.next()) {
                        roundID = resultSet1.getInt(1);
                    }
                }
                GlobalVariables.getInstance().setRoundID(roundID);
            } else {
                String connectionResult = "Check Connection";
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
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