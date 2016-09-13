package com.quartzo.ircontrol.persistence;

/*
* VO que representa a entidade responsável por salvar os códigos dos controles
*
 */

import org.json.JSONException;
import org.json.JSONObject;

public class Comando {

    private String descricao;

    private String codigo;

    private Posicao posicao;

    private Appliance appliance;

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public Appliance getAppliance() {
        return appliance;
    }

    public void setAppliance(Appliance appliance) {
        this.appliance = appliance;
    }

    public Posicao getPosicao() {
        return posicao;
    }

    public void setPosicao(Posicao posicao) {
        this.posicao = posicao;
    }

    public String toJSON(){

        JSONObject jsonObject= new JSONObject();
        try {
            jsonObject.put("descricao", getDescricao());
            jsonObject.put("codigo", getCodigo());
            jsonObject.put("posicao", getPosicao());

            return jsonObject.toString();
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return "";
        }

    }
}