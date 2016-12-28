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


public class MainActivity extends AppCompatActivity implements SensorEventListener {
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
            contactSensor(event);
            lastChange = currentTime;
        }
    }

    private void contactSensor(SensorEvent event) {
        double axisX = event.values[0];
        double axisY = event.values[1];
        double axisZ = event.values[2];
        System.out.println("X = " + axisX + "Y = " + axisY + "Z = " + axisZ);
        xTextView.setText(String.valueOf("X " + axisX));
        yTextView.setText(String.valueOf("Y " + axisY));
        zTextView.setText(String.valueOf("Z " + axisZ));
        CarMove carMove = calculateCarMoves(axisY, axisZ);

        new ConnectionService().execute(carMove);
        float[] deltaRotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
    }

    private CarMove calculateCarMoves(double y, double z) {
        int turn = calculateTurnValue(y);
        int straight = calculateStraightValue(z);
        return new CarMove(turn, straight);
    }

    private int calculateTurnValue(double y) {
        int degrees = 90 + (9 * (int) Math.round(y));
        return degrees;
    }

    private int calculateStraightValue(double z) {
        int power = (int) Math.round(z);
        return power;
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
