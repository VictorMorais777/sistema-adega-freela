package com.adega.gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class AdegaApp extends Application {

    private final BorderPane root = new BorderPane();

    @Override
    public void start(Stage stage) {
        root.setLeft(criarMenuLateral());
        mostrarEstoque();

        Scene scene = new Scene(root, 950, 650);

        stage.setTitle("Sistema Adega");
        stage.setScene(scene);
        stage.show();
    }

    private VBox criarMenuLateral() {
        Button botaoEstoque = new Button("Estoque");
        Button botaoMontarCopao = new Button("Montar Copão");
        Button botaoVenderGarrafa = new Button("Vender Garrafa");
        Button botaoRelatorio = new Button("Relatório");

        for (Button b : new Button[]{botaoEstoque, botaoMontarCopao, botaoVenderGarrafa, botaoRelatorio}) {
            b.setMaxWidth(Double.MAX_VALUE);
        }

        botaoEstoque.setOnAction(e -> mostrarEstoque());
        botaoMontarCopao.setOnAction(e -> mostrarMontarCopao());
        botaoVenderGarrafa.setOnAction(e -> mostrarVenderGarrafa());
        botaoRelatorio.setOnAction(e -> mostrarRelatorio());

        VBox menu = new VBox(8, botaoEstoque, botaoMontarCopao, botaoVenderGarrafa, botaoRelatorio);
        menu.setPadding(new Insets(15));
        menu.setPrefWidth(170);
        menu.setStyle("-fx-background-color: #f0f0f0;");
        return menu;
    }

    private void mostrarEstoque() {
        root.setCenter(new EstoqueView().getView());
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

    public static void main(String[] args) {
        launch(args);
    }
}