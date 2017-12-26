#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>
#include <ArduinoJson.h>

//#define RELAY1 2

const char* ssid = "<SSID>";
const char* password = "<WiFi Password>";
const String serverUrl = "<sever url>/api";
const String getStatusAPI = "/getStatus";


void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
  pinMode(2, OUTPUT);
  digitalWrite(2, LOW);
  WiFi.begin(ssid, password);  //Connect to the WiFi network
  while (WiFi.status() != WL_CONNECTED) {  //Wait for connection
    delay(1000);
    Serial.println("Waiting to connect...");
  }
  Serial.print("IP address: ");
  Serial.println(WiFi.localIP());  //Print the local IP
}

void loop() {
  // put your main code here, to run repeatedly:
  if (WiFi.status() == WL_CONNECTED) {
    HTTPClient http;
    http.begin(serverUrl + getStatusAPI + "?nodeId=" + nodeId);
    int httpCode = http.GET();

    if (httpCode > 0) {
      String jsonString = http.getString();
      Serial.println(jsonString);
      // read the incoming byte:
      char json[400];
      jsonString.toCharArray(json, 400);
      StaticJsonBuffer<200> jsonBuffer;
      JsonObject& root = jsonBuffer.parseObject(json);
      if (root.containsKey("isLampSwitchedOn"))
        if (root["isLampSwitchedOn"]) {
          pinMode(2, OUTPUT);
          digitalWrite(2, HIGH);
          Serial.println("Relay On command was executed");
        } else {
          pinMode(2, INPUT);
          Serial.println("Relay off command was executed");
        }
    }
    http.end();
  } 
  //else
    //Serial.println("WiFi Not connected");
  delay(3000);
}

