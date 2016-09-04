package com.quartzo.ircontrol.application;

import android.app.Application;

import com.quartzo.ircontrol.persistence.Ambiente;
import com.quartzo.ircontrol.persistence.Dispositivo;

/**
 * Created by victor on 26/05/15.
 */
public class MyApplication extends Application {

    private Ambiente roomSelected;
    private Dispositivo applianceSelected;

    public Ambiente getRoomSelected() {
        return roomSelected;
    }

    public void setRoomSelected(Ambiente roomSelected) {
        this.roomSelected = roomSelected;
    }

    public Dispositivo getApplianceSelected() {
        return applianceSelected;
    }

    public void setApplianceSelected(Dispositivo applianceSelected) {
        this.applianceSelected = applianceSelected;
    }
}
