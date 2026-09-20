package com.adega.gui;

import com.adega.model.ItemEstoque;
import com.adega.model.ItemVendido;
import com.adega.model.VendaRegistro;
import com.adega.model.venda.FormaPagamento;
import com.adega.repository.EstoqueRepository;
import com.adega.repository.VendaRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RelatorioView {

    private final VendaRepository vendaRepository = new VendaRepository();
    private final EstoqueRepository estoqueRepository = new EstoqueRepository();

    private final TableView<VendaRegistro> tabelaVendas = new TableView<>();
    private final ObservableList<VendaRegistro> dadosVendas = FXCollections.observableArrayList();
    private final Label labelFaturamento = new Label();
    private final Label labelMensagem = new Label();

    private final TableView<FaturamentoDia> tabelaFaturamentoDia = new TableView<>();
    private final ObservableList<FaturamentoDia> dadosFaturamentoDia = FXCollections.observableArrayList();

    private final TableView<RankingItem> tabelaRanking = new TableView<>();
    private final ObservableList<RankingItem> dadosRanking = FXCollections.observableArrayList();

    private final TableView<ItemEstoque> tabelaEstoqueBaixo = new TableView<>();
    private final ObservableList<ItemEstoque> dadosEstoqueBaixo = FXCollections.observableArrayList();

    public BorderPane getView() {
        TabPane abas = new TabPane();
        abas.getTabs().add(criarAbaVendas());
        abas.getTabs().add(criarAbaFaturamentoPorDia());
        abas.getTabs().add(criarAbaRanking());
        abas.getTabs().add(criarAbaEstoqueBaixo());
        abas.setTabClosingPolicy(TabPane.TabClosingPolicy.UNAVAILABLE);

        carregarTudo();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(abas);

        Button botaoAtualizar = new Button("Atualizar tudo");
        botaoAtualizar.setOnAction(e -> carregarTudo());

        HBox rodape = new HBox(10, botaoAtualizar);
        rodape.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(rodape);

        return root;
    }

    private Tab criarAbaVendas() {
        TableColumn<VendaRegistro, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colDescricao.setPrefWidth(240);

        TableColumn<VendaRegistro, Double> colBruto = new TableColumn<>("Valor");
        colBruto.setCellValueFactory(new PropertyValueFactory<>("valorBruto"));

        TableColumn<VendaRegistro, Double> colDesconto = new TableColumn<>("Desconto");
        colDesconto.setCellValueFactory(new PropertyValueFactory<>("desconto"));

        TableColumn<VendaRegistro, Double> colTotal = new TableColumn<>("Total");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("valor"));

        TableColumn<VendaRegistro, String> colPagamento = new TableColumn<>("Pagamento");
        colPagamento.setCellValueFactory(new PropertyValueFactory<>("formaPagamentoDescricao"));

        TableColumn<VendaRegistro, Double> colTroco = new TableColumn<>("Troco");
        colTroco.setCellValueFactory(new PropertyValueFactory<>("troco"));

        TableColumn<VendaRegistro, java.time.LocalDateTime> colData = new TableColumn<>("Data/Hora");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colData.setPrefWidth(140);

        TableColumn<VendaRegistro, String> colStatus = new TableColumn<>("Status");
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        tabelaVendas.getColumns().addAll(colDescricao, colBruto, colDesconto, colTotal, colPagamento, colTroco, colData, colStatus);
        tabelaVendas.setItems(dadosVendas);
        tabelaVendas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        labelFaturamento.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Button botaoCancelar = new Button("Cancelar venda selecionada");
        botaoCancelar.setOnAction(e -> cancelarVenda());

        Button botaoAlterar = new Button("Alterar venda selecionada");
        botaoAlterar.setOnAction(e -> alterarVenda());

        HBox acoes = new HBox(10, botaoCancelar, botaoAlterar);

        VBox conteudo = new VBox(10, tabelaVendas, labelFaturamento, acoes, labelMensagem);
        conteudo.setPadding(new Insets(10));

        return new Tab("Vendas", conteudo);
    }

    private void cancelarVenda() {
        VendaRegistro venda = tabelaVendas.getSelectionModel().getSelectedItem();

        if (venda == null) {
            mostrarErro("Selecione uma venda na tabela.");
            return;
        }

        if (venda.isCancelada()) {
            mostrarErro("Esta venda já está cancelada.");
            return;
        }

        String texto = "Cancelar esta venda?\n\n" + venda.getDescricao()
                + String.format("%nTotal: R$ %.2f%n%n", venda.getValor())
                + "Os itens consumidos serão devolvidos ao estoque.";

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION, texto, ButtonType.YES, ButtonType.NO);
        confirmacao.setTitle("Confirmar cancelamento");
        confirmacao.setHeaderText(null);
        confirmacao.getDialogPane().setMinWidth(380);

        Optional<ButtonType> resposta = confirmacao.showAndWait();

        if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
            List<ItemVendido> itens = vendaRepository.listarItensDaVenda(venda.getId());

            for (ItemVendido item : itens) {
                ItemEstoque doEstoque = estoqueRepository.buscarPorId(item.getItemEstoqueId());
                if (doEstoque != null) {
                    estoqueRepository.atualizarQuantidade(doEstoque.getId(),
                            doEstoque.getQuantidade() + item.getQuantidade());
                }
            }

            vendaRepository.marcarComoCancelada(venda.getId());

            String aviso = itens.isEmpty()
                    ? "Venda cancelada. (Venda antiga, sem itens vinculados — o estoque não foi alterado.)"
                    : "Venda cancelada e itens devolvidos ao estoque.";
            mostrarSucesso(aviso);
            carregarTudo();
        }
    }

    private void alterarVenda() {
        VendaRegistro venda = tabelaVendas.getSelectionModel().getSelectedItem();

        if (venda == null) {
            mostrarErro("Selecione uma venda na tabela.");
            return;
        }

        if (venda.isCancelada()) {
            mostrarErro("Não é possível alterar uma venda cancelada.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("Alterar venda");

        Label labelInfo = new Label(venda.getDescricao());
        labelInfo.setWrapText(true);
        labelInfo.setStyle("-fx-font-weight: bold;");

        Label labelAviso = new Label("Os itens consumidos não mudam. Para trocar produtos, cancele a venda e registre outra.");
        labelAviso.setWrapText(true);
        labelAviso.setStyle("-fx-font-size: 11px; -fx-text-fill: #666;");

        TextField campoValorBruto = new TextField(String.format("%.2f", venda.getValorBruto()).replace(".", ","));
        FormatadorCampo.aplicarFormatoPreco(campoValorBruto);

        TextField campoDesconto = new TextField(String.format("%.2f", venda.getDesconto()).replace(".", ","));
        FormatadorCampo.aplicarFormatoPreco(campoDesconto);

        ComboBox<FormaPagamento> comboForma = new ComboBox<>();
        comboForma.getItems().addAll(FormaPagamento.values());
        comboForma.setValue(venda.getFormaPagamento());
        comboForma.setConverter(new javafx.util.StringConverter<>() {
            @Override
            public String toString(FormaPagamento forma) {
                return forma == null ? "" : forma.getDescricao();
            }

            @Override
            public FormaPagamento fromString(String string) {
                return null;
            }
        });

        TextField campoValorPago = new TextField(String.format("%.2f", venda.getValorPago()).replace(".", ","));
        FormatadorCampo.aplicarFormatoPreco(campoValorPago);

        Label labelErro = new Label();
        labelErro.setStyle("-fx-text-fill: #b00020;");
        labelErro.setWrapText(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(8);
        grid.add(new Label("Valor:"), 0, 0);
        grid.add(campoValorBruto, 1, 0);
        grid.add(new Label("Desconto:"), 0, 1);
        grid.add(campoDesconto, 1, 1);
        grid.add(new Label("Pagamento:"), 0, 2);
        grid.add(comboForma, 1, 2);
        grid.add(new Label("Valor recebido:"), 0, 3);
        grid.add(campoValorPago, 1, 3);

        Button botaoSalvar = new Button("Salvar alterações");
        botaoSalvar.setOnAction(e -> {
            double valorBruto = lerValor(campoValorBruto.getText());
            double desconto = lerValor(campoDesconto.getText());
            FormaPagamento forma = comboForma.getValue();
            double valorPago = lerValor(campoValorPago.getText());

            if (forma == null) {
                labelErro.setText("Selecione a forma de pagamento.");
                return;
            }
            if (valorBruto <= 0) {
                labelErro.setText("O valor precisa ser maior que zero.");
                return;
            }
            if (desconto > valorBruto) {
                labelErro.setText("O desconto não pode ser maior que o valor.");
                return;
            }

            double total = valorBruto - desconto;

            if (forma == FormaPagamento.DINHEIRO && valorPago < total) {
                labelErro.setText(String.format("Valor recebido insuficiente. Faltam R$ %.2f.", total - valorPago));
                return;
            }

            Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION,
                    String.format("Salvar alterações desta venda?%n%nNovo total: R$ %.2f", total),
                    ButtonType.YES, ButtonType.NO);
            confirmacao.setTitle("Confirmar alteração");
            confirmacao.setHeaderText(null);

            Optional<ButtonType> resposta = confirmacao.showAndWait();

            if (resposta.isPresent() && resposta.get() == ButtonType.YES) {
                vendaRepository.atualizarValores(venda.getId(), valorBruto, desconto, forma, valorPago);
                mostrarSucesso("Venda alterada com sucesso.");
                carregarTudo();
                dialog.close();
            }
        });

        Button botaoFechar = new Button("Cancelar");
        botaoFechar.setOnAction(e -> dialog.close());

        VBox root = new VBox(12, labelInfo, labelAviso, grid, labelErro, new HBox(10, botaoSalvar, botaoFechar));
        root.setPadding(new Insets(20));

        dialog.setScene(new Scene(root, 400, 340));
        dialog.showAndWait();
    }

    private double lerValor(String texto) {
        if (texto == null || texto.trim().isEmpty()) {
            return 0;
        }
        try {
            return Double.parseDouble(texto.trim().replace(",", "."));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private Tab criarAbaFaturamentoPorDia() {
        TableColumn<FaturamentoDia, String> colDia = new TableColumn<>("Dia");
        colDia.setCellValueFactory(new PropertyValueFactory<>("dia"));

        TableColumn<FaturamentoDia, Double> colTotal = new TableColumn<>("Faturamento");
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));

        tabelaFaturamentoDia.getColumns().addAll(colDia, colTotal);
        tabelaFaturamentoDia.setItems(dadosFaturamentoDia);
        tabelaFaturamentoDia.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox conteudo = new VBox(10, tabelaFaturamentoDia);
        conteudo.setPadding(new Insets(10));

        return new Tab("Faturamento por dia", conteudo);
    }

    private Tab criarAbaRanking() {
        TableColumn<RankingItem, String> colDescricao = new TableColumn<>("Bebida");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colDescricao.setPrefWidth(320);

        TableColumn<RankingItem, Integer> colQuantidade = new TableColumn<>("Vendas");
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        tabelaRanking.getColumns().addAll(colDescricao, colQuantidade);
        tabelaRanking.setItems(dadosRanking);
        tabelaRanking.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        VBox conteudo = new VBox(10, tabelaRanking);
        conteudo.setPadding(new Insets(10));

        return new Tab("Ranking de bebidas", conteudo);
    }

    private Tab criarAbaEstoqueBaixo() {
        TableColumn<ItemEstoque, String> colCategoria = new TableColumn<>("Categoria");
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        TableColumn<ItemEstoque, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<ItemEstoque, Integer> colQuantidade = new TableColumn<>("Quantidade");
        colQuantidade.setCellValueFactory(new PropertyValueFactory<>("quantidade"));

        TableColumn<ItemEstoque, Integer> colMinimo = new TableColumn<>("Estoque Mínimo");
        colMinimo.setCellValueFactory(new PropertyValueFactory<>("estoqueMinimo"));

        tabelaEstoqueBaixo.getColumns().addAll(colCategoria, colNome, colQuantidade, colMinimo);
        tabelaEstoqueBaixo.setItems(dadosEstoqueBaixo);
        tabelaEstoqueBaixo.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        Label labelAviso = new Label("⚠ Itens com quantidade igual ou abaixo do estoque mínimo cadastrado.");
        labelAviso.setStyle("-fx-text-fill: #b00020;");

        VBox conteudo = new VBox(10, labelAviso, tabelaEstoqueBaixo);
        conteudo.setPadding(new Insets(10));

        return new Tab("Estoque baixo", conteudo);
    }

    private void carregarTudo() {
        dadosVendas.setAll(vendaRepository.listarTodas());

        double faturamento = vendaRepository.calcularFaturamentoTotal();
        double descontos = vendaRepository.calcularTotalDescontos();
        labelFaturamento.setText(String.format("Faturamento total: R$ %.2f  |  Descontos concedidos: R$ %.2f",
                faturamento, descontos));

        dadosFaturamentoDia.clear();
        for (Map.Entry<String, Double> entry : vendaRepository.faturamentoPorDia().entrySet()) {
            dadosFaturamentoDia.add(new FaturamentoDia(entry.getKey(), entry.getValue()));
        }

        dadosRanking.clear();
        for (Map.Entry<String, Integer> entry : vendaRepository.rankingBebidas().entrySet()) {
            dadosRanking.add(new RankingItem(entry.getKey(), entry.getValue()));
        }

        dadosEstoqueBaixo.setAll(estoqueRepository.listarComEstoqueBaixo());
    }

    private void mostrarErro(String texto) {
        labelMensagem.setStyle("-fx-text-fill: #b00020;");
        labelMensagem.setText(texto);
    }

    private void mostrarSucesso(String texto) {
        labelMensagem.setStyle("-fx-text-fill: #1b5e20;");
        labelMensagem.setText(texto);
    }

    public static class FaturamentoDia {
        private final String dia;
        private final double total;

        public FaturamentoDia(String dia, double total) {
            this.dia = dia;
            this.total = total;
        }

        public String getDia() {
            return dia;
        }

        public double getTotal() {
            return total;
        }
    }

    public static class RankingItem {
        private final String descricao;
        private final int quantidade;

        public RankingItem(String descricao, int quantidade) {
            this.descricao = descricao;
            this.quantidade = quantidade;
        }

        public String getDescricao() {
            return descricao;
        }

        public int getQuantidade() {
            return quantidade;
        }
    }
}