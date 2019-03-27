/*
    This sketch establishes a TCP connection to our server and sends humidity and temperature and IR data.
*/

#include <ESP8266WiFi.h>
#include "DHT.h"

#ifndef STASSID
#define STASSID "Travis"    // WiFi name (SSID)
#define STAPSK  "password" // WiFi password
#endif

#ifndef DHTINFO
#define DHTPIN 12       // what digital pin the DHT22 is conected to
#define DHTTYPE DHT22  // there are multiple kinds of DHT sensors
#endif
DHT dht(DHTPIN, DHTTYPE);

// Temperature/Humidity/IR sensor
const String g_datatypeTH = "TH_SENSOR";
const String g_datatypeB = "B_SENSOR";

const long TH_POLL_RATE = 20000;
const long B_POLL_RATE = 10000;

// Hive ID (CHANGE FOR EACH HIVE)
const int g_hiveID = 65;

const char* ssid     = STASSID;
const char* password = STAPSK;

// This is a temporary hostname to combat dynamic IP addresses
//const char* host = "testingarduino.hopto.org";
// Alternatively you could just use an IP address if you know it's static
const char* host = "10.13.145.57";
const uint16_t port = 4444;

void setup()
{
    Serial.begin(115200);

    dht.begin();
    pinMode(16,INPUT);
    pinMode(14,INPUT);
    // back to our code

    // We start by connecting to a WiFi network
    Serial.print("Connecting to WiFi network: ");
    Serial.println(ssid);

    /* Explicitly set the ESP8266 to be a WiFi-client, otherwise, it by default,
     would try to act as both a client and an access-point and could cause
     network-issues with your other WiFi-devices on your WiFi-network. */
    WiFi.mode(WIFI_STA);
    WiFi.begin(ssid, password);

    // Connecting to WiFi
    while (WiFi.status() != WL_CONNECTED)
    {
        delay(500);
        Serial.print(".");
    }

    Serial.println("");
    Serial.println("WiFi connected");
    Serial.print("IP address: ");
    Serial.println(WiFi.localIP());
}

int rateTH = 0;
int rateB = 0;
void loop()
{
    if(rateTH > TH_POLL_RATE)
    {
        // Check that we're still connected to WiFi
        if(WiFi.status() != WL_CONNECTED)
        {
            Serial.print("Trying to reconnect to WiFi");
            while(WiFi.status() != WL_CONNECTED)
            {
                delay(500);
                Serial.print(".");
            }
            Serial.print("WiFi connected");
        }

        Serial.print("\nConnecting to ");
        Serial.print(host);
        Serial.print(':');
        Serial.println(port);
  
        // Use WiFiClient class to create TCP connections
        WiFiClient client;
        if (!client.connect(host, port))
        {
            Serial.println("connection failed");
            delay(1000);
            return;
        }

        // This will send our temp & humidity
        if (client.connected())
        {
            Serial.println("Sending TH data to server");
            float humid = dht.readHumidity();
            float temp = dht.readTemperature();

            if(isnan(humid) || isnan(temp))
            {
                Serial.println("Failed to read data from sensor");
                rateTH = 0;
                return;
            }

            String str = g_datatypeTH + " " + g_hiveID + " " + String(temp) + " " + String(humid);
            
            Serial.println("Sending: " + str);
            client.println(str);
            rateTH = 0;
        }
        // Close the connection
        Serial.println();
        Serial.println("Closing connection");
        client.stop();
    }

    // Check that we're still connected to WiFi
    if(rateB > B_POLL_RATE)
    {
        if(WiFi.status() != WL_CONNECTED)
        {
            Serial.print("Trying to reconnect to WiFi");
            while(WiFi.status() != WL_CONNECTED)
            {
                delay(500);
                Serial.print(".");
            }
            Serial.print("WiFi connected");
        }

        Serial.print("\nConnecting to ");
        Serial.print(host);
        Serial.print(':');
        Serial.println(port);

        // Use WiFiClient class to create TCP connections
        WiFiClient client;
        if (!client.connect(host, port))
        {
            Serial.println("connection failed");
            delay(1000);
            return;
        }

        // This will send our blockages
        if (client.connected())
        {
            String str = g_datatypeB + " " + g_hiveID + " ";
            if((digitalRead(16))==1)
                str += "false";
             else
                str += "true";
             Serial.println("Sending: " + str);
             client.println(str);
             rateB = 0;
        }
        Serial.println();
        Serial.println("Closing connection");
        client.stop();
    }

    delay(100);
    rateTH += 100;
    rateB += 100;
}
