package borkowski.rc_controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import com.loopj.android.http.RequestParams;
import java.net.URI;

public class ConnectionService extends android.os.AsyncTask{
    private String defaultIP = "http://192.168.4.1:80";

    public ConnectionService() {
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        CarMove carMove = (CarMove) objects[0];
        RequestParams moveParams = countMoveParams(carMove);
        executeQuery(moveParams);
        return null;
    }

    // TODO: Change deprecated httpClient to any other Android httpClient
    private void executeQuery(RequestParams params) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet();

            System.out.println(params.toString());

            URI uri = new URI(defaultIP + "/?" + params);
            request.setURI(uri);
            HttpResponse httpResponse = httpClient.execute(request);
            System.out.println(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private RequestParams countMoveParams(CarMove carMove) {
        String degrees;
        String power;
        degrees =  String.valueOf(carMove.getTurn());
        if (carMove.getStraight() < -3) {
            power = "back";
        } else if (carMove.getStraight() > 3) {
            power = "slow";
        } else {
            power = "stop";
        }

        RequestParams params = new RequestParams();
        params.put("power", power);
        params.put("degrees", degrees);

        return params;
    }
}
