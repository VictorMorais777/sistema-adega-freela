package com.adega.model.copao;

import com.adega.model.item.Destilado;

public class DestiladoDecorator extends CopaoDecorator {

    private final Destilado destilado;
    private final int mlUsado;

    public DestiladoDecorator(CopaoComponente componente, Destilado destilado, int mlUsado) {
        super(componente);
        this.destilado = destilado;
        this.mlUsado = mlUsado;
    }

    public Destilado getDestilado() {
        return destilado;
    }

    public int getMlUsado() {
        return mlUsado;
    }

    @Override
    public double getPreco() {
        return super.getPreco() + (destilado.getPrecoPorMl() * mlUsado);
    }

    @Override
    public String getDescricao() {
        return super.getDescricao() + " com " + mlUsado + "ml de " + destilado.getTipo() + " " + destilado.getNome();
    }
}