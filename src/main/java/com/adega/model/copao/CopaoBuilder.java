package com.adega.model.copao;

import com.adega.model.item.Gin;
import com.adega.model.item.Energetico;
import com.adega.model.item.Gelo;

public class CopaoBuilder {

    private String nome;
    private Gin gin;
    private Energetico energetico;
    private Gelo gelo;

    public CopaoBuilder setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public CopaoBuilder setGin(Gin gin) {
        this.gin = gin;
        return this;
    }

    public CopaoBuilder setEnergetico(Energetico energetico) {
        this.energetico = energetico;
        return this;
    }

    public CopaoBuilder setGelo(Gelo gelo) {
        this.gelo = gelo;
        return this;
    }

    public Copao build() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalStateException("Nome do copão é obrigatório");
        }
        if (gin == null) {
            throw new IllegalStateException("Gin é obrigatório para montar o copão");
        }
        if (energetico == null) {
            throw new IllegalStateException("Energético é obrigatório para montar o copão");
        }
        if (gelo == null) {
            throw new IllegalStateException("Gelo é obrigatório para montar o copão");
        }
        return new Copao(nome, gin, energetico, gelo);
    }
}