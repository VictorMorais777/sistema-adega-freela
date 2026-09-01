package com.adega;

import com.adega.model.Bebida;
import com.adega.model.Cerveja;
import com.adega.model.Pinga;
import com.adega.model.Vinho;
import com.adega.model.copao.CopaoComponente;
import com.adega.model.item.Destilado;
import com.adega.model.item.Energetico;
import com.adega.model.item.Gelo;
import com.adega.model.venda.FormaPagamento;
import com.adega.model.venda.Venda;
import com.adega.service.ArquivoService;
import com.adega.service.EstoqueService;
import com.adega.service.GarrafaService;
import com.adega.service.VendaService;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final int ESTOQUE_MINIMO = 3;
    private static final int ESTOQUE_MINIMO_ML = 100;

    private static final Scanner scanner = new Scanner(System.in);
    private static final EstoqueService estoque = new EstoqueService();
    private static final GarrafaService garrafas = new GarrafaService();
    private static final VendaService vendaService = new VendaService();
    private static final ArquivoService arquivoService = new ArquivoService();

    public static void main(String[] args) {
        System.out.println("=========================================");
        System.out.println("      SISTEMA ADEGA - COPÕES E GARRAFAS");
        System.out.println("=========================================");

        arquivoService.carregarEstoqueItens(estoque);
        arquivoService.carregarEstoqueGarrafas(garrafas);
        verificarEstoqueBaixo();

        boolean rodando = true;
        while (rodando) {
            exibirMenu();
            int opcao = lerInt("Escolha uma opção: ");

            switch (opcao) {
                case 1 -> cadastrarItemCopao();
                case 2 -> listarEstoqueCopao();
                case 3 -> montarCopao();
                case 4 -> cadastrarGarrafa();
                case 5 -> listarGarrafas();
                case 6 -> venderGarrafa();
                case 7 -> vendaService.imprimirRelatorio();
                case 8 -> salvarVendas();
                case 9 -> salvarEstoque();
                case 10 -> relatorioFaturamentoPorDia();
                case 11 -> relatorioRankingBebidas();
                case 12 -> verificarEstoqueBaixo();
                case 0 -> rodando = confirmarSaida();
                default -> System.out.println("Opção inválida. Tente novamente.");
            }

            System.out.println();
        }

        scanner.close();
        System.out.println("Sistema encerrado. Até a próxima!");
    }

    private static void exibirMenu() {
        System.out.println("-----------------------------------------");
        System.out.println("-- Copões --");
        System.out.println("1 - Cadastrar item de copão (Destilado/Energético/Gelo)");
        System.out.println("2 - Listar estoque de itens de copão");
        System.out.println("3 - Montar e vender copão");
        System.out.println("-- Garrafas --");
        System.out.println("4 - Cadastrar garrafa (Cerveja/Vinho/Pinga/Gin)");
        System.out.println("5 - Listar estoque de garrafas");
        System.out.println("6 - Vender garrafa");
        System.out.println("-- Geral --");
        System.out.println("7 - Ver relatório de vendas");
        System.out.println("8 - Salvar vendas em arquivo");
        System.out.println("9 - Salvar estoque em arquivo");
        System.out.println("10 - Faturamento por dia");
        System.out.println("11 - Ranking de bebidas mais vendidas");
        System.out.println("12 - Alertas de estoque baixo");
        System.out.println("0 - Sair");
        System.out.println("-----------------------------------------");
    }

    // ===================== ITENS DE COPÃO =====================

    private static void cadastrarItemCopao() {
        System.out.println("\nQual item deseja cadastrar?");
        System.out.println("1 - Destilado (Gin, Whisky, Vodka...)");
        System.out.println("2 - Energético");
        System.out.println("3 - Gelo");
        int tipo = lerInt("Opção: ");

        switch (tipo) {
            case 1 -> cadastrarDestilado();
            case 2 -> cadastrarEnergetico();
            case 3 -> cadastrarGelo();
            default -> System.out.println("Tipo inválido.");
        }
    }

    private static void cadastrarDestilado() {
        String nome = lerTexto("Nome (ex: Beefeater, Red Label): ");
        String tipoDestilado = lerTexto("Tipo (ex: Gin, Whisky, Vodka): ");
        double precoGarrafa = lerDouble("Preço da garrafa: R$ ");
        int volumeGarrafaMl = lerInt("Volume da garrafa (em ml, ex: 1000): ");

        double precoPorMl = precoGarrafa / volumeGarrafaMl;

        estoque.cadastrarDestilado(new Destilado(nome, tipoDestilado, precoPorMl, volumeGarrafaMl));
        System.out.printf("%s \"%s\" cadastrado! (R$ %.4f por ml, %d ml em estoque)%n", tipoDestilado, nome, precoPorMl, volumeGarrafaMl);
    }

    private static void cadastrarEnergetico() {
        String nome = lerTexto("Nome do energético: ");
        double preco = lerDouble("Preço: ");
        int quantidade = lerInt("Quantidade em estoque: ");

        estoque.cadastrarEnergetico(new Energetico(nome, preco, quantidade));
        System.out.println("Energético \"" + nome + "\" cadastrado com sucesso!");
    }

    private static void cadastrarGelo() {
        String sabor = lerTexto("Sabor do gelo: ");
        double preco = lerDouble("Preço: ");
        int quantidade = lerInt("Quantidade em estoque: ");

        estoque.cadastrarGelo(new Gelo(sabor, preco, quantidade));
        System.out.println("Gelo sabor \"" + sabor + "\" cadastrado com sucesso!");
    }

    private static void listarEstoqueCopao() {
        System.out.println("\n===== ESTOQUE DE DESTILADOS =====");
        List<Destilado> destilados = estoque.listarDestilados();
        if (destilados.isEmpty()) {
            System.out.println("Nenhum destilado cadastrado.");
        } else {
            for (Destilado d : destilados) {
                System.out.printf("- [%s] %s | R$ %.4f/ml | %dml em estoque%s%n",
                        d.getTipo(), d.getNome(), d.getPrecoPorMl(), d.getQuantidadeEstoque(), alertaEstoqueMl(d.getQuantidadeEstoque()));
            }
        }

        System.out.println("\n===== ESTOQUE DE ENERGÉTICOS =====");
        List<Energetico> energeticos = estoque.listarEnergeticos();
        if (energeticos.isEmpty()) {
            System.out.println("Nenhum energético cadastrado.");
        } else {
            for (Energetico e : energeticos) {
                System.out.printf("- %s | R$ %.2f | Qtd: %d%s%n", e.getNome(), e.getPreco(), e.getQuantidadeEstoque(), alertaEstoque(e.getQuantidadeEstoque()));
            }
        }

        System.out.println("\n===== ESTOQUE DE GELOS =====");
        List<Gelo> gelos = estoque.listarGelos();
        if (gelos.isEmpty()) {
            System.out.println("Nenhum gelo cadastrado.");
        } else {
            for (Gelo g : gelos) {
                System.out.printf("- %s | R$ %.2f | Qtd: %d%s%n", g.getSabor(), g.getPreco(), g.getQuantidadeEstoque(), alertaEstoque(g.getQuantidadeEstoque()));
            }
        }
    }

    private static void montarCopao() {
        System.out.println("\nVamos montar um copão!");
        String nomeCopao = lerTexto("Nome do copão: ");
        String nomeDestilado = lerTexto("Nome do destilado (gin/whisky/etc.): ");
        int mlDestilado = lerInt("Quantidade de ml de destilado usada: ");
        String nomeEnergetico = lerTexto("Nome do energético: ");
        String saborGelo = lerTexto("Sabor do gelo: ");
        int quantidadeGelo = lerInt("Quantidade de gelo (ex: 1 ou 2): ");

        try {
            CopaoComponente copao = estoque.criarCopao(nomeCopao, nomeDestilado, mlDestilado, nomeEnergetico, saborGelo, quantidadeGelo);
            System.out.printf("Copão \"%s\" montado! Valor: R$ %.2f%n", copao.getNome(), copao.getPreco());
            processarPagamento(copao);
        } catch (RuntimeException e) {
            System.out.println("Não foi possível montar o copão: " + e.getMessage());
        }
    }

    // ===================== GARRAFAS =====================

    private static void cadastrarGarrafa() {
        System.out.println("\nQual garrafa deseja cadastrar?");
        System.out.println("1 - Cerveja");
        System.out.println("2 - Vinho");
        System.out.println("3 - Pinga");
        System.out.println("4 - Gin");
        int tipo = lerInt("Opção: ");

        String nome = lerTexto("Nome: ");
        double preco = lerDouble("Preço: ");
        int quantidade = lerInt("Quantidade em estoque: ");

        switch (tipo) {
            case 1 -> {
                garrafas.cadastrarCerveja(new Cerveja(nome, preco, quantidade));
                System.out.println("Cerveja \"" + nome + "\" cadastrada com sucesso!");
            }
            case 2 -> {
                garrafas.cadastrarVinho(new Vinho(nome, preco, quantidade));
                System.out.println("Vinho \"" + nome + "\" cadastrado com sucesso!");
            }
            case 3 -> {
                garrafas.cadastrarPinga(new Pinga(nome, preco, quantidade));
                System.out.println("Pinga \"" + nome + "\" cadastrada com sucesso!");
            }
            case 4 -> {
                garrafas.cadastrarGin(new com.adega.model.Gin(nome, preco, quantidade));
                System.out.println("Gin \"" + nome + "\" cadastrado com sucesso!");
            }
            default -> System.out.println("Tipo inválido.");
        }
    }

    private static void listarGarrafas() {
        System.out.println("\n===== ESTOQUE DE CERVEJAS =====");
        List<Cerveja> cervejas = garrafas.listarCervejas();
        if (cervejas.isEmpty()) {
            System.out.println("Nenhuma cerveja cadastrada.");
        } else {
            for (Cerveja c : cervejas) {
                System.out.printf("- %s | R$ %.2f | Qtd: %d%s%n", c.getNome(), c.getPreco(), c.getQuantidadeEstoque(), alertaEstoque(c.getQuantidadeEstoque()));
            }
        }

        System.out.println("\n===== ESTOQUE DE VINHOS =====");
        List<Vinho> vinhos = garrafas.listarVinhos();
        if (vinhos.isEmpty()) {
            System.out.println("Nenhum vinho cadastrado.");
        } else {
            for (Vinho v : vinhos) {
                System.out.printf("- %s | R$ %.2f | Qtd: %d%s%n", v.getNome(), v.getPreco(), v.getQuantidadeEstoque(), alertaEstoque(v.getQuantidadeEstoque()));
            }
        }

        System.out.println("\n===== ESTOQUE DE PINGAS =====");
        List<Pinga> pingas = garrafas.listarPingas();
        if (pingas.isEmpty()) {
            System.out.println("Nenhuma pinga cadastrada.");
        } else {
            for (Pinga p : pingas) {
                System.out.printf("- %s | R$ %.2f | Qtd: %d%s%n", p.getNome(), p.getPreco(), p.getQuantidadeEstoque(), alertaEstoque(p.getQuantidadeEstoque()));
            }
        }

        System.out.println("\n===== ESTOQUE DE GINS (garrafa) =====");
        List<com.adega.model.Gin> gins = garrafas.listarGins();
        if (gins.isEmpty()) {
            System.out.println("Nenhum gin cadastrado.");
        } else {
            for (com.adega.model.Gin g : gins) {
                System.out.printf("- %s | R$ %.2f | Qtd: %d%s%n", g.getNome(), g.getPreco(), g.getQuantidadeEstoque(), alertaEstoque(g.getQuantidadeEstoque()));
            }
        }
    }

    private static void venderGarrafa() {
        System.out.println("\nQual tipo de garrafa deseja vender?");
        System.out.println("1 - Cerveja");
        System.out.println("2 - Vinho");
        System.out.println("3 - Pinga");
        System.out.println("4 - Gin");
        int tipo = lerInt("Opção: ");

        String nome = lerTexto("Nome da garrafa: ");

        try {
            Bebida vendida = switch (tipo) {
                case 1 -> garrafas.venderCerveja(nome);
                case 2 -> garrafas.venderVinho(nome);
                case 3 -> garrafas.venderPinga(nome);
                case 4 -> garrafas.venderGin(nome);
                default -> throw new RuntimeException("Tipo inválido.");
            };
            System.out.printf("%s selecionado(a)! Valor: R$ %.2f%n", vendida.getDescricao(), vendida.getPreco());
            processarPagamento(vendida);
        } catch (RuntimeException e) {
            System.out.println("Não foi possível vender a garrafa: " + e.getMessage());
        }
    }

    // ===================== PAGAMENTO =====================

    private static void processarPagamento(Bebida vendida) {
        System.out.println("\nForma de pagamento:");
        System.out.println("1 - Dinheiro");
        System.out.println("2 - Cartão de Débito");
        System.out.println("3 - Cartão de Crédito");
        System.out.println("4 - Pix");
        int opcao = lerInt("Opção: ");

        FormaPagamento forma = switch (opcao) {
            case 1 -> FormaPagamento.DINHEIRO;
            case 2 -> FormaPagamento.DEBITO;
            case 3 -> FormaPagamento.CREDITO;
            case 4 -> FormaPagamento.PIX;
            default -> null;
        };

        if (forma == null) {
            System.out.println("Forma de pagamento inválida. Venda não registrada.");
            return;
        }

        try {
            if (forma == FormaPagamento.DINHEIRO) {
                double valorPago = lerValorPagoValido(vendida.getPreco());
                Venda venda = vendaService.registrarVenda(vendida, forma, valorPago);
                System.out.printf("Venda registrada! Pago em dinheiro: R$ %.2f | Troco: R$ %.2f%n",
                        valorPago, venda.getTroco());
            } else {
                vendaService.registrarVenda(vendida, forma);
                System.out.printf("Venda registrada via %s!%n", forma.getDescricao());
            }
        } catch (RuntimeException e) {
            System.out.println("Não foi possível registrar a venda: " + e.getMessage());
        }
    }

    private static double lerValorPagoValido(double valorDaVenda) {
        while (true) {
            double valorPago = lerDouble("Valor recebido em dinheiro: R$ ");
            if (valorPago < valorDaVenda) {
                System.out.printf("Valor insuficiente. Faltam R$ %.2f. Tente novamente.%n", valorDaVenda - valorPago);
                continue;
            }
            return valorPago;
        }
    }

    // ===================== RELATÓRIOS =====================

    private static void relatorioFaturamentoPorDia() {
        Map<LocalDate, Double> mapa = vendaService.faturamentoPorDia();

        if (mapa.isEmpty()) {
            System.out.println("Nenhuma venda registrada ainda.");
            return;
        }

        System.out.println("\n===== FATURAMENTO POR DIA =====");
        mapa.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> System.out.printf("%s | R$ %.2f%n", entry.getKey(), entry.getValue()));
    }

    private static void relatorioRankingBebidas() {
        Map<String, Integer> ranking = vendaService.rankingBebidas();

        if (ranking.isEmpty()) {
            System.out.println("Nenhuma venda registrada ainda.");
            return;
        }

        System.out.println("\n===== RANKING DE BEBIDAS MAIS VENDIDAS =====");
        ranking.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(entry -> System.out.printf("%s | %d venda(s)%n", entry.getKey(), entry.getValue()));
    }

    private static void verificarEstoqueBaixo() {
        System.out.println("\n===== ALERTAS DE ESTOQUE BAIXO =====");
        boolean algumAlerta = false;

        for (Destilado d : estoque.listarDestilados()) {
            if (d.getQuantidadeEstoque() <= ESTOQUE_MINIMO_ML) {
                System.out.printf("- [%s] %s | %dml restantes%n", d.getTipo(), d.getNome(), d.getQuantidadeEstoque());
                algumAlerta = true;
            }
        }
        for (Energetico e : estoque.listarEnergeticos()) {
            if (e.getQuantidadeEstoque() <= ESTOQUE_MINIMO) {
                System.out.printf("- Energético: %s | Qtd: %d%n", e.getNome(), e.getQuantidadeEstoque());
                algumAlerta = true;
            }
        }
        for (Gelo g : estoque.listarGelos()) {
            if (g.getQuantidadeEstoque() <= ESTOQUE_MINIMO) {
                System.out.printf("- Gelo: %s | Qtd: %d%n", g.getSabor(), g.getQuantidadeEstoque());
                algumAlerta = true;
            }
        }
        for (Cerveja c : garrafas.listarCervejas()) {
            if (c.getQuantidadeEstoque() <= ESTOQUE_MINIMO) {
                System.out.printf("- Cerveja: %s | Qtd: %d%n", c.getNome(), c.getQuantidadeEstoque());
                algumAlerta = true;
            }
        }
        for (Vinho v : garrafas.listarVinhos()) {
            if (v.getQuantidadeEstoque() <= ESTOQUE_MINIMO) {
                System.out.printf("- Vinho: %s | Qtd: %d%n", v.getNome(), v.getQuantidadeEstoque());
                algumAlerta = true;
            }
        }
        for (Pinga p : garrafas.listarPingas()) {
            if (p.getQuantidadeEstoque() <= ESTOQUE_MINIMO) {
                System.out.printf("- Pinga: %s | Qtd: %d%n", p.getNome(), p.getQuantidadeEstoque());
                algumAlerta = true;
            }
        }
        for (com.adega.model.Gin g : garrafas.listarGins()) {
            if (g.getQuantidadeEstoque() <= ESTOQUE_MINIMO) {
                System.out.printf("- Gin (garrafa): %s | Qtd: %d%n", g.getNome(), g.getQuantidadeEstoque());
                algumAlerta = true;
            }
        }

        if (!algumAlerta) {
            System.out.println("Nenhum item com estoque baixo. Tudo certo!");
        }
    }

    private static String alertaEstoque(int quantidade) {
        return quantidade <= ESTOQUE_MINIMO ? "  ⚠ ESTOQUE BAIXO" : "";
    }

    private static String alertaEstoqueMl(int quantidadeMl) {
        return quantidadeMl <= ESTOQUE_MINIMO_ML ? "  ⚠ ESTOQUE BAIXO" : "";
    }

    // ===================== PERSISTÊNCIA =====================

    private static void salvarVendas() {
        if (vendaService.listarVendas().isEmpty()) {
            System.out.println("Nenhuma venda registrada ainda.");
            return;
        }
        arquivoService.salvarVendas(vendaService.listarVendas());
    }

    private static void salvarEstoque() {
        arquivoService.salvarEstoqueItens(estoque);
        arquivoService.salvarEstoqueGarrafas(garrafas);
        System.out.println("Estoque salvo com sucesso!");
    }

    private static boolean confirmarSaida() {
        arquivoService.salvarEstoqueItens(estoque);
        arquivoService.salvarEstoqueGarrafas(garrafas);

        if (!vendaService.listarVendas().isEmpty()) {
            String resposta = lerTexto("Deseja salvar as vendas antes de sair? (s/n): ");
            if (resposta.equalsIgnoreCase("s")) {
                salvarVendas();
            }
        }
        return false;
    }

    // ===================== HELPERS DE LEITURA =====================

    private static String lerTexto(String mensagem) {
        System.out.print(mensagem);
        return scanner.nextLine().trim();
    }

    private static int lerInt(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim();
            try {
                return Integer.parseInt(entrada);
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número inteiro.");
            }
        }
    }

    private static double lerDouble(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String entrada = scanner.nextLine().trim().replace(",", ".");
            try {
                double valor = Double.parseDouble(entrada);
                if (valor < 0) {
                    System.out.println("O valor não pode ser negativo.");
                    continue;
                }
                return valor;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Digite um número (ex: 10.50).");
            }
        }
    }
}