package com.fullstackoasis.stepcounterproject;

import androidx.appcompat.app.AppCompatActivity;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

/**
 * Use https://developer.android.com/guide/topics/sensors/sensors_motion#java
 * to see how many steps the person holding the device has taken during a specific period of time.
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener, SensorEventListener {
    private String TAG = MainActivity.class.getCanonicalName();
    private DataStorage dataStorage;
    private SensorManager sensorManager;
    private Sensor sensor;
    private boolean isCounting;
    private int counter = 0; // TODO FIXME store data
    // If the user hit the DELETE STEPS button, then you want the step count to reset to 0.
    // But, there's no way to do that with the step counter sensor!
    // So, we have to record the current number of steps counted from the sensor, and subtract
    // that from any new amount.
    private int nStepsToSubtract = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.cbToggleCounting).setOnClickListener(this);
        findViewById(R.id.btnDelete).setOnClickListener(this);
        // Even in onCreate, if the app has been used since previous reboot, the step counter
        // sensor knows about any previously counted steps.
        // So if you never reboot your device, there will be a million days of steps in here.
        dataStorage = new DataStorage(this);
        resetSensor();
        counter = dataStorage.getStepsFromSharedPreferences();
        nStepsToSubtract = dataStorage.getNStepsToSubtractFromSharedPreferences();
        toggleCountingSteps();
        int nSteps = getStepsToDisplay();
        setStepCounterText(nSteps);
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerStepListener();
        nStepsToSubtract = dataStorage.getNStepsToSubtractFromSharedPreferences();
        counter = dataStorage.getStepsFromSharedPreferences();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterStepListener();
        // for example, user has walked 200 steps. Then other app comes into foreground. Store
        // the 200 value for later retrieval. You only need this number if the user hits the
        // DELETE button, because the step counter sensor maintains the count independently of
        // this app running.
        dataStorage.setStepsToSharedPreferences(counter);
        dataStorage.setNStepsToSubtractFromSharedPreferences(nStepsToSubtract);
    }

    private void registerStepListener() {
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    private void unregisterStepListener() {
        sensorManager.unregisterListener(this);
    }

    private void toggleCountingSteps() {
        isCounting = !isCounting;
        CheckBox cb = findViewById(R.id.cbToggleCounting);
        if (isCounting) {
            cb.setText(R.string.checked_string);
            registerStepListener();
            /* TODO FIXME work on adding a service that updates step count periodically.
            But for demo purposes, this works fine.
            ComponentName componentName = new ComponentName(this, StepCounterService.class);
            JobInfo jobInfo = new JobInfo.Builder(12, componentName)
                    .setPeriodic(3000)
                    .build();
            JobScheduler jobScheduler = (JobScheduler)getSystemService(JOB_SCHEDULER_SERVICE);
            int resultCode = jobScheduler.schedule(jobInfo);
            if (resultCode == JobScheduler.RESULT_SUCCESS) {
                Log.d(TAG, "Job scheduled!");
            } else {
                Log.d(TAG, "Job not scheduled");
            }

             */
        } else {
            cb.setText(R.string.unchecked_string);
            unregisterStepListener();
        }
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.btnDelete:
                handleDeleteButton();
                break;
            case R.id.cbToggleCounting:
                toggleCountingSteps();
                break;
        }

    }

    private void handleDeleteButton() {
        // There is no way to "delete" steps. So instead of doing that, maintain a running count
        // of steps that have been taken since the last reboot, AT THIS TIME. When the user hits
        // the DELETE button, this running count is stored in SharedPreferences, and in this app,
        // as nStepsToSubtract.
        nStepsToSubtract = counter;
        // You have to store it to SharedPreferences just in case the app is killed. Then if the
        // app is created again, we know how much to subtract from the running count.
        dataStorage.setNStepsToSubtractFromSharedPreferences(nStepsToSubtract);
        setStepCounterText(0);
    }

    private void resetSensor() {
        if (isCounting) {
            // turn it off
            toggleCountingSteps();
            // this will unregister the step counter sensor.
        }
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER); // returns Step Counter
        String name = "";
        if (sensor != null) { // TODO FIXME cannot use app if sensor not found
            name = sensor.getName();
        }
        Log.d(TAG, "Sensor is null? " + (sensor == null) + ", name = " + name);
    }

    private void setStepCounterText(int nSteps) {
        TextView tv = findViewById(R.id.tvMessage);
        String template = getResources().getString(R.string.stepping_message);
        String s = String.format(template, nSteps);
        tv.setText(s);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.d(TAG, "onSensorChanged was called");
        TextView tv = findViewById(R.id.tvMessage);
        // A SensorEvent for Step Counter has just 1 value.
        // int len = event.values.length; => len will be 1 if you check it.
        // See https://developer.android.com/reference/android/hardware/Sensor#TYPE_STEP_COUNTER
        counter = (int)event.values[0]; // This is the source of truth for number of steps taken
        // since reboot
        int nSteps = getStepsToDisplay();
        setStepCounterText(nSteps);
    }

    private int getStepsToDisplay() {
        int nSteps = counter;
        if (counter >= nStepsToSubtract) {
            nSteps = nSteps - nStepsToSubtract;
        }
        return nSteps;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**********************************************************************************************
     * Lifecycle methods
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterStepListener();
    }
}
