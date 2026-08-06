package com.adega.model;

public class ItemEstoque {

    private int id;
    private String categoria;
    private String nome;
    private int quantidade;
    private double precoCompra;
    private double precoVenda;
    private int estoqueMinimo;

    public ItemEstoque(String categoria, String nome, int quantidade, double precoCompra, double precoVenda, int estoqueMinimo){
        this.categoria = categoria;
        this.nome = nome;
        this.quantidade = quantidade;
        this.precoCompra = precoCompra;
        this.precoVenda = precoVenda;
        this.estoqueMinimo = estoqueMinimo;
    }

    public ItemEstoque(int id, String categoria, String nome, int quantidade, double precoCompra, double precoVenda, int estoqueMinimo){
        this(categoria, nome, quantidade, precoCompra, precoVenda, estoqueMinimo);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public String getNome() {
        return nome;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public double getPrecoCompra() {
        return precoCompra;
    }

    public double getPrecoVenda() {
        return precoVenda;
    }

    public int getEstoqueMinimo() {
        return estoqueMinimo;
    }

    public void setNome(String nome) {
        if (nome != null && !nome.isBlank()) {
            this.nome = nome;
        }
    }

    public void setQuantidade(int quantidade) {
        if (quantidade >= 0) {
            this.quantidade = quantidade;
        }
    }

    public void setPrecoCompra(double precoCompra) {
        if (precoCompra >= 0) {
            this.precoCompra = precoCompra;
        }
    }

    public void setPrecoVenda(double precoVenda) {
        if (precoVenda >= 0) {
            this.precoVenda = precoVenda;
        }
    }

    public void setEstoqueMinimo(int estoqueMinimo) {
        if (estoqueMinimo >= 0) {
            this.estoqueMinimo = estoqueMinimo;
        }
    }

    public void adicionarEstoque(int quantidade) {
        if (quantidade > 0) {
            this.quantidade += quantidade;
        }
    }

    public boolean removerEstoque(int quantidade) {
        if (quantidade <= 0) return false;

        if (this.quantidade >= quantidade) {
            this.quantidade -= quantidade;
            return true;
        }

        return false;
    }

    public boolean estoqueBaixo() {
        return this.quantidade <= estoqueMinimo;
    }

    public double calcularValorEstoque() {
        return quantidade * precoCompra;
    }

    public double calcularValorVendaEstoque() {
        return quantidade * precoVenda;
    }

    public boolean precisaReposicaoUrgente() {
        return quantidade == 0;
    }
}