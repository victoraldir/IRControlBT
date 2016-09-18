package com.quartzo.ircontrol.persistence;

/*
* VO que representa a entidade responsável por salvar os códigos dos controles
*
 */

public class Command {

    private String description;

    private String code;

    private Position position;

    private Appliance appliance;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }

}