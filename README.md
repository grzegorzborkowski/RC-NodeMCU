# Remote control car 

## Introduction

This repo contains a project of a remote control car controlled by a NodeMCU microcontroller and an Android device.

This projects is in a client-server model. The server is the microcontroller programmed in Lua, the client is a mobile phone.

Client retrieves its motion and orientation using built-in accelerometer and sends measurements to the server.

The server after receiving the request, forwards the request to a servomechanism and an electronic speed control to turn tyres and move forward/back respectively.

Authors: <br>
Grzegorz Borkowski <br>
<a href="https://github.com/annaaniol"> Anna Anioł </a>
