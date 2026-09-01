package com.adega.repository;

import com.adega.model.ItemEstoque;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class EstoqueRepository {

    private static final String URL = "jdbc:sqlite:adega.db";

    public EstoqueRepository() {
        criarTabelaSeNaoExistir();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelaSeNaoExistir() {
        String sql = """
                CREATE TABLE IF NOT EXISTS estoque (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    categoria TEXT NOT NULL,
                    nome TEXT NOT NULL,
                    quantidade INTEGER NOT NULL,
                    preco_compra REAL NOT NULL,
                    preco_venda REAL NOT NULL,
                    estoque_minimo INTEGER NOT NULL
                )
                """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de estoque: " + e.getMessage(), e);
        }
    }

    public void salvar(ItemEstoque item) {
        String sql = "INSERT INTO estoque (categoria, nome, quantidade, preco_compra, preco_venda, estoque_minimo) VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getCategoria());
            stmt.setString(2, item.getNome());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoCompra());
            stmt.setDouble(5, item.getPrecoVenda());
            stmt.setInt(6, item.getEstoqueMinimo());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar item no estoque: " + e.getMessage(), e);
        }
    }

    public void atualizarQuantidade(int id, int novaQuantidade) {
        String sql = "UPDATE estoque SET quantidade = ? WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, novaQuantidade);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar quantidade: " + e.getMessage(), e);
        }
    }

    public void atualizar(ItemEstoque item) {
        String sql = "UPDATE estoque SET categoria = ?, nome = ?, quantidade = ?, preco_compra = ?, preco_venda = ?, estoque_minimo = ? WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, item.getCategoria());
            stmt.setString(2, item.getNome());
            stmt.setInt(3, item.getQuantidade());
            stmt.setDouble(4, item.getPrecoCompra());
            stmt.setDouble(5, item.getPrecoVenda());
            stmt.setInt(6, item.getEstoqueMinimo());
            stmt.setInt(7, item.getId());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar item: " + e.getMessage(), e);
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM estoque WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover item: " + e.getMessage(), e);
        }
    }

    public List<ItemEstoque> listarTodos() {
        String sql = "SELECT * FROM estoque ORDER BY categoria, nome";
        List<ItemEstoque> itens = new ArrayList<>();

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                itens.add(new ItemEstoque(
                        rs.getInt("id"),
                        rs.getString("categoria"),
                        rs.getString("nome"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco_compra"),
                        rs.getDouble("preco_venda"),
                        rs.getInt("estoque_minimo")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar estoque: " + e.getMessage(), e);
        }

        return itens;
    }
}