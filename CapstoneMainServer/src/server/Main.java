package server;
import java.net.InetAddress;

/**
 * The Main class which handles the Server and it's communications.
 */
public class Main
{

    public static void main(String argv[]) throws Exception
    {
        // Open ServerSocket
        ServerMain server = new ServerMain(4444);
        System.out.println(InetAddress.getLocalHost());

        while (true)
        {
            // Accept the client, receive a string message.
            String clientSentence = server.ReadMessageFromDevice();
            Utilities.PrintMessage("\nReceived: " + clientSentence);
            ParseData(server, clientSentence);

        } // endWhile()
    }

    private static void ParseData(ServerMain server, String clientSentence)
    {
        try
        {
            // Parse client response
            String[] clientVals = clientSentence.split(" ");

            // Should be at least 3 values, one for data type, one for hiveID, one for data.
            Utilities.AssertMessage(clientVals.length >= 4, true, "Numbers of values received from client < 4");
            int hiveID = Integer.parseInt(clientVals[1]);
            if(Utilities.g_temperatureSensor.equals(clientVals[0]))
            {
                // Temperature/Humidity Sensor
                Utilities.AssertMessage(clientVals.length == 5, true, "Incorrect number of values received from sensors");
                float temp = Float.parseFloat(clientVals[2]);
                float humid = Float.parseFloat(clientVals[3]);
                boolean blocked = Boolean.parseBoolean(clientVals[4]);
                server.SendTempHumidToDatabase(hiveID, temp, humid);
                server.IsBlocked(hiveID, blocked);
            }
            else
            {
                Utilities.AssertMessage(false, true, "Unknown data type received: " + clientVals[0]);
            }
        }
        catch(Exception e)
        {
            // Catch errors and try again
        }
    }

}
