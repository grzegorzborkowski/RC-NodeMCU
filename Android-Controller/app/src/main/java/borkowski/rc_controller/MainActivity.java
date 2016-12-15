package borkowski.rc_controller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private static final double EPSILON = 0.00001;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static long lastChange = 0;
    private final float[] deltaRotationVector = new float[4];
    private TextView xTextView;
    private TextView yTextView;
    private TextView zTextView;
    private Button stopButton;
    private CarMove stopMove = new CarMove(90, 0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        setContentView(R.layout.activity_main);
        xTextView = (TextView) findViewById(R.id.xTextView);
        yTextView = (TextView) findViewById(R.id.yTextView);
        zTextView = (TextView) findViewById(R.id.zTextView);
        if (this.mSensor == null) {
            xTextView.setText(R.string.noAccelerometerMessage);
        }
        this.stopButton = (Button) findViewById(R.id.stopButton);
        this.stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new ConnectionService().execute(stopMove);
            }
        });
        new ConnectionService().execute(stopMove);
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
        long currentTime = event.timestamp;
        long diffInNanoSeconds = currentTime - lastChange;
        double diffInSeconds = diffInNanoSeconds / 1000000000.0;
        if(diffInSeconds > 0.5) {
            contactSensor(event, diffInSeconds);
            lastChange = currentTime;
        }
    }

    private void contactSensor(SensorEvent event, double timediff) {
        double axisX = event.values[0];
        double axisY = event.values[1];
        double axisZ = event.values[2];
        double omegaMagnitude = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
        if (omegaMagnitude > EPSILON) {
            axisX /= omegaMagnitude;
            axisY /= omegaMagnitude;
            axisZ /= omegaMagnitude;
        }
        double thetaOverTwo = omegaMagnitude * timediff / 2.0f;
        double sinThetaOverTwo = Math.sin(thetaOverTwo);
        double cosThetaOverTwo = Math.cos(thetaOverTwo);
        deltaRotationVector[0] = (float) (sinThetaOverTwo * axisX);
        deltaRotationVector[1] = (float) (sinThetaOverTwo * axisY);
        deltaRotationVector[2] = (float) (sinThetaOverTwo * axisZ);
        deltaRotationVector[3] = (float) cosThetaOverTwo;
        xTextView.setText(String.valueOf("X " + deltaRotationVector[0]));
        yTextView.setText(String.valueOf("Y " + deltaRotationVector[1]));
        zTextView.setText(String.valueOf("Z " + deltaRotationVector[2]));
        CarMove carMove = calculateCarMoves(deltaRotationVector[1], deltaRotationVector[2]);
        new ConnectionService().execute(carMove);
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
    }

    private CarMove calculateCarMoves(float x, float z) {
        int turn = calculateTurnValue(x);
        int straight = calculateStraightValue(z);
        return new CarMove(turn, straight);
    }

    private int calculateTurnValue(float x) {
        float calculatedX = -x * 100;
        calculatedX += 80;
        if (calculatedX >= 40 && 80 >= calculatedX) {
            return 90;
        } else {
            return (int) calculatedX;
        }
    }

    private int calculateStraightValue(float z) {
        float calculatedZ = z * 100;
        if (10 >= calculatedZ && calculatedZ >= -5) {
            return 0;
        }
        if (z < 0) {
          return -1;
        }
         return 1;
    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        new ConnectionService().execute(stopMove);
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
