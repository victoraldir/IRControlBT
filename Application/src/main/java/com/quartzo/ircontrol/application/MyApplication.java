package com.quartzo.ircontrol.application;

import android.app.Application;
import android.util.Log;

import com.quartzo.ircontrol.BuildConfig;
import com.quartzo.ircontrol.persistence.Appliance;
import com.quartzo.ircontrol.persistence.Device;

/**
 * Created by victor on 26/05/15.
 */
public class MyApplication extends Application {

    private Device deviceSelected;
    private Appliance applianceSelected;
    private String TAG = "myIrcontrolLog";

    public MyApplication() {

    }

    public Device getDeviceSelected() {
        return deviceSelected;
    }

    public void setDeviceSelected(Device deviceSelected) {

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "MyApplication.setDeviceSelected: " + deviceSelected);
        }

        this.deviceSelected = deviceSelected;
    }

    public Appliance getApplianceSelected() {
        return applianceSelected;
    }

    public void setApplianceSelected(Appliance applianceSelected) {

        if (BuildConfig.DEBUG) {
            Log.d(TAG, "MyApplication.setApplianceSelected: " + applianceSelected);
        }
        this.applianceSelected = applianceSelected;
    }
}
