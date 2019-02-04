package database;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class Database {

	private Connection connection;
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
			connection = DriverManager.getConnection(db, username, password);
		} 
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public void closeConnection(){
		try
		{
			connection.close();
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
	}
	
	public ArrayList<String> getStakeholderEmail(int HiveID){
		try
		{
			ResultSet rs;
			ArrayList<String> emailList = new ArrayList<String>();
			String query = "SELECT Email FROM stakeholder WHERE HiveID = ?";
			PreparedStatement prepared = connection.prepareStatement(query);
			prepared.setInt(1, HiveID);
			rs = prepared.executeQuery();
			while(rs.next()){
				emailList.add(rs.getString("Email"));
			}
			return emailList;
		}
		catch (SQLException e) 
		{
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}
	public static void main(String[] args){
		Database db = new Database();
		ArrayList<String> myList = db.getStakeholderEmail(65);
		System.out.println(myList.toString());
		db.closeConnection();
		System.out.println("connection closed, goodbye");
	}
	
}
