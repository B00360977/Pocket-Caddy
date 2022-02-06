package com.example.golfapp;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnector {

    String uname, pass, ip, port, database;

    public Connection connectionClass() {

        ip = "pocket-caddy.database.windows.net";
        database = "pocket-caddy-db";
        pass = "Aardvark1";
        uname = "pocketCaddyAdmin";
        port = "1433";

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection connection = null;
        String connectionURL = null;

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
