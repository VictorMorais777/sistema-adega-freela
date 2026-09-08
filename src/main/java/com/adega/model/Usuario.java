package com.adega.model;

public class Usuario {

    private int id;
    private String usuario;
    private String senhaHash;
    private String salt;
    private Papel papel;

    public Usuario(String usuario, String senhaHash, String salt, Papel papel) {
        this.usuario = usuario;
        this.senhaHash = senhaHash;
        this.salt = salt;
        this.papel = papel;
    }

    public Usuario(int id, String usuario, String senhaHash, String salt, Papel papel) {
        this(usuario, senhaHash, salt, papel);
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getUsuario() {
        return usuario;
    }

    public String getSenhaHash() {
        return senhaHash;
    }

    public String getSalt() {
        return salt;
    }

    public Papel getPapel() {
        return papel;
    }
}