#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

const char* ssid = "<WiFi SSID Here>";
const char* password = "<WiFi Password here>";

// Set web server port number to 80
WiFiServer server(80);

const String lampNodeId = "<A unique ID for this ESP>";

// Variable to store the HTTP request
String header;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  WiFi.begin(ssid, password);  //Connect to the WiFi network
  while (WiFi.status() != WL_CONNECTED) {  //Wait for connection
    delay(1000);
    //Serial.println("Trying to connect to WiFi network");
  }
//  Serial.print("Acquired IP address: ");
//  Serial.println(WiFi.localIP());  //Print the local IP
  server.begin();
}

void loop() {
  WiFiClient client = server.available();   // Listen for incoming clients
  if (client) {                             // If a new client connects,
//    Serial.println("New Client has connected");          // print a message out in the serial port
    String currentLine = "";                // make a String to hold incoming data from the client
    while (client.connected()) {            // loop while the client's connected
      if (client.available()) {             // if there's bytes to read from the client,
        char c = client.read();             // read a byte, then
//        Serial.write(c);                    // print it out the serial monitor
        header += c;
        if (c == '\n') {                    // if the byte is a newline character
          // if the current line is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            if(header.indexOf("GET / HTTP/1.1") >= 0){
              client.println("HTTP/1.1 302 Redirect");
              client.println("Location : /discover");
              client.println("Content-type:text/plain");
              client.println("Connection: close");
              client.println();
            } else {
              client.println("HTTP/1.1 200 OK");
              client.println("Content-type:application/json");
              client.println("Connection: close");
              client.println();
            }
            
            // Informs the Arduino of an action taken by a REST call
            if (header.indexOf("GET /action?status=on") >= 0) {
              Serial.print("{\"status\":\"true\"}");
              client.println("{\"success\": true, \"status\": true}");
            } else if(header.indexOf("GET /action?status=off") >= 0) {
              Serial.print("{\"status\":\"false\"}");
              client.println("{\"success\": true, \"status\": false}");
            } else if(header.indexOf("GET /discover") >= 0) {
              // Send ID of this node to whoever is requesting
              client.println("[{\"nodeId\" : \"" + lampNodeId + "\", \"endpoints\" : [\"action\"], \"parameters\": [\"status\"], \"values\": [\"on\", \"off\"]}]");
            }
            
            // The HTTP response ends with another blank line
            client.println();
            // Break out of the while loop
            break;
          } else { // if you got a newline, then clear currentLine
            currentLine = "";
          }
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }
      }
    }
    // Clear the header variable
    header = "";
    // Close the connection
    client.stop();
//    Serial.println("Client disconnected.");
//    Serial.println("");
  }
}






  
//  // If the wifi is connected, it iteratively keeps polling the server
//  // to retrieve the status of the connected nodes to control the relay.
//    if (WiFi.status() == WL_CONNECTED) {
//      //Declare the Http client that will connect to the REST service
//      HTTPClient http;
//      
//      // Lamp related code begins here.
//      http.begin(serverUrl + getStatusAPI + "?nodeId=" + lampNodeId);
//      int httpCode = http.GET();
//      //If HTTP status code is not zero, try to read the response
//      if (httpCode > 0) {
//        String jsonString = http.getString();
//        //Send it on the serial for the Arduino to parse
////        Serial.print(jsonString);
//        const size_t bufferSize = JSON_OBJECT_SIZE(4) + 150;
//        DynamicJsonBuffer jsonBuffer(bufferSize);
//        JsonObject& root = jsonBuffer.parseObject(jsonString);
//        if (root.success() && root.containsKey("isNodeOn") && root.containsKey("isNodeActivated"))
//          if (root["isNodeOn"] && root["isNodeActivated"]) {
//            Serial.print("{\"status\":\"true\"}");
//          } else {
//            Serial.print("{\"status\":\"false\"}");
//          }
//        http.end();
//      }
//
//      //Watering systtem code begins here
//      //Check if Arduino sent any JSON to update back to server
//      if (Serial.available() > 0) {
//        String jsonString = Serial.readString();
//        //Continue only if some data was received. Water system is updated only on demand
//        if(jsonString != NULL) {
//          //Serial.println("Received data from Arduino : " + jsonString);  
//          const size_t bufferSize = JSON_OBJECT_SIZE(1) + 30;
//          DynamicJsonBuffer jsonBuffer(bufferSize);
//          JsonObject& root = jsonBuffer.parseObject(jsonString);
//          http.begin(serverUrl + updateMoistureLevel + "?nodeId=" + soilNodeId + "&" + "status=" + root["status"].as<String>());
//          int httpCode = http.GET();
//          //If HTTP status code is not zero, try to read the response
//          if (httpCode > 0)
//            String jsonString = http.getString();
//        }
//      }
//      http.end();
//    } else {
//      //Serial.println("WiFi Not connected");
//    }
//    
//    delay(3000);
//}
