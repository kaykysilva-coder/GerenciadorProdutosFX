# Mercadinho do Pedro - Sistema de Gerenciamento de Produtos

Sistema desenvolvido em **JavaFX (MVC)** com persistência em **CSV** e relatórios analíticos utilizando a **Stream API**, desenvolvido para a disciplina de Programação Orientada a Objetos III (POO III).

## Funcionalidades Principais
- **Cadastro e Validação de Produtos**: Validação rigorosa de códigos, nomes, datas de fabricação e validade, regras de preços (Venda > Compra) e controle de estoque não negativo.
- **Persistência em CSV**: Salvamento automático e carregamento dos dados via arquivo CSV (`produtos.csv`), garantindo que as informações não se percam ao fechar o sistema.
- **Interface Monocromática (Preto e Branco)**: Design moderno com detalhes em destaque vermelho para o cabeçalho.
- **Relatórios Analíticos com Stream API**:
    - **Estoque Baixo**: Filtra produtos com menos de 10 unidades diretamente na tabela.
    - **Próximos ao Vencimento**: Filtra produtos com validade nos próximos 60 dias.
    - **Lucro Médio por Categoria**: Calcula o lucro médio de cada categoria utilizando fluxos de Stream e exibe o resumo na tabela.
    - **Agrupamento por Setor/Categoria**: Organiza e ordena os produtos diretamente na tabela de acordo com a categoria.

---

## Como Executar o Programa

### Pré-requisitos
- **Java Development Kit (JDK 17 ou superior)** instalado.
- **JavaFX SDK** configurado no teu ambiente de desenvolvimento (ex: IntelliJ IDEA).

### Passos para Execução (IntelliJ IDEA)
1. Certifica-te de que o projeto está aberto como um projeto Maven ou Gradle (ou com as bibliotecas JavaFX ligadas no *Project Structure*).
2. Abre a classe principal que inicializa a aplicação (ex: `Main.java` ou a classe que herda de `Application`).
3. Clica no botão **Run** ▶️ do IntelliJ para iniciar a interface gráfica.
4. O arquivo `produtos.csv` será gerado automaticamente na raiz do projeto para armazenar os dados cadastrados.