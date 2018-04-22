# HomeAutomation [![Build Status](https://travis-ci.org/dakshsrivastava/HomeAutomation.svg?branch=nodejs)](https://travis-ci.org/dakshsrivastava/HomeAutomation)

The first step of any home automation project is to control a light bulb. Its not complex and can be done with minimum resources. The biggest reason though, it serves as a stepping stone for other connected hardware in the home network. Quite naturally, you are limited by your own imagination.

At current stage, this project gives the capability to a user to control a switch (not a wall switch). This repository gives you everything you need to program your Arduino, ESP8266 WiFi module, a node service & an Android App which can be setup on your local network or on the cloud. In turn, the ESP module & Android app will communicate with the node service to read or modify switch status. As for the Arduino, it reads the JSON response & controls the relay to act as a switch.

#### Head out to the [Wiki](https://github.com/dakshsrivastava/HomeAutomation/wiki) for more info & how to assemble all components.
