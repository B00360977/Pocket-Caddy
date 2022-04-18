package com.example.golfapp;

import androidx.appcompat.app.AppCompatActivity;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * This class is used to search and display results for each Hole
 * This class links closely to RoundResultsActivity & SearchResultsActivity
 */

public class HoleResultsActivity extends AppCompatActivity {

    private String roundID;
    private String holeNumber;
    private TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set content for the screen
        setContentView(R.layout.activity_hole_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView tableTitle = findViewById(R.id.tableTitleHole);
        TextView tableCourseName = findViewById(R.id.tableCourseName);

        // retrieve variables fro last activity that have been passed
        Bundle extras = getIntent().getExtras();
        tableLayout = findViewById(R.id.tableMainHole);

        if (extras != null) {
            // getting variables from bundle
            roundID = extras.getString("roundID");
            String courseName = extras.getString("courseName");
            String roundDate = extras.getString("roundDate");
            holeNumber = extras.getString("holeNumber");

            tableTitle.setText("Details for Hole No. " + holeNumber);
            tableCourseName.setText(courseName + " - " + roundDate);

            ResultSet data = getHoleDetails(roundID);
            populateTableData(data);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // retrieves results from the database
    private ResultSet getHoleDetails(String roundDetails) {

        ResultSet resultSet = null;
        try {
            // creating a new database connection
            DatabaseConnector databaseConnector = new DatabaseConnector();
            Connection connection = databaseConnector.connectionClass();
            if (connection != null) {
                // building the SQL query
                String query = "SELECT dbo.[tbl.Shots].shotNumber, dbo.[tbl.Golf_Clubs].clubName, dbo.[tbl.Shots].distance\n" +
                    "FROM dbo.[tbl.Shots] INNER JOIN\n" +
                    "dbo.[tbl.Hole] ON dbo.[tbl.Shots].holeID = dbo.[tbl.Hole].holeID INNER JOIN\n" +
                    "dbo.[tbl.Golf_Clubs] ON dbo.[tbl.Shots].clubID = dbo.[tbl.Golf_Clubs].clubID\n" +
                    "WHERE (dbo.[tbl.Hole].roundID = " + roundID + ") AND (dbo.[tbl.Hole].holeNumber = " + holeNumber + ")";

                // execute statement and get response
                Statement statement = connection.createStatement();
                resultSet =  statement.executeQuery(query);
            } else {
                String connectionResult = "Check Connection";
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return resultSet;
    }

    // updates the screen with the data received from the database
    private void populateTableData(ResultSet data) {

        while (true) {
            try {
                if (!data.next()) break;
                TableRow tableRow = new TableRow(this);
                TextView tv1 = new TextView(this);
                TextView tv2 = new TextView(this);
                TextView tv3 = new TextView(this);

                try {
                    tv1.setText(data.getString(1));
                    tv2.setText(data.getString(2));
                    tv3.setText(data.getString(3));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                tv1.setGravity(Gravity.CENTER);
                tv2.setGravity(Gravity.CENTER);
                tv3.setGravity(Gravity.CENTER);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv3.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv1.setTypeface(null, Typeface.BOLD);
                tv2.setTypeface(null, Typeface.BOLD);
                tv3.setTypeface(null, Typeface.BOLD);
                tv1.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tv2.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tv3.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                tv1.setTextColor(Color.BLACK);
                tv2.setTextColor(Color.BLACK);
                tv3.setTextColor(Color.BLACK);

                tv1.setTextSize(15);
                tv2.setTextSize(15);
                tv3.setTextSize(15);

                tv1.setPadding(1, 20, 20, 20);
                tv2.setPadding(1, 20, 10, 20);
                tv3.setPadding(1, 20, 10, 20);

                tableRow.addView(tv1);
                tableRow.addView(tv2);
                tableRow.addView(tv3);

                tableLayout.addView(tableRow);

                View line = new View(this);
                line.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
                line.setBackgroundColor(Color.rgb(51, 51, 51));
                tableLayout.addView(line);

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

}