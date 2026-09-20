package com.adega.model;

public class ItemVendido {

    private final int itemEstoqueId;
    private final int quantidade;

    public ItemVendido(int itemEstoqueId, int quantidade) {
        this.itemEstoqueId = itemEstoqueId;
        this.quantidade = quantidade;
    }

    public int getItemEstoqueId() {
        return itemEstoqueId;
    }

    public int getQuantidade() {
        return quantidade;
    }
}