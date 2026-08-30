package com.adega.model.copao;

public abstract class CopaoDecorator extends CopaoComponente {

    protected final CopaoComponente componente;

    public CopaoDecorator(CopaoComponente componente) {
        super(componente.getNome());
        this.componente = componente;
    }

    @Override
    public double getPreco() {
        return componente.getPreco();
    }

    @Override
    public String getDescricao() {
        return componente.getDescricao();
    }
}