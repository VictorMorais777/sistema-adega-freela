package com.adega.model;

public class Pinga extends Bebida {

    public Pinga(String nome, double preco, int quantidade) {
        super(nome, preco, quantidade);
    }

    @Override
    public String getDescricao() {
        return "Pinga " + getNome();
    }
}