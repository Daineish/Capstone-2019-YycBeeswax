package server;
import database.Database;

import javax.xml.crypto.Data;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * This class creates a server which acts at the communication point between
 * the Arduino devices and other services (such as the database).
 *
 * Currently, this server is able to receive temperature and humidity data from the Arduino.
 * TODO: Enable this server to handle video feed and other data.
 * TODO: Enable this server to communicate from the Database to the Arduino (if necessary).
 * TODO: Actually connect this server to the Database.
 * TODO: How will the loop handle multiple clients? Will they just be queued?
 */
public class ServerMain extends ServerSocket
{
	private Database db;
	private boolean m_isBlocked = false;
	private long m_elapsedTime = 0;
	private long m_startTime = 0;
	private long m_cooldown = 120000; // 20 seconds
	private long m_previousTime = 0;
	private Socket m_socket = null;

    public ServerMain(int port) throws java.io.IOException
    {
        super(port);
        
        // Open Database Connection
        db = new Database();
    }

    public String ReadMessageFromDevice() throws java.io.IOException
    {
        m_socket = accept();
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(m_socket.getInputStream()));
        return inFromClient.readLine();
    }

    /**
     *
     */
    public void IsBlocked(int hiveID, boolean blocked)
    {
        // TODO: Store blockage data in DB.
        if(m_isBlocked)
        {
            if(blocked)
            {
                // was blocked & is still blocked, check time blocked.
                long elapsed = System.currentTimeMillis() - m_startTime;
                elapsed /= 1000; // 10^3 -> should be in seconds now.
                System.out.println("Blocked: Checking for alert: " + elapsed);
                if(db.getBlockTime(hiveID) >= 0 && elapsed > db.getBlockTime(hiveID))
                {
                    if(m_cooldown < ((System.currentTimeMillis()) - m_previousTime))
                    {
                        System.out.println("SEND ALERT");
                        m_previousTime = System.currentTimeMillis();
                        db.storeBlockage(hiveID, elapsed);
                        // has been blocked for elapsed
                        // last email send at m_previousTime
                    }
                }
            }
            else
            {
                // was blocked & no longer blocked, reset timer
                m_isBlocked = false;
                m_startTime = 0;
            }
        }
        else
        {
            if(blocked)
            {
                // wasn't blocked & is now blocked, start timer
                System.out.println("Blocked: Timer started");
                m_startTime = System.currentTimeMillis();
                m_isBlocked = true;
            }
            else
            {
                // wasn't blocked & still isn't, do nothing
            }
        }
    }

    /**
     * Sends the temperature and humidity data to the database.
     * TODO: Not implemented.
     * @param hiveID - the hiveID where the data is coming from.
     * @param temp  - the temperature to send.
     * @param humid - the humidity to send.
     * @return true if successfully sent, else false.
     */
    public boolean SendTempHumidToDatabase(int hiveID, float temp, float humid)
    {
        boolean sent = false;

        try
        {
            //Utilities.AssertMessage(false, false, "Sending data to database not yet implemented.");
            sent = db.storeSensorData(hiveID, temp, humid);
        }
        catch(Exception e) { }

        return sent;
    }

    /**
     * Sends video data to the database.
     * TODO: Not implemented
     * @param hiveID - the hiveID where the data is coming from.
     * @return true if successfully sent, else false.
     */
    public boolean SendVideoDataToDatabase(int hiveID)
    {
        boolean sent = false;

        try
        {
            Utilities.AssertMessage(false, false, "Sending data to database not yet implemented.");
        }
        catch(Exception e) { }
        //sent = false;

        return sent;
    }

    /**
     * Sends blockage data to the database.
     * TODO: Not implemented
     * @param hiveID - the hiveID where the data is coming from.
     * @return true if successfully sent, else false.
     */
    public boolean SendBlockagesToDatabase(int hiveID)
    {
        boolean sent = false;

        try
        {
            Utilities.AssertMessage(false, false, "Sending data to database not yet implemented.");
        }
        catch(Exception e) { }
        //sent = false;

        return sent;
    }

//    public ResultSet SendMessageToDatabase(String msg)
//    {
//        return db.sendArbitraryQuery(msg);
//    }

    public void SendMessageToClient(String msg)
    {
        try
        {
            PrintWriter out = new PrintWriter(m_socket.getOutputStream(), true);
            out.println(msg);
            out.flush();
            out.close();
        }
        catch(java.io.IOException e) { System.out.println("Error..."); }
    }

    public ResultSet GetHiveList()
    {
        return db.getHiveList();
    }

    public ResultSet GetStakeholderList()
    {
        return db.getStakeholderList();
    }

    public ResultSet GetStakeholderInfo(String name)
    {
        return db.getWatchingList(name);
    }

    public void UpdateHive(int hiveId, String loc, String owner, float tempLB,
                           float tempUB, float humidLB, float humidUB, float blockTime, int origId)
    {
        db.updateHive(hiveId, loc, owner, tempLB, tempUB, humidLB, humidUB, blockTime, origId);
    }

    public ResultSet GetSensorData(String hiveId, String start, String end, String sensor)
    {
        return db.getSensorData(hiveId, start, end, sensor);
    }

    public boolean UpdateWatching(int hive, String name, boolean t, boolean h, boolean b)
    {
        return db.setNotificationType(hive, name, t, h, b);
    }

    public String GetStakeholderName(int id)
    {
        String name = "";
        try
        {
            ResultSet rs1 = GetStakeholderList();
            while(rs1.next())
            {
                int st = rs1.getInt("StakeholderId");
                if(st == id)
                {
                    name = rs1.getString("Name");
                    break;
                }
            }
        }
        catch(SQLException e) {}
        return name;

    }
}
