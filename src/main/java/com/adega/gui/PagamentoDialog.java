package com.adega.gui;

import com.adega.model.venda.FormaPagamento;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.function.BiConsumer;

public class PagamentoDialog {

    public interface AoConfirmar {
        void confirmar(FormaPagamento formaPagamento, double valorPago, double troco);
    }

    public static void abrir(String descricao, double valor, AoConfirmar aoConfirmar) {
        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Pagamento");

        Label labelResumo = new Label(descricao + "\nValor: R$ " + String.format("%.2f", valor));
        labelResumo.setStyle("-fx-font-weight: bold;");

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
                labelMensagem.setStyle("-fx-text-fill: #b00020;");
                labelMensagem.setText("Selecione a forma de pagamento.");
                return;
            }

            double valorPago = valor;
            double troco = 0;

            if (forma == FormaPagamento.DINHEIRO) {
                try {
                    valorPago = Double.parseDouble(campoValorPago.getText().trim().replace(",", "."));
                } catch (NumberFormatException ex) {
                    labelMensagem.setStyle("-fx-text-fill: #b00020;");
                    labelMensagem.setText("Digite um valor recebido válido.");
                    return;
                }

                if (valorPago < valor) {
                    labelMensagem.setStyle("-fx-text-fill: #b00020;");
                    labelMensagem.setText(String.format("Valor insuficiente. Faltam R$ %.2f.", valor - valorPago));
                    return;
                }

                troco = valorPago - valor;
            }

            aoConfirmar.confirmar(forma, valorPago, troco);
            dialog.close();
        });

        Button botaoCancelar = new Button("Cancelar");
        botaoCancelar.setOnAction(e -> dialog.close());

        VBox root = new VBox(12,
                labelResumo,
                labelForma, comboForma,
                labelValorPago, campoValorPago,
                labelMensagem,
                new javafx.scene.layout.HBox(10, botaoConfirmar, botaoCancelar)
        );
        root.setPadding(new Insets(20));

        dialog.setScene(new Scene(root, 340, 320));
        dialog.showAndWait();
    }
}