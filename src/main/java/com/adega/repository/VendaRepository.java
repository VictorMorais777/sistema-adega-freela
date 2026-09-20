package com.adega.repository;

import com.adega.model.ItemVendido;
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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class VendaRepository {

    private static final String URL = "jdbc:sqlite:adega.db";
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VendaRepository() {
        criarTabelasSeNaoExistirem();
        migrarColunasNovas();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelasSeNaoExistirem() {
        String sqlVendas = """
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

        String sqlItens = """
                CREATE TABLE IF NOT EXISTS venda_itens (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    venda_id INTEGER NOT NULL,
                    item_estoque_id INTEGER NOT NULL,
                    quantidade INTEGER NOT NULL
                )
                """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlVendas);
            stmt.execute(sqlItens);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabelas de vendas: " + e.getMessage(), e);
        }
    }

    private void migrarColunasNovas() {
        Set<String> colunas = new HashSet<>();

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("PRAGMA table_info(vendas)")) {

            while (rs.next()) {
                colunas.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao inspecionar tabela de vendas: " + e.getMessage(), e);
        }

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {

            if (!colunas.contains("valor_bruto")) {
                stmt.execute("ALTER TABLE vendas ADD COLUMN valor_bruto REAL NOT NULL DEFAULT 0");
                stmt.execute("UPDATE vendas SET valor_bruto = valor WHERE valor_bruto = 0");
            }
            if (!colunas.contains("desconto")) {
                stmt.execute("ALTER TABLE vendas ADD COLUMN desconto REAL NOT NULL DEFAULT 0");
            }
            if (!colunas.contains("cancelada")) {
                stmt.execute("ALTER TABLE vendas ADD COLUMN cancelada INTEGER NOT NULL DEFAULT 0");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao migrar tabela de vendas: " + e.getMessage(), e);
        }
    }

    public void salvar(VendaRegistro venda, List<ItemVendido> itens) {
        String sqlVenda = """
                INSERT INTO vendas (descricao, valor_bruto, desconto, valor, forma_pagamento, valor_pago, troco, data_hora, cancelada)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, 0)
                """;

        try (Connection conn = conectar()) {
            conn.setAutoCommit(false);

            int vendaId;

            try (PreparedStatement stmt = conn.prepareStatement(sqlVenda)) {
                stmt.setString(1, venda.getDescricao());
                stmt.setDouble(2, venda.getValorBruto());
                stmt.setDouble(3, venda.getDesconto());
                stmt.setDouble(4, venda.getValor());
                stmt.setString(5, venda.getFormaPagamento().name());
                stmt.setDouble(6, venda.getValorPago());
                stmt.setDouble(7, venda.getTroco());
                stmt.setString(8, venda.getDataHora().format(FORMATO_DATA));
                stmt.executeUpdate();
            }

            try (Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT last_insert_rowid() AS id")) {
                if (!rs.next()) {
                    conn.rollback();
                    throw new RuntimeException("Não foi possível obter o ID da venda salva.");
                }
                vendaId = rs.getInt("id");
            }

            if (itens != null && !itens.isEmpty()) {
                String sqlItem = "INSERT INTO venda_itens (venda_id, item_estoque_id, quantidade) VALUES (?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(sqlItem)) {
                    for (ItemVendido item : itens) {
                        stmt.setInt(1, vendaId);
                        stmt.setInt(2, item.getItemEstoqueId());
                        stmt.setInt(3, item.getQuantidade());
                        stmt.addBatch();
                    }
                    stmt.executeBatch();
                }
            }

            conn.commit();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar venda: " + e.getMessage(), e);
        }
    }

    public List<ItemVendido> listarItensDaVenda(int vendaId) {
        String sql = "SELECT item_estoque_id, quantidade FROM venda_itens WHERE venda_id = ?";
        List<ItemVendido> itens = new ArrayList<>();

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendaId);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    itens.add(new ItemVendido(rs.getInt("item_estoque_id"), rs.getInt("quantidade")));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar itens da venda: " + e.getMessage(), e);
        }

        return itens;
    }

    public void marcarComoCancelada(int vendaId) {
        String sql = "UPDATE vendas SET cancelada = 1 WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, vendaId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao cancelar venda: " + e.getMessage(), e);
        }
    }

    public void atualizarValores(int vendaId, double valorBruto, double desconto,
                                 FormaPagamento formaPagamento, double valorPago) {
        double valor = valorBruto - desconto;
        double troco = (formaPagamento == FormaPagamento.DINHEIRO) ? (valorPago - valor) : 0;

        String sql = """
                UPDATE vendas
                SET valor_bruto = ?, desconto = ?, valor = ?, forma_pagamento = ?, valor_pago = ?, troco = ?
                WHERE id = ?
                """;

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDouble(1, valorBruto);
            stmt.setDouble(2, desconto);
            stmt.setDouble(3, valor);
            stmt.setString(4, formaPagamento.name());
            stmt.setDouble(5, valorPago);
            stmt.setDouble(6, troco);
            stmt.setInt(7, vendaId);

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao atualizar venda: " + e.getMessage(), e);
        }
    }

    public List<VendaRegistro> listarTodas() {
        String sql = "SELECT * FROM vendas ORDER BY data_hora DESC";
        List<VendaRegistro> vendas = new ArrayList<>();

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                vendas.add(montarVenda(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar vendas: " + e.getMessage(), e);
        }

        return vendas;
    }

    private VendaRegistro montarVenda(ResultSet rs) throws SQLException {
        return new VendaRegistro(
                rs.getInt("id"),
                rs.getString("descricao"),
                rs.getDouble("valor_bruto"),
                rs.getDouble("desconto"),
                rs.getDouble("valor"),
                FormaPagamento.valueOf(rs.getString("forma_pagamento")),
                rs.getDouble("valor_pago"),
                rs.getDouble("troco"),
                LocalDateTime.parse(rs.getString("data_hora"), FORMATO_DATA),
                rs.getInt("cancelada") == 1
        );
    }

    public double calcularFaturamentoTotal() {
        String sql = "SELECT SUM(valor) AS total FROM vendas WHERE cancelada = 0";

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

    public double calcularTotalDescontos() {
        String sql = "SELECT SUM(desconto) AS total FROM vendas WHERE cancelada = 0";

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            if (rs.next()) {
                return rs.getDouble("total");
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular descontos: " + e.getMessage(), e);
        }

        return 0;
    }

    public Map<String, Double> faturamentoPorDia() {
        String sql = """
                SELECT substr(data_hora, 1, 10) AS dia, SUM(valor) AS total
                FROM vendas WHERE cancelada = 0
                GROUP BY dia ORDER BY dia DESC
                """;
        Map<String, Double> resultado = new LinkedHashMap<>();

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultado.put(rs.getString("dia"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular faturamento por dia: " + e.getMessage(), e);
        }

        return resultado;
    }

    public Map<String, Integer> rankingBebidas() {
        String sql = """
                SELECT descricao, COUNT(*) AS quantidade
                FROM vendas WHERE cancelada = 0
                GROUP BY descricao ORDER BY quantidade DESC
                """;
        Map<String, Integer> resultado = new LinkedHashMap<>();

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                resultado.put(rs.getString("descricao"), rs.getInt("quantidade"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao calcular ranking de bebidas: " + e.getMessage(), e);
        }

        return resultado;
    }
}