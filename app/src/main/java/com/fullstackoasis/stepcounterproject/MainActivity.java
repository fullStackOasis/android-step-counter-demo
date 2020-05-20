package com.fullstackoasis.stepcounterproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Use https://developer.android.com/guide/topics/sensors/sensors_motion#java
 * to see how many steps the person holding the device has taken during a specific period of time.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean isCounting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CheckBox cb = findViewById(R.id.cbToggleCounting);
        cb.setOnClickListener(this);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
    }

    private void toggleCountingSteps() {
        isCounting = !isCounting;
        CheckBox cb = findViewById(R.id.cbToggleCounting);
        if (isCounting) {
            cb.setText(R.string.checked_string);
        } else {
            cb.setText(R.string.unchecked_string);
        }
    }

    @Override
    public void onClick(View v) {
        toggleCountingSteps();
    }
}
