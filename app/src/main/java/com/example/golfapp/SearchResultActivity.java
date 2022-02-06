package com.example.golfapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class SearchResultActivity extends AppCompatActivity {

    private String startDate, endDate;
    Connection connection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            startDate = extras.getString("startDate");
            endDate = extras.getString("endDate");
        }

        getSearchResults();


    }

    public void getSearchResults() {

        try {
            DatabaseConnector databaseConnector = new DatabaseConnector();
            connection = databaseConnector.connectionClass();
            if (connection != null) {
                String query = "SELECT   dbo.[tbl.Round].dateOfMatch, dbo.[tbl.Courses].courseName, dbo.[tbl.Round].roundID\n" +
                        "FROM         dbo.[tbl.Round] INNER JOIN\n" +
                        "                         dbo.[tbl.Courses] ON dbo.[tbl.Round].courseID = dbo.[tbl.Courses].courseID";
                Statement statement = connection.createStatement();
                ResultSet resultSet =  statement.executeQuery(query);

                while (resultSet.next()) {
                    System.out.println(resultSet.getString(1));
                    System.out.println(resultSet.getString(2));
                    System.out.println(resultSet.getString(3));
                }
            } else {
                String connectionResult = "Check Connection";
            }


        } catch (Exception e) {

        }


    }


}