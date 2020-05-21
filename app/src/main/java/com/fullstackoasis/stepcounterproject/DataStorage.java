package com.fullstackoasis.stepcounterproject;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

public class DataStorage {
    private String TAG = DataStorage.class.getCanonicalName();
    private Context context;
    private static String VERSION_CODE = "VERSION_CODE";
    protected static final String N_STEPS_TAKEN = "N_STEPS_TAKEN";
    protected static final String N_STEPS_TO_SUBTRACT = "N_STEPS_TO_SUBTRACT";
    protected static final String SHARED_PREFS_NAME = "com.fullstackoasis.stepcounterproject" +
            ".DataStorage";
    public DataStorage(Context context) {
        this.context = context;
        /**
         * Immediately test to see if this application's SharedPreferences should be cleared.
         * This is done if there's a new version of the app. See AndroidManifest.xml
         * android:versionCode.
         */
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(),
                    0);
            SharedPreferences pref = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
            // For information about versionCode and versioning, see here:
            // https://developer.android.com/studio/publish/versioning
            if (pref.getInt(VERSION_CODE, 0) != pInfo.versionCode) {
                SharedPreferences.Editor edit = pref.edit();
                edit.clear();
                edit.putInt(VERSION_CODE, pInfo.versionCode);
                edit.commit();
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

        }
    }

    /**
     * Returns the delay until screensaver should appear in seconds. This is taken from
     * SharedPreferences. If not found, the MIN_DELAY value is returned.
     * @return
     */
    protected int getNStepsToSubtractFromSharedPreferences() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        int result = mySharedPreferences.getInt(N_STEPS_TO_SUBTRACT, 0);
        return result;
    }

    /**
     * Stores the given number of steps, which will be subtracted from the total number of steps
     * provided by the step counter. Only used if the user has hit the delete button.
     * @param nStepsToSubtract
     * @return
     */
    protected void setNStepsToSubtractFromSharedPreferences(int nStepsToSubtract) {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putInt(N_STEPS_TO_SUBTRACT, nStepsToSubtract);
        myEditor.commit();
    }

    protected void setStepsToSharedPreferences(int nSteps) {
        Log.d(TAG, "Going to store " + nSteps);
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        SharedPreferences.Editor myEditor = mySharedPreferences.edit();
        myEditor.putInt(N_STEPS_TAKEN, nSteps);
        myEditor.commit();
    }

    /**
     * Returns the delay until screensaver should appear in seconds. This is taken from
     * SharedPreferences. If not found, the MIN_DELAY value is returned.
     * @return
     */
    protected int getStepsFromSharedPreferences() {
        SharedPreferences mySharedPreferences = context.getSharedPreferences(SHARED_PREFS_NAME, 0);
        int result = mySharedPreferences.getInt(N_STEPS_TAKEN, 0);
        return result;
    }

}
