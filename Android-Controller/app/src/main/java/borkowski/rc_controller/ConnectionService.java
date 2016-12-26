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
        String turn;
        String drive;
        turn =  String.valueOf(carMove.getTurn());
        if (carMove.getStraight() > 0) {
            drive = "back";
        } else if (carMove.getStraight() == 0) {
            drive = "stop";
        } else {
            drive = "slow";
        }

        RequestParams params = new RequestParams();
        params.put("pinD", drive);
        params.put("pin", turn);

        return params;
    }
}
