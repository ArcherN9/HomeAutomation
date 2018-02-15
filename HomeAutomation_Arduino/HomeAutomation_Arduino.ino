#include <ArduinoJson.h>

//The pin that controls the lamp relay
#define RELAYLAMP1 13
//The pin that controls the Soil Relay
#define RELAYSOIL1 12
//The pin that controls the power to soil sensor itself
#define SENSORSOIL 11
//The pint that receives the value from soil sensor
#define INPUTSOIL1 A0

//A threshold value is received from a service that ESP executes
int intSoilThreshold = 613;

//The integer holds the last moisture level read from the sensor
//This is done to periodically update the server with latest information
int intMoistureLevel = 0;

//A boolean to track if the pump is running or not currently
int isPumpOn = false;

//A 32bit integer that holds the milliseconds elapsed since boot time. This is required
//because we cannot control how often is the soil moisture code executed using delay method.
//So instead, we check elapsed time instead
uint32_t startTime = 0;

void setup() {
  Serial.begin(115200);     // opens serial port, sets data rate to 115200 bps
  //Pin 12 for Analog input
  pinMode(INPUTSOIL1, INPUT);
  //Turn off all relays at the beginning
  pinMode(RELAYSOIL1, INPUT);
  pinMode(RELAYLAMP1, INPUT);
  //Turn off the Soil sensor
  pinMode(SENSORSOIL, INPUT);

  //get milliseconds elapsed for the first run
  startTime = millis();
}

void loop() {
  //Lamp specific code
  // send data only when you receive data:
  if (Serial.available() > 0) {
    // read the incoming byte:
    String jsonString = Serial.readString();
    const size_t bufferSize = JSON_OBJECT_SIZE(4) + 150;
    DynamicJsonBuffer jsonBuffer(bufferSize);
    JsonObject& root = jsonBuffer.parseObject(jsonString);
    if (root.success()) {
      if (root["status"]) {
        pinMode(RELAYLAMP1, OUTPUT);
        digitalWrite(RELAYLAMP1, HIGH);
        Serial.println("Relay On command was executed");
      } else {
        pinMode(RELAYLAMP1, INPUT);
        Serial.println("Relay off command was executed");
      }
    } else {
      Serial.println("Either parsing failed, or required keys are missing");
    }
  }

  //Check if adequate time has elapsed before trying to access soil moisture information
  //Why 43200000? Check after 12 hours ((60 * 60 * 12) * 1000) = 43200000
  if((millis() - startTime) > 43200000 || millis() < startTime) {
    //Soil moisture specific code
    //Check the current reading from the soil moisture module. The threshold using the potentiameter is set at 61% for a digital read
    //if the value decreased below this, the value from DO changes to LOW. (Low on resistance, high on moisture) Switch the relay
    //ON
    //Turn on the sensor
    pinMode(SENSORSOIL, OUTPUT);
    digitalWrite(SENSORSOIL, HIGH);
    delay(100);
    //Above comment is for code running on ESP. On Arduino, we will use Analog information
    int intNew = analogRead(INPUTSOIL1);
  
    //If new moisture data does not match previous, update the server
    if (intNew != intMoistureLevel) {
      //Log to console
      Serial.println("New moisture level is " + String(intNew) + ". Last recorded was " + String(intMoistureLevel));

      //Send ESP DATA
      Serial.print("{\"status\":\"" + String(intNew) + "\"}");
      intMoistureLevel = intNew;

      //If pump is turned on, water plants until they reach at least 800 value
      if(isPumpOn) {
        //Log to console
        Serial.println("Water pump is running");
  
        if(intMoistureLevel > 800) {
           //Turn off the relay | Switch off the pump just in case
          pinMode(RELAYSOIL1, INPUT);

          //Update the boolean value that the pump has been switched off
          isPumpOn = false;

          //Send ESP DATA | Send HIGH so that push notification is executed
          Serial.println("{\"status\":\"HIGH\"}");

          //Reset the start time with a new value because pump has been turned off and this code does
          //need calling for another 12 hours
          startTime = millis();

          //Turn off the sensor
          pinMode(SENSORSOIL, INPUT);

          //Standard delay in the end
          delay(1000);
        } else {
          //Pump is still running. Let it run
          //Call the loop quicky again so that we don't over water the plants
          delay(300);
        }
      } else if (intMoistureLevel < intSoilThreshold) {
        //Turn on the relay. This will start the pump
        pinMode(RELAYSOIL1, OUTPUT);
        digitalWrite(RELAYSOIL1, HIGH);
        delay(100);
  
        //Update boolean value for pump that is has been turned on
        isPumpOn = true;
  
        //Send ESP DATA | Send LOW so that push notification is executed
        Serial.println("{\"status\":\"LOW\"}");
      
        //Call the loop quicky again so that we don't over water the plants
        delay(300);
      } else {
          //Turn off the sensor
          pinMode(SENSORSOIL, INPUT);
          
          //Reset the start time with a new value because pump has been turned off and this code does
          //need calling for another 12 hours
          startTime = millis();

          //Standard delay in the end
          delay(1000);
      }
    } else {
      //Standard delay in the end
      delay(1000);
    }
  } else {
    //Standard delay in the end
    delay(1000);
  }
}
