package com.adega.model;

import com.adega.model.venda.FormaPagamento;

import java.time.LocalDateTime;

public class VendaRegistro {

    private int id;
    private String descricao;
    private double valorBruto;
    private double desconto;
    private double valor;
    private FormaPagamento formaPagamento;
    private double valorPago;
    private double troco;
    private LocalDateTime dataHora;
    private boolean cancelada;

    public VendaRegistro(String descricao, double valorBruto, double desconto,
                         FormaPagamento formaPagamento, double valorPago, double troco) {
        this.descricao = descricao;
        this.valorBruto = valorBruto;
        this.desconto = desconto;
        this.valor = valorBruto - desconto;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.troco = troco;
        this.dataHora = LocalDateTime.now();
        this.cancelada = false;
    }

    public VendaRegistro(int id, String descricao, double valorBruto, double desconto, double valor,
                         FormaPagamento formaPagamento, double valorPago, double troco,
                         LocalDateTime dataHora, boolean cancelada) {
        this.id = id;
        this.descricao = descricao;
        this.valorBruto = valorBruto;
        this.desconto = desconto;
        this.valor = valor;
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.troco = troco;
        this.dataHora = dataHora;
        this.cancelada = cancelada;
    }

    public int getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public double getValorBruto() {
        return valorBruto;
    }

    public double getDesconto() {
        return desconto;
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

    public boolean isCancelada() {
        return cancelada;
    }

    public String getFormaPagamentoDescricao() {
        return formaPagamento.getDescricao();
    }

    public String getStatus() {
        return cancelada ? "CANCELADA" : "Concluída";
    }
}