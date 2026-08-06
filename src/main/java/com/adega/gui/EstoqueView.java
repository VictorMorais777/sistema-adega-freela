package com.adega.gui;

import com.adega.model.ItemEstoque;
import com.adega.repository.EstoqueRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

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

    public EstoqueView() {
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

        grid.add(new Label("Quantidade:"), 0, 1);
        grid.add(campoQuantidade, 1, 1);
        grid.add(new Label("Estoque mínimo:"), 2, 1);
        grid.add(campoEstoqueMinimo, 3, 1);

        grid.add(new Label("Preço compra:"), 0, 2);
        grid.add(campoPrecoCompra, 1, 2);
        grid.add(new Label("Preço venda:"), 2, 2);
        grid.add(campoPrecoVenda, 3, 2);

        Button botaoCadastrar = new Button("Cadastrar item");
        botaoCadastrar.setOnAction(e -> cadastrarItem());

        VBox container = new VBox(10, grid, botaoCadastrar, labelMensagem);
        return container;
    }

    private void cadastrarItem() {
        try {
            String categoria = campoCategoria.getText().trim();
            String nome = campoNome.getText().trim();
            int quantidade = Integer.parseInt(campoQuantidade.getText().trim());
            double precoCompra = Double.parseDouble(campoPrecoCompra.getText().trim().replace(",", "."));
            double precoVenda = Double.parseDouble(campoPrecoVenda.getText().trim().replace(",", "."));
            int estoqueMinimo = Integer.parseInt(campoEstoqueMinimo.getText().trim());

            if (categoria.isBlank() || nome.isBlank()) {
                mostrarErro("Categoria e nome são obrigatórios.");
                return;
            }

            ItemEstoque item = new ItemEstoque(categoria, nome, quantidade, precoCompra, precoVenda, estoqueMinimo);
            repository.salvar(item);

            mostrarSucesso("Item \"" + nome + "\" cadastrado com sucesso!");
            limparCampos();
            carregarDados();

        } catch (NumberFormatException e) {
            mostrarErro("Quantidade, preços e estoque mínimo precisam ser números válidos.");
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