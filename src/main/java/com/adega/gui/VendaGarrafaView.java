package com.adega.gui;

import com.adega.model.ItemEstoque;
import com.adega.model.ItemVendido;
import com.adega.model.VendaRegistro;
import com.adega.repository.EstoqueRepository;
import com.adega.repository.VendaRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class VendaGarrafaView {

    private final EstoqueRepository estoqueRepository = new EstoqueRepository();
    private final VendaRepository vendaRepository = new VendaRepository();

    private final ComboBox<ItemEstoque> comboProduto = new ComboBox<>();
    private final Spinner<Integer> spinnerQuantidade = new Spinner<>(1, 999, 1);
    private final Label labelPrecoUnitario = new Label();
    private final Label labelTotal = new Label();
    private final Label labelMensagem = new Label();

    public VBox getView() {
        carregarCombo();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Produto:"), 0, 0);
        grid.add(comboProduto, 1, 0);

        grid.add(new Label("Quantidade:"), 0, 1);
        grid.add(spinnerQuantidade, 1, 1);

        grid.add(new Label("Preço unitário:"), 0, 2);
        grid.add(labelPrecoUnitario, 1, 2);

        grid.add(new Label("Total:"), 0, 3);
        labelTotal.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        grid.add(labelTotal, 1, 3);

        comboProduto.setOnAction(e -> atualizarValores());
        spinnerQuantidade.valueProperty().addListener((obs, antigo, novo) -> atualizarValores());

        Button botaoVender = new Button("Vender");
        botaoVender.setOnAction(e -> vender());

        VBox root = new VBox(15, grid, botaoVender, labelMensagem);
        root.setPadding(new Insets(10));
        return root;
    }

    private void carregarCombo() {
        List<ItemEstoque> todos = estoqueRepository.listarTodos();
        comboProduto.getItems().setAll(todos);

        comboProduto.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(ItemEstoque item) {
                return item == null ? "" : item.getCategoria() + " - " + item.getNome() + " (Qtd: " + item.getQuantidade() + ")";
            }

            @Override
            public ItemEstoque fromString(String string) {
                return null;
            }
        });
    }

    private void atualizarValores() {
        ItemEstoque item = comboProduto.getValue();

        if (item == null) {
            labelPrecoUnitario.setText("");
            labelTotal.setText("");
            return;
        }

        int quantidade = spinnerQuantidade.getValue();
        labelPrecoUnitario.setText(String.format("R$ %.2f", item.getPrecoVenda()));
        labelTotal.setText(String.format("R$ %.2f", item.getPrecoVenda() * quantidade));
    }

    private void vender() {
        ItemEstoque item = comboProduto.getValue();

        if (item == null) {
            mostrarErro("Selecione um produto.");
            return;
        }

        int quantidade = spinnerQuantidade.getValue();

        if (item.getQuantidade() < quantidade) {
            mostrarErro("Estoque insuficiente de " + item.getNome() + " (disponível: " + item.getQuantidade() + ").");
            return;
        }

        double valorBruto = item.getPrecoVenda() * quantidade;
        String descricao = quantidade + "x " + item.getCategoria() + " " + item.getNome();

        PagamentoDialog.abrir(descricao, valorBruto, (forma, desconto, valorPago, troco) -> {
            estoqueRepository.atualizarQuantidade(item.getId(), item.getQuantidade() - quantidade);

            VendaRegistro venda = new VendaRegistro(descricao, valorBruto, desconto, forma, valorPago, troco);
            vendaRepository.salvar(venda, List.of(new ItemVendido(item.getId(), quantidade)));

            mostrarSucesso(descricao + " vendido(a)! Total: R$ " + String.format("%.2f", valorBruto - desconto)
                    + (troco > 0 ? " | Troco: R$ " + String.format("%.2f", troco) : ""));
            carregarCombo();
            spinnerQuantidade.getValueFactory().setValue(1);
            labelPrecoUnitario.setText("");
            labelTotal.setText("");
        });
    }

    private void mostrarErro(String texto) {
        labelMensagem.setStyle("-fx-text-fill: #b00020;");
        labelMensagem.setText(texto);
    }

    private void mostrarSucesso(String texto) {
        labelMensagem.setStyle("-fx-text-fill: #1b5e20;");
        labelMensagem.setText(texto);
    }
}