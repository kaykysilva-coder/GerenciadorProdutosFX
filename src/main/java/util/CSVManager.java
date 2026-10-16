package util;

import model.Categoria;
import model.Produto;

import java.io.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CSVManager {

    private static final String CAMINHO_ARQUIVO = "produtos.csv";

    public static void salvarProdutos(List<Produto> produtos) {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(CAMINHO_ARQUIVO))) {

            for (Produto p : produtos) {

                String linha = p.getCodigo() + ";"
                        + p.getNome() + ";"
                        + p.getDescricao() + ";"
                        + p.getDataFabricacao() + ";"
                        + p.getDataValidade() + ";"
                        + p.getPrecoCompra() + ";"
                        + p.getPrecoVenda() + ";"
                        + p.getQuantidadeEstoque() + ";"
                        + p.getCategoria().getId() + ";"
                        + p.getCategoria().getNome() + ";"
                        + p.getCategoria().getDescricao() + ";"
                        + p.getCategoria().getSetor();

                bw.write(linha);
                bw.newLine();
            }

        } catch (IOException e) {
            System.out.println("Erro ao salvar os produtos.");
        }
    }

    public static List<Produto> carregarProdutos() {

        List<Produto> produtos = new ArrayList<>();
        File arquivo = new File(CAMINHO_ARQUIVO);

        if (!arquivo.exists()) {
            return produtos;
        }

        try (BufferedReader br = new BufferedReader(
                new FileReader(arquivo))) {

            String linha;

            while ((linha = br.readLine()) != null) {

                String[] partes = linha.split(";", -1);

                if (partes.length >= 12) {

                    String codigo = partes[0];
                    String nome = partes[1];
                    String descricao = partes[2];

                    LocalDate dataFab = partes[3].isEmpty()
                            ? null : LocalDate.parse(partes[3]);

                    LocalDate dataVal = partes[4].isEmpty()
                            ? null : LocalDate.parse(partes[4]);

                    BigDecimal precoCompra = partes[5].isEmpty()
                            ? null : new BigDecimal(partes[5]);

                    BigDecimal precoVenda = partes[6].isEmpty()
                            ? null : new BigDecimal(partes[6]);

                    int quantidade = Integer.parseInt(partes[7]);

                    int catId = Integer.parseInt(partes[8]);
                    String catNome = partes[9];
                    String catDesc = partes[10];
                    String catSetor = partes[11];

                    Categoria categoria = new Categoria(
                            catId, catNome, catDesc, catSetor);

                    Produto produto = new Produto(
                            codigo, nome, descricao,
                            dataFab, dataVal, precoCompra,
                            precoVenda, quantidade, categoria);

                    produtos.add(produto);
                }
            }

        } catch (IOException e) {
            System.out.println("Erro ao carregar os produtos.");
        }

        return produtos;
    }
}