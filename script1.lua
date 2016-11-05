wifi.setmode(wifi.STATION)
wifi.sta.config("WiFi on ESP8266","init")
print(wifi.sta.getip())
ledRED = 0
ledBLUE = 4
gpio.mode(ledRED, gpio.OUTPUT)
gpio.mode(ledBLUE, gpio.OUTPUT)
srv=net.createServer(net.TCP)
srv:listen(80,function(conn)
    conn:on("receive", function(client,request)
        local buf = "";
        local _, _, method, path, vars = string.find(request, "([A-Z]+) (.+)?(.+) HTTP");
        if(method == nil)then
            _, _, method, path = string.find(request, "([A-Z]+) (.+) HTTP");
        end
        local _GET = {}
        if (vars ~= nil)then
            for k, v in string.gmatch(vars, "(%w+)=(%w+)&*") do
                _GET[k] = v
            end
        end
        buf = buf.."<h1>ESP8266 Web Server</h1>";
        buf = buf.."<p>RED <a href=\"?pin=onRED\"><button>ON</button></a>&nbsp;<a href=\"?pin=offRED\"><button>OFF</button></a></p>";
        buf = buf.."<p>BLUE <a href=\"?pin=onBLUE\"><button>ON</button></a>&nbsp;<a href=\"?pin=offBLUE\"><button>OFF</button></a></p>";
        local _on,_off = "",""
        if(_GET.pin == "offRED")then
              gpio.write(ledRED, gpio.HIGH);
        elseif(_GET.pin == "onRED")then
              gpio.write(ledRED, gpio.LOW);
        elseif(_GET.pin == "offBLUE")then
              gpio.write(ledBLUE, gpio.HIGH);
        elseif(_GET.pin == "onBLUE")then
              gpio.write(ledBLUE, gpio.LOW);
        end
        client:send(buf);
        client:close();
        collectgarbage();
    end)
end)
