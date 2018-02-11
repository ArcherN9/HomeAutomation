#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

const char* ssid = "WiFi SSID";
const char* password = "WiFi password";
const String serverUrl = "API Server URI";
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
    Serial.println("Trying to connect to WiFi network");
  }
  Serial.print("Acquired IP address: ");
  Serial.println(WiFi.localIP());  //Print the local IP
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
        Serial.println(jsonString);
      }
      http.end();

      //Watering system code begins here
      //Check if Arduino sent any JSON to update back to server
      String jsonString = Serial.readString();
      Serial.println("Received data from Arduino : " + jsonString);  
      //Continue only if some data was received. Water system is updated only on demand
      if(jsonString != NULL) {
        char json[400];
        jsonString.toCharArray(json, 400);
        StaticJsonBuffer<200> jsonBuffer;
        JsonObject& root = jsonBuffer.parseObject(json);
        http.begin(serverUrl + updateMoistureLevel + "?nodeId=" + soilNodeId + "&" + "status=" + root["status"].as<String>());
        int httpCode = http.GET();
        //If HTTP status code is not zero, try to read the response
        if (httpCode > 0)
          String jsonString = http.getString();
      }
      http.end();
    } else {
      Serial.println("WiFi Not connected");
    }
    
    delay(3000);
}


  
//  //Check the current reading from the soil moisture module. The threshold using the potentiameter is set at 61%.
//  //if the value decreased below this, the value from DO changes to LOW. (Low on resistance, high on moisture) Switch the relay
//  //ON
//  int intMoistureLevel = digitalRead(0);
//  if (intMoistureLevel == HIGH) {
//    //Log to console | Moisture is LOW, resitance is HIGH
//    Serial.println("Moisture level is LOW");
//    //Switch GPIO2 to output mode and turn on the relay. This will start the pump
//    pinMode(2, OUTPUT);
//    digitalWrite(2, HIGH);
//    //Call the loop quicky again so that we don't over water the plants
//    delay(300);
//
//    //If the WiFi is connected, update the value in the system as well
//    if (WiFi.status() == WL_CONNECTED && !isUpdated) {
//      HTTPClient http;
//      http.begin(serverUrl + updateMoistureLevel + "?nodeId=" + nodeId + "&" + "status=" + "LOW");
//      int httpCode = http.GET();
//
//      if (httpCode > 0) {
//        String jsonString = http.getString();
//        Serial.println(jsonString);
//      }
//      http.end();
//      isUpdated = true;
//    }
//
//  } else {
//    //Log to console | Soil moisture level is High, resistance is low
//    Serial.println("Moisture level is HIGH");
//   
//    //Turn off the relay | Switch off the pump just in case
//    pinMode(2, INPUT);
//    //Moisture level is within threshold limit, Chill | Check after 1 hour
//    delay(3600000);
//    //delay(1000);
//
//    //If the WiFi is connected, update the value in the system as well
//    if (WiFi.status() == WL_CONNECTED && isUpdated) {
//      HTTPClient http;
//      http.begin(serverUrl + updateMoistureLevel + "?nodeId=" + nodeId + "&" + "status=" + "HIGH");
//      int httpCode = http.GET();
//
//      if (httpCode > 0) {
//        String jsonString = http.getString();
//        Serial.println(jsonString);
//      }
//      http.end();
//      //Inverse of previous if condition | This is done so that each of the If conditions to update the server is 
//      //done only once
//      isUpdated = false;
//    }
//  }
//}
