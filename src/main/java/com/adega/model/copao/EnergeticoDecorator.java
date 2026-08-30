package com.adega.model.copao;

import com.adega.model.item.Energetico;

public class EnergeticoDecorator extends CopaoDecorator {

    private final Energetico energetico;

    public EnergeticoDecorator(CopaoComponente componente, Energetico energetico) {
        super(componente);
        this.energetico = energetico;
    }

    public Energetico getEnergetico() {
        return energetico;
    }

    @Override
    public double getPreco() {
        return super.getPreco() + energetico.getPreco();
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + ", " + energetico.getNome();
    }
}