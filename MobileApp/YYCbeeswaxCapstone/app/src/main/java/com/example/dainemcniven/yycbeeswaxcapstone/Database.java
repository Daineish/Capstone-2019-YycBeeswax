package com.example.dainemcniven.yycbeeswaxcapstone;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by dainemcniven on 2019-03-07.
 */

public class Database
{
    private static Database single_instance = null;

    private Connection connection;

    //TODO move these values into a external config file
    private String db = "jdbc:mysql://localhost:3306/capstone_db";
    private String username = "root";
    private String password = "capstone";

    public static Database getInstance()
    {
        if (single_instance == null)
            single_instance = new Database();

        return single_instance;
    }

    private Database()
    {
        initializeConnection();
    }

    public void initializeConnection()
    {
        try
        {
            //attempt to start connection with config file values
            connection = DriverManager.getConnection(db, username, password);
        }
        catch (SQLException e)
        {
            //exit if db connection fails and print error log
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void closeConnection()
    {
        try
        {
            //close database connection
            connection.close();
        }
        catch (SQLException e)
        {
            //exit if db connection fails to close and print error log
            e.printStackTrace();
            System.exit(1);
        }
    }

    //////////////////////////////////////
    // Functions to be used to get data //
    //////////////////////////////////////


    public ArrayList<String> GetSensors()
    {
        // TODO: Get actual sensor names from database
        ArrayList<String> list = new ArrayList<String>();
        list.add("Sensor 1");
        list.add("Sensor 2");
        list.add("Sensor 3");

//        try
//        {
//            ResultSet rs;
//            String query = "SELECT name FROM sensordata WHERE ?"; // yeah idk how the DB is structured...
//            PreparedStatement prepared = connection.prepareStatement(query);
//            prepared.setInt(1, 0); // what is this
//            //execute query and put result into result set rs
//            rs = prepared.executeQuery();
//
//            //parse result set, adding emails to the ArrayList until result set is empty
//            while (rs.next())
//            {
//                list.add(rs.getString("Email"));
//            }
//        }
//        catch(SQLException e)
//        {}

        return list;
    }
}
