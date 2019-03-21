package server;
import java.io.BufferedReader;
import java.io.FileReader;
import java.net.InetAddress;
import java.sql.ResultSet;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * The Main class which handles the Server and it's communications.
 */
public class Main
{

    public static void main(String argv[]) throws Exception
    {
        // Open ServerSocket
        ServerMain server = new ServerMain(4444);
        ServerMain server2 = new ServerMain(4445);
        System.out.println(InetAddress.getLocalHost());

        Runnable r = new DeviceServer(server);
        Thread t = new Thread(r);
        Runnable r2 = new AndroidServer(server2);
        Thread t2 = new Thread(r2);
        t.start();
        t2.start();
    }
}

class DeviceServer implements Runnable
{
    ServerMain m_server;
    public DeviceServer(ServerMain server)
    {
        m_server = server;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                String clientSentence = m_server.ReadMessageFromDevice();
                Utilities.PrintMessage("\nReceived: " + clientSentence);
                ParseData(clientSentence);
            }
            catch(java.io.IOException e) { }
        }
    }

    private void ParseData(String clientSentence)
    {
        try
        {
            // Parse client response
            String[] clientVals = clientSentence.split(" ");
            List<String> clientStrings = Arrays.asList(clientVals);


            if(Utilities.g_tempHumidIrSensor.equals(clientVals[0]))
            {
                // Temperature/Humidity Sensor
                Utilities.AssertMessage(clientVals.length == 5, true, "Incorrect number of values received from sensors");
                int hiveID = Integer.parseInt(clientVals[1]);
                float temp = Float.parseFloat(clientVals[2]);
                float humid = Float.parseFloat(clientVals[3]);
                boolean blocked = Boolean.parseBoolean(clientVals[4]);
                m_server.SendTempHumidToDatabase(hiveID, temp, humid);
                m_server.IsBlocked(hiveID, blocked);
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

class AndroidServer implements Runnable
{
    ServerMain m_server;

    public AndroidServer(ServerMain server)
    {
        m_server = server;
    }

    public void run()
    {
        while(true)
        {
            try
            {
                String clientSentence = m_server.ReadMessageFromDevice();
                Utilities.PrintMessage("\nReceived!: " + clientSentence);
                ParseData(clientSentence);
            }
            catch(java.io.IOException e) { }
        }

    }

    private void ParseData(String clientSentence)
    {
        try
        {
            // Parse client response
            String[] clientVals = clientSentence.split(" ");

            if(Utilities.g_androidRequest.equals(clientVals[0]))
            {
                Utilities.AssertMessage(clientVals.length >= 2, true, "Numbers of values received from client < 2");
                String str = "";

                if("HIVE_LIST".equals(clientVals[1]))
                {
                    ResultSet rs = m_server.GetHiveList();
                    while(rs.next())
                    {
                        int hiveID = rs.getInt("HiveId");
                        str += hiveID + " ";
                    }
                }
                else if("HIVE_INFO".equals(clientVals[1]))
                {
                    ResultSet rs = m_server.GetHiveList();

                    while(rs.next())
                    {
                        int hiveVal = rs.getInt("HiveID");
                        if(hiveVal == Integer.parseInt(clientVals[2]))
                        {
                            str += hiveVal + "_";
                            str += rs.getString("Location") + "_";
                            str += rs.getString("Owner") + "_";
                            str += String.valueOf(rs.getFloat("TempLB")) + "_";
                            str += String.valueOf(rs.getFloat("TempUB")) + "_";
                            str += String.valueOf(rs.getFloat("HumidLB")) + "_";
                            str += String.valueOf(rs.getFloat("HumidUB")) + "_";
                            float blockSec = (rs.getFloat("BlockTime"));
                            str += String.valueOf(blockSec/60.0);// TODO: minutes (I hope)
                            break;
                        }
                    }
                }
                else if("HIVE_UPDATE".equals(clientVals[1]))
                {
                    String dataStr = "";
                    for(int i = 2; i < clientVals.length; i++)
                        dataStr += clientVals[i];
                    String[] data = dataStr.split("_");

                    if(data.length != 9)
                    {
                        System.err.println("Length should be 8, received: " + data.length);
                        return;
                    }
                    int hiveId = Integer.parseInt(data[0]);
                    String loc = data[1];
                    String owner = data[2];
                    float tempLB = Float.parseFloat(data[3]);
                    float tempUB = Float.parseFloat(data[4]);
                    float humidLB = Float.parseFloat(data[5]);
                    float humidUB = Float.parseFloat(data[6]);
                    float blockTime = Float.parseFloat(data[7]);
                    int origId = Integer.parseInt(data[8]);

                    m_server.UpdateHive(hiveId, loc, owner, tempLB, tempUB, humidLB, humidUB, blockTime, origId);
                }
                else if("SENSOR_DETAILS".equals(clientVals[1]))
                {
                    String dataStr = "";
                    for(int i = 2; i < clientVals.length; i++)
                        dataStr += clientVals[i];
                    String[] data = dataStr.split("_");

                    if(data.length != 4)
                    {
                        System.err.println("Length should be 4, received: " + data.length);
                        return;
                    }
                    String hiveId = data[0];
                    if("-1".equals(hiveId))
                        hiveId = null;
                    String sensor = data[1];
                    if("All".equals(sensor))
                        sensor = null;
                    else if("Temperature".equals(sensor))
                        sensor = "TEMP";
                    else if("Humidity".equals(sensor))
                        sensor = "HUMIDITY";
                    else if("Blockages".equals(sensor))
                        sensor = "BLOCKAGE";

                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    String start = format.format(new Date(Long.parseLong(data[2])));
                    String end = format.format(new Date(Long.parseLong(data[3])));

                    ResultSet rs = m_server.GetSensorData(hiveId, start, end, sensor);
                    str += "SENSORDATA_";
                    while(rs.next())
                    {
                        int hiveVal = rs.getInt("HiveID");
                        Time time = rs.getTime("Time");
                        String type = rs.getString("SensorType");
                        float sensorData = rs.getFloat("SensorData");

                        str += hiveVal + " ";
                        str += time + " ";
                        str += type + " ";
                        str += sensorData + "_";
                    }
                }
                else if("STAKEHOLDER_LIST".equals(clientVals[1]))
                {
                    ResultSet rs = m_server.GetStakeholderList();
                    while(rs.next())
                    {
                        //System.out.println("Get");
                        String stakeholder = rs.getString("Name");
                        int id = rs.getInt("StakeholderId");
                        //System.out.println("Stake: " + stakeholder);
                        str += stakeholder + "_" + id + "_";
                    }
                }
                else if("STAKEHOLDER_INFO".equals(clientVals[1]))
                {
                    int id = Integer.parseInt(clientVals[2]);
                    String name = m_server.GetStakeholderName(id);

                    Utilities.AssertMessage(name != "", true, "Stakeholder not found!");
                    ResultSet rs = m_server.GetStakeholderInfo(name);

                    while(rs.next())
                    {
                        int hiveId = rs.getInt("HiveId");
                        String notif = rs.getString("NotificationType");

                        str += hiveId + " " + notif + "_";
                    }
                }
                else if("STAKEHOLDER_UPDATE".equals(clientVals[1]))
                {
                    int stakeholder = Integer.parseInt(clientVals[2]);
                    int numHives = Integer.parseInt(clientVals[3]);
                    String name = m_server.GetStakeholderName(stakeholder);
                    String data = clientVals[4];
                    String[] vals = data.split("_");
                    if(vals.length != (numHives*4))
                    {
                        System.err.println("Length should be: " + ((numHives*4)) + " got: " + vals.length);
                        return;
                    }
                    for(int i = 0; i < numHives; i++)
                    {
                        int hive = Integer.parseInt(vals[i*4 + 0]);
                        boolean b = Boolean.parseBoolean(vals[i*4 + 1]);
                        boolean h = Boolean.parseBoolean(vals[i*4 + 2]);
                        boolean t = Boolean.parseBoolean(vals[i*4 + 3]);

                        m_server.UpdateWatching(hive, name, t, h, b);
                    }
                }
                else
                {
                    Utilities.AssertMessage(false, true, "Unknown SQL cmd received: " + clientVals[1]);
                }

                if(str != "")
                {
                    System.out.println("Sending: " + str);
                    m_server.SendMessageToClient(str);
                }
            }
            else
            {
                Utilities.AssertMessage(false, true, "Unknown data type received: " + clientVals[0]);
            }
        }
        catch(Exception e)
        {
            // Catch errors and try again
            System.err.println("Error: " + e.getMessage());
        }
    }
}