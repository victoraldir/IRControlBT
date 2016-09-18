package com.quartzo.ircontrol.persistence;

/**
 * Created by victor on 19/08/13.
 */

public class Appliance {

    private long id;

    private String description;

    private Device device;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Device getDevice() {
        return device;
    }

    public void setDevice(Device device) {
        this.device = device;
    }

    @Override
    public String toString() {
        return "Appliance{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", device=" + device +
                '}';
    }
}
