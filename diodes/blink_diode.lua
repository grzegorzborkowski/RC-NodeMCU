WiFiName = "WiFi on ESP8266"
WiFiPasswd = "init"
wifi.setmode(wifi.STATION)
wifi.sta.config(WiFiName, WiFiPasswd)
print(wifi.sta.getip())

DEFAULT_REDLED_GPIO = 0
DEFAULT_BLUELED_GPIO = 4
gpio.mode(DEFAULT_REDLED_GPIO, gpio.OUTPUT)
gpio.mode(DEFAULT_BLUELED_GPIO, gpio.OUTPUT)

PIN_COMMAND_REGEXP = "(%w+)=(%w+)&*"

function generateButtonTags(buttonName, onSignature, offSignature)
    local sum = "";
    sum = "<p>"..buttonName.." <a href=\"?pin="..
        onSignature.."\"><button>ON</button></a>&nbsp;<a href=\"?pin="..
        offSignature.."\"><button>OFF</button></a></p>";
    return sum
end

srv = net.createServer(net.TCP)
srv:listen(80,function(conn)
    conn:on("receive", function(client, request)
        local buf = "";
        local _, _, _, _, requestContent = string.find(request, "([A-Z]+) (.+)?(.+) HTTP");
        local _GET = {}
        if (requestContent ~= nil) then
            for key, value in string.gmatch(requestContent, PIN_COMMAND_REGEXP) do
                _GET[key] = value
            end
        end
        buf = buf.."<h1>ESP8266 Web Server</h1>";
        buf = buf..generateButtonTags("RED", "onRED", "offRED");
        buf = buf..generateButtonTags("BLUE", "onBLUE", "offBLUE");
        if(_GET.pin == "offRED") then
              gpio.write(DEFAULT_REDLED_GPIO, gpio.HIGH);
        elseif(_GET.pin == "onRED") then
              gpio.write(DEFAULT_REDLED_GPIO, gpio.LOW);
        elseif(_GET.pin == "offBLUE") then
              gpio.write(DEFAULT_BLUELED_GPIO, gpio.HIGH);
        elseif(_GET.pin == "onBLUE") then
              gpio.write(DEFAULT_BLUELED_GPIO, gpio.LOW);
        end
        client:send(buf);
        client:close();
        collectgarbage();
    end)
end)
