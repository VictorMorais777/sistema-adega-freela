package com.adega.gui;

import com.adega.model.Usuario;
import com.adega.repository.UsuarioRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class LoginView {

    public static VBox criar(UsuarioRepository usuarioRepository, Consumer<Usuario> aoLogar) {
        Label titulo = new Label("Sistema Adega - Login");
        titulo.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");

        TextField campoUsuario = new TextField();
        campoUsuario.setPromptText("Usuário");
        campoUsuario.setMaxWidth(220);

        PasswordField campoSenha = new PasswordField();
        campoSenha.setPromptText("Senha");
        campoSenha.setMaxWidth(220);

        Label labelMensagem = new Label();
        labelMensagem.setStyle("-fx-text-fill: #b00020;");

        Label labelDica = new Label("Primeiro acesso? usuário: admin | senha: admin");
        labelDica.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        Button botaoEntrar = new Button("Entrar");
        botaoEntrar.setOnAction(e -> {
            Usuario usuario = usuarioRepository.autenticar(campoUsuario.getText().trim(), campoSenha.getText());
            if (usuario == null) {
                labelMensagem.setText("Usuário ou senha inválidos.");
            } else {
                aoLogar.accept(usuario);
            }
        });

        campoSenha.setOnAction(e -> botaoEntrar.fire());

        VBox root = new VBox(12, titulo, campoUsuario, campoSenha, botaoEntrar, labelMensagem, labelDica);
        root.setAlignment(Pos.CENTER);
        root.setPadding(new Insets(40));
        return root;
    }
}