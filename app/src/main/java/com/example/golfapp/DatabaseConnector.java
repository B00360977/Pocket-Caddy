package com.example.golfapp;

import android.os.StrictMode;
import android.util.Log;
import java.sql.Connection;
import java.sql.DriverManager;

/**
 * This class is used to generate a connection to the cloud based database
 * This is used in a number of classes to run SQL queries on the database
 * for inserting new records and searching the user previous history
 */

public class DatabaseConnector {

    public Connection connectionClass() {

        //key parameters for connecting to the database
        String ip = "pocket-caddy.database.windows.net";
        String database = "pocket-caddy-db";

        //secret keys which are hidden to users and stored securely within the app
        String pass = BuildConfig.DATABASE_PASSWORD;
        String uname = BuildConfig.DATABASE_USERNAME;

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL;

        try {
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            connectionURL = "jdbc:jtds:sqlserver://" + ip + "/" + database + ";ssl=request";
            connection = DriverManager.getConnection(connectionURL, uname, pass);
        } catch (Exception e) {
            Log.e("Error ", e.getMessage());
        }
        return connection;
    }
}
