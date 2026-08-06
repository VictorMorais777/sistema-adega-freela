package com.adega.model.venda;

import com.adega.model.Bebida;
import com.adega.model.copao.Copao;

import java.time.LocalDateTime;

public class Venda {

    private Bebida bebida;
    private double valor;
    private LocalDateTime data;
    private FormaPagamento formaPagamento;
    private double valorPago;
    private double troco;

    public Venda(Bebida bebida, FormaPagamento formaPagamento) {
        this(bebida, formaPagamento, bebida.getPreco());
    }

    public Venda(Bebida bebida, FormaPagamento formaPagamento, double valorPago) {
        this.bebida = bebida;
        this.valor = bebida.getPreco();
        this.data = LocalDateTime.now();
        this.formaPagamento = formaPagamento;
        this.valorPago = valorPago;
        this.troco = (formaPagamento == FormaPagamento.DINHEIRO) ? (valorPago - valor) : 0;
    }

    public double getValor() {
        return valor;
    }

    public LocalDateTime getData() {
        return data;
    }

    public Bebida getBebida() {
        return bebida;
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

    public String formatar() {
        String tipo = (bebida instanceof Copao) ? "Copão" : bebida.getClass().getSimpleName();

        StringBuilder sb = new StringBuilder();
        sb.append("----------------------------------\n");
        sb.append(tipo).append(": ").append(bebida.getNome()).append("\n");
        sb.append("Descrição: ").append(bebida.getDescricao()).append("\n");
        sb.append("Valor: R$ ").append(String.format("%.2f", valor)).append("\n");
        sb.append("Pagamento: ").append(formaPagamento.getDescricao()).append("\n");

        if (formaPagamento == FormaPagamento.DINHEIRO) {
            sb.append("Valor recebido: R$ ").append(String.format("%.2f", valorPago)).append("\n");
            sb.append("Troco: R$ ").append(String.format("%.2f", troco)).append("\n");
        }

        sb.append("Data: ").append(data.toLocalDate()).append(" ").append(data.toLocalTime().withNano(0)).append("\n");
        sb.append("----------------------------------");

        return sb.toString();
    }
}