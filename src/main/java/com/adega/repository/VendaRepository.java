package com.adega.repository;

import com.adega.model.VendaRegistro;
import com.adega.model.venda.FormaPagamento;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VendaRepository {

    private static final String URL = "jdbc:sqlite:adega.db";
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VendaRepository() {
        criarTabelaSeNaoExistir();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelaSeNaoExistir() {
        String sql = """
                CREATE TABLE IF NOT EXISTS vendas (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    descricao TEXT NOT NULL,
                    valor REAL NOT NULL,
                    forma_pagamento TEXT NOT NULL,
                    valor_pago REAL NOT NULL,
                    troco REAL NOT NULL,
                    data_hora TEXT NOT NULL
                )
                """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de vendas: " + e.getMessage(), e);
        }
    }

    public void salvar(VendaRegistro venda) {
        String sql = "INSERT INTO vendas (descricao, valor, forma_pagamento, valor_pago, troco, data_hora) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, venda.getDescricao());
            stmt.setDouble(2, venda.getValor());
            stmt.setString(3, venda.getFormaPagamento().name());
            stmt.setDouble(4, venda.getValorPago());
            stmt.setDouble(5, venda.getTroco());
            stmt.setString(6, venda.getDataHora().format(FORMATO_DATA));

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar venda: " + e.getMessage(), e);
        }
    }

    public List<VendaRegistro> listarTodas() {
        String sql = "SELECT * FROM vendas ORDER BY data_hora DESC";
        List<VendaRegistro> vendas = new ArrayList<>();

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vendas.add(new VendaRegistro(
                        rs.getInt("id"),
                        rs.getString("descricao"),
                        rs.getDouble("valor"),
                        FormaPagamento.valueOf(rs.getString("forma_pagamento")),
                        rs.getDouble("valor_pago"),
                        rs.getDouble("troco"),
                        LocalDateTime.parse(rs.getString("data_hora"), FORMATO_DATA)
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas: " + e.getMessage(), e);
        }

        return vendas;
    }

    public double calcularFaturamentoTotal() {
        String sql = "SELECT SUM(valor) AS total FROM vendas";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular faturamento: " + e.getMessage(), e);
        }

        return 0;
    }
}