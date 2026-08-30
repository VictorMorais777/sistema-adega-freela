package com.adega.model;

import com.adega.model.venda.FormaPagamento;

import java.time.LocalDateTime;

public class VendaRegistro {

    private int id;
    private String descricao;
    private double valor;
    private FormaPagamento formaPagamento;
    private double valorPago;
    private double troco;
    private LocalDateTime dataHora;

    public VendaRegistro(String descricao, double valor, FormaPagamento formaPagamento, double valorPago, double troco) {
        this.descricao = descricao;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.troco = troco;
        this.dataHora = LocalDateTime.now();
    }

    public VendaRegistro(int id, String descricao, double valor, FormaPagamento formaPagamento, double valorPago, double troco, LocalDateTime dataHora) {
        this.id = id;
        this.descricao = descricao;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.troco = troco;
        this.dataHora = dataHora;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValor() {
        return valor;
    }

    public FormaPagamento getFormaPagamento() {
        return formaPagamento;
    }

    public double getValorPago() {
        return valorPago;
    }

    public double getTroco() {
        return troco;
    }

    public LocalDateTime getDataHora() {
        return dataHora;
    }

    public String getFormaPagamentoDescricao() {
        return formaPagamento.getDescricao();
    }
}