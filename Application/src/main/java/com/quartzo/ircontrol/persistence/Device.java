package com.quartzo.ircontrol.persistence;

/**
 * Created by victor on 05/04/15.
 */
public class Device {

    private long id;
    private String description;
    private String host;
    private int port;

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

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return getHost().concat(":").concat(getPort() + " ").concat("(").concat(getDescription()).concat(")");
    }
}
