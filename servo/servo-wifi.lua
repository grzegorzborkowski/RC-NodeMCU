SERVO_GPIO = 5
CLOCK = 50
DELAY1 = 500000
DELAY2 = 100000
DUTY_CYCLE_0 = 71
DUTY_CYCLE_R = 127
DUTY_CYCLE_L = 28
PIN_COMMAND_REGEXP = "(%w+)=(%w+)&*"

cfg={}
cfg.ssid="mywifi"
cfg.pwd="qwerty123"

cfg.ip="192.168.4.1"
cfg.netmask="255.255.255.0"
cfg.gateway="192.168.4.1"

wifi.setmode(wifi.SOFTAP)
wifi.ap.setip(cfg)
wifi.ap.config(cfg)

function generateButtonTags(sigLEFT90, sigRIGHT90, sigLEFT10, sigRIGHT10)
    local sum = "";
    sum = "<p><a href=\"?pin="..sigLEFT90.."\"><button>LEFT 90</button></a>&nbsp;"..
    "<a href=\"?pin="..sigRIGHT90.."\"><button>RIGHT 90</button></a>&nbsp;"..
    "<a href=\"?pin="..sigLEFT10.."\"><button>LEFT 10</button></a>&nbsp;"..
    "<a href=\"?pin="..sigRIGHT10.."\"><button>RIGHT 10</button></a></p>";
    return sum
end

function turn (side, degree)
    pwm.setup(SERVO_GPIO, CLOCK, DUTY_CYCLE_0);
    pwm.start(SERVO_GPIO);

    if(side=="left") then
        pwm.setduty(SERVO_GPIO, DUTY_CYCLE_L);
    elseif(side=="right") then
        pwm.setduty(SERVO_GPIO, DUTY_CYCLE_R);    
    end
    if(degree==90) then
        tmr.delay(DELAY1);
    elseif(degree==10) then
        tmr.delay(DELAY2);
    end

    pwm.stop(SERVO_GPIO);
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

        buf = buf.."<h1>My Car</h1>";
        buf = buf.."<h2>TURN</h2>";
        buf = buf..generateButtonTags("left90", "right90", "left10", "right10");

        if(_GET.pin == "left90") then
            turn("left", 90);
        elseif(_GET.pin == "right90") then
            turn("right", 90);
        elseif(_GET.pin == "left10") then
            turn("left", 10);
        elseif(_GET.pin == "right10") then
            turn("right", 10);
        end

        client:send(buf);
        client:close();
        collectgarbage();
    end)
end)
