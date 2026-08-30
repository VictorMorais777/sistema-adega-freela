package com.adega.model.copao;

public class CopaoBase extends CopaoComponente {

    public CopaoBase(String nome) {
        super(nome);
    }

    @Override
    public String getDescricao() {
        return nome;
    }

    @Override
    public double getPreco() {
        return 0;
    }
}