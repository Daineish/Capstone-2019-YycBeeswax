package database;


import java.io.FileInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database {

	private Connection connection;
	private NotificationHandler handler;
	private String database;
	private String username;
	private String password;
	private String fromEmail;
	private String fromPass;
	
	
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
     * Gets the list of stakeholder emails and their notificationTypes from the database who are watching the hive with HiveID.
     * @param HiveID - the hiveID where the data is coming from.
     * @return the list of stakeholders emails and notificationTypes in a ResultSet, or return a null if an error is detected
     */
	public ResultSet getStakeholderEmail(int HiveID){
		try
		{
			ResultSet rs;
			
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
     * @param hiveId - the hiveID where the data is coming from.
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
     * stores the blockage data in the database and alerts watching stakeholders
     * @param hiveID - the hiveID where the data is coming from.
     * @param blockTime - time entrance was blocked, to be stored in database
     * @return true if successfully stored, else false.
     */
	public boolean storeBlockage(int hiveId, float blockTime){
		try
		{	
			ResultSet list = getStakeholderEmail(hiveId);
			handler.notifyStakeholders(list, hiveId, "B", 0, 0, blockTime);
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
     * @return returns passed if no bounds were exceeded, otherwise, returns T, H, or TH corresponding to the exceeded bounds
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
	
	

	
	
	
	/////////////////////////////////////////////////////////////////////////////////
	/////////////////////////////Mobile App functions////////////////////////////////
	/////////////////////////////////////////////////////////////////////////////////
	/**
     * prints sensor data from a ResultSet to the console in a readable format, used for testing
     * @param rs - the ResultSet to be printed
     */
	public void printSensorData(ResultSet rs){
		try{
			while(rs.next()){
				System.out.println("HiveId: " + rs.getString("HiveId") + "	Time: " + rs.getString("Time") + "	Type: " + rs.getString("SensorType") + "	Data: " + rs.getString("SensorData") + "\n");
			}
		}catch (SQLException e) 
		{
			e.printStackTrace();
			//return false if error occurred
		}
	}
	
	/**
     * retrieves the list of all hives from database
     * @return returns a ResultSet containing all hiveinfo for every hive in database
     */
	public ResultSet getHiveList()
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
	
	/**
     * retrieves the list of all stakeholders from database
     * @return returns a ResultSet containing all stakeholders and associated data
     */
	public ResultSet getStakeholderList(){
		try
		{
			ResultSet rs;
			//build query to be executed
			String query = "SELECT * FROM stakeholder";
			PreparedStatement prepared = connection.prepareStatement(query);
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
     * retrieves the list of all watching entries from database where the given stakeholderName corresponds to the rows Name column
     * @param stakeholderName - the name of the stakeholder to retrieve all associated watching rows for
     * @return returns a ResultSet containing all watching rows and associated data where stakeholderName = Name
     */
	public ResultSet getWatchingList(String stakeholderName){
		try
		{
			ResultSet rs;
			//build query to be executed
			String query = "SELECT * FROM watching WHERE Name = ?";
			PreparedStatement prepared = connection.prepareStatement(query);
			prepared.setString(1, stakeholderName);
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
     * updates a hive row in database corresponding to the hiveId in database equal to "origId"
     * @param hiveId - the new hiveId
     * @param loc  - the new location of the hive
     * @param owner - the new owner of the hive
     * @param tempLB - the new temperature lower bound for the hive
     * @param tempUB - the new temperature upper bound for the hive  
     * @param humidLB - the new humidity lower bound for the hive
     * @param humidUB - the new humidity upper bound for the hive   
     * @param blockTime - the new blockage time threshold
     * @param origId - the original hiveId of the hive prior to requesting update, used to find the correct hive for purpose of the update 
     * @return returns true if update was successful, false otherwise
     */
	public boolean updateHive(int hiveId, String loc, String owner, float tempLB,
						   float tempUB, float humidLB, float humidUB, float blockTime, int origId)
	{
		try
		{
			String query = "UPDATE hiveinfo SET HiveId = ?, Location = ?, Owner = ?, TempLB = ?, TempUB = ?, HumidLB = ?," +
					"HumidUB = ?, BlockTime = ? WHERE HiveId = ?";
			PreparedStatement prepared = connection.prepareStatement(query);
			prepared.setInt(1, hiveId);
			prepared.setString(2, loc);
			prepared.setString(3, owner);
			prepared.setFloat(4, tempLB);
			prepared.setFloat(5, tempUB);
			prepared.setFloat(6, humidLB);
			prepared.setFloat(7, humidUB);
			prepared.setFloat(8, blockTime);
			prepared.setInt(9, origId);
			prepared.executeUpdate();
			return true;
		}
		catch(SQLException e)
		{
			System.err.println("Error: " + e.getMessage()); 
			return false;
		}
	}
	
	/**
     * updates a stakeholder row in database corresponding to the stakeholderId in database equal to "stakeholderId"
     * @param stakeholderId - the stakeholderId used to identify the stakeholder
     * @param name  - the updated stakeholder name
     * @param email - the updated stakeholder email
     * @return returns true if update was successful, false otherwise
     */
	public boolean updateStakeholder(int stakeholderId, String name, String email){
		try
		{
			String query = "UPDATE stakeholder SET Name = ?, Email = ? WHERE StakeholderId = ?";
			PreparedStatement prepared = connection.prepareStatement(query);
			prepared.setString(1, name);
			prepared.setString(2, email);
			prepared.setInt(3, stakeholderId);
			prepared.executeUpdate();
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
     * retrieves a subset of the sensordata table based on given query restrictions
     * @param stakeholderId - the stakeholderId used to identify the stakeholder
     * @param hiveId - the hiveId of the hive to get relevant sensor data from, setting it to null retrieves data from all hives
     * @param fromTime - the earliest time bound of data to be retrieved
     * @param toTime - the latest time bound of data to be retrieved
     * @param sensorType - the sensorType of the data to retrieve, setting it to null retrieves all sensor types (TEMP, HUMIDITY, BLOCK)
     * @return returns a ResultSet of all data satisfying the query, or null if error occurs
     */
	public ResultSet getSensorData(String HiveId, String fromTime, String toTime, String sensorType){
		try
		{
			ResultSet rs;
			
			//build query to be executed
			//get email and notification type for stakeholders watching the hive who's notificationType isn't set to NONE
			//String query = "SELECT * FROM sensordata WHERE HiveId = ? AND Time BETWEEN ? AND ? AND SensorType = ?";
			String query = "SELECT * FROM sensordata WHERE (HiveId = ? OR (HiveId IS NOT NULL AND ? IS NULL)) AND (SensorType = ? OR (SensorType IS NOT NULL AND ? IS NULL)) AND (Time BETWEEN ? AND ?)";
			PreparedStatement prepared = connection.prepareStatement(query);
			prepared.setString(1, HiveId);
			prepared.setString(2, HiveId);
			prepared.setString(3, sensorType);
			prepared.setString(4, sensorType);
			prepared.setDate(5, Date.valueOf(fromTime));
			prepared.setDate(6, Date.valueOf(toTime));
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
     * updates a row of watching table with a new value for notificationType
     * * @param hiveId - the hiveId used to indentify a row of watching
     * @param stakeholderName - the stakeholderName used to identify a row of watching
     * @param watchTemp - a boolean whose value reflects whether or not the stakeholder wants to be notified of temperature anomalies 
     * @param watchHumid -  a boolean whose value reflects whether or not the stakeholder wants to be notified of humidity anomalies
     * @param watchBlock - a boolean whose value reflects whether or not the stakeholder wants to be notified of a blockage beyond threshold
     * @return returns true if update was successful, false otherwise
     */
	public boolean setNotificationType(int hiveId, String stakeholderName, boolean watchTemp, boolean watchHumid, boolean watchBlock){
		//construct the notification type based on watch stakeholder wants to watch
		String notifyType = "";
		if(watchTemp){
			notifyType += "T";
		}
		if(watchHumid){
			notifyType += "H";
		}
		if(watchBlock){
			notifyType += "B";
		}
		if(notifyType.equals("")){ //if no types are watched, set type to NONE
			notifyType = "NONE";
		}
		
		try
		{
			String query = "UPDATE watching SET NotificationType = ? WHERE HiveId = ? AND Name = ?";
			PreparedStatement prepared = connection.prepareStatement(query);
			prepared.setString(1, notifyType);
			prepared.setInt(2, hiveId);
			prepared.setString(3, stakeholderName);
			prepared.executeUpdate();
			return true;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			//return false if error occurred
			return false;
		}
	}
	
	
	
	
	
	
	public static void main(String[] args){
		Database db = new Database();
		db.storeBlockage(20, 65666);
		
		/*//for populating database with fake sensor data for testing
		int hiveId;
		for(int i=0; i<30; i++){
			if(i%2 == 1){
				hiveId = 65;
			}
			else{
				hiveId = 20;
			}
			System.out.println("generating fake data for hive " + hiveId + "\n");
			db.storeBlockage(hiveId, (float)(1000000*Math.random()));
			db.storeSensorData(hiveId, (float)(10+(15*Math.random())) , (float)(20+(30*Math.random())));
		}
		
		 */
		
		//ResultSet rs = db.getSensorData("65", "2019-01-01", "2019-03-03", "HUMIDITY");
		//db.printSensorData(rs);
		//db.setNotificationType(20, "Travis Manchee", true, true, true);
	}
	
}
