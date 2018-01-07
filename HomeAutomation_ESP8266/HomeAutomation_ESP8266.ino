#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

const char* ssid = "ENTER SSID HERE";
const char* password = "ENTER WiFi PASSWORD HERE";
const String serverUrl = "SERVER URL HERE";
const String getStatusAPI = "/getStatus";
const String updateMoistureLevel = "/updateMoistureLevel";
const String nodeId = "NODE ID HERE";

boolean isUpdated = false;

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(0, INPUT);
  pinMode(2, OUTPUT);
  WiFi.begin(ssid, password);  //Connect to the WiFi network
  while (WiFi.status() != WL_CONNECTED) {  //Wait for connection
    delay(1000);
    Serial.println("Waiting to connect...");
  }
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //Print the local IP
}

void loop() {

  //Check the current reading from the soil moisture module. The threshold using the potentiameter is set at 61%.
  //if the value decreased below this, the value from DO changes to LOW. (Low on resistance, high on moisture) Switch the relay
  //ON
  int intMoistureLevel = digitalRead(0);
  if (intMoistureLevel == HIGH) {
    //Log to console | Moisture is LOW, resitance is HIGH
    Serial.println("Moisture level is LOW");
    //Switch GPIO2 to output mode and turn on the relay. This will start the pump
    pinMode(2, OUTPUT);
    digitalWrite(2, HIGH);
    //Call the loop quicky again so that we don't over water the plants
    delay(300);

    //If the WiFi is connected, update the value in the system as well
    if (WiFi.status() == WL_CONNECTED && !isUpdated) {
      HTTPClient http;
      http.begin(serverUrl + updateMoistureLevel + "?nodeId=" + nodeId + "&" + "status=" + "LOW");
      int httpCode = http.GET();

      if (httpCode > 0) {
        String jsonString = http.getString();
        Serial.println(jsonString);
      }
      http.end();
      isUpdated = true;
    }

  } else {
    //Log to console | Soil moisture level is High, resistance is low
    Serial.println("Moisture level is HIGH");
    //If the WiFi is connected, update the value in the system as well
    if (WiFi.status() == WL_CONNECTED && isUpdated) {
      HTTPClient http;
      http.begin(serverUrl + updateMoistureLevel + "?nodeId=" + nodeId + "&" + "status=" + "HIGH");
      int httpCode = http.GET();

      if (httpCode > 0) {
        String jsonString = http.getString();
        Serial.println(jsonString);
      }
      http.end();
      //Inverse of previous if condition | This is done so that each of the If conditions to update the server is 
      //done only once
      isUpdated = false;
    }

    //Turn off the relay | Switch off the pump just in case
    pinMode(2, INPUT);
    //Moisture level is within threshold limit, Chill | Check after 1 hour
    //delay(3600000);
    delay(1000);
  }
}
