#include <ArduinoJson.h>

#define RELAY1 7

void setup() {
  Serial.begin(115200);     // opens serial port, sets data rate to 9600 bps
  pinMode(RELAY1, OUTPUT);
}

void loop() {

  // send data only when you receive data:
  if (Serial.available() > 0) {
    // read the incoming byte:
    char json[400];
    String jsonString = Serial.readString();
    jsonString.toCharArray(json, 400);
    StaticJsonBuffer<200> jsonBuffer;
    JsonObject& root = jsonBuffer.parseObject(json);
    Serial.println(jsonString);
    if (root.containsKey("isLampSwitchedOn"))
      if (root["isLampSwitchedOn"]) {
        digitalWrite(RELAY1, 1);
        Serial.println("Relay On command was executed");
      } else {
        digitalWrite(RELAY1, 0);
        Serial.println("Relay off command was executed");
      }
  }
}
