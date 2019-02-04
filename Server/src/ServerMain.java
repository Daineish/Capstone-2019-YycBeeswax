import java.net.*;
import java.io.*;

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
public class ServerMain
{
    // Whether or not to print messages.
    public static final boolean g_printDebugMessage = true;

    // Constants for data types received from devices.
    public static final String g_temperatureSensor = "TH_SENSOR";
    public static final String g_videoData = "VIDEO_DATA";

    public static void main(String argv[]) throws Exception
    {
        // Open ServerSocket
        ServerSocket welcomeSocket = new ServerSocket(4444);
        System.out.println(InetAddress.getLocalHost());

        while (true)
        {
            // Accept the client, receive a string message.
            Socket connectionSocket = welcomeSocket.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String clientSentence = inFromClient.readLine();
            PrintMessage("\nReceived: " + clientSentence);

            try
            {
                // Parse client response
                String[] clientVals = clientSentence.split(" ");

                // Should be at least 3 values, one for data type, one for hiveID, one for data.
                AssertMessage(clientVals.length >= 3, true, "Numbers of values received from client < 3");
                int hiveID = Integer.parseInt(clientVals[1]);
                if(g_temperatureSensor.equals(clientVals[0]))
                {
                    // Temperature/Humidity Sensor
                    AssertMessage(clientVals.length == 4, true, "Incorrect number of values received from temperature/humidity sensor");
                    float temp = Float.parseFloat(clientVals[2]);
                    float humid = Float.parseFloat(clientVals[3]);
                    PrintMessage("Hive ID: " + hiveID + "\nTemperature read: " + temp + "\nHumidity read: " + humid);
                    SendTempHumidToDatabase(hiveID, temp, humid);
                }
                else if(g_videoData.equals(clientVals[0]))
                {
                    AssertMessage(false, true, "Video data currently in development");
                }
                else
                {
                    AssertMessage(false, true, "Unknown data type received: " + clientVals[0]);
                }
            }
            catch(Exception e)
            {
                // Catch errors and try again
                continue;
            }
        } // endWhile()
    }

    /**
     * Sends the temperature and humidity data to the database.
     * TODO: Not implemented.
     * @param hiveID - the hiveID where the data is coming from.
     * @param temp  - the temperature to send.
     * @param humid - the humidity to send.
     * @return true if successfully sent, else false.
     */
    private static boolean SendTempHumidToDatabase(int hiveID, float temp, float humid)
    {
        boolean sent = false;

        try
        {
            AssertMessage(false, false, "Sending data to database not yet implemented.");
        }
        catch(Exception e) { }
        sent = true;

        return sent;
    }

    /**
     * Sends video data to the database.
     * TODO: Not implemented
     * @param hiveID - the hiveID where the data is coming from.
     * @return true if successfully sent, else false.
     */
    private static boolean SendVideoDataToDatabase(int hiveID)
    {
        boolean sent = false;

        try
        {
            AssertMessage(false, false, "Sending data to database not yet implemented.");
        }
        catch(Exception e) { }
        sent = false;

        return sent;
    }

    /**
     * Sends blockage data to the database.
     * TODO: Not implemented
     * @param hiveID - the hiveID where the data is coming from.
     * @return true if successfully sent, else false.
     */
    private static boolean SendBlockagesToDatabase(int hiveID)
    {
        boolean sent = false;

        try
        {
            AssertMessage(false, false, "Sending data to database not yet implemented.");
        }
        catch(Exception e) { }
        sent = false;

        return sent;
    }

    /**
     * Asserts that b is true, if false it prints msg to System.err if thr is false,
     *     else it throws a new exception with msg.
     * @param b - the value to assert is true.
     * @param thr - whether or not to throw an exception.
     * @param msg - the message to print or throw
     * @return b
     * @throws Exception if b is false and thr is true
     */
    private static boolean AssertMessage(boolean b, boolean thr, String msg) throws Exception
    {
        if (!b)
        {
            if(thr)
            {
                throw new Exception("Assertion failed: " + msg);
            }
            else
            {
                System.err.println("Assertion failed: " + msg);
            }
        }
        return b;
    }

    /**
     * Debugging print. Only prints s if g_printDebugMessage is true.
     * @param s - the string to print
     * @return
     */
    private static void PrintMessage(String s)
    {
        if(g_printDebugMessage)
        {
            System.out.println(s);
        }
    }
}
