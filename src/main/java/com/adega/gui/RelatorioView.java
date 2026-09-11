package com.adega.gui;

import com.adega.model.ItemEstoque;
import com.adega.model.VendaRegistro;
import com.adega.repository.EstoqueRepository;
import com.adega.repository.VendaRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Map;

public class RelatorioView {

    private final VendaRepository vendaRepository = new VendaRepository();
    private final EstoqueRepository estoqueRepository = new EstoqueRepository();

    // --- aba Vendas ---
    private final TableView<VendaRegistro> tabelaVendas = new TableView<>();
    private final ObservableList<VendaRegistro> dadosVendas = FXCollections.observableArrayList();
    private final Label labelFaturamento = new Label();

    // --- aba Faturamento por dia ---
    private final TableView<FaturamentoDia> tabelaFaturamentoDia = new TableView<>();
    private final ObservableList<FaturamentoDia> dadosFaturamentoDia = FXCollections.observableArrayList();

    // --- aba Ranking ---
    private final TableView<RankingItem> tabelaRanking = new TableView<>();
    private final ObservableList<RankingItem> dadosRanking = FXCollections.observableArrayList();

    // --- aba Estoque baixo ---
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

    // ===================== ABA: VENDAS =====================

    private Tab criarAbaVendas() {
        TableColumn<VendaRegistro, String> colDescricao = new TableColumn<>("Descrição");
        colDescricao.setCellValueFactory(new PropertyValueFactory<>("descricao"));
        colDescricao.setPrefWidth(280);

        TableColumn<VendaRegistro, Double> colValor = new TableColumn<>("Valor");
        colValor.setCellValueFactory(new PropertyValueFactory<>("valor"));

        TableColumn<VendaRegistro, String> colPagamento = new TableColumn<>("Pagamento");
        colPagamento.setCellValueFactory(new PropertyValueFactory<>("formaPagamentoDescricao"));

        TableColumn<VendaRegistro, Double> colTroco = new TableColumn<>("Troco");
        colTroco.setCellValueFactory(new PropertyValueFactory<>("troco"));

        TableColumn<VendaRegistro, java.time.LocalDateTime> colData = new TableColumn<>("Data/Hora");
        colData.setCellValueFactory(new PropertyValueFactory<>("dataHora"));
        colData.setPrefWidth(150);

        tabelaVendas.getColumns().addAll(colDescricao, colValor, colPagamento, colTroco, colData);
        tabelaVendas.setItems(dadosVendas);
        tabelaVendas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        labelFaturamento.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        VBox conteudo = new VBox(10, tabelaVendas, labelFaturamento);
        conteudo.setPadding(new Insets(10));

        Tab aba = new Tab("Vendas", conteudo);
        return aba;
    }

    // ===================== ABA: FATURAMENTO POR DIA =====================

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

    // ===================== ABA: RANKING =====================

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

    // ===================== ABA: ESTOQUE BAIXO =====================

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

    // ===================== CARREGAMENTO =====================

    private void carregarTudo() {
        dadosVendas.setAll(vendaRepository.listarTodas());
        labelFaturamento.setText("Faturamento total: R$ " + String.format("%.2f", vendaRepository.calcularFaturamentoTotal()));

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

    // ===================== CLASSES AUXILIARES PARA AS TABELAS =====================

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