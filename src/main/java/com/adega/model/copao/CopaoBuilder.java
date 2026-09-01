package com.adega.model.copao;

import com.adega.model.item.Destilado;
import com.adega.model.item.Energetico;
import com.adega.model.item.Gelo;

/**
 * Orquestra a montagem do copão encadeando os decoradores
 * (base -> destilado -> energético -> gelo) e entrega o componente final pronto.
 */
public class CopaoBuilder {

    private String nome;
    private Destilado destilado;
    private int mlDestilado;
    private Energetico energetico;
    private Gelo gelo;
    private int quantidadeGelo = 1;

    public CopaoBuilder setNome(String nome) {
        this.nome = nome;
        return this;
    }

    public CopaoBuilder setDestilado(Destilado destilado, int mlDestilado) {
        this.destilado = destilado;
        this.mlDestilado = mlDestilado;
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

    public CopaoBuilder setQuantidadeGelo(int quantidadeGelo) {
        this.quantidadeGelo = quantidadeGelo;
        return this;
    }

    public CopaoComponente build() {
        if (nome == null || nome.isBlank()) {
            throw new IllegalStateException("Nome do copão é obrigatório");
        }
        if (destilado == null) {
            throw new IllegalStateException("Destilado é obrigatório para montar o copão");
        }
        if (mlDestilado <= 0) {
            throw new IllegalStateException("Quantidade de ml do destilado deve ser maior que zero");
        }
        if (energetico == null) {
            throw new IllegalStateException("Energético é obrigatório para montar o copão");
        }
        if (gelo == null) {
            throw new IllegalStateException("Gelo é obrigatório para montar o copão");
        }
        if (quantidadeGelo <= 0) {
            throw new IllegalStateException("Quantidade de gelo deve ser maior que zero");
        }

        CopaoComponente copao = new CopaoBase(nome);
        copao = new DestiladoDecorator(copao, destilado, mlDestilado);
        copao = new EnergeticoDecorator(copao, energetico);
        copao = new GeloDecorator(copao, gelo, quantidadeGelo);

        return copao;
    }
}