To run blink_diode.lua with ESPlorer and an additional (red) diode:

1. Create connection: D0 - diode - 3V3 (resistor recomended)
2. Connect NodeMCU with ESPlorer:
select a serial port - the port, which was found during updating the firmware
set baud rate 9600
click "Open" to open selected selected serial port
3. Open blink_diode.lua in the script window
4. Click "Save to ESP"
4. Send "wifi.setmode(wifi.SOFTAP)" to NodeMCU
5. Connect with NodeMCU via WiFi
6. The server is available on the default port 192.168.4.1
