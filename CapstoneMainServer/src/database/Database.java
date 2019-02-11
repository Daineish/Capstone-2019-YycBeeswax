package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {

	private Connection connection;
	
	//TODO move these values into a external config file
	private String db = "jdbc:mysql://localhost:3306/capstone_db";
	private String username = "root";
	private String password = "capstone";
	
	
	public Database()
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
     * Gets the list of stakeholders from the database who are watching the hive with HiveID.
     * @param hiveID - the hiveID where the data is coming from.
     * @return the list of stakeholders in an ArrayList, or return a null if an error is detected
     */
	public ArrayList<String> getStakeholderEmail(int HiveID){
		try
		{
			ResultSet rs;
			ArrayList<String> emailList = new ArrayList<String>();
			
			//build query to be executed
			String query = "SELECT stakeholder.Email FROM stakeholder INNER JOIN watching ON stakeholder.Name = watching.Name WHERE watching.HiveID = ?";
			PreparedStatement prepared = connection.prepareStatement(query);
			prepared.setInt(1, HiveID);
			//execute query and put result into result set rs
			rs = prepared.executeQuery();
			
			//parse result set, adding emails to the ArrayList until result set is empty
			while(rs.next()){
				emailList.add(rs.getString("Email"));
			}
			return emailList;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			return null;
		}
	}
	
	/**
     * performs a threshold check on parameter data, notifying stakeholders of failed check and stores the data in the database regardless
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
				NotificationHandler handler = new NotificationHandler();
				ArrayList<String> list = getStakeholderEmail(hiveId);
				handler.notifyStakeholders(list, checkResult, hiveId);
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
	
	//returns "passed" if it passes check, else, generates the notification msg
	public String thresholdCheck(int hiveId, float temp, float humidity)throws SQLException{
		boolean passedCheck = true;
		String msg = "";
		ResultSet rs;
		String query = "SELECT * FROM hiveinfo WHERE HiveId = ?";
		PreparedStatement prepared = connection.prepareStatement(query);
		prepared.setInt(1, hiveId);
		rs = prepared.executeQuery();
		rs.next();
		//check if temperature is within bounds
		if(temp > rs.getFloat("TempUB") || temp < rs.getFloat("TempLB")){
			msg += "Anomalous Temperature of " + temp + "�C detected in hive #" + hiveId + "\n";
			passedCheck = false;
		}
		//check if humidity is within bounds
		if(humidity > rs.getFloat("HumidUB") || humidity < rs.getFloat("HumidLB")){
			msg += "Anomalous Humidity of " + humidity + "% detected in hive #" + hiveId + "\n";
			passedCheck = false;
		}
		if (passedCheck) return "passed";
		else return msg;
	}
	
	public static void main(String[] args){
		Database db = new Database();
		//ArrayList<String> myList = db.getStakeholderEmail(20);
		//System.out.println(myList.toString());
		boolean result = db.storeSensorData(20, 65, 90);
		System.out.println(result);
		db.closeConnection();
		System.out.println("connection closed, goodbye");
	}
	
}
