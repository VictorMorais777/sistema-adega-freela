package com.adega.model.copao;

import com.adega.model.item.Gin;

public class GinDecorator extends CopaoDecorator {

    private final Gin gin;

    public GinDecorator(CopaoComponente componente, Gin gin) {
        super(componente);
        this.gin = gin;
    }

    public Gin getGin() {
        return gin;
    }

    @Override
    public double getPreco() {
        return super.getPreco() + gin.getPreco();
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " com " + gin.getNome();
    }
}