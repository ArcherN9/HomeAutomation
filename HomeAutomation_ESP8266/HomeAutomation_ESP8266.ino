#include <ESP8266WiFi.h>
#include <ESP8266HTTPClient.h>

const char* ssid = "<SSID HERE>";
const char* password = "WiFi PASSWORD HERE";
const String serverUrl = "API SERVER URL";
const String getStatusAPI = "/getStatus";

void setup() {
  // put your setup code here, to run once:
  Serial.begin(115200);
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
    http.begin(serverUrl + getStatusAPI);
    int httpCode = http.GET();

    if (httpCode > 0) {
      Serial.println(http.getString());
    }
    http.end();
  } 
  //else
    //Serial.println("WiFi Not connected");
  delay(3000);
}

