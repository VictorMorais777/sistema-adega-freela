package com.adega.gui;

import com.adega.model.venda.FormaPagamento;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Optional;

public class PagamentoDialog {

    public interface AoConfirmar {
        void confirmar(FormaPagamento formaPagamento, double desconto, double valorPago, double troco);
    }

    public static void abrir(String descricao, double valorBruto, AoConfirmar aoConfirmar) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Pagamento");

        Label labelResumo = new Label(descricao);
        labelResumo.setWrapText(true);
        labelResumo.setStyle("-fx-font-weight: bold;");

        Label labelValorBruto = new Label(String.format("Valor: R$ %.2f", valorBruto));

        Label labelDesconto = new Label("Desconto (R$), se houver:");
        TextField campoDesconto = new TextField();
        FormatadorCampo.aplicarFormatoPreco(campoDesconto);

        Label labelTotal = new Label(String.format("Total a pagar: R$ %.2f", valorBruto));
        labelTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label labelForma = new Label("Forma de pagamento:");
        ComboBox<FormaPagamento> comboForma = new ComboBox<>();
        comboForma.getItems().addAll(FormaPagamento.values());
        comboForma.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(FormaPagamento forma) {
                return forma == null ? "" : forma.getDescricao();
            }

            @Override
            public FormaPagamento fromString(String string) {
                return null;
            }
        });

        Label labelValorPago = new Label("Valor recebido em dinheiro:");
        TextField campoValorPago = new TextField();
        FormatadorCampo.aplicarFormatoPreco(campoValorPago);
        labelValorPago.setVisible(false);
        labelValorPago.setManaged(false);
        campoValorPago.setVisible(false);
        campoValorPago.setManaged(false);

        Label labelMensagem = new Label();
        labelMensagem.setWrapText(true);

        campoDesconto.textProperty().addListener((obs, antigo, novo) -> {
            double desconto = lerValor(campoDesconto.getText());
            double total = valorBruto - desconto;
            if (total < 0) {
                labelTotal.setText("Desconto maior que o valor da venda!");
                labelTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #b00020;");
            } else {
                labelTotal.setText(String.format("Total a pagar: R$ %.2f", total));
                labelTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
            }
        });

        comboForma.setOnAction(e -> {
            boolean dinheiro = comboForma.getValue() == FormaPagamento.DINHEIRO;
            labelValorPago.setVisible(dinheiro);
            labelValorPago.setManaged(dinheiro);
            campoValorPago.setVisible(dinheiro);
            campoValorPago.setManaged(dinheiro);
            labelMensagem.setText("");
        });

        Button botaoConfirmar = new Button("Confirmar");
        botaoConfirmar.setOnAction(e -> {
            FormaPagamento forma = comboForma.getValue();

            if (forma == null) {
                mostrarErro(labelMensagem, "Selecione a forma de pagamento.");
                return;
            }

            double desconto = lerValor(campoDesconto.getText());
            if (desconto < 0) {
                mostrarErro(labelMensagem, "Desconto inválido.");
                return;
            }
            if (desconto > valorBruto) {
                mostrarErro(labelMensagem, "O desconto não pode ser maior que o valor da venda.");
                return;
            }

            double total = valorBruto - desconto;
            double valorPago = total;
            double troco = 0;

            if (forma == FormaPagamento.DINHEIRO) {
                if (campoValorPago.getText().trim().isEmpty()) {
                    mostrarErro(labelMensagem, "Digite o valor recebido.");
                    return;
                }

                valorPago = lerValor(campoValorPago.getText());

                if (valorPago < total) {
                    mostrarErro(labelMensagem, String.format("Valor insuficiente. Faltam R$ %.2f.", total - valorPago));
                    return;
                }

                troco = valorPago - total;
            }

            StringBuilder resumo = new StringBuilder();
            resumo.append(descricao).append("\n\n");
            resumo.append(String.format("Valor: R$ %.2f%n", valorBruto));
            if (desconto > 0) {
                resumo.append(String.format("Desconto: R$ %.2f%n", desconto));
            }
            resumo.append(String.format("Total: R$ %.2f%n", total));
            resumo.append("Pagamento: ").append(forma.getDescricao()).append("\n");
            if (forma == FormaPagamento.DINHEIRO) {
                resumo.append(String.format("Recebido: R$ %.2f%n", valorPago));
                resumo.append(String.format("Troco: R$ %.2f%n", troco));
            }
            resumo.append("\nConfirmar esta venda?");

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, resumo.toString(), ButtonType.YES, ButtonType.NO);
            confirmacao.setTitle("Confirmar venda");
            confirmacao.setHeaderText(null);
            confirmacao.getDialogPane().setMinWidth(380);

            Optional<ButtonType> resposta = confirmacao.showAndWait();

            if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
                aoConfirmar.confirmar(forma, desconto, valorPago, troco);
                dialog.close();
            }
        });

        Button botaoCancelar = new Button("Cancelar");
        botaoCancelar.setOnAction(e -> dialog.close());

        VBox root = new VBox(10,
                labelResumo,
                labelValorBruto,
                labelDesconto, campoDesconto,
                labelTotal,
                labelForma, comboForma,
                labelValorPago, campoValorPago,
                labelMensagem,
                new HBox(10, botaoConfirmar, botaoCancelar)
        );
        root.setPadding(new Insets(20));

        dialog.setScene(new Scene(root, 380, 430));
        dialog.showAndWait();
    }

    private static double lerValor(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private static void mostrarErro(Label label, String texto) {
        label.setStyle("-fx-text-fill: #b00020;");
        label.setText(texto);
    }
}