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
        CarMove carMove = (CarMove) objects[0];
        executeQuery("/?pinD=", carMove, false);
        executeQuery("/?pin=", carMove, true);
        return null;
    }

    // TODO: Change deprecated httpClient to: http://loopj.com/android-async-http/
    private void executeQuery(String link, CarMove carMove, boolean turn) {
        try {
            HttpClient httpClient = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            String suffix;
            if(turn) suffix = String.valueOf(carMove.getTurn());
            else {
                if(carMove.getStraight() > 0) {
                    suffix = "slow";
                }
                else if (carMove.getStraight() == 0) {
                    suffix = "stop";
                } else {
                    suffix = "back";
                }
            }
            URI uri = new URI(defaultIP + link + suffix);
            request.setURI(uri);
            HttpResponse httpResponse = httpClient.execute(request);
            System.out.println(httpResponse);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}