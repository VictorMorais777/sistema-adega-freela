package com.adega.service;

import com.adega.model.copao.CopaoBuilder;
import com.adega.model.copao.CopaoComponente;
import com.adega.model.item.Gin;
import com.adega.model.item.Energetico;
import com.adega.model.item.Gelo;

import java.util.ArrayList;
import java.util.List;

public class EstoqueService {

    private List<Gin> gins = new ArrayList<>();
    private List<Energetico> energeticos = new ArrayList<>();
    private List<Gelo> gelos = new ArrayList<>();

    public void cadastrarGin(Gin gin) {
        gins.add(gin);
    }

    public void cadastrarEnergetico(Energetico energetico) {
        energeticos.add(energetico);
    }

    public void cadastrarGelo(Gelo gelo) {
        gelos.add(gelo);
    }

    public Gin buscarGinPorNome(String nome) {
        for (Gin gin : gins) {
            if (gin.getNome().equalsIgnoreCase(nome)) {
                return gin;
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

    public List<Gin> listarGins() {
        return gins;
    }

    public List<Energetico> listarEnergeticos() {
        return energeticos;
    }

    public List<Gelo> listarGelos() {
        return gelos;
    }

    public void adicionarEstoqueGin(String nome, int quantidade) {
        Gin gin = buscarGinPorNome(nome);
        if (gin == null) {
            throw new RuntimeException("Gin não encontrado");
        }
        gin.adicionarEstoque(quantidade);
    }

    public void removerEstoqueGin(String nome, int quantidade) {
        Gin gin = buscarGinPorNome(nome);
        if (gin == null) {
            throw new RuntimeException("Gin não encontrado");
        }
        gin.removerEstoque(quantidade);
    }

    public void removerGin(String nome) {
        gins.removeIf(gin -> gin.getNome().equalsIgnoreCase(nome));
    }

    public void removerEnergetico(String nome) {
        energeticos.removeIf(e -> e.getNome().equalsIgnoreCase(nome));
    }

    public void removerGelo(String sabor) {
        gelos.removeIf(g -> g.getSabor().equalsIgnoreCase(sabor));
    }

    public CopaoComponente criarCopao(String nome, String nomeGin, String nomeEnergetico, String saborGelo, int quantidadeGelo) {

        Gin gin = buscarGinPorNome(nomeGin);
        Energetico energetico = buscarEnergeticoPorNome(nomeEnergetico);
        Gelo gelo = buscarGeloPorSabor(saborGelo);

        if (gin == null) {
            throw new RuntimeException("Gin não encontrado: " + nomeGin);
        }

        if (energetico == null) {
            throw new RuntimeException("Energético não encontrado: " + nomeEnergetico);
        }

        if (gelo == null) {
            throw new RuntimeException("Gelo não encontrado: " + saborGelo);
        }

        if (gin.getQuantidadeEstoque() <= 0) {
            throw new RuntimeException("Sem estoque de gin");
        }

        if (energetico.getQuantidadeEstoque() <= 0) {
            throw new RuntimeException("Sem estoque de energético");
        }

        if (gelo.getQuantidadeEstoque() < quantidadeGelo) {
            throw new RuntimeException("Estoque insuficiente de gelo");
        }

        CopaoComponente copao = new CopaoBuilder()
                .setNome(nome)
                .setGin(gin)
                .setEnergetico(energetico)
                .setGelo(gelo)
                .setQuantidadeGelo(quantidadeGelo)
                .build();

        gin.removerEstoque(1);
        energetico.removerEstoque(1);
        gelo.removerEstoque(quantidadeGelo);

        return copao;
    }
}