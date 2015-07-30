package com.example.android.persistence;

import java.util.Set;

/**
 * Created by victor on 19/08/13.
 */

public class Dispositivo {

    private long id;

    private String descricao;

    private Ambiente ambiente;

    private Set<Comando> comandos;

    public long getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Ambiente getAmbiente() {
        return ambiente;
    }

    public void setAmbiente(Ambiente ambiente) {
        this.ambiente = ambiente;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Set<Comando> getComandos() {
        return comandos;
    }

    public void setComandos(Set<Comando> comandos) {
        this.comandos = comandos;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
