package com.adega.model;

public enum Papel {
    PATRAO("Patrão"),
    FUNCIONARIO("Funcionário");

    private final String descricao;

    Papel(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}