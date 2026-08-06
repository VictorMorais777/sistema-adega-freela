package com.adega.service;

import com.adega.model.Bebida;
import com.adega.model.Cerveja;
import com.adega.model.Gin;
import com.adega.model.Pinga;
import com.adega.model.Vinho;

import java.util.ArrayList;
import java.util.List;

public class GarrafaService {

    private List<Cerveja> cervejas = new ArrayList<>();
    private List<Vinho> vinhos = new ArrayList<>();
    private List<Pinga> pingas = new ArrayList<>();
    private List<Gin> gins = new ArrayList<>();

    // ---------- Cadastro ----------

    public void cadastrarCerveja(Cerveja cerveja) {
        cervejas.add(cerveja);
    }

    public void cadastrarVinho(Vinho vinho) {
        vinhos.add(vinho);
    }

    public void cadastrarPinga(Pinga pinga) {
        pingas.add(pinga);
    }

    public void cadastrarGin(Gin gin) {
        gins.add(gin);
    }

    // ---------- Listagem ----------

    public List<Cerveja> listarCervejas() {
        return cervejas;
    }

    public List<Vinho> listarVinhos() {
        return vinhos;
    }

    public List<Pinga> listarPingas() {
        return pingas;
    }

    public List<Gin> listarGins() {
        return gins;
    }

    // ---------- Busca ----------

    public Cerveja buscarCervejaPorNome(String nome) {
        return buscarPorNome(cervejas, nome);
    }

    public Vinho buscarVinhoPorNome(String nome) {
        return buscarPorNome(vinhos, nome);
    }

    public Pinga buscarPingaPorNome(String nome) {
        return buscarPorNome(pingas, nome);
    }

    public Gin buscarGinPorNome(String nome) {
        return buscarPorNome(gins, nome);
    }

    private <T extends Bebida> T buscarPorNome(List<T> lista, String nome) {
        for (T item : lista) {
            if (item.getNome().equalsIgnoreCase(nome)) {
                return item;
            }
        }
        return null;
    }

    // ---------- Venda ----------

    public Cerveja venderCerveja(String nome) {
        Cerveja cerveja = buscarCervejaPorNome(nome);
        if (cerveja == null) {
            throw new RuntimeException("Cerveja não encontrada: " + nome);
        }
        cerveja.removerEstoque(1);
        return cerveja;
    }

    public Vinho venderVinho(String nome) {
        Vinho vinho = buscarVinhoPorNome(nome);
        if (vinho == null) {
            throw new RuntimeException("Vinho não encontrado: " + nome);
        }
        vinho.removerEstoque(1);
        return vinho;
    }

    public Pinga venderPinga(String nome) {
        Pinga pinga = buscarPingaPorNome(nome);
        if (pinga == null) {
            throw new RuntimeException("Pinga não encontrada: " + nome);
        }
        pinga.removerEstoque(1);
        return pinga;
    }

    public Gin venderGin(String nome) {
        Gin gin = buscarGinPorNome(nome);
        if (gin == null) {
            throw new RuntimeException("Gin não encontrado: " + nome);
        }
        gin.removerEstoque(1);
        return gin;
    }
}