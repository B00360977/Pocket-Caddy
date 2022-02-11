package com.example.golfapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.golfapp.ui.history.HistoryFragment;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SearchResultActivity extends AppCompatActivity {

    private String startDate, endDate;
    private TableLayout tableLayout;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        tableLayout = findViewById(R.id.tableMain);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            startDate = extras.getString("startDate");
            endDate = extras.getString("endDate");
        }

        ResultSet data = getSearchResults(startDate, endDate);

        if (data != null) {

            createBaseTable();

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

                    tv1.setTypeface(null, Typeface.BOLD);
                    tv2.setTypeface(null, Typeface.BOLD);
                    tv3.setTypeface(null, Typeface.BOLD);

                    tv1.setTextColor(Color.BLACK);
                    tv2.setTextColor(Color.BLACK);
                    tv3.setTextColor(Color.BLACK);

                    tv1.setTextSize(15);
                    tv2.setTextSize(15);
                    tv3.setTextSize(15);

                    tv1.setPadding(10,20,20,20);
                    tv2.setPadding(20,20,10,20);
                    tv3.setPadding(10,20,10,20);

                    tableRow.addView(tv1);
                    tableRow.addView(tv2);
                    tableRow.addView(tv3);

                    tableRow.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(getApplicationContext(), RoundResultsActivity.class);
                            i.putExtra("roundID", tv3.getText());
                            i.putExtra("courseName", tv2.getText());
                            i.putExtra("date", tv1.getText());
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
        } else {
            noSearchResult();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void noSearchResult() {

        new AlertDialog.Builder(this)
                .setTitle("No Results Found")
                .setMessage("Your search returned no results please widen your search")
                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, new HistoryFragment()).commit();
                    }
                })
                .setCancelable(false)
                .show();
    }

    public ResultSet getSearchResults(String startDate, String endDate) {

        ResultSet resultSet = null;
        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            connection = databaseConnector.connectionClass();
            if (connection != null) {
                String query = "SELECT   dbo.[tbl.Round].dateOfMatch, dbo.[tbl.Courses].courseName, dbo.[tbl.Round].roundID\n" +
                "FROM dbo.[tbl.Round] INNER JOIN\n" +
                "dbo.[tbl.Courses] ON dbo.[tbl.Round].courseID = dbo.[tbl.Courses].courseID\n" +
                "WHERE (dbo.[tbl.Round].dateOfMatch >= CONVERT(DATE, '"+ startDate +"', 102)) AND (dbo.[tbl.Round].dateOfMatch <= CONVERT(DATE, '"+ endDate +"', 102))";

                Statement statement = connection.createStatement();
                resultSet =  statement.executeQuery(query);
                System.out.println(resultSet);
            } else {
                String connectionResult = "Check Connection";
            }
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
        }
        return resultSet;
    }

    public void createBaseTable() {
        TableRow tableRow0 = new TableRow(this);
        TextView textView0 = new TextView(this);
        textView0.setText("Date");
        textView0.setTextColor(Color.BLACK);
        textView0.setTextSize(30);
        textView0.setGravity(Gravity.CENTER);
        textView0.setPadding(10,20,10,20);
        tableRow0.addView(textView0);

        TextView textView1 = new TextView(this);
        textView1.setText("Course");
        textView1.setTextColor(Color.BLACK);
        textView1.setTextSize(30);
        textView1.setGravity(Gravity.CENTER);
        textView1.setPadding(10,20,10,20);
        tableRow0.addView(textView1);

        TextView textView2 = new TextView(this);
        textView2.setText("Round ID");
        textView2.setTextColor(Color.BLACK);
        textView2.setTextSize(30);
        textView2.setGravity(Gravity.CENTER);
        textView2.setPadding(10,20,10,20);
        tableRow0.addView(textView2);

        tableLayout.addView(tableRow0);
    }


}