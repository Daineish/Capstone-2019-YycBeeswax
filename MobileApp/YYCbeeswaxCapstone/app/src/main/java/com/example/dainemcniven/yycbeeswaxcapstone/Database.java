package com.example.dainemcniven.yycbeeswaxcapstone;

import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database
{
    private static Database single_instance = null;

    private Connection connection;
    private String propertiesFileName = "config.properties";
    private String database = "jdbc:mysql://remotemysql.com:3306/jYq4805Trh";
    private String username = "jYq4805Trh";
    private String password = "AAuzfpgw6V";

    public static Database getInstance()
    {
        if (single_instance == null)
            single_instance = new Database();

        return single_instance;
    }

    public Database()
    {
        getProperties();
        initializeConnection();
    }

    public void getProperties()
    {

//        FileInputStream inStream;
//        try
//        {
//            String current = new java.io.File( "." ).getCanonicalPath();
//            Log.e("Current Directory: ", current);
//            Properties properties = new Properties();
//            inStream = new FileInputStream(propertiesFileName);
//            properties.load(inStream);
//
//            //load config values from file
//            database = properties.getProperty("database");
//            username = properties.getProperty("username");
//            password = properties.getProperty("password");
//            inStream.close();
//
//        }
//        catch (IOException e)
//        {
//            e.printStackTrace();
//            System.exit(1);
//        }
    }

    public void initializeConnection()
    {
        try
        {
            //attempt to start connection with config file values
            connection = DriverManager.getConnection(database, username, password);
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

    /**
     * Gets the BlockageThreshhold for a given hiveID, needed PCB side to know when to send alerts on blockage
     * @param HiveID - the hiveID of the PCB starting up
     * @return the BlockTime from the database, return -1 on error
     */
    public float getBlockTime(int HiveID)
    {
        try
        {
            ResultSet rs;
            //build query to be executed
            String query = "SELECT BlockTime FROM hiveinfo WHERE watching.HiveID = ?";
            PreparedStatement prepared = connection.prepareStatement(query);
            prepared.setInt(1, HiveID);
            //execute query and put result into result set rs
            rs = prepared.executeQuery();

            //check if result set has a result, return the BlockTime of the result
            rs.next();
            return rs.getFloat("BlockTime");
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return -1;//error occurred
        }
    }

    /**
     * Gets the list of stakeholders and their notificationTypes from the database who are watching the hive with HiveID.
     * @param HiveID - the hiveID where the data is coming from.
     * @return the list of stakeholders emails and notificationTypes in a ResultSet, or return a null if an error is detected
     */
    public ResultSet getStakeholderEmail(int HiveID){
        try
        {
            ResultSet rs;
            //ArrayList<String> emailList = new ArrayList<String>();

            //build query to be executed
            //get email and notification type for stakeholders watching the hive who's notificationType isn't set to NONE
            String query = "SELECT stakeholder.Email, watching.NotificationType FROM stakeholder INNER JOIN watching ON stakeholder.Name = watching.Name WHERE watching.HiveID = ? AND watching.NotificationType != ?";
            PreparedStatement prepared = connection.prepareStatement(query);
            prepared.setInt(1, HiveID);
            prepared.setString(2, "NONE");
            //execute query and put result into result set rs
            rs = prepared.executeQuery();
            return rs;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return null;
        }
    }

    public boolean UpdateHives(String loc, String owner, float tempLB, float tempUB, float humidLB, float humidUB, float blockTime, int id)
    {
        try
        {
            String update = "UPDATE hiveinfo SET Location = ?, Owner = ?, TempLB = ?, TempUB = ?, HumidLB = ?, HumidUB = ?, BlockTime = ? WHERE HiveId = ?";
            PreparedStatement prepared = connection.prepareStatement(update);
            prepared.setString(1, loc);
            prepared.setString(2, owner);
            prepared.setFloat(3, tempLB);
            prepared.setFloat(4, tempUB);
            prepared.setFloat(5, humidLB);
            prepared.setFloat(6, humidUB);
            prepared.setFloat(7, blockTime);
            prepared.setInt(8, id);
            // execute update
            prepared.executeUpdate();
        }
        catch(SQLException e) { return false; }
        return true;
    }

    public boolean UpdateHives(ResultSet rs)
    {
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

    public boolean AuthenticateLogin (String username, String password)
    {
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

    public void ServerQuery(String statement)
    {

    }

}
