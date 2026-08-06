package com.adega.model;

public class Vinho extends Bebida {

    public Vinho(String nome, double preco, int quantidadeEstoque) {
        super(nome, preco, quantidadeEstoque);
    }

    @Override
    public String getDescricao() {
        return "Vinho " + getNome();
    }
}