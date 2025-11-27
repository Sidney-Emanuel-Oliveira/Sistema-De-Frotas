package br.com.utils;

import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.model.TipoDespesa;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Classe para cálculos de matrizes de relatórios conforme especificação:
 * - Matriz A: Quantidade de abastecimentos por Veículo/Marca (m x n)
 * - Matriz B: Custo médio por abastecimento/marca (n x p)
 * - Matriz C: Produto A x B = Gasto Total Estimado com Combustível de cada Veículo por Marca
 */
public class MatrizRelatorios {

    /**
     * Gera a Matriz A - Quantidade de abastecimentos por Veículo/Marca
     * m = número de veículos
     * n = número de meses
     *
     * @param veiculos Lista de veículos
     * @param movimentacoes Lista de movimentações
     * @param meses Lista de meses (formato "MM/yyyy")
     * @return Matriz A [m x n]
     */
    public static double[][] gerarMatrizA(List<Veiculo> veiculos, List<Movimentacao> movimentacoes, List<String> meses) {
        int m = veiculos.size();
        int n = meses.size();
        double[][] matrizA = new double[m][n];

        // Filtrar apenas movimentações de combustível
        List<Movimentacao> combustiveis = movimentacoes.stream()
                .filter(mov -> mov.getTipo() != null &&
                        (mov.getTipo().equalsIgnoreCase("Combustível") ||
                         mov.getTipo().equalsIgnoreCase("Combustivel")))
                .toList();

        for (int i = 0; i < m; i++) {
            Veiculo veiculo = veiculos.get(i);
            for (int j = 0; j < n; j++) {
                String mes = meses.get(j);

                // Contar quantas movimentações de combustível o veículo teve nesse mês
                long quantidade = combustiveis.stream()
                        .filter(mov -> mov.getIdVeiculo().equals(veiculo.getIdVeiculo()))
                        .filter(mov -> {
                            String[] dataParts = mov.getData().split("/");
                            String mesAnoMov = dataParts[1] + "/" + dataParts[2];
                            return mesAnoMov.equals(mes);
                        })
                        .count();

                matrizA[i][j] = quantidade;
            }
        }

        return matrizA;
    }

    /**
     * Gera a Matriz B - Custo médio por abastecimento/marca
     * n = número de meses
     * p = número de marcas de veículos
     *
     * @param meses Lista de meses (formato "MM/yyyy")
     * @param marcas Lista de marcas de veículos
     * @param veiculos Lista de veículos
     * @param movimentacoes Lista de movimentações
     * @return Matriz B [n x p]
     */
    public static double[][] gerarMatrizB(List<String> meses, List<String> marcas, List<Veiculo> veiculos, List<Movimentacao> movimentacoes) {
        int n = meses.size();
        int p = marcas.size();
        double[][] matrizB = new double[n][p];

        // Filtrar apenas movimentações de combustível
        List<Movimentacao> combustiveis = movimentacoes.stream()
                .filter(mov -> mov.getTipo() != null &&
                        (mov.getTipo().equalsIgnoreCase("Combustível") ||
                         mov.getTipo().equalsIgnoreCase("Combustivel")))
                .toList();

        // Criar mapa de veículo -> marca
        Map<Long, String> veiculoMarcaMap = new HashMap<>();
        for (Veiculo v : veiculos) {
            veiculoMarcaMap.put(v.getIdVeiculo(), v.getMarca());
        }

        for (int i = 0; i < n; i++) {
            String mes = meses.get(i);
            for (int j = 0; j < p; j++) {
                String marca = marcas.get(j);

                // Filtrar movimentações desse mês e dessa marca
                List<Movimentacao> movsMesMarca = combustiveis.stream()
                        .filter(mov -> {
                            String[] dataParts = mov.getData().split("/");
                            String mesAnoMov = dataParts[1] + "/" + dataParts[2];
                            return mesAnoMov.equals(mes);
                        })
                        .filter(mov -> {
                            String marcaVeiculo = veiculoMarcaMap.get(mov.getIdVeiculo());
                            return marcaVeiculo != null && marcaVeiculo.equals(marca);
                        })
                        .collect(Collectors.toList());

                // Calcular custo médio
                if (!movsMesMarca.isEmpty()) {
                    double soma = movsMesMarca.stream().mapToDouble(Movimentacao::getValor).sum();
                    double custoMedio = soma / movsMesMarca.size();
                    matrizB[i][j] = custoMedio;
                } else {
                    matrizB[i][j] = 0.0;
                }
            }
        }

        return matrizB;
    }

    /**
     * Gera a Matriz C - Produto A x B
     * Gasto Total Estimado com Combustível de cada Veículo discriminado pela Marca do Veículo
     * C = A × B
     *
     * @param matrizA Matriz A [m x n]
     * @param matrizB Matriz B [n x p]
     * @return Matriz C [m x p]
     */
    public static double[][] gerarMatrizC(double[][] matrizA, double[][] matrizB) {
        int m = matrizA.length;
        int n = matrizA[0].length;
        int p = matrizB[0].length;

        double[][] matrizC = new double[m][p];

        // Multiplicação de matrizes: C[i][j] = Σ(A[i][k] * B[k][j])
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < p; j++) {
                double soma = 0;
                for (int k = 0; k < n; k++) {
                    soma += matrizA[i][k] * matrizB[k][j];
                }
                matrizC[i][j] = soma;
            }
        }

        return matrizC;
    }

    /**
     * Calcula o total geral de uma matriz (soma de todos os elementos)
     *
     * @param matriz Matriz a ser somada
     * @return Total geral
     */
    public static double calcularTotalGeral(double[][] matriz) {
        double total = 0;
        for (double[] linha : matriz) {
            for (double valor : linha) {
                total += valor;
            }
        }
        return total;
    }

    /**
     * Formata uma matriz para exibição em String
     *
     * @param matriz Matriz a ser formatada
     * @param rotulos Rótulos das linhas
     * @param colunas Rótulos das colunas
     * @param titulo Título da matriz
     * @return String formatada da matriz
     */
    public static String formatarMatriz(double[][] matriz, List<String> rotulos, List<String> colunas, String titulo) {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  ").append(titulo).append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        // Cabeçalho com colunas
        sb.append(String.format("%-20s", ""));
        for (String coluna : colunas) {
            sb.append(String.format("│ %-12s ", coluna.length() > 12 ? coluna.substring(0, 12) : coluna));
        }
        sb.append("\n");

        sb.append("─".repeat(20));
        for (int i = 0; i < colunas.size(); i++) {
            sb.append("┼──────────────");
        }
        sb.append("\n");

        // Linhas da matriz
        for (int i = 0; i < matriz.length; i++) {
            String rotulo = rotulos.get(i);
            sb.append(String.format("%-20s", rotulo.length() > 20 ? rotulo.substring(0, 17) + "..." : rotulo));

            for (int j = 0; j < matriz[i].length; j++) {
                sb.append(String.format("│ %12.2f ", matriz[i][j]));
            }
            sb.append("\n");
        }

        sb.append("─".repeat(20));
        for (int i = 0; i < colunas.size(); i++) {
            sb.append("┴──────────────");
        }
        sb.append("\n");

        // Total geral
        double total = calcularTotalGeral(matriz);
        sb.append(String.format("\nTOTAL GERAL: R$ %.2f\n", total));

        return sb.toString();
    }

    /**
     * Extrai lista de marcas únicas dos veículos
     *
     * @param veiculos Lista de veículos
     * @return Lista de marcas únicas
     */
    public static List<String> extrairMarcas(List<Veiculo> veiculos) {
        return veiculos.stream()
                .map(Veiculo::getMarca)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Gera lista de meses no formato MM/yyyy a partir das movimentações
     *
     * @param movimentacoes Lista de movimentações
     * @return Lista de meses únicos ordenados
     */
    public static List<String> extrairMeses(List<Movimentacao> movimentacoes) {
        return movimentacoes.stream()
                .map(mov -> {
                    String[] dataParts = mov.getData().split("/");
                    return dataParts[1] + "/" + dataParts[2];
                })
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Formata rótulo do veículo para exibição
     *
     * @param veiculo Veículo
     * @return String formatada
     */
    public static String formatarRotuloVeiculo(Veiculo veiculo) {
        return veiculo.getPlaca() + " - " + veiculo.getMarca() + " " + veiculo.getModelo();
    }

    /**
     * Filtra meses por período (mês/ano inicial e final)
     *
     * @param movimentacoes Lista de movimentações
     * @param mesInicial Mês inicial (1-12)
     * @param anoInicial Ano inicial
     * @param mesFinal Mês final (1-12)
     * @param anoFinal Ano final
     * @return Lista de meses filtrados no formato MM/yyyy
     */
    public static List<String> extrairMesesPorPeriodo(List<Movimentacao> movimentacoes,
                                                       int mesInicial, int anoInicial,
                                                       int mesFinal, int anoFinal) {
        return movimentacoes.stream()
                .map(mov -> {
                    String[] dataParts = mov.getData().split("/");
                    return dataParts[1] + "/" + dataParts[2];
                })
                .distinct()
                .filter(mesAno -> {
                    String[] parts = mesAno.split("/");
                    int mes = Integer.parseInt(parts[0]);
                    int ano = Integer.parseInt(parts[1]);

                    // Converter para número comparável (YYYYMM)
                    int dataAtual = ano * 100 + mes;
                    int dataInicial = anoInicial * 100 + mesInicial;
                    int dataFinal = anoFinal * 100 + mesFinal;

                    return dataAtual >= dataInicial && dataAtual <= dataFinal;
                })
                .sorted((a, b) -> {
                    // Ordenar por ano e mês
                    String[] partsA = a.split("/");
                    String[] partsB = b.split("/");
                    int anoA = Integer.parseInt(partsA[1]);
                    int anoB = Integer.parseInt(partsB[1]);
                    int mesA = Integer.parseInt(partsA[0]);
                    int mesB = Integer.parseInt(partsB[0]);

                    if (anoA != anoB) {
                        return Integer.compare(anoA, anoB);
                    }
                    return Integer.compare(mesA, mesB);
                })
                .collect(Collectors.toList());
    }

    /**
     * Verifica se uma movimentação está dentro do período especificado
     *
     * @param movimentacao Movimentação a verificar
     * @param mesInicial Mês inicial (1-12)
     * @param anoInicial Ano inicial
     * @param mesFinal Mês final (1-12)
     * @param anoFinal Ano final
     * @return true se está no período, false caso contrário
     */
    public static boolean estaNoPeriodo(Movimentacao movimentacao,
                                        int mesInicial, int anoInicial,
                                        int mesFinal, int anoFinal) {
        String[] dataParts = movimentacao.getData().split("/");
        int mes = Integer.parseInt(dataParts[1]);
        int ano = Integer.parseInt(dataParts[2]);

        int dataAtual = ano * 100 + mes;
        int dataInicial = anoInicial * 100 + mesInicial;
        int dataFinal = anoFinal * 100 + mesFinal;

        return dataAtual >= dataInicial && dataAtual <= dataFinal;
    }
}

