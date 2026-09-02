package com.adega.gui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;

public class FormatadorCampo {

    public static void aplicarFormatoPreco(TextField campo) {
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.matches("\\d*(,\\d*)?")) {
                return change;
            }
            return null;
        });
        campo.setTextFormatter(formatter);
    }

    public static void aplicarFormatoInteiro(TextField campo) {
        TextFormatter<String> formatter = new TextFormatter<>(change -> {
            String novoTexto = change.getControlNewText();
            if (novoTexto.matches("\\d*")) {
                return change;
            }
            return null;
        });
        campo.setTextFormatter(formatter);
    }
}