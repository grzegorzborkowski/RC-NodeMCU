package borkowski.rc_controller;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import java.net.URI;

public class ConnectionService extends android.os.AsyncTask{
    private String defaultIP = "http://192.168.4.1:80";

    public ConnectionService() {
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        System.out.println(objects[0]);
        executeTurnQuery((CarMove) objects[0]);
        executeStraightQuery((CarMove) objects[0]);
        return null;
    }

    private void executeStraightQuery(CarMove carMove) {
        String code;
        if(carMove.getStraight() > 0) {
            code = "slow";
        }
        else if (carMove.getStraight() == 0) {
            code = "stop";
        } else {
            code = "back";
        }

        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            URI uri = new URI(defaultIP + "/?pinD=" + code);
            System.out.println(uri);
            request.setURI(uri);
            HttpResponse httpResponse = httpClient.execute(request);
            System.out.println(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void executeTurnQuery(CarMove carMove) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            URI uri = new URI(defaultIP + "/?pin=" + carMove.getTurn());
//            URI uri = new URI(defaultIP + "/?pinD=slow");
            System.out.println(uri);
            request.setURI(uri);
            HttpResponse httpResponse = httpClient.execute(request);
            System.out.println(httpResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}