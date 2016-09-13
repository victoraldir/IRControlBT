package com.quartzo.ircontrol.application;

import android.app.Application;

import com.quartzo.ircontrol.persistence.Device;
import com.quartzo.ircontrol.persistence.Appliance;

/**
 * Created by victor on 26/05/15.
 */
public class MyApplication extends Application {

    private Device roomSelected;
    private Appliance applianceSelected;

    public Device getRoomSelected() {
        return roomSelected;
    }

    public void setRoomSelected(Device roomSelected) {
        this.roomSelected = roomSelected;
    }

    public Appliance getApplianceSelected() {
        return applianceSelected;
    }

    public void setApplianceSelected(Appliance applianceSelected) {
        this.applianceSelected = applianceSelected;
    }
}
