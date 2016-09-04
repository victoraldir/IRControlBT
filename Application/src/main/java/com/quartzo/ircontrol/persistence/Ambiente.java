package com.quartzo.ircontrol.persistence;

/**
 * Created by victor on 05/04/15.
 */
public class Ambiente {

    private long id;

    private String descricao;


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
