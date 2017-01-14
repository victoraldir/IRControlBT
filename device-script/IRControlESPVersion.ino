/*
  This a simple example of the aREST Library for the ESP8266 WiFi chip.
  See the README file for more details.

  Written in 2015 by Marco Schwartz under a GPL license.
*/

// Import required libraries
#include <ESP8266WiFi.h>
#include <aREST.h>

// Create aREST instance
aREST rest = aREST();

// WiFi parameters
const char* ssid = "Cheetos";
const char* password = "J3rv1sT3c01";

#define LIGHTWEIGHT 1
// The port to listen for incoming TCP connections
#define LISTEN_PORT           80
// Time MAX of waiting for a command
#define TIMEOUT               3000

// Create an instance of the server
WiFiServer server(LISTEN_PORT);

// Variables to be exposed to the API
String ircode;

// Declare functions to be exposed to the API
int sendCommand(String command);
int receiveCommand(String command);

void setup(void)
{
  // Start Serial
  Serial.begin(115200);

  // Init variables and expose them to REST API
  ircode = "empty";
  rest.variable("code",&ircode);
  
  // Function to be exposed
  rest.function("send",sendCommand);
  rest.function("receive",receiveCommand);

  // Give name & ID to the device (ID should be 6 characters long)
  rest.set_id("1");
  rest.set_name("ircontroldevice");

  // Connect to WiFi
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }
  Serial.println("");
  Serial.println("WiFi connected");

  // Start the server
  server.begin();
  Serial.println("Server started");

  // Print the IP address
  Serial.println(WiFi.localIP());
}

void loop() {

  // Handle REST calls
  WiFiClient client = server.available();
  if (!client) {
    return;
  }
  while(!client.available()){
    delay(1);
  }
  rest.handle(client);

}

// Custom function accessible by the API
// Wait the command for 3 seconds.
int receiveCommand(String command){

  int timeStart = millis();
  boolean flag = true;
  while(flag){

    long remaining = millis() - timeStart;
    
    Serial.print("Waiting for command. Time remaining: ");
    Serial.println(remaining);
    if(remaining > TIMEOUT){
        //TODO Implement code reception here
        flag = false;
    }

    delay(33);
  }

  yield();
  return 1;  
}


// TODO Implement send command here
int sendCommand(String command) {

  
  
  return 1;
}
