# Relatório de Implementação e Decisões de Design - Mercadinho do Pedro

## 1. Introdução
O projeto "Mercadinho do Pedro" consiste numa aplicação desktop desenvolvida em Java utilizando o framework **JavaFX** e o padrão arquitetural **MVC (Model-View-Controller)**. O objetivo principal é automatizar o controle de inventário de um estabelecimento comercial, garantindo integridade de dados, persistência em ficheiros e análises gerenciais avançadas.

## 2. Decisões de Design e Arquitetura
- **Padrão MVC**: ASeparar as classes de modelo (`Produto`, `Categoria`), a interface gráfica (`produto-view.fxml`) e a lógica de controlo (`ProdutoController`) permitiu um código modular, limpo e de fácil manutenção.
- **Tema Visual**: Optou-se por um design minimalista em tons de preto, branco e cinza, complementado por um cabeçalho em vermelho (`#d32f2f`), conferindo um aspeto profissional e sóbrio à aplicação.
- **Tratamento de Exceções e Regras de Negócio**: Validações robustas foram aplicadas no construtor da classe `Produto` e no controlador (como preço de venda superior ao de compra, estoques não negativos e datas consistentes), impedindo estados inválidos no sistema.
- **Máscara de Data**: Implementação de um `TextFormatter` customizado para guiar o utilizador no formato brasileiro (`DD/MM/AAAA`), reduzindo erros de digitação.

## 3. Uso da Stream API nos Relatórios
Para atender aos requisitos analíticos, a **Java Stream API** foi extensivamente aplicada:
- **Filtros Dinâmicos**: Utilização de `.stream().filter(...)` para isolar produtos com estoque crítico (< 10) e prazos de validade próximos (< 60 dias).
- **Agrupamento e Agregação**: Emprego de `Collectors.groupingBy` combinado com `Collectors.averagingDouble` para calcular de forma eficiente a margem de lucro média por categoria, transformando os resultados em objetos de resumo exibidos de forma fluida na `TableView`.

## 4. Conclusão
O sistema cumpre integralmente todos os requisitos funcionais e técnicos propostos, oferecendo uma interface intuitiva, persistência de dados estável via CSV e relatórios analíticos de alto desempenho baseados nos recursos modernos da linguagem Java.