package com.adega.model.venda;

public enum FormaPagamento {
    DINHEIRO("Dinheiro"),
    DEBITO("Cartão de Débito"),
    CREDITO("Cartão de Crédito"),
    PIX("Pix");

    private final String descricao;

    FormaPagamento(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}