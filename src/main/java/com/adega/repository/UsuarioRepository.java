package com.adega.repository;

import com.adega.model.Papel;
import com.adega.model.Usuario;
import com.adega.util.SenhaUtil;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    private static final String URL = "jdbc:sqlite:adega.db";

    public UsuarioRepository() {
        criarTabelaSeNaoExistir();
        criarUsuarioPadraoSeNecessario();
    }

    private Connection conectar() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    private void criarTabelaSeNaoExistir() {
        String sql = """
                CREATE TABLE IF NOT EXISTS usuarios (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    usuario TEXT NOT NULL UNIQUE,
                    senha_hash TEXT NOT NULL,
                    salt TEXT NOT NULL,
                    papel TEXT NOT NULL
                )
                """;

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao criar tabela de usuários: " + e.getMessage(), e);
        }
    }

    private void criarUsuarioPadraoSeNecessario() {
        if (listarTodos().isEmpty()) {
            salvar("admin", "admin", Papel.PATRAO);
        }
    }

    public void salvar(String usuario, String senha, Papel papel) {
        String salt = SenhaUtil.gerarSalt();
        String hash = SenhaUtil.hash(senha, salt);

        String sql = "INSERT INTO usuarios (usuario, senha_hash, salt, papel) VALUES (?, ?, ?, ?)";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);
            stmt.setString(2, hash);
            stmt.setString(3, salt);
            stmt.setString(4, papel.name());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar usuário (talvez já exista): " + e.getMessage(), e);
        }
    }

    public void remover(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover usuário: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listarTodos() {
        String sql = "SELECT * FROM usuarios ORDER BY usuario";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conn = conectar();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                usuarios.add(new Usuario(
                        rs.getInt("id"),
                        rs.getString("usuario"),
                        rs.getString("senha_hash"),
                        rs.getString("salt"),
                        Papel.valueOf(rs.getString("papel"))
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar usuários: " + e.getMessage(), e);
        }

        return usuarios;
    }

    public Usuario autenticar(String usuario, String senha) {
        String sql = "SELECT * FROM usuarios WHERE usuario = ?";

        try (Connection conn = conectar();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, usuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String salt = rs.getString("salt");
                    String hashEsperado = rs.getString("senha_hash");

                    if (SenhaUtil.verificar(senha, salt, hashEsperado)) {
                        return new Usuario(
                                rs.getInt("id"),
                                rs.getString("usuario"),
                                hashEsperado,
                                salt,
                                Papel.valueOf(rs.getString("papel"))
                        );
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erro ao autenticar: " + e.getMessage(), e);
        }

        return null;
    }
}