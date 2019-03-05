package server;
import database.Database;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

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
    public ServerMain(int port) throws java.io.IOException
    {
        super(port);
        
        // Open Database Connection
        db = new Database();
    }

    public String ReadMessageFromDevice() throws java.io.IOException
    {
        Socket connectionSocket = accept();
        BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
        return inFromClient.readLine();
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
}