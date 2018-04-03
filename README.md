# HomeAutomation

The first step of any home automation project is to control a light bulb. Its not complex and can be done with minimum resources. The biggest reason though, it serves as a stepping stone for other connected hardware in the home network. Quite naturally, you are limited by your own imagination.

At current stage, this project gives the capability to a user to control a switch (not a wall switch) and control a water pump based on current moisture levels. This repository gives you everything you need to program your Arduino, ESP8266 WiFi module, a node service & an Android App which can be setup on your local network or on the cloud. In turn, the ESP module & Android app will communicate with the node service to read or modify switch status. As for the Arduino, it reads the JSON response & controls the relay to act as a switch and the water pump via another relay.

Checkout the current build status for each of the branches / platforms : 


| Platform      | Build Status  |
| ------------- |:-------------:|
| ESP           | [![Build Status](https://travis-ci.org/dakshsrivastava/HomeAutomation.svg?branch=esp)](https://travis-ci.org/dakshsrivastava/HomeAutomation) |
| Arduino       | [![Build Status](https://travis-ci.org/dakshsrivastava/HomeAutomation.svg?branch=arduino)](https://travis-ci.org/dakshsrivastava/HomeAutomation) | 
| Android       | [![Build Status](https://travis-ci.org/dakshsrivastava/HomeAutomation.svg?branch=android)](https://travis-ci.org/dakshsrivastava/HomeAutomation) |
| Node          | [![Build Status](https://travis-ci.org/dakshsrivastava/HomeAutomation.svg?branch=nodejs)](https://travis-ci.org/dakshsrivastava/HomeAutomation) |

#### Head out to the [Wiki](https://github.com/dakshsrivastava/HomeAutomation/wiki) for more info & how to assemble all components.
