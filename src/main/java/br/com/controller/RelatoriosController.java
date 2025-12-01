package br.com.controller;

import br.com.dao.MovimentacaoDAO;
import br.com.dao.VeiculoDAO;
import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RelatoriosController {
    private MovimentacaoDAO movimentacaoDAO;
    private VeiculoDAO veiculoDAO;

    public RelatoriosController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
        this.veiculoDAO = new VeiculoDAO();
    }

    public List<Movimentacao> obterDespesasVeiculo(Long idVeiculo) throws IOException {
        return movimentacaoDAO.obterPorVeiculo(idVeiculo);
    }

    public double obterTotalDespesasVeiculo(Long idVeiculo) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterPorVeiculo(idVeiculo);
        return movimentacoes.stream()
                .mapToDouble(Movimentacao::getValor)
                .sum();
    }

    public double obterTotalDespesasMes(String mesAno) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        return movimentacoes.stream()
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
                    return mesAnoMovimentacao.equals(mesAno);
                })
                .mapToDouble(Movimentacao::getValor)
                .sum();
    }

    public double obterTotalCombustivelMes(String mesAno) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        return movimentacoes.stream()
                .filter(m -> m.getTipo() != null && m.getTipo().equalsIgnoreCase("Combustivel"))
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
                    return mesAnoMovimentacao.equals(mesAno);
                })
                .mapToDouble(Movimentacao::getValor)
                .sum();
    }

    public double obterTotalIPVAAno(String ano) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        return movimentacoes.stream()
                .filter(m -> m.getTipo() != null && m.getTipo().equalsIgnoreCase("IPVA"))
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String anoMovimentacao = dataParts[2];
                    return anoMovimentacao.equals(ano);
                })
                .mapToDouble(Movimentacao::getValor)
                .sum();
    }

    public List<Veiculo> obterVeiculosInativos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(v -> !v.getAtivo())
                .toList();
    }

    public double obterTotalMultasVeiculoAno(Long idVeiculo, String ano) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterPorVeiculo(idVeiculo);
        return movimentacoes.stream()
                .filter(m -> m.getTipo() != null && m.getTipo().equalsIgnoreCase("Multa"))
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String anoMovimentacao = dataParts[2];
                    return anoMovimentacao.equals(ano);
                })
                .mapToDouble(Movimentacao::getValor)
                .sum();
    }

    public List<Movimentacao> obterMultasVeiculoAno(Long idVeiculo, String ano) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterPorVeiculo(idVeiculo);
        return movimentacoes.stream()
                .filter(m -> m.getTipo() != null && m.getTipo().equalsIgnoreCase("Multa"))
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String anoMovimentacao = dataParts[2];
                    return anoMovimentacao.equals(ano);
                })
                .toList();
    }

    public Map<Long, Double> obterDespesasPorVeiculoMes(String mesAno) throws IOException {
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();
        Map<Long, Double> despesasPorVeiculo = new HashMap<>();

        for (Movimentacao m : movimentacoes) {
            String[] dataParts = m.getData().split("/");
            String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
            if (mesAnoMovimentacao.equals(mesAno)) {
                Long idVeiculo = m.getIdVeiculo();
                despesasPorVeiculo.put(idVeiculo,
                    despesasPorVeiculo.getOrDefault(idVeiculo, 0.0) + m.getValor());
            }
        }

        return despesasPorVeiculo;
    }

    public List<Movimentacao> obterTodasMovimentacoes() throws IOException {
        return movimentacaoDAO.obterTodos();
    }

    /**
     * Obtém todos os veículos
     */
    public List<Veiculo> obterTodosVeiculos() throws IOException {
        return veiculoDAO.obterTodos();
    }

    /**
     * Gera relatório da Matriz A - Quantidade de abastecimentos por Veículo/Mês
     */
    public String gerarRelatorioMatrizA() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        // Extrair meses das movimentações
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMeses(movimentacoes);

        // Gerar Matriz A
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoes, meses);

        // Preparar rótulos
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        return br.com.utils.MatrizRelatorios.formatarMatriz(
                matrizA,
                rotulosVeiculos,
                meses,
                "MATRIZ A - Quantidade de Abastecimentos por Veículo/Mês"
        );
    }

    /**
     * Gera relatório da Matriz B - Custo médio por Abastecimento/Marca
     */
    public String gerarRelatorioMatrizB() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        // Extrair meses e marcas
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMeses(movimentacoes);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        // Gerar Matriz B
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoes);

        return br.com.utils.MatrizRelatorios.formatarMatriz(
                matrizB,
                meses,
                marcas,
                "MATRIZ B - Custo Médio por Abastecimento/Marca (R$)"
        );
    }

    /**
     * Gera relatório da Matriz C - Gasto Total Estimado por Veículo/Marca
     */
    public String gerarRelatorioMatrizC() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        // Extrair meses e marcas
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMeses(movimentacoes);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        // Gerar Matrizes A e B
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoes, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoes);

        // Calcular Matriz C (produto A x B)
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        // Preparar rótulos
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        return br.com.utils.MatrizRelatorios.formatarMatriz(
                matrizC,
                rotulosVeiculos,
                marcas,
                "MATRIZ C - Gasto Total Estimado com Combustível por Veículo/Marca (R$)"
        );
    }

    /**
     * Gera relatório completo com todas as matrizes
     */
    public String gerarRelatorioMatrizCompleto() throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO COMPLETO - ANÁLISE MATRICIAL DE CUSTOS COM COMBUSTÍVEL\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        sb.append("DESCRIÇÃO:\n");
        sb.append("- Matriz A: Quantidade de abastecimentos por Veículo/Mês (m x n)\n");
        sb.append("- Matriz B: Custo médio por abastecimento/Marca (n x p)\n");
        sb.append("- Matriz C: Gasto Total Estimado = A × B (m x p)\n\n");

        sb.append(gerarRelatorioMatrizA()).append("\n\n");
        sb.append(gerarRelatorioMatrizB()).append("\n\n");
        sb.append(gerarRelatorioMatrizC()).append("\n\n");

        return sb.toString();
    }

    /**
     * Gera relatório da Matriz A com filtro de período
     */
    public String gerarRelatorioMatrizAComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ A - Quantidade de Abastecimentos por Veículo/Mês (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        // Validar se há veículos cadastrados
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        // Extrair meses do período
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);

        // Preparar rótulos
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        // Gerar Matriz A
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizA, rotulosVeiculos, meses, titulo);
    }

    /**
     * Gera relatório da Matriz B com filtro de período
     */
    public String gerarRelatorioMatrizBComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ B - Custo Médio por Abastecimento/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        // Validar se há veículos cadastrados
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        // Extrair meses e marcas
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        // Gerar Matriz B
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);


        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizB, meses, marcas, titulo);
    }

    /**
     * Gera relatório da Matriz C com filtro de período
     */
    public String gerarRelatorioMatrizCComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ C - Gasto Total Estimado com Combustível por Veículo/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        // Validar se há veículos cadastrados
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        // Extrair meses e marcas
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        // Preparar rótulos
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        // Gerar Matrizes A e B
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        // Calcular Matriz C (produto A x B)
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);


        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizC, rotulosVeiculos, marcas, titulo);
    }

    /**
     * Gera relatório completo com filtro de período
     */
    public String gerarRelatorioMatrizCompletoComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("  RELATÓRIO COMPLETO - ANÁLISE MATRICIAL (%02d/%d a %02d/%d)\n",
                mesInicial, anoInicial, mesFinal, anoFinal));
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        sb.append("DESCRIÇÃO:\n");
        sb.append("- Matriz A: Quantidade de abastecimentos por Veículo/Mês (m x n)\n");
        sb.append("- Matriz B: Custo médio por abastecimento/Marca (n x p)\n");
        sb.append("- Matriz C: Gasto Total Estimado = A × B (m x p)\n\n");

        sb.append(gerarRelatorioMatrizAComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal)).append("\n\n");
        sb.append(gerarRelatorioMatrizBComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal)).append("\n\n");
        sb.append(gerarRelatorioMatrizCComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal)).append("\n\n");

        return sb.toString();
    }

    // ========== MÉTODOS COM FILTRO DE VEÍCULO ==========

    /**
     * Gera relatório da Matriz A com filtro de período e veículo
     */
    public String gerarRelatorioMatrizAComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();

        // Aplicar filtro de veículo se fornecido
        if (idVeiculo != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(idVeiculo))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ A - Quantidade de Abastecimentos por Veículo/Mês (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        // Validar se há veículos cadastrados
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        // Extrair meses do período
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);

        // Preparar rótulos
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        // Gerar Matriz A
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizA, rotulosVeiculos, meses, titulo);
    }

    /**
     * Gera relatório da Matriz B com filtro de período e veículo
     */
    public String gerarRelatorioMatrizBComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();

        // Aplicar filtro de veículo se fornecido
        if (idVeiculo != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(idVeiculo))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ B - Custo Médio por Abastecimento/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        // Validar se há veículos cadastrados
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        // Extrair meses e marcas
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        // Gerar Matriz B
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizB, meses, marcas, titulo);
    }

    /**
     * Gera relatório da Matriz C com filtro de período e veículo
     */
    public String gerarRelatorioMatrizCComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();

        // Aplicar filtro de veículo se fornecido
        if (idVeiculo != null) {
            veiculos = veiculos.stream()
                    .filter(v -> v.getIdVeiculo().equals(idVeiculo))
                    .collect(java.util.stream.Collectors.toList());
        }

        List<Movimentacao> movimentacoes = movimentacaoDAO.obterTodos();

        String titulo = String.format("MATRIZ C - Gasto Total Estimado com Combustível por Veículo/Marca (%02d/%d a %02d/%d)",
                mesInicial, anoInicial, mesFinal, anoFinal);

        // Validar se há veículos cadastrados
        if (veiculos == null || veiculos.isEmpty()) {
            StringBuilder sb = new StringBuilder();
            sb.append("═══════════════════════════════════════════════════════════════\n");
            sb.append("  ").append(titulo).append("\n");
            sb.append("═══════════════════════════════════════════════════════════════\n\n");
            sb.append("Nenhum veículo cadastrado no sistema.\n");
            return sb.toString();
        }

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .collect(java.util.stream.Collectors.toList());

        // Extrair meses e marcas
        java.util.List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        java.util.List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        // Preparar rótulos
        java.util.List<String> rotulosVeiculos = veiculos.stream()
                .map(br.com.utils.MatrizRelatorios::formatarRotuloVeiculo)
                .collect(java.util.stream.Collectors.toList());

        // Gerar Matrizes A e B
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        // Calcular Matriz C (produto A x B)
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        return br.com.utils.MatrizRelatorios.formatarMatriz(matrizC, rotulosVeiculos, marcas, titulo);
    }

    /**
     * Gera relatório completo com filtro de período e veículo
     */
    public String gerarRelatorioMatrizCompletoComPeriodo(int mesInicial, int anoInicial, int mesFinal, int anoFinal, Long idVeiculo) throws IOException {
        StringBuilder sb = new StringBuilder();

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("  RELATÓRIO COMPLETO - ANÁLISE MATRICIAL (%02d/%d a %02d/%d)\n",
                mesInicial, anoInicial, mesFinal, anoFinal));
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        sb.append("DESCRIÇÃO:\n");
        sb.append("- Matriz A: Quantidade de abastecimentos por Veículo/Mês (m x n)\n");
        sb.append("- Matriz B: Custo médio por abastecimento/Marca (n x p)\n");
        sb.append("- Matriz C: Gasto Total Estimado = A × B (m x p)\n\n");

        sb.append(gerarRelatorioMatrizAComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal, idVeiculo)).append("\n\n");
        sb.append(gerarRelatorioMatrizBComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal, idVeiculo)).append("\n\n");
        sb.append(gerarRelatorioMatrizCComPeriodo(mesInicial, anoInicial, mesFinal, anoFinal, idVeiculo)).append("\n\n");

        return sb.toString();
    }
}
