package com.example.golfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import javax.xml.transform.Result;

public class RoundResultsActivity extends AppCompatActivity {

    private TextView textView;
    private String roundID, courseName, roundDate;
    private TableLayout tableLayout;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_results);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        tableLayout = findViewById(R.id.tableMain2);

        if (extras != null) {
            roundID = extras.getString("roundID");
            courseName = extras.getString("courseName");
            roundDate = extras.getString("date");
            TextView tableTitle = findViewById(R.id.tableTitle);
            TextView tableCourseName = findViewById(R.id.tableCourseName);
            tableTitle.setText("Details for Round " + roundID);
            tableCourseName.setText(courseName + " - " + roundDate);

            ResultSet tableData = getRoundDetails(roundID);
            populateTableData(tableData);
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

    public ResultSet getRoundDetails(String roundDetails) {

        ResultSet resultSet = null;
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            connection = databaseConnector.connectionClass();
            if (connection != null) {
                String query = "SELECT dbo.[tbl.Hole].holeNumber, dbo.[tbl.Hole].numberOfShots\n" +
                "FROM         dbo.[tbl.Round] INNER JOIN\n" +
                "dbo.[tbl.Hole] ON dbo.[tbl.Round].roundID = dbo.[tbl.Hole].roundID INNER JOIN\n" +
                "dbo.[tbl.Shots] ON dbo.[tbl.Hole].holeID = dbo.[tbl.Shots].holeID\n" +
                "WHERE     (dbo.[tbl.Hole].roundID = " + roundDetails +")\n" +
                "GROUP BY dbo.[tbl.Hole].holeNumber, dbo.[tbl.Hole].numberOfShots\n" +
                "ORDER BY dbo.[tbl.Hole].holeNumber";

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

    public void populateTableData(ResultSet data) {

        while (true) {
            try {
                if (!data.next()) break;
                TableRow tableRow = new TableRow(this);
                TextView tv1 = new TextView(this);
                TextView tv2 = new TextView(this);

                try {
                    tv1.setText(data.getString(1));
                    tv2.setText(data.getString(2));
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                tv1.setGravity(Gravity.CENTER);
                tv2.setGravity(Gravity.CENTER);
                tv1.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv2.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                tv1.setTypeface(null, Typeface.BOLD);
                tv2.setTypeface(null, Typeface.BOLD);
                tv1.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));
                tv2.setLayoutParams(new TableRow.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f));

                tv1.setTextColor(Color.BLACK);
                tv2.setTextColor(Color.BLACK);

                tv1.setTextSize(15);
                tv2.setTextSize(15);

                tv1.setPadding(1,20,20,20);
                tv2.setPadding(1,20,10,20);

                tableRow.addView(tv1);
                tableRow.addView(tv2);

                tableRow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(getApplicationContext(), HoleResultsActivity.class);
                        i.putExtra("roundID", roundID);
                        i.putExtra("holeNumber", tv1.getText());
                        i.putExtra("courseName", courseName);
                        i.putExtra("roundDate", roundDate);
                        startActivity(i);
                    }
                });
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
