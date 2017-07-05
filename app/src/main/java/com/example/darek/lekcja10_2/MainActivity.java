package com.example.darek.lekcja10_2;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    SensorManager mSensorManager;
    Sensor accSensor;
    Sensor magnetSensor;

    float gravity[];
    float geoMagnetic[];
    double azimut;
    double pitch;
    double roll;

    TextView azimuthTextView;
    TextView pitchTextView;
    TextView rollTextView;
    TextView distTextView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        magnetSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        mSensorManager.registerListener(this, accSensor, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(this, magnetSensor, SensorManager.SENSOR_DELAY_NORMAL);


        azimuthTextView = (TextView) findViewById(R.id.azimuth);
        pitchTextView = (TextView) findViewById(R.id.pitch);
        rollTextView = (TextView) findViewById(R.id.roll);
        distTextView = (TextView) findViewById(R.id.dist);

        showSensors();
    }

    public void showSensors(){
        TextView sensorsTextView = (TextView) findViewById(R.id.sensorlist);
        List<Sensor> deviceSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        String allSensorsList = new String();
        Sensor tmp;
        for(int i=0; i<deviceSensors.size(); i++) {
            tmp = deviceSensors.get(i);
            allSensorsList += tmp.getName() + "\n";
        }
        sensorsTextView.setText(allSensorsList);

        //String allSensorsList = new String("Lista czujników: \n");
        /*
        for(Sensor sensor : deviceSensors){
            allSensorsList += sensor.getName() + "\n";
        }
        sensorsTextView.setText(allSensorsList);
        */
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this, accSensor);
        mSensorManager.unregisterListener(this, magnetSensor);
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER)
            gravity = event.values.clone();
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD)
            geoMagnetic = event.values.clone();

        if (gravity != null && geoMagnetic != null) {

            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R, I, gravity, geoMagnetic);
            if (success) {
                float orientation[] = new float[3];
                SensorManager.getOrientation(R, orientation);
                azimut = Math.toDegrees(orientation[0]);
                pitch = Math.toDegrees(orientation[1]);
                roll = Math.toDegrees(orientation[2]);

                float dist = Math.abs((float) (1.4f * Math.tan(pitch * Math.PI / 180)));

                azimuthTextView.setText(String.format("Azimuth: %.4f ", azimut));
                pitchTextView.setText(String.format("Pitch: %.4f ", pitch));
                rollTextView.setText(String.format("Roll: %.4f", roll));
                distTextView.setText(String.format("Dist: %.4f", dist));

               // Toast.makeText(this, "Orientation: " + azimut + "/" + pitch + "/" + roll + "/" + " dist = " + dist , Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Sprawdź swoje położenie" , Toast.LENGTH_SHORT).show();

            }
        }
    }
}
