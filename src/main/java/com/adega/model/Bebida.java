package com.adega.model;

public abstract class Bebida {

    protected String nome;
    protected double preco;
    protected int quantidadeEstoque;

    public Bebida(String nome, double preco) {
        this(nome, preco, 0);
    }

    public Bebida(String nome, double preco, int quantidadeEstoque) {
        this.nome = nome;
        this.preco = preco;
        this.quantidadeEstoque = quantidadeEstoque;
    }

    public String getNome(){
        return nome;
    }

    public double getPreco(){
        return preco;
    }

    public int getQuantidadeEstoque() {
        return quantidadeEstoque;
    }

    public void adicionarEstoque(int quantidade) {
        this.quantidadeEstoque += quantidade;
    }

    public void removerEstoque(int quantidade) {
        if (quantidade > quantidadeEstoque) {
            throw new RuntimeException("Estoque insuficiente de " + nome);
        }
        this.quantidadeEstoque -= quantidade;
    }

    public abstract String getDescricao();
}