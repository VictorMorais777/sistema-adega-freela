package com.adega.gui;

import com.adega.model.ItemEstoque;
import com.adega.repository.EstoqueRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class EstoqueView {

    private final EstoqueRepository repository = new EstoqueRepository();
    private final TableView<ItemEstoque> tabela = new TableView<>();
    private final ObservableList<ItemEstoque> dados = FXCollections.observableArrayList();

    private final TextField campoCategoria = new TextField();
    private final TextField campoNome = new TextField();
    private final TextField campoQuantidade = new TextField();
    private final TextField campoPrecoCompra = new TextField();
    private final TextField campoPrecoVenda = new TextField();
    private final TextField campoEstoqueMinimo = new TextField();
    private final Label labelMensagem = new Label();
    private final Label labelModo = new Label("Cadastrando novo item");

    private final CheckBox checkDestilado = new CheckBox("É um destilado (Gin, Whisky, Vodka...) controlado em ml?");
    private final Label labelQuantidade = new Label("Quantidade:");
    private final Label labelPrecoCompra = new Label("Preço compra:");
    private final Label labelPrecoVenda = new Label("Preço venda:");

    private final Button botaoSalvar = new Button("Cadastrar item");
    private final Button botaoRemover = new Button("Remover selecionado");
    private final Button botaoCancelar = new Button("Cancelar edição");

    private ItemEstoque itemSelecionado;

    public EstoqueView() {
        FormatadorCampo.aplicarFormatoInteiro(campoQuantidade);
        FormatadorCampo.aplicarFormatoInteiro(campoEstoqueMinimo);
        FormatadorCampo.aplicarFormatoPreco(campoPrecoCompra);
        FormatadorCampo.aplicarFormatoPreco(campoPrecoVenda);
        configurarTabela();
        carregarDados();
    }

    public BorderPane getView() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(tabela);
        root.setBottom(criarFormulario());
        return root;
    }

    private void configurarTabela() {
        TableColumn<ItemEstoque, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<ItemEstoque, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<ItemEstoque, Integer> colQuantidade = new TableColumn<>("Quantidade");
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<ItemEstoque, Double> colPrecoCompra = new TableColumn<>("Preço Compra");
        colPrecoCompra.setCellValueFactory(new PropertyValueFactory<>("precoCompra"));

        TableColumn<ItemEstoque, Double> colPrecoVenda = new TableColumn<>("Preço Venda");
        colPrecoVenda.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));

        TableColumn<ItemEstoque, Integer> colEstoqueMinimo = new TableColumn<>("Estoque Mínimo");
        colEstoqueMinimo.setCellValueFactory(new PropertyValueFactory<>("estoqueMinimo"));

        tabela.getColumns().addAll(colCategoria, colNome, colQuantidade, colPrecoCompra, colPrecoVenda, colEstoqueMinimo);
        tabela.setItems(dados);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        tabela.getSelectionModel().selectedItemProperty().addListener((obs, antigo, novo) -> {
            if (novo != null) {
                entrarModoEdicao(novo);
            }
        });
    }

    private void carregarDados() {
        dados.setAll(repository.listarTodos());
    }

    private VBox criarFormulario() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.setPadding(new Insets(15, 0, 0, 0));

        grid.add(new Label("Categoria:"), 0, 0);
        grid.add(campoCategoria, 1, 0);
        grid.add(new Label("Nome:"), 2, 0);
        grid.add(campoNome, 3, 0);

        grid.add(labelQuantidade, 0, 1);
        grid.add(campoQuantidade, 1, 1);
        grid.add(new Label("Estoque mínimo:"), 2, 1);
        grid.add(campoEstoqueMinimo, 3, 1);

        grid.add(labelPrecoCompra, 0, 2);
        grid.add(campoPrecoCompra, 1, 2);
        grid.add(labelPrecoVenda, 2, 2);
        grid.add(campoPrecoVenda, 3, 2);

        checkDestilado.setOnAction(e -> atualizarRotulosDestilado());

        botaoSalvar.setOnAction(e -> salvarItem());
        botaoRemover.setOnAction(e -> removerItem());
        botaoCancelar.setOnAction(e -> sairDoModoEdicao());

        botaoRemover.setDisable(true);
        botaoCancelar.setDisable(true);

        labelModo.setStyle("-fx-font-weight: bold;");

        HBox botoes = new HBox(10, botaoSalvar, botaoRemover, botaoCancelar);

        VBox container = new VBox(10, labelModo, checkDestilado, grid, botoes, labelMensagem);
        return container;
    }

    private void atualizarRotulosDestilado() {
        if (checkDestilado.isSelected()) {
            labelQuantidade.setText("Volume da garrafa (ml):");
            labelPrecoCompra.setText("Preço compra (garrafa):");
            labelPrecoVenda.setText("Preço venda (garrafa):");
        } else {
            labelQuantidade.setText("Quantidade:");
            labelPrecoCompra.setText("Preço compra:");
            labelPrecoVenda.setText("Preço venda:");
        }
    }

    private void entrarModoEdicao(ItemEstoque item) {
        itemSelecionado = item;

        checkDestilado.setSelected(false);
        atualizarRotulosDestilado();

        campoCategoria.setText(item.getCategoria());
        campoNome.setText(item.getNome());
        campoQuantidade.setText(String.valueOf(item.getQuantidade()));
        campoPrecoCompra.setText(String.valueOf(item.getPrecoCompra()));
        campoPrecoVenda.setText(String.valueOf(item.getPrecoVenda()));
        campoEstoqueMinimo.setText(String.valueOf(item.getEstoqueMinimo()));

        labelModo.setText("Editando: " + item.getNome());
        botaoSalvar.setText("Salvar alterações");
        botaoRemover.setDisable(false);
        botaoCancelar.setDisable(false);
        labelMensagem.setText("");
    }

    private void sairDoModoEdicao() {
        itemSelecionado = null;
        tabela.getSelectionModel().clearSelection();

        labelModo.setText("Cadastrando novo item");
        botaoSalvar.setText("Cadastrar item");
        botaoRemover.setDisable(true);
        botaoCancelar.setDisable(true);

        checkDestilado.setSelected(false);
        atualizarRotulosDestilado();

        limparCampos();
    }

    private void salvarItem() {
        try {
            String categoria = campoCategoria.getText().trim();
            String nome = campoNome.getText().trim();

            if (categoria.isBlank() || nome.isBlank()) {
                mostrarErro("Categoria e nome são obrigatórios.");
                return;
            }

            int quantidade;
            double precoCompra;
            double precoVenda;
            int estoqueMinimo = Integer.parseInt(campoEstoqueMinimo.getText().trim());

            if (checkDestilado.isSelected() && itemSelecionado == null) {
                double precoCompraGarrafa = Double.parseDouble(campoPrecoCompra.getText().trim().replace(",", "."));
                double precoVendaGarrafa = Double.parseDouble(campoPrecoVenda.getText().trim().replace(",", "."));
                int volumeGarrafaMl = Integer.parseInt(campoQuantidade.getText().trim());

                if (volumeGarrafaMl <= 0) {
                    mostrarErro("O volume da garrafa precisa ser maior que zero.");
                    return;
                }

                precoCompra = precoCompraGarrafa / volumeGarrafaMl;
                precoVenda = precoVendaGarrafa / volumeGarrafaMl;
                quantidade = volumeGarrafaMl;
            } else {
                quantidade = Integer.parseInt(campoQuantidade.getText().trim());
                precoCompra = Double.parseDouble(campoPrecoCompra.getText().trim().replace(",", "."));
                precoVenda = Double.parseDouble(campoPrecoVenda.getText().trim().replace(",", "."));
            }

            if (itemSelecionado == null) {
                ItemEstoque novo = new ItemEstoque(categoria, nome, quantidade, precoCompra, precoVenda, estoqueMinimo);
                repository.salvar(novo);
                String extra = checkDestilado.isSelected() ? String.format(" (R$ %.4f/ml)", precoVenda) : "";
                mostrarSucesso("Item \"" + nome + "\" cadastrado com sucesso!" + extra);
                limparCampos();
            } else {
                itemSelecionado.setCategoria(categoria);
                itemSelecionado.setNome(nome);
                itemSelecionado.setQuantidade(quantidade);
                itemSelecionado.setPrecoCompra(precoCompra);
                itemSelecionado.setPrecoVenda(precoVenda);
                itemSelecionado.setEstoqueMinimo(estoqueMinimo);
                repository.atualizar(itemSelecionado);
                mostrarSucesso("Item \"" + nome + "\" atualizado com sucesso!");
                sairDoModoEdicao();
            }

            carregarDados();

        } catch (NumberFormatException e) {
            mostrarErro("Quantidade, preços e estoque mínimo precisam ser números válidos.");
        }
    }

    private void removerItem() {
        if (itemSelecionado == null) {
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Remover \"" + itemSelecionado.getNome() + "\" do estoque? Essa ação não pode ser desfeita.",
                ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar remoção");
        confirmacao.setHeaderText(null);

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            String nomeRemovido = itemSelecionado.getNome();
            repository.remover(itemSelecionado.getId());
            mostrarSucesso("Item \"" + nomeRemovido + "\" removido.");
            sairDoModoEdicao();
            carregarDados();
        }
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
        campoCategoria.clear();
        campoNome.clear();
        campoQuantidade.clear();
        campoPrecoCompra.clear();
        campoPrecoVenda.clear();
        campoEstoqueMinimo.clear();
    }
}