package com.adega.model.copao;

import com.adega.model.item.Gelo;

public class GeloDecorator extends CopaoDecorator {

    private final Gelo gelo;
    private final int quantidade;

    public GeloDecorator(CopaoComponente componente, Gelo gelo, int quantidade) {
        super(componente);
        this.gelo = gelo;
        this.quantidade = quantidade;
    }

    public Gelo getGelo() {
        return gelo;
    }

    public int getQuantidade() {
        return quantidade;
    }

    @Override
    public double getPreco() {
        return super.getPreco() + (gelo.getPreco() * quantidade);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " e " + quantidade + "x gelo de " + gelo.getSabor();
    }
}