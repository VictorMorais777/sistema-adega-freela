package com.adega.model.item;

public class Gelo extends Item {

    public Gelo(String sabor, double preco, int quantidadeEstoque) {
        super(sabor, preco, quantidadeEstoque);
    }

    public String getSabor() {
        return nome;
    }
}