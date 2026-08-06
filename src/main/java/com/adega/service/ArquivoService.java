package com.adega.service;

import com.adega.model.Cerveja;
import com.adega.model.Gin;
import com.adega.model.Pinga;
import com.adega.model.Vinho;
import com.adega.model.item.Energetico;
import com.adega.model.item.Gelo;
import com.adega.model.venda.Venda;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class ArquivoService {

    private static final String ARQUIVO_VENDAS = "vendas.txt";
    private static final String ARQUIVO_ESTOQUE_ITENS = "estoque_itens.txt";
    private static final String ARQUIVO_ESTOQUE_GARRAFAS = "estoque_garrafas.txt";

    // ===================== VENDAS =====================

    public void salvarVendas(List<Venda> vendas) {
        try {
            FileWriter writer = new FileWriter(ARQUIVO_VENDAS);

            for (Venda v : vendas) {
                writer.write(v.formatar() + "\n");
            }

            writer.close();
            System.out.println("Vendas salvas com sucesso!");

        } catch (IOException e) {
            System.out.println("Erro ao salvar arquivo de vendas.");
        }
    }

    // ===================== ESTOQUE DE ITENS DE COPÃO =====================

    public void salvarEstoqueItens(EstoqueService estoque) {
        try (FileWriter writer = new FileWriter(ARQUIVO_ESTOQUE_ITENS)) {
            for (com.adega.model.item.Gin gin : estoque.listarGins()) {
                writer.write(linha("GIN", gin.getNome(), gin.getPreco(), gin.getQuantidadeEstoque()));
            }
            for (Energetico e : estoque.listarEnergeticos()) {
                writer.write(linha("ENERGETICO", e.getNome(), e.getPreco(), e.getQuantidadeEstoque()));
            }
            for (Gelo g : estoque.listarGelos()) {
                writer.write(linha("GELO", g.getSabor(), g.getPreco(), g.getQuantidadeEstoque()));
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar estoque de itens de copão.");
        }
    }

    public void carregarEstoqueItens(EstoqueService estoque) {
        if (!Files.exists(Path.of(ARQUIVO_ESTOQUE_ITENS))) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_ESTOQUE_ITENS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] campos = linha.split(";");
                String tipo = campos[0];
                String nome = campos[1];
                double preco = Double.parseDouble(campos[2]);
                int quantidade = Integer.parseInt(campos[3]);

                switch (tipo) {
                    case "GIN" -> estoque.cadastrarGin(new com.adega.model.item.Gin(nome, preco, quantidade));
                    case "ENERGETICO" -> estoque.cadastrarEnergetico(new Energetico(nome, preco, quantidade));
                    case "GELO" -> estoque.cadastrarGelo(new Gelo(nome, preco, quantidade));
                }
            }
            System.out.println("Estoque de itens de copão carregado com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao carregar estoque de itens de copão.");
        }
    }

    // ===================== ESTOQUE DE GARRAFAS =====================

    public void salvarEstoqueGarrafas(GarrafaService garrafas) {
        try (FileWriter writer = new FileWriter(ARQUIVO_ESTOQUE_GARRAFAS)) {
            for (Cerveja c : garrafas.listarCervejas()) {
                writer.write(linha("CERVEJA", c.getNome(), c.getPreco(), c.getQuantidadeEstoque()));
            }
            for (Vinho v : garrafas.listarVinhos()) {
                writer.write(linha("VINHO", v.getNome(), v.getPreco(), v.getQuantidadeEstoque()));
            }
            for (Pinga p : garrafas.listarPingas()) {
                writer.write(linha("PINGA", p.getNome(), p.getPreco(), p.getQuantidadeEstoque()));
            }
            for (Gin g : garrafas.listarGins()) {
                writer.write(linha("GIN_GARRAFA", g.getNome(), g.getPreco(), g.getQuantidadeEstoque()));
            }
        } catch (IOException e) {
            System.out.println("Erro ao salvar estoque de garrafas.");
        }
    }

    public void carregarEstoqueGarrafas(GarrafaService garrafas) {
        if (!Files.exists(Path.of(ARQUIVO_ESTOQUE_GARRAFAS))) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(ARQUIVO_ESTOQUE_GARRAFAS))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                if (linha.isBlank()) continue;
                String[] campos = linha.split(";");
                String tipo = campos[0];
                String nome = campos[1];
                double preco = Double.parseDouble(campos[2]);
                int quantidade = Integer.parseInt(campos[3]);

                switch (tipo) {
                    case "CERVEJA" -> garrafas.cadastrarCerveja(new Cerveja(nome, preco, quantidade));
                    case "VINHO" -> garrafas.cadastrarVinho(new Vinho(nome, preco, quantidade));
                    case "PINGA" -> garrafas.cadastrarPinga(new Pinga(nome, preco, quantidade));
                    case "GIN_GARRAFA" -> garrafas.cadastrarGin(new Gin(nome, preco, quantidade));
                }
            }
            System.out.println("Estoque de garrafas carregado com sucesso!");
        } catch (IOException e) {
            System.out.println("Erro ao carregar estoque de garrafas.");
        }
    }

    // ===================== HELPER =====================

    private String linha(String tipo, String nome, double preco, int quantidade) {
        return tipo + ";" + nome + ";" + preco + ";" + quantidade + "\n";
    }
}