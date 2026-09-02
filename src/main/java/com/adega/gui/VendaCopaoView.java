package com.adega.gui;

import com.adega.model.ItemEstoque;
import com.adega.model.VendaRegistro;
import com.adega.repository.EstoqueRepository;
import com.adega.repository.VendaRepository;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.text.Normalizer;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class VendaCopaoView {

    private static final Set<String> PALAVRAS_DESTILADO = Set.of(
            "gin", "whisky", "whiskey", "vodka", "rum", "tequila", "destilado", "conhaque", "cachaca"
    );

    private final EstoqueRepository estoqueRepository = new EstoqueRepository();
    private final VendaRepository vendaRepository = new VendaRepository();

    private final TextField campoNomeCopao = new TextField();
    private final ComboBox<ItemEstoque> comboDestilado = new ComboBox<>();
    private final TextField campoMlDestilado = new TextField();
    private final ComboBox<ItemEstoque> comboEnergetico = new ComboBox<>();
    private final ComboBox<ItemEstoque> comboGelo = new ComboBox<>();
    private final Spinner<Integer> spinnerQuantidadeGelo = new Spinner<>(1, 10, 1);
    private final Label labelCustoReferencia = new Label();
    private final TextField campoPrecoVenda = new TextField();
    private final Label labelMensagem = new Label();

    public VBox getView() {
        FormatadorCampo.aplicarFormatoInteiro(campoMlDestilado);
        FormatadorCampo.aplicarFormatoPreco(campoPrecoVenda);
        carregarCombos();

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        grid.add(new Label("Nome do copão:"), 0, 0);
        grid.add(campoNomeCopao, 1, 0);

        grid.add(new Label("Destilado (Gin/Whisky/Vodka...):"), 0, 1);
        grid.add(comboDestilado, 1, 1);

        grid.add(new Label("Ml de destilado usado:"), 0, 2);
        grid.add(campoMlDestilado, 1, 2);

        grid.add(new Label("Energético:"), 0, 3);
        grid.add(comboEnergetico, 1, 3);

        grid.add(new Label("Gelo:"), 0, 4);
        grid.add(comboGelo, 1, 4);

        grid.add(new Label("Quantidade de gelo:"), 0, 5);
        grid.add(spinnerQuantidadeGelo, 1, 5);

        grid.add(new Label("Custo de referência:"), 0, 6);
        grid.add(labelCustoReferencia, 1, 6);

        grid.add(new Label("Preço de venda:"), 0, 7);
        grid.add(campoPrecoVenda, 1, 7);

        comboDestilado.setOnAction(e -> atualizarCustoReferencia());
        comboEnergetico.setOnAction(e -> atualizarCustoReferencia());
        comboGelo.setOnAction(e -> atualizarCustoReferencia());
        campoMlDestilado.textProperty().addListener((obs, oldV, newV) -> atualizarCustoReferencia());
        spinnerQuantidadeGelo.valueProperty().addListener((obs, oldV, newV) -> atualizarCustoReferencia());

        Button botaoMontar = new Button("Montar e Vender Copão");
        botaoMontar.setOnAction(e -> montarEVender());

        VBox root = new VBox(15, grid, botaoMontar, labelMensagem);
        root.setPadding(new Insets(10));
        return root;
    }

    private void carregarCombos() {
        List<ItemEstoque> todos = estoqueRepository.listarTodos();

        comboDestilado.getItems().setAll(filtrarDestilados(todos));
        comboEnergetico.getItems().setAll(filtrarPorCategoria(todos, "energetico"));
        comboGelo.getItems().setAll(filtrarPorCategoria(todos, "gelo"));

        javafx.util.StringConverter<ItemEstoque> conversorDestilado = new javafx.util.StringConverter<>() {
            @Override
            public String toString(ItemEstoque item) {
                return item == null ? "" : "[" + item.getCategoria() + "] " + item.getNome()
                        + " (R$ " + String.format("%.4f", item.getPrecoVenda()) + "/ml | " + item.getQuantidade() + "ml em estoque)";
            }

            @Override
            public ItemEstoque fromString(String string) {
                return null;
            }
        };
        comboDestilado.setConverter(conversorDestilado);

        javafx.util.StringConverter<ItemEstoque> conversorPadrao = new javafx.util.StringConverter<>() {
            @Override
            public String toString(ItemEstoque item) {
                return item == null ? "" : item.getNome() + " (R$ " + String.format("%.2f", item.getPrecoVenda()) + " | Qtd: " + item.getQuantidade() + ")";
            }

            @Override
            public ItemEstoque fromString(String string) {
                return null;
            }
        };
        comboEnergetico.setConverter(conversorPadrao);
        comboGelo.setConverter(conversorPadrao);
    }

    private List<ItemEstoque> filtrarDestilados(List<ItemEstoque> itens) {
        return itens.stream()
                .filter(i -> PALAVRAS_DESTILADO.stream().anyMatch(palavra -> normalizar(i.getCategoria()).contains(palavra)))
                .collect(Collectors.toList());
    }

    private List<ItemEstoque> filtrarPorCategoria(List<ItemEstoque> itens, String categoriaAlvo) {
        return itens.stream()
                .filter(i -> normalizar(i.getCategoria()).contains(categoriaAlvo))
                .collect(Collectors.toList());
    }

    private String normalizar(String texto) {
        String semAcento = Normalizer.normalize(texto, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "");
        return semAcento.toLowerCase();
    }

    private void atualizarCustoReferencia() {
        double custo = 0;

        ItemEstoque destilado = comboDestilado.getValue();
        if (destilado != null) {
            int ml = lerMlDigitado();
            custo += destilado.getPrecoVenda() * ml;
        }

        if (comboEnergetico.getValue() != null) custo += comboEnergetico.getValue().getPrecoVenda();
        if (comboGelo.getValue() != null) custo += comboGelo.getValue().getPrecoVenda() * spinnerQuantidadeGelo.getValue();

        labelCustoReferencia.setText("R$ " + String.format("%.2f", custo));
    }

    private int lerMlDigitado() {
        try {
            return Integer.parseInt(campoMlDestilado.getText().trim());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private void montarEVender() {
        String nome = campoNomeCopao.getText().trim();
        ItemEstoque destilado = comboDestilado.getValue();
        ItemEstoque energetico = comboEnergetico.getValue();
        ItemEstoque gelo = comboGelo.getValue();
        int qtdGelo = spinnerQuantidadeGelo.getValue();

        if (nome.isBlank()) {
            mostrarErro("Digite o nome do copão.");
            return;
        }
        if (destilado == null || energetico == null || gelo == null) {
            mostrarErro("Selecione o destilado, energético e gelo.");
            return;
        }

        int mlDestilado;
        try {
            mlDestilado = Integer.parseInt(campoMlDestilado.getText().trim());
        } catch (NumberFormatException e) {
            mostrarErro("Digite a quantidade de ml do destilado.");
            return;
        }

        if (mlDestilado <= 0) {
            mostrarErro("A quantidade de ml precisa ser maior que zero.");
            return;
        }
        if (destilado.getQuantidade() < mlDestilado) {
            mostrarErro("Estoque insuficiente de " + destilado.getNome() + " (disponível: " + destilado.getQuantidade() + "ml).");
            return;
        }
        if (energetico.getQuantidade() < 1) {
            mostrarErro("Sem estoque de " + energetico.getNome() + ".");
            return;
        }
        if (gelo.getQuantidade() < qtdGelo) {
            mostrarErro("Estoque insuficiente de " + gelo.getNome() + ".");
            return;
        }

        double precoVenda;
        try {
            precoVenda = Double.parseDouble(campoPrecoVenda.getText().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            mostrarErro("Digite um preço de venda válido.");
            return;
        }

        String descricao = nome + " (" + mlDestilado + "ml de " + destilado.getCategoria() + " " + destilado.getNome()
                + ", " + energetico.getNome() + ", " + qtdGelo + "x " + gelo.getNome() + ")";

        PagamentoDialog.abrir(descricao, precoVenda, (forma, valorPago, troco) -> {
            estoqueRepository.atualizarQuantidade(destilado.getId(), destilado.getQuantidade() - mlDestilado);
            estoqueRepository.atualizarQuantidade(energetico.getId(), energetico.getQuantidade() - 1);
            estoqueRepository.atualizarQuantidade(gelo.getId(), gelo.getQuantidade() - qtdGelo);

            vendaRepository.salvar(new VendaRegistro(descricao, precoVenda, forma, valorPago, troco));

            mostrarSucesso("Copão \"" + nome + "\" vendido! Troco: R$ " + String.format("%.2f", troco));
            limparCampos();
            carregarCombos();
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

    private void limparCampos() {
        campoNomeCopao.clear();
        campoMlDestilado.clear();
        campoPrecoVenda.clear();
        spinnerQuantidadeGelo.getValueFactory().setValue(1);
        labelCustoReferencia.setText("");
    }
}