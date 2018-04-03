#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

const char* ssid = "<ENTER WiFi Name here>";
const char* password = "WiFi Password Here";
const String serverUrl = "<PRODUCTION SERVER URL HERE>";
//const String serverUrl = "<TESTING SERVER URL HERE>";
const String getStatusAPI = "/getStatus";
const String updateMoistureLevel = "/updateMoistureLevel";
const String soilNodeId = "5a48c7bbce31f73679e2b027";
const String lampNodeId = "5a043eeece31f7367996a795";

//boolean isUpdated = true;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  WiFi.begin(ssid, password);  //Connect to the WiFi network
  while (WiFi.status() != WL_CONNECTED) {  //Wait for connection
    delay(1000);
    //Serial.println("Trying to connect to WiFi network");
  }
  //Serial.print("Acquired IP address: ");
  //Serial.println(WiFi.localIP());  //Print the local IP
}

void loop() {
  // If the wifi is connected, it iteratively keeps polling the server
  // to retrieve the status of the connected nodes to control the relay.
    if (WiFi.status() == WL_CONNECTED) {
      //Declare the Http client that will connect to the REST service
      HTTPClient http;
      
      // Lamp related code begins here.
      http.begin(serverUrl + getStatusAPI + "?nodeId=" + lampNodeId);
      int httpCode = http.GET();
      //If HTTP status code is not zero, try to read the response
      if (httpCode > 0) {
        String jsonString = http.getString();
        //Send it on the serial for the Arduino to parse
//        Serial.print(jsonString);
        const size_t bufferSize = JSON_OBJECT_SIZE(4) + 150;
        DynamicJsonBuffer jsonBuffer(bufferSize);
        JsonObject& root = jsonBuffer.parseObject(jsonString);
        if (root.success() && root.containsKey("isNodeOn") && root.containsKey("isNodeActivated"))
          if (root["isNodeOn"] && root["isNodeActivated"]) {
            Serial.print("{\"status\":\"true\"}");
          } else {
            Serial.print("{\"status\":\"false\"}");
          }
        http.end();
      }

      //Watering system code begins here
      //Check if Arduino sent any JSON to update back to server
      if (Serial.available() > 0) {
        String jsonString = Serial.readString();
        //Continue only if some data was received. Water system is updated only on demand
        if(jsonString != NULL) {
          //Serial.println("Received data from Arduino : " + jsonString);  
          const size_t bufferSize = JSON_OBJECT_SIZE(1) + 30;
          DynamicJsonBuffer jsonBuffer(bufferSize);
          JsonObject& root = jsonBuffer.parseObject(jsonString);
          http.begin(serverUrl + updateMoistureLevel + "?nodeId=" + soilNodeId + "&" + "status=" + root["status"].as<String>());
          int httpCode = http.GET();
          //If HTTP status code is not zero, try to read the response
          if (httpCode > 0)
            String jsonString = http.getString();
        }
      }
      http.end();
    } else {
      //Serial.println("WiFi Not connected");
    }
    
    delay(3000);
}
