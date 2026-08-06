# Sistema Adega

Sistema de gestão para uma adega, com controle de estoque, montagem e venda de "copões" personalizados, venda de garrafas, controle de pagamento com troco e relatórios de vendas. Desenvolvido em Java, com duas formas de uso: um CLI (linha de comando) e uma interface gráfica em JavaFX com persistência em SQLite.

## Funcionalidades

- **Estoque de copões**: cadastro de Gin, Energético e Gelo, com controle de quantidade
- **Montagem de copões**: monta um copão a partir dos ingredientes em estoque, descontando automaticamente as quantidades usadas
- **Estoque de garrafas**: cadastro de Cerveja, Vinho, Pinga e Gin (garrafa)
- **Venda de garrafas**
- **Forma de pagamento**: Dinheiro (com cálculo de troco), Débito, Crédito ou Pix
- **Relatórios**: faturamento total, faturamento por dia, ranking de bebidas mais vendidas, alertas de estoque baixo
- **Persistência**: estoque e vendas salvos em arquivo, para não perder dados ao fechar o sistema
- **Interface gráfica (em desenvolvimento)**: tela de estoque em JavaFX, com dados salvos em banco SQLite

## Tecnologias

- Java 21
- Maven
- JavaFX (interface gráfica)
- SQLite (persistência da interface gráfica)

## Como rodar

### Modo CLI (linha de comando)

```
mvn compile exec:java -Dexec.mainClass="com.adega.Main"
```

Ou, direto pela IDE, execute a classe `com.adega.Main`.

### Modo interface gráfica (JavaFX)

```
mvn org.openjfx:javafx-maven-plugin:0.0.8:run
```

## Estrutura do projeto

```
src/main/java/com/adega/
├── Main.java              # Ponto de entrada do CLI
├── gui/                    # Interface gráfica (JavaFX)
├── model/                  # Entidades: Bebida, Cerveja, Vinho, Pinga, Gin, ItemEstoque
│   ├── copao/               # Copão e seu builder
│   ├── item/                 # Ingredientes de copão: Gin, Energético, Gelo
│   └── venda/                 # Venda e FormaPagamento
├── repository/             # Acesso ao banco SQLite
└── service/                # Regras de negócio: estoque, garrafas, vendas, arquivos
```

## Status do projeto

Projeto em desenvolvimento contínuo como trabalho freelance.
