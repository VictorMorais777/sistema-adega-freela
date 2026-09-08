package com.adega.gui;

import com.adega.model.Papel;
import com.adega.model.Usuario;
import com.adega.repository.UsuarioRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Optional;

public class GerenciarUsuariosView {

    private final UsuarioRepository repository = new UsuarioRepository();
    private final TableView<Usuario> tabela = new TableView<>();
    private final ObservableList<Usuario> dados = FXCollections.observableArrayList();

    private final TextField campoUsuario = new TextField();
    private final PasswordField campoSenha = new PasswordField();
    private final ComboBox<Papel> comboPapel = new ComboBox<>();
    private final Label labelMensagem = new Label();

    public BorderPane getView() {
        configurarTabela();
        carregarDados();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(tabela);
        root.setBottom(criarFormulario());
        return root;
    }

    private void configurarTabela() {
        TableColumn<Usuario, String> colUsuario = new TableColumn<>("Usuário");
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));

        TableColumn<Usuario, Papel> colPapel = new TableColumn<>("Papel");
        colPapel.setCellValueFactory(new PropertyValueFactory<>("papel"));

        tabela.getColumns().addAll(colUsuario, colPapel);
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

        comboPapel.getItems().addAll(Papel.values());
        comboPapel.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(Papel papel) {
                return papel == null ? "" : papel.getDescricao();
            }

            @Override
            public Papel fromString(String string) {
                return null;
            }
        });

        grid.add(new Label("Usuário:"), 0, 0);
        grid.add(campoUsuario, 1, 0);
        grid.add(new Label("Senha:"), 2, 0);
        grid.add(campoSenha, 3, 0);
        grid.add(new Label("Papel:"), 0, 1);
        grid.add(comboPapel, 1, 1);

        Button botaoCriar = new Button("Criar usuário");
        botaoCriar.setOnAction(e -> criarUsuario());

        Button botaoRemover = new Button("Remover selecionado");
        botaoRemover.setOnAction(e -> removerUsuario());

        HBox botoes = new HBox(10, botaoCriar, botaoRemover);

        VBox container = new VBox(10, grid, botoes, labelMensagem);
        return container;
    }

    private void criarUsuario() {
        String usuario = campoUsuario.getText().trim();
        String senha = campoSenha.getText();
        Papel papel = comboPapel.getValue();

        if (usuario.isBlank() || senha.isBlank() || papel == null) {
            mostrarErro("Preencha usuário, senha e papel.");
            return;
        }

        try {
            repository.salvar(usuario, senha, papel);
            mostrarSucesso("Usuário \"" + usuario + "\" criado com sucesso!");
            campoUsuario.clear();
            campoSenha.clear();
            comboPapel.setValue(null);
            carregarDados();
        } catch (RuntimeException e) {
            mostrarErro("Não foi possível criar (usuário já existe?).");
        }
    }

    private void removerUsuario() {
        Usuario selecionado = tabela.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarErro("Selecione um usuário na tabela.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                "Remover o usuário \"" + selecionado.getUsuario() + "\"?", ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar remoção");
        confirmacao.setHeaderText(null);

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            repository.remover(selecionado.getId());
            mostrarSucesso("Usuário removido.");
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
}