# HomeAutomation
This project serves just as a component of a bigger undertaking.

## Problem statement
The task is to control a floor lamp wirelessly using an Arduino Uno and an Android app. The lamp needs to be controlled regardless of whether the Android phone is on the same network or not. 

## The Solution
To do this, consider the diagram below.

![Circuit Diagram](https://github.com/dakshsrivastava/HomeAutomation/blob/master/images/Circuit_Diagram.png "Circuit Diagram")

The floor lamp is connected to a socket for power and a switch is made using a 5v single relay. The switch is controlled by the Arduino Uno. The Arduino flips the relay on / off when a boolean `isLampSwitchedOn`'s value changes to `true`. It fetches the updated status of the `isLampSwitchedOn` boolean value from the node service `<custom domain>/api/getStatus/` running in Google Cloud App engine. To communicate with the webservice, the Arduino uses the ESP8266 wifi module & fetches the status every 2 seconds when activated. 

On the other end, the Android app may execute `<custom domain>/api/toggleSwitch` API to flip the value of `isLampSwitchedOn` thereby turning on the lamp. The node service running on Google Cloud does not persist any data & hence the functionality may incur errors when the service is restarted. 

## Services

### Toggle Switch
GET - http://localhost:8080/api/toggleSwitch

The service is used to toggle the boolean value `isLampSwitchedOn`. As long as `isLampSwitchedOn` is `true`, the lamp will remain turned on.

Reponse : 
```
{ 
		message                 : 'The switch has been toggled',
		isLampSwitchedOn	: true,
		isArduinoUpdating	: true,
		success                 : true
}
```

### Get Status
GET - http://localhost:8080/api/getStatus

The service is used by Arduino recursively to keep check on whether a new toggle request has been received or not. Also, the android app uses this at start up to sync with Arduino.

Response : 
```
{
		message             : "The lamp is currently switched on.",
		isLampSwitchedOn    : true,
		isArduinoUpdating   : true,
		success             : true
}
```

### Toggle Arduino
GET - http://localhost:8080/api/arduino?isArduinoUpdating=false

The service is used by the client to turn the Arduino functionality on or off. When set to `false`, Arduino will stop sending GET requests to Google cloud to get the updated value of `isLampSwitchedOn`. Since Arduino seizes all communication with the server, the above service with the param `isArduinoUpdating=true` can only be exeucted when on the same network.

Response : 
```
{
			message             : "Service requests to Google Console have been turned off for Arduino.",
			isLampSwitchedOn    : false,
			isArduinoUpdating   : false,
			success             : true
}
```

## License
```
Home Automation is an open source project that is used to control the switch of a lamp wirelessly from an Android app
Copyright (C) 2016  Daksh Srivastava

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
```
