package com.adega.gui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class AdegaApp extends Application {

    @Override
    public void start(Stage stage) {
        EstoqueView estoqueView = new EstoqueView();
        Scene scene = new Scene(estoqueView.getView(), 800, 600);

        stage.setTitle("Sistema Adega - Estoque");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}