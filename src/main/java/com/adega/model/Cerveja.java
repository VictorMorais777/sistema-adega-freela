package com.adega.model;

public class Cerveja extends Bebida {

    public Cerveja(String nome, double preco, int quantidadeEstoque){
        super(nome, preco, quantidadeEstoque);
    }

    @Override
    public String getDescricao() {
        return "Cerveja " + getNome();
    }
}