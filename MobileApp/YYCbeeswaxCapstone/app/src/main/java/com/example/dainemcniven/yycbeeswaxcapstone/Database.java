package com.example.dainemcniven.yycbeeswaxcapstone;

import java.sql.Connection;
import java.sql.Date;
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


    public ResultSet GetSensorsData(int hiveId, Date startTimeFrame, Date endTimeFrame, String sensorType)
    {
        ResultSet rs;
        try
        {
            String query = "SELECT * FROM sensordata WHERE hiveId = ? AND Time >= ? AND Time <= ? AND SensorType = ?";
            PreparedStatement prepared = connection.prepareStatement(query);
            prepared.setInt(1, hiveId);
            prepared.setDate(2, startTimeFrame);
            prepared.setDate(3, endTimeFrame);
            prepared.setString(4, sensorType);
            //execute query and put result into result set rs
            rs = prepared.executeQuery();
        }
        catch(SQLException e)
        {return null;}//return null if failed to get sensor data

        return rs;
    }

    public boolean AuthenticateLogin (String username, String password){
        try{
            String query = "SELECT * FROM login WHERE Username = ? AND Password = ?";
            PreparedStatement prepared = connection.prepareStatement(query);
            prepared.setString(1, username);
            prepared.setString(2, password);
            //execute query and put result into result set rs
            ResultSet rs = prepared.executeQuery();
            return rs.next();//return 1 if an object exists in resultset (valid user & pass), return 0 if no objects in rs
        }
        catch(SQLException e)
        {}
        return true;
    }


    public ResultSet GetHives()
    {
        ResultSet rs;
        try
        {
            String query = "SELECT * FROM hiveinfo";
            PreparedStatement prepared = connection.prepareStatement(query);
            //execute query and put result into result set rs
            rs = prepared.executeQuery();
        }
        catch(SQLException e)
        {return null;}//return null if failed to retrieve hiveinfo

        return rs;
    }

    public boolean UpdateHives(ResultSet rs){
        try{
            String update = "UPDATE hiveinfo SET Location = ?, Owner = ?, TempLB = ?, TempUB = ?, HumidLB = ?, HumidUB = ?, BlockTime = ? WHERE HiveId = ?";
            PreparedStatement prepared = connection.prepareStatement(update);
            while(rs.next()) {
                prepared.setString(1, rs.getString("Location"));
                prepared.setString(2, rs.getString("Owner"));
                prepared.setFloat(3, rs.getFloat("TempLB"));
                prepared.setFloat(4, rs.getFloat("TempUB"));
                prepared.setFloat(5, rs.getFloat("HumidLB"));
                prepared.setFloat(6, rs.getFloat("HumidUB"));
                prepared.setFloat(7, rs.getFloat("BlockTime"));
                prepared.setInt(8, rs.getInt("HiveId"));
                //execute update
                prepared.executeUpdate();
            }
        }
        catch(SQLException e)
        {return false;}//failed to successfully update
        return true;//successfully updated
    }

}
