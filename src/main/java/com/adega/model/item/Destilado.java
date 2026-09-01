package com.adega.model.item;

public class Destilado extends Item {

    private String tipo;

    public Destilado(String nome, String tipo, double precoPorMl, int quantidadeMl) {
        super(nome, precoPorMl, quantidadeMl);
        this.tipo = tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public double getPrecoPorMl() {
        return preco;
    }
}