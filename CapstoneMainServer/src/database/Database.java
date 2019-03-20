package database;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

	private Connection connection;
	private NotificationHandler handler;
	private String propertiesFileName = "config.properties";//**************
	//TODO move these values into a external config file that is read on database initialization
	private String database;// = "jdbc:mysql://localhost:3306/capstone_db";
	private String username; // = "root";
	private String password; // = "capstone";
	private String fromEmail; // = "hivenotificationalert@gmail.com";
	private String fromPass; // = "Capstone2019";
	
	
	public Database()
	{
		getProperties();
		handler = new NotificationHandler(fromEmail, fromPass);
		initializeConnection();
	}
	
	public void getProperties(){
		
		FileInputStream inStream;
		try{
			Properties properties = new Properties();
			inStream = new FileInputStream("config.properties");
			properties.load(inStream);
			
			//load config values from file
			database = properties.getProperty("database");
			username = properties.getProperty("username");
			password = properties.getProperty("password");
			fromEmail = properties.getProperty("fromEmail");
			fromPass = properties.getProperty("fromPass");
			inStream.close();
			
		}catch (IOException e){
			e.printStackTrace();
			System.exit(1);
		}
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
	
	public void closeConnection(){
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
	public float getBlockTime(int HiveID){
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
     * @param hiveID - the hiveID where the data is coming from.
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
	
	/**
     * requests a threshold check, notifying stakeholders of failed check and stores the data in the database regardless
     * @param hiveID - the hiveID where the data is coming from.
     * @param temp  - the temperature to send.
     * @param humid - the humidity to send.
     * @return true if successfully stored, else false.
     */
	public boolean storeSensorData(int hiveId, float temp, float humid){
		try
		{
			//perform check to see if notification required
			String checkResult = thresholdCheck(hiveId, temp, humid);
			if(!checkResult.equals("passed")){
				ResultSet list = getStakeholderEmail(hiveId);
				handler.notifyStakeholders(list, hiveId, checkResult, temp, humid, 0);
			}
			
			//prepared statement for security AND for ease of reading
			String query = "INSERT INTO sensordata (HiveId, Time, SensorType, SensorData) VALUES (?, NOW(), ?, ?)";
			PreparedStatement prepared = connection.prepareStatement(query);
			
			//INSERT temp data
			prepared.setInt(1, hiveId);
			prepared.setString(2, "TEMP");
			prepared.setFloat(3, temp);
			prepared.execute();
			System.out.println("Temperature data of " + temp + " successfully added to database");
			
			//INSERT humidity data
			prepared.setString(2, "HUMIDITY");
			prepared.setFloat(3, humid);
			prepared.execute();
			System.out.println("Humidity data of " + humid + " successfully added to database");
			
			//return true if both values added without error
			return true;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			//return false if error occurred
			return false;
		}
	}
	
	/**
     * stores the blockage data in the database
     * @param hiveID - the hiveID where the data is coming from.
     * @param blockTime - time entrance was blocked, to be stored in database
     * @return true if successfully stored, else false.
     */
	public boolean storeBlockage(int hiveId, float blockTime){
		try
		{
			
			//prepared statement for security AND for ease of reading
			String query = "INSERT INTO sensordata (HiveId, Time, SensorType, SensorData) VALUES (?, NOW(), ?, ?)";
			PreparedStatement prepared = connection.prepareStatement(query);
			
			//INSERT blockage data
			prepared.setInt(1, hiveId);
			prepared.setString(2, "BLOCKAGE");
			prepared.setFloat(3, blockTime);
			prepared.execute();
			System.out.println("Entrance blockage data of " + blockTime + " seconds successfully added to database");
			
			//return true if blockage added to database correctly
			return true;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			//return false if error occurred
			return false;
		}
	}

	/**
     * performs a threshold check on temp and humidity against the database sensor bounds
     * @param hiveId - the hiveID where the data is coming from.
     * @param temp  - the temperature to check.
     * @param humid - the humidity to check
     * @return returns passed if no bounds were exceded, otherwise, returns T, H, or TH corresponding to the exceeded bounds
     */
	public String thresholdCheck(int hiveId, float temp, float humid)throws SQLException{
		int notificationCase = 0;//used to track what values failed the check
		ResultSet rs;
		String query = "SELECT * FROM hiveinfo WHERE HiveId = ?";
		PreparedStatement prepared = connection.prepareStatement(query);
		prepared.setInt(1, hiveId);
		rs = prepared.executeQuery();
		rs.next();
		//check if temperature is within bounds
		if(temp > rs.getFloat("TempUB") || temp < rs.getFloat("TempLB")){
			notificationCase += 1;
		}
		//check if humidity is within bounds
		if(humid > rs.getFloat("HumidUB") || humid < rs.getFloat("HumidLB")){
			notificationCase += 2;
		}
		//0 = all passed, 1 = temp failed, 2 = humidity failed, 3 = both failed, default to passed
		switch(notificationCase){
			case 0:
				return "passed";
			case 1:
				return "T";
			case 2:
				return "H";
			case 3:
				return "TH";
			default:
				return "passed";
		}
	}
	
	public static void main(String[] args){
		Database db = new Database();
		db.storeSensorData(65, 25, 25);
	}
	
}
