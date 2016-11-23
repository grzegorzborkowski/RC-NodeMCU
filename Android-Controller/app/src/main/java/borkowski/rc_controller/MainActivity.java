package borkowski.rc_controller;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.net.URL;

public class MainActivity extends AppCompatActivity implements SensorEventListener{
    private static final double EPSILON = 0.00001;
    private SensorManager mSensorManager;
    private Sensor mSensor;
    private static final double N2S = 1.0f / 1000000000.0f;
    private static final double sensorTimeMeasurement = 0.1f;
    private final float[] deltaRotationVector = new float[4];
    private double timestamp;
    public TextView xTextView;
    public TextView zTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        this.mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        setContentView(R.layout.activity_main);
        xTextView = (TextView) findViewById(R.id.xTextView);
        zTextView = (TextView) findViewById(R.id.zTextView);
        if (this.mSensor == null) {
            xTextView.setText(R.string.noAccelerometerMessage);
        }
    }

    @Override
    public final void onSensorChanged(SensorEvent event) {
            if (timestamp != 0) {
                final double dT = (event.timestamp - timestamp) * N2S;
                if (dT > sensorTimeMeasurement) {
                    double axisX = event.values[0];
                    double axisY = event.values[1];
                    double axisZ = event.values[2];
                    double omegaMagnitude = Math.sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ);
                    if (omegaMagnitude > EPSILON) {
                        axisX /= omegaMagnitude;
                        axisY /= omegaMagnitude;
                        axisZ /= omegaMagnitude;
                    }

                    double thetaOverTwo = omegaMagnitude * dT / 2.0f;
                    double sinThetaOverTwo = Math.sin(thetaOverTwo);
                    double cosThetaOverTwo = Math.cos(thetaOverTwo);
                    deltaRotationVector[0] = (float) (sinThetaOverTwo * axisX);
                    deltaRotationVector[1] = (float) (sinThetaOverTwo * axisY);
                    deltaRotationVector[2] = (float) (sinThetaOverTwo * axisZ);
                    deltaRotationVector[3] = (float) cosThetaOverTwo;
                    xTextView.setText(String.valueOf("X " + deltaRotationVector[0]));
                    zTextView.setText(String.valueOf("Z " + deltaRotationVector[2]));
                    passPositionToMicroController(deltaRotationVector[0], deltaRotationVector[2]);

                }
            }
        timestamp = event.timestamp;
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
    }

    // TODO: calibrate those values due to accelerometer's accurany and servo's range
    public final void passPositionToMicroController(float turn, float straight) {
        /**
         * don't move straight, it's normal position. Anomaly due to gravity force
         */
        if (straight > 0 && straight < 0.3) {
            straight = 0;
        }
        /**
         * so far ignore driving back
         */
        if(straight < 0) {
            // pass
        }
        /**
         * don't turn, it's normal position. Anomaly due to gravity force.
         */
        if(Math.abs(turn) < 0.2) {
            turn = 0;
        }
        turn *= 100;
        turn += 80;

        /**
         * before that, turning left positive, turning right negative
         */
        straight *= -1;
        straight *= 100;
        straight += 80;

        executePOSTRequesToMicroController((int)turn, (int)straight);
    }

    public final void executePOSTRequesToMicroController(int turn, int straight) {

    }

    @Override
    public final void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Do something here if sensor accuracy changes.
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }
}
