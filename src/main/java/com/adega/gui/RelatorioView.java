package com.adega.gui;

import com.adega.model.VendaRegistro;
import com.adega.repository.VendaRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class RelatorioView {

    private final VendaRepository vendaRepository = new VendaRepository();
    private final TableView<VendaRegistro> tabela = new TableView<>();
    private final ObservableList<VendaRegistro> dados = FXCollections.observableArrayList();
    private final Label labelFaturamento = new Label();

    public BorderPane getView() {
        configurarTabela();
        carregarDados();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(10));
        root.setCenter(tabela);

        Button botaoAtualizar = new Button("Atualizar");
        botaoAtualizar.setOnAction(e -> carregarDados());

        labelFaturamento.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        HBox rodape = new HBox(20, labelFaturamento, botaoAtualizar);
        rodape.setPadding(new Insets(10, 0, 0, 0));
        root.setBottom(rodape);

        return root;
    }

    private void configurarTabela() {
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

        tabela.getColumns().addAll(colDescricao, colValor, colPagamento, colTroco, colData);
        tabela.setItems(dados);
        tabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void carregarDados() {
        dados.setAll(vendaRepository.listarTodas());
        labelFaturamento.setText("Faturamento total: R$ " + String.format("%.2f", vendaRepository.calcularFaturamentoTotal()));
    }
}