package com.adega.gui;

import com.adega.model.Papel;
import com.adega.model.Usuario;
import com.adega.repository.UsuarioRepository;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdegaApp extends Application {

    private final BorderPane root = new BorderPane();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();

    @Override
    public void start(Stage stage) {
        mostrarLogin();

        Scene scene = new Scene(root, 950, 650);

        stage.setTitle("Sistema Adega");
        stage.setScene(scene);
        stage.show();
    }

    private void mostrarLogin() {
        root.setLeft(null);
        root.setCenter(LoginView.criar(usuarioRepository, this::aoLogar));
    }

    private void aoLogar(Usuario usuario) {
        boolean isPatrao = usuario.getPapel() == Papel.PATRAO;
        root.setLeft(criarMenuLateral(usuario, isPatrao));
        mostrarEstoque(!isPatrao);
    }

    private VBox criarMenuLateral(Usuario usuario, boolean isPatrao) {
        Label labelUsuario = new Label(usuario.getUsuario() + "\n(" + usuario.getPapel().getDescricao() + ")");
        labelUsuario.setStyle("-fx-font-weight: bold;");
        labelUsuario.setWrapText(true);

        Button botaoEstoque = new Button("Estoque");
        Button botaoMontarCopao = new Button("Montar Copão");
        Button botaoVenderGarrafa = new Button("Vender Garrafa");

        botaoEstoque.setOnAction(e -> mostrarEstoque(!isPatrao));
        botaoMontarCopao.setOnAction(e -> mostrarMontarCopao());
        botaoVenderGarrafa.setOnAction(e -> mostrarVenderGarrafa());

        VBox menu = new VBox(8, labelUsuario, botaoEstoque, botaoMontarCopao, botaoVenderGarrafa);

        if (isPatrao) {
            Button botaoRelatorio = new Button("Relatório");
            Button botaoUsuarios = new Button("Gerenciar Usuários");

            botaoRelatorio.setOnAction(e -> mostrarRelatorio());
            botaoUsuarios.setOnAction(e -> mostrarUsuarios());

            menu.getChildren().addAll(botaoRelatorio, botaoUsuarios);
        }

        Button botaoSair = new Button("Sair");
        botaoSair.setOnAction(e -> mostrarLogin());
        menu.getChildren().add(botaoSair);

        for (var filho : menu.getChildren()) {
            if (filho instanceof Button botao) {
                botao.setMaxWidth(Double.MAX_VALUE);
            }
        }

        menu.setPadding(new Insets(15));
        menu.setPrefWidth(190);
        menu.setStyle("-fx-background-color: #f0f0f0;");
        return menu;
    }

    private void mostrarEstoque(boolean somenteLeitura) {
        root.setCenter(new EstoqueView(somenteLeitura).getView());
    }

    private void mostrarMontarCopao() {
        root.setCenter(new VendaCopaoView().getView());
    }

    private void mostrarVenderGarrafa() {
        root.setCenter(new VendaGarrafaView().getView());
    }

    private void mostrarRelatorio() {
        root.setCenter(new RelatorioView().getView());
    }

    private void mostrarUsuarios() {
        root.setCenter(new GerenciarUsuariosView().getView());
    }

    public static void main(String[] args) {
        launch(args);
    }
}