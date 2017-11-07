# HomeAutomation

The first step of any home automation project is to control a light bulb. Its not complex and can be done with minimum resources. The biggest reason though, it serves as a stepping stone for other connected hardware in the home network. Quite naturally, you are limited by your own imagination.

At current stage, this project gives the capability to a user to control a switch (not a wall switch). This repository gives you everything you need to program your Arduino, ESP8266 WiFi module, a node service & an Android App which can be setup on your local network or on the cloud. In turn, the ESP module & Android app will communicate with the node service to read or modify switch status. As for the Arduino, it reads the JSON response & controls the relay to act as a switch.

#### Head out to the Wiki to get more info & how to assemble all components.


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
