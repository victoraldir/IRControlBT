package com.quartzo.ircontrol.persistence;

import com.quartzo.ircontrol.utils.Constants;

import java.net.MalformedURLException;
import java.net.URL;

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

    public String getPrettyDescription() {
        return getHost().concat(":").concat(getPort() + " ").concat("(").concat(getDescription()).concat(")");
    }

    public URL getDomain() {

        URL url = null;

        try {
            url = new URL("http://" + getHost() + ":" + getPort());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public URL getURLSender(String code) {
        URL url = null;
        try {
            url = new URL(getDomain(), Constants.URL_SENDER + "/" + code);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public URL getURLReceiver() {
        URL url = null;
        try {
            url = new URL(getDomain(), Constants.URL_RECEIVER);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", host='" + host + '\'' +
                ", port=" + port +
                '}';
    }
}
