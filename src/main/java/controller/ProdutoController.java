package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Categoria;
import model.Produto;
import util.CSVManager;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;
import java.util.function.UnaryOperator;
import java.util.stream.Collectors;

public class ProdutoController {

    @FXML private TextField txtCodigo;
    @FXML private TextField txtNome;
    @FXML private TextField txtDescricao;
    @FXML private TextField txtDataFab;
    @FXML private TextField txtDataVal;
    @FXML private TextField txtPrecoCompra;
    @FXML private TextField txtPrecoVenda;
    @FXML private TextField txtEstoque;
    @FXML private ComboBox<Categoria> cbCategoria;

    @FXML private TableView<Produto> tabelaProdutos;
    @FXML private TableColumn<Produto, String> colCodigo;
    @FXML private TableColumn<Produto, String> colNome;
    @FXML private TableColumn<Produto, LocalDate> colFabricacao;
    @FXML private TableColumn<Produto, LocalDate> colValidade;
    @FXML private TableColumn<Produto, BigDecimal> colPrecoVenda;
    @FXML private TableColumn<Produto, Integer> colEstoque;
    @FXML private TableColumn<Produto, Categoria> colCategoria;

    private ObservableList<Produto> listaProdutos = FXCollections.observableArrayList();
    private ObservableList<Categoria> listaCategorias = FXCollections.observableArrayList();

    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));
        colFabricacao.setCellValueFactory(new PropertyValueFactory<>("dataFabricacao"));
        colValidade.setCellValueFactory(new PropertyValueFactory<>("dataValidade"));
        colPrecoVenda.setCellValueFactory(new PropertyValueFactory<>("precoVenda"));
        colEstoque.setCellValueFactory(new PropertyValueFactory<>("quantidadeEstoque"));
        colCategoria.setCellValueFactory(new PropertyValueFactory<>("categoria"));

        listaCategorias.add(new Categoria(1, "Alimentos", "Produtos perecíveis e secos", "Mercearia"));
        listaCategorias.add(new Categoria(2, "Bebidas", "Com e sem álcool", "Bebidas"));
        listaCategorias.add(new Categoria(3, "Limpeza", "Produtos de higiene e casa", "Limpeza"));
        cbCategoria.setItems(listaCategorias);

        List<Produto> produtosSalvos = CSVManager.carregarProdutos();
        listaProdutos.addAll(produtosSalvos);
        tabelaProdutos.setItems(listaProdutos);

        UnaryOperator<TextFormatter.Change> dataFilter = change -> {
            String textoNovo = change.getControlNewText();

            if (change.isDeleted()) {
                return change;
            }

            if (textoNovo.length() > 10) {
                return null;
            }

            if (textoNovo.length() == 2 || textoNovo.length() == 5) {
                if (!textoNovo.endsWith("/")) {
                    change.setText(change.getText() + "/");
                    change.setCaretPosition(textoNovo.length() + 1);
                }
            }

            return change;
        };

        if (txtDataFab != null && txtDataVal != null) {
            txtDataFab.setTextFormatter(new TextFormatter<>(dataFilter));
            txtDataVal.setTextFormatter(new TextFormatter<>(dataFilter));
        }
    }

    @FXML
    private void cadastrarProduto() {
        try {
            String codigo = txtCodigo.getText();
            String nome = txtNome.getText();
            String descricao = txtDescricao.getText();

            LocalDate dataFab = LocalDate.parse(txtDataFab.getText(), dateFormatter);
            LocalDate dataVal = LocalDate.parse(txtDataVal.getText(), dateFormatter);

            BigDecimal precoCompra = new BigDecimal(txtPrecoCompra.getText());
            BigDecimal precoVenda = new BigDecimal(txtPrecoVenda.getText());
            int estoque = Integer.parseInt(txtEstoque.getText());
            Categoria categoria = cbCategoria.getValue();

            if (categoria == null) {
                mostrarAlerta("Erro de Validação", "Selecione uma categoria para o produto.");
                return;
            }

            Produto novoProduto = new Produto(codigo, nome, descricao, dataFab, dataVal, precoCompra, precoVenda, estoque, categoria);

            listaProdutos.add(novoProduto);
            CSVManager.salvarProdutos(listaProdutos);

            limparCampos();
            mostrarSucesso("Sucesso", "Produto cadastrado e salvo com sucesso!");

        } catch (DateTimeParseException e) {
            mostrarAlerta("Erro de Formato", "As datas devem estar no formato DD/MM/AAAA (ex: 16/10/2026).");
        } catch (NumberFormatException e) {
            mostrarAlerta("Erro de Formato", "Preencha os campos de preço e estoque com números válidos.");
        } catch (IllegalArgumentException e) {
            mostrarAlerta("Regra de Negócio", e.getMessage());
        }
    }

    @FXML
    private void excluirProduto() {
        Produto selecionado = tabelaProdutos.getSelectionModel().getSelectedItem();
        if (selecionado != null) {
            listaProdutos.remove(selecionado);
            CSVManager.salvarProdutos(listaProdutos);
            mostrarSucesso("Sucesso", "Produto excluído com sucesso!");
        } else {
            mostrarAlerta("Aviso", "Selecione um produto na tabela para excluir.");
        }
    }

    @FXML
    private void relatorioEstoqueBaixo() {
        List<Produto> filtrados = listaProdutos.stream()
                .filter(p -> p.getQuantidadeEstoque() < 10)
                .collect(Collectors.toList());

        tabelaProdutos.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void relatorioVencimento() {
        LocalDate hoje = LocalDate.now();
        List<Produto> filtrados = listaProdutos.stream()
                .filter(p -> p.getDataValidade() != null &&
                        p.getDataValidade().isAfter(hoje) &&
                        p.getDataValidade().isBefore(hoje.plusDays(60)))
                .collect(Collectors.toList());

        tabelaProdutos.setItems(FXCollections.observableArrayList(filtrados));
    }

    @FXML
    private void relatorioLucroMedio() {
        if (listaProdutos.isEmpty()) {
            mostrarAlerta("Aviso", "Não há produtos cadastrados para calcular o lucro.");
            return;
        }

        // Agrupa e calcula o lucro médio por categoria, transformando em "produtos fakes" para exibir na tabela
        Map<Categoria, Double> lucroPorCategoria = listaProdutos.stream()
                .filter(p -> p.getCategoria() != null)
                .collect(Collectors.groupingBy(
                        Produto::getCategoria,
                        Collectors.averagingDouble(p -> p.getPrecoVenda().subtract(p.getPrecoCompra()).doubleValue())
                ));

        ObservableList<Produto> listaResumo = FXCollections.observableArrayList();
        int i = 1;
        for (Map.Entry<Categoria, Double> entry : lucroPorCategoria.entrySet()) {
            // Criamos um produto representativo para exibir o lucro médio na coluna Preço Venda
            Produto resumo = new Produto(
                    "RES-" + i++,
                    "Lucro Médio da Categoria",
                    "Média de margem de lucro",
                    LocalDate.now(),
                    LocalDate.now(),
                    BigDecimal.ZERO,
                    BigDecimal.valueOf(entry.getValue()),
                    0,
                    entry.getKey()
            );
            listaResumo.add(resumo);
        }

        tabelaProdutos.setItems(listaResumo);
    }

    @FXML
    private void relatorioAgrupadoSetor() {
        // Requisito: Agrupar e ordenar os produtos por Categoria diretamente na TableView usando Stream API
        List<Produto> agrupadosPorCategoria = listaProdutos.stream()
                .sorted((p1, p2) -> {
                    String cat1 = p1.getCategoria() != null ? p1.getCategoria().getNome() : "";
                    String cat2 = p2.getCategoria() != null ? p2.getCategoria().getNome() : "";
                    return cat1.compareTo(cat2);
                })
                .collect(Collectors.toList());

        tabelaProdutos.setItems(FXCollections.observableArrayList(agrupadosPorCategoria));
    }

    @FXML
    private void mostrarTodos() {
        List<Produto> todos = CSVManager.carregarProdutos();
        listaProdutos.setAll(todos);
        tabelaProdutos.setItems(listaProdutos);
    }

    private void limparCampos() {
        txtCodigo.clear();
        txtNome.clear();
        txtDescricao.clear();
        txtDataFab.clear();
        txtDataVal.clear();
        txtPrecoCompra.clear();
        txtPrecoVenda.clear();
        txtEstoque.clear();
        cbCategoria.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }

    private void mostrarSucesso(String titulo, String mensagem) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensagem);
        alert.showAndWait();
    }
}