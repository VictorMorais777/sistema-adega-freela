package com.adega.model;

public class Gin extends Bebida{

    public Gin(String nome, double preco, int quantidadeEstoque){
        super(nome, preco, quantidadeEstoque);
    }

    @Override
    public String getDescricao() {
        return "Gin " + getNome();
    }

}