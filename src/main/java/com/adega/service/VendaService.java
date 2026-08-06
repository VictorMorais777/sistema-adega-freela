package com.adega.service;

import com.adega.model.Bebida;
import com.adega.model.venda.FormaPagamento;
import com.adega.model.venda.Venda;

import java.time.LocalDate;
import java.util.*;

public class VendaService {

    private List<Venda> vendas = new ArrayList<>();

    public Venda registrarVenda(Bebida bebida, FormaPagamento formaPagamento) {
        return registrarVenda(bebida, formaPagamento, bebida.getPreco());
    }

    public Venda registrarVenda(Bebida bebida, FormaPagamento formaPagamento, double valorPago) {
        if (formaPagamento == FormaPagamento.DINHEIRO && valorPago < bebida.getPreco()) {
            throw new RuntimeException("Valor recebido insuficiente. Faltam R$ "
                    + String.format("%.2f", bebida.getPreco() - valorPago));
        }

        Venda venda = new Venda(bebida, formaPagamento, valorPago);
        vendas.add(venda);
        return venda;
    }

    public double calcularFaturamento() {
        double total = 0;
        for (Venda v : vendas) {
            total += v.getValor();
        }
        return total;
    }

    public List<Venda> listarVendas() {
        return vendas;
    }

    public Map<LocalDate, Double> faturamentoPorDia() {
        Map<LocalDate, Double> mapa = new HashMap<>();

        for (Venda v : vendas) {
            LocalDate dia = v.getData().toLocalDate();

            mapa.put(dia, mapa.getOrDefault(dia, 0.0) + v.getValor());
        }

        return mapa;
    }

    public String bebidaMaisVendida() {
        Map<String, Integer> contagem = new HashMap<>();

        for (Venda v : vendas) {
            String nome = v.getBebida().getNome();
            contagem.put(nome, contagem.getOrDefault(nome, 0) + 1);
        }

        return Collections.max(contagem.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    public Map<String, Integer> rankingBebidas() {
        Map<String, Integer> ranking = new HashMap<>();

        for (Venda v : vendas) {
            String nome = v.getBebida().getNome();
            ranking.put(nome, ranking.getOrDefault(nome, 0) + 1);
        }

        return ranking;
    }

    public List<Venda> filtrarPorPeriodo(LocalDate inicio, LocalDate fim) {
        List<Venda> resultado = new ArrayList<>();

        for (Venda v : vendas) {
            LocalDate data = v.getData().toLocalDate();

            if (!data.isBefore(inicio) && !data.isAfter(fim)) {
                resultado.add(v);
            }
        }

        return resultado;
    }

    public void imprimirRelatorio() {
        System.out.println("\n========= RELATÓRIO DE VENDAS =========\n");

        for (Venda v : vendas) {
            System.out.println(v.formatar());
        }

        System.out.println("\nFATURAMENTO TOTAL: R$ " + String.format("%.2f", calcularFaturamento()));
    }
}