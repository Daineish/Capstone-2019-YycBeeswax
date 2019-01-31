/*
    This sketch establishes a TCP connection to a "quote of the day" service.
    It sends a "hello" message, and then prints received data.
*/

#include <ESP8266WiFi.h>

#ifndef STASSID
#define STASSID "Daine"    // WiFi name (SSID)
#define STAPSK  "password" // WiFi password
#endif

const char* ssid     = STASSID;
const char* password = STAPSK;

// This is a temporary hostname to combat dynamic IP addresses
const char* host = "testingarduino.hopto.org";
// Alternatively you could just use an IP address if you know it's static
//const char* host = "50.99.141.177";
const uint16_t port = 4444;

void setup()
{
  Serial.begin(115200);

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

void loop()
{
  Serial.print("\nConnecting to ");
  Serial.print(host);
  Serial.print(':');
  Serial.println(port);

  // Use WiFiClient class to create TCP connections
  WiFiClient client;
  if (!client.connect(host, port))
  {
    Serial.println("connection failed");
    delay(5000);
    return;
  }

  // This will send a string to the server
  Serial.println("Sending data to server");
  if (client.connected())
  {
    client.println("Hello from ESP8266");
  }

  // wait for data to be available
  unsigned long timeout = millis();
  while (client.available() == 0)
  {
    if (millis() - timeout > 5000)
    {
      Serial.println(">>> Client Timeout !");
      client.stop();
      delay(60000); // One minute
      return;
    }
  }

  // Read all the lines of the reply from server and print them to Serial
  Serial.println("Received from remote server: ");
  // not testing 'client.connected()' since we do not need to send data here
  delay(1000); // TODO: this is hacky, if I don't have this we ususally just receive 1 byte...
  while (client.available())
  {
    char ch = static_cast<char>(client.read());
    Serial.print(ch);
  }

  // Close the connection
  Serial.println();
  Serial.println("Closing connection");
  client.stop();

  delay(300000); // execute once every 5 minutes, don't flood remote service
}
