import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class Main
{
    public static void main(String argv[]) throws Exception
    {
        // Open ServerSocket
        ServerMain server = new ServerMain(4444);

        while (true)
        {
            // Accept the client, receive a string message.
            Socket connectionSocket = server.accept();
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            String clientSentence = inFromClient.readLine();
            Utilities.PrintMessage("\nReceived: " + clientSentence);

            try
            {
                // Parse client response
                String[] clientVals = clientSentence.split(" ");

                // Should be at least 3 values, one for data type, one for hiveID, one for data.
                Utilities.AssertMessage(clientVals.length >= 3, true, "Numbers of values received from client < 3");
                int hiveID = Integer.parseInt(clientVals[1]);
                if(Utilities.g_temperatureSensor.equals(clientVals[0]))
                {
                    // Temperature/Humidity Sensor
                    Utilities.AssertMessage(clientVals.length == 4, true, "Incorrect number of values received from temperature/humidity sensor");
                    float temp = Float.parseFloat(clientVals[2]);
                    float humid = Float.parseFloat(clientVals[3]);
                    Utilities.PrintMessage("Hive ID: " + hiveID + "\nTemperature read: " + temp + "\nHumidity read: " + humid);
                    server.SendTempHumidToDatabase(hiveID, temp, humid);
                }
                else if(Utilities.g_videoData.equals(clientVals[0]))
                {
                    Utilities.AssertMessage(false, true, "Video data currently in development");
                }
                else
                {
                    Utilities.AssertMessage(false, true, "Unknown data type received: " + clientVals[0]);
                }
            }
            catch(Exception e)
            {
                // Catch errors and try again
                continue;
            }
        } // endWhile()
    }
}
