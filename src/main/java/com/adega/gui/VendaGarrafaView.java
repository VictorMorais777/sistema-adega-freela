package com.adega.gui;

import com.adega.model.ItemEstoque;
import com.adega.model.VendaRegistro;
import com.adega.repository.EstoqueRepository;
import com.adega.repository.VendaRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

public class VendaGarrafaView {

    private final EstoqueRepository estoqueRepository = new EstoqueRepository();
    private final VendaRepository vendaRepository = new VendaRepository();

    private final ComboBox<ItemEstoque> comboProduto = new ComboBox<>();
    private final Label labelPreco = new Label();
    private final Label labelMensagem = new Label();

    public VBox getView() {
        carregarCombo();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Produto:"), 0, 0);
        grid.add(comboProduto, 1, 0);

        grid.add(new Label("Preço de venda:"), 0, 1);
        grid.add(labelPreco, 1, 1);

        comboProduto.setOnAction(e -> {
            ItemEstoque item = comboProduto.getValue();
            labelPreco.setText(item == null ? "" : "R$ " + String.format("%.2f", item.getPrecoVenda()));
        });

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

    private void vender() {
        ItemEstoque item = comboProduto.getValue();

        if (item == null) {
            mostrarErro("Selecione um produto.");
            return;
        }
        if (item.getQuantidade() < 1) {
            mostrarErro("Sem estoque de " + item.getNome() + ".");
            return;
        }

        String descricao = item.getCategoria() + " " + item.getNome();
        double preco = item.getPrecoVenda();

        PagamentoDialog.abrir(descricao, preco, (forma, valorPago, troco) -> {
            estoqueRepository.atualizarQuantidade(item.getId(), item.getQuantidade() - 1);
            vendaRepository.salvar(new VendaRegistro(descricao, preco, forma, valorPago, troco));

            mostrarSucesso(descricao + " vendido(a)! Troco: R$ " + String.format("%.2f", troco));
            carregarCombo();
            labelPreco.setText("");
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