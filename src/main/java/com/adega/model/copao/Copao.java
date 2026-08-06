package com.adega.model.copao;

import com.adega.model.item.Gin;
import com.adega.model.item.Energetico;
import com.adega.model.item.Gelo;
import com.adega.model.Bebida;

public class Copao extends Bebida {

    private Gin gin;
    private Energetico energetico;
    private Gelo gelo;

    public Copao(String nome, Gin gin, Energetico energetico, Gelo gelo) {
        super(nome, 0);
        this.gin = gin;
        this.energetico = energetico;
        this.gelo = gelo;
    }

    @Override
    public String getDescricao() {
        return nome + " com "
                + gin.getNome() + ", "
                + energetico.getNome()
                + " e gelo de " + gelo.getSabor();
    }

    @Override
    public double getPreco() {
        return gin.getPreco() + energetico.getPreco() + gelo.getPreco();
    }
}