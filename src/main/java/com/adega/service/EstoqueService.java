package com.adega.service;

import com.adega.model.copao.CopaoBuilder;
import com.adega.model.copao.CopaoComponente;
import com.adega.model.item.Destilado;
import com.adega.model.item.Energetico;
import com.adega.model.item.Gelo;

import java.util.ArrayList;
import java.util.List;

public class EstoqueService {

    private List<Destilado> destilados = new ArrayList<>();
    private List<Energetico> energeticos = new ArrayList<>();
    private List<Gelo> gelos = new ArrayList<>();

    public void cadastrarDestilado(Destilado destilado) {
        destilados.add(destilado);
    }

    public void cadastrarEnergetico(Energetico energetico) {
        energeticos.add(energetico);
    }

    public void cadastrarGelo(Gelo gelo) {
        gelos.add(gelo);
    }

    public Destilado buscarDestiladoPorNome(String nome) {
        for (Destilado destilado : destilados) {
            if (destilado.getNome().equalsIgnoreCase(nome)) {
                return destilado;
            }
        }
        return null;
    }

    public Energetico buscarEnergeticoPorNome(String nome) {
        for (Energetico e : energeticos) {
            if (e.getNome().equalsIgnoreCase(nome)) {
                return e;
            }
        }
        return null;
    }

    public Gelo buscarGeloPorSabor(String sabor) {
        for (Gelo g : gelos) {
            if (g.getSabor().equalsIgnoreCase(sabor)) {
                return g;
            }
        }
        return null;
    }

    public List<Destilado> listarDestilados() {
        return destilados;
    }

    public List<Energetico> listarEnergeticos() {
        return energeticos;
    }

    public List<Gelo> listarGelos() {
        return gelos;
    }

    public void adicionarEstoqueDestilado(String nome, int quantidadeMl) {
        Destilado destilado = buscarDestiladoPorNome(nome);
        if (destilado == null) {
            throw new RuntimeException("Destilado não encontrado");
        }
        destilado.adicionarEstoque(quantidadeMl);
    }

    public void removerDestilado(String nome) {
        destilados.removeIf(d -> d.getNome().equalsIgnoreCase(nome));
    }

    public void removerEnergetico(String nome) {
        energeticos.removeIf(e -> e.getNome().equalsIgnoreCase(nome));
    }

    public void removerGelo(String sabor) {
        gelos.removeIf(g -> g.getSabor().equalsIgnoreCase(sabor));
    }

    public CopaoComponente criarCopao(String nome, String nomeDestilado, int mlDestilado, String nomeEnergetico, String saborGelo, int quantidadeGelo) {

        Destilado destilado = buscarDestiladoPorNome(nomeDestilado);
        Energetico energetico = buscarEnergeticoPorNome(nomeEnergetico);
        Gelo gelo = buscarGeloPorSabor(saborGelo);

        if (destilado == null) {
            throw new RuntimeException("Destilado não encontrado: " + nomeDestilado);
        }

        if (energetico == null) {
            throw new RuntimeException("Energético não encontrado: " + nomeEnergetico);
        }

        if (gelo == null) {
            throw new RuntimeException("Gelo não encontrado: " + saborGelo);
        }

        if (mlDestilado <= 0) {
            throw new RuntimeException("Quantidade de ml do destilado deve ser maior que zero");
        }

        if (destilado.getQuantidadeEstoque() < mlDestilado) {
            throw new RuntimeException("Estoque insuficiente de " + destilado.getNome() + " (disponível: " + destilado.getQuantidadeEstoque() + "ml)");
        }

        if (energetico.getQuantidadeEstoque() <= 0) {
            throw new RuntimeException("Sem estoque de energético");
        }

        if (gelo.getQuantidadeEstoque() < quantidadeGelo) {
            throw new RuntimeException("Estoque insuficiente de gelo");
        }

        CopaoComponente copao = new CopaoBuilder()
                .setNome(nome)
                .setDestilado(destilado, mlDestilado)
                .setEnergetico(energetico)
                .setGelo(gelo)
                .setQuantidadeGelo(quantidadeGelo)
                .build();

        destilado.removerEstoque(mlDestilado);
        energetico.removerEstoque(1);
        gelo.removerEstoque(quantidadeGelo);

        return copao;
    }
}