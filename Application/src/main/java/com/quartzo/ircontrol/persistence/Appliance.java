package com.quartzo.ircontrol.persistence;

import java.util.Set;

/**
 * Created by victor on 19/08/13.
 */

public class Appliance {

    private long id;

    private String description;

    private Device device;

    private Set<Comando> commands;

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

    public Set<Comando> getCommands() {
        return commands;
    }

    public void setCommands(Set<Comando> commands) {
        this.commands = commands;
    }

    @Override
    public String toString() {
        return description;
    }
}
