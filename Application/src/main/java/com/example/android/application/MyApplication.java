package com.example.android.application;

import android.app.Application;

import com.example.android.persistence.Ambiente;
import com.example.android.persistence.Dispositivo;

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
