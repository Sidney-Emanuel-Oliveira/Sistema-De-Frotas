package br.com.view;

import br.com.controller.RelatoriosController;
import br.com.controller.VeiculoController;
import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.model.VeiculoComboItem;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.ui.ModernInnerTabbedPane;
import br.com.ui.ModernComboBox;
import br.com.utils.GeradorCSV;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * Tela moderna de relatórios com design elegante
 */
public class TelaRelatorios extends JPanel {
    private RelatoriosController relatoriosController;
    private VeiculoController veiculoController;
    private JComboBox<Integer> cmbAno;
    private JComboBox<Integer> cmbMes;
    private JComboBox<Integer> cmbAnoFinal;
    private JComboBox<Integer> cmbMesFinal;
    private JComboBox<VeiculoComboItem> cmbVeiculo;
    private JTextArea areaRelatorio;
    private JTable tabelaRelatorio;
    private DefaultTableModel modeloTabela;
    private JLabel lblTitulo;

    // Controle de relatório gerado
    private boolean relatorioGerado = false;
    private String tipoRelatorioAtual = "";

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 12);

    public TelaRelatorios() {
        relatoriosController = new RelatoriosController();
        veiculoController = new VeiculoController();

        setLayout(new BorderLayout());
        setBackground(ModernColors.BG_PRIMARY);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Cabeçalho
        JPanel headerPanel = criarPainelCabecalho();
        add(headerPanel, BorderLayout.NORTH);

        // Painel de seleção e resultado
        JPanel mainPanel = criarPainelPrincipal();
        add(mainPanel, BorderLayout.CENTER);
    }

    private JPanel criarPainelCabecalho() {
        RoundedPanel panel = new RoundedPanel(8, ModernColors.WHITE);
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(0, 70));

        lblTitulo = new JLabel("Relatórios");
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setForeground(ModernColors.DARK_GRAY);

        panel.add(lblTitulo, BorderLayout.WEST);
        return panel;
    }

    private JPanel criarPainelPrincipal() {
        JPanel panel = new JPanel(new BorderLayout(20, 20));
        panel.setOpaque(false);

        // Painel de filtros
        JPanel filtrosPanel = criarPainelFiltros();
        panel.add(filtrosPanel, BorderLayout.NORTH);

        // Painel de relatórios (abas)
        JPanel relatoriosPanel = criarPainelRelatorios();
        panel.add(relatoriosPanel, BorderLayout.CENTER);

        // Painel de botões de exportação
        JPanel botoesPanel = criarPainelBotoes();
        panel.add(botoesPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel criarPainelFiltros() {
        RoundedPanel panel = new RoundedPanel(8, ModernColors.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(0, 200));

        // Título da seção
        JLabel titleLabel = new JLabel("Filtros e Opções de Relatório");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(ModernColors.DARK_GRAY);

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        panel.add(titlePanel, BorderLayout.NORTH);

        // Painéis de opções
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);

        // Botões de tipos de relatório (inclui matrizes)
        JPanel botoesRelatorios = criarPainelBotoesRelatorios();
        botoesRelatorios.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(botoesRelatorios);
        contentPanel.add(Box.createVerticalStrut(10));

        // Seletores
        JPanel seletores = criarPainelSeletores();
        seletores.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(seletores);

        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelBotoesRelatorios() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Primeira linha - Relatórios padrão
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel1.setOpaque(false);

        String[] opcoes = {
            "Despesas por Veículo",
            "Despesas por Mês",
            "Combustível por Mês",
            "IPVA por Ano",
            "Veículos Inativos",
            "Multas por Veículo"
        };

        for (String opcao : opcoes) {
            ModernButton btn = new ModernButton(opcao, ModernColors.PRIMARY_BLUE);
            btn.setPreferredSize(new Dimension(150, 35));

            btn.addActionListener(e -> {
                try {
                    switch (opcao) {
                        case "Despesas por Veículo" -> gerarRelatorioDespesasVeiculo();
                        case "Despesas por Mês" -> gerarRelatorioDespesasMes();
                        case "Combustível por Mês" -> gerarRelatorioCombustivelMes();
                        case "IPVA por Ano" -> gerarRelatorioIPVAAno();
                        case "Veículos Inativos" -> gerarRelatorioVeiculosInativos();
                        case "Multas por Veículo" -> gerarRelatorioMultas();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel1.add(btn);
        }

        // Segunda linha - Relatórios de Matrizes
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panel2.setOpaque(false);

        // Separador visual
        JLabel lblMatrizes = new JLabel("Análise Matricial:");
        lblMatrizes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMatrizes.setForeground(ModernColors.PRIMARY_BLUE);
        panel2.add(lblMatrizes);

        String[] opcoesMatrizes = {
            "Matriz A (Abastecimentos)",
            "Matriz B (Custo Médio)",
            "Matriz C (Gasto Total)",
            "Relatório Completo"
        };

        Color corMatriz = new Color(0, 150, 136); // Cor diferente para destacar

        for (String opcao : opcoesMatrizes) {
            ModernButton btn = new ModernButton(opcao, corMatriz);
            btn.setPreferredSize(new Dimension(170, 35));

            btn.addActionListener(e -> {
                try {
                    switch (opcao) {
                        case "Matriz A (Abastecimentos)" -> gerarRelatorioMatrizA();
                        case "Matriz B (Custo Médio)" -> gerarRelatorioMatrizB();
                        case "Matriz C (Gasto Total)" -> gerarRelatorioMatrizC();
                        case "Relatório Completo" -> gerarRelatorioMatrizCompleto();
                    }
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(this, "Erro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
                }
            });

            panel2.add(btn);
        }

        mainPanel.add(panel1);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panel2);

        return mainPanel;
    }

    private JPanel criarPainelSeletores() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setOpaque(false);

        // Primeira linha - Seletores básicos
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel1.setOpaque(false);

        // Ano
        JLabel lblAno = new JLabel("Ano:");
        lblAno.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAno.setForeground(ModernColors.DARK_GRAY);
        cmbAno = new ModernComboBox<>();
        int anoAtual = LocalDate.now().getYear();
        for (int i = anoAtual - 10; i <= anoAtual; i++) {
            cmbAno.addItem(i);
        }
        cmbAno.setSelectedItem(anoAtual);
        cmbAno.setPreferredSize(new Dimension(100, 32));
        panel1.add(lblAno);
        panel1.add(cmbAno);

        // Mês
        JLabel lblMes = new JLabel("Mês:");
        lblMes.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMes.setForeground(ModernColors.DARK_GRAY);
        cmbMes = new ModernComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cmbMes.addItem(i);
        }
        cmbMes.setSelectedItem(LocalDate.now().getMonthValue());
        cmbMes.setPreferredSize(new Dimension(100, 32));
        panel1.add(lblMes);
        panel1.add(cmbMes);

        // Veículo
        JLabel lblVeiculo = new JLabel("Veículo:");
        lblVeiculo.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblVeiculo.setForeground(ModernColors.DARK_GRAY);
        cmbVeiculo = new ModernComboBox<>();
        carregarVeiculos();
        cmbVeiculo.setPreferredSize(new Dimension(220, 32));
        panel1.add(lblVeiculo);
        panel1.add(cmbVeiculo);

        // Segunda linha - Período para Matrizes
        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 5));
        panel2.setOpaque(false);

        JLabel lblPeriodoMatriz = new JLabel("Período (Matrizes):");
        lblPeriodoMatriz.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblPeriodoMatriz.setForeground(new Color(0, 150, 136));
        panel2.add(lblPeriodoMatriz);

        // Mês/Ano Final
        JLabel lblAte = new JLabel("até");
        lblAte.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        lblAte.setForeground(ModernColors.DARK_GRAY);

        JLabel lblMesFinal = new JLabel("Mês:");
        lblMesFinal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblMesFinal.setForeground(ModernColors.DARK_GRAY);
        cmbMesFinal = new ModernComboBox<>();
        for (int i = 1; i <= 12; i++) {
            cmbMesFinal.addItem(i);
        }
        cmbMesFinal.setSelectedItem(LocalDate.now().getMonthValue());
        cmbMesFinal.setPreferredSize(new Dimension(80, 32));

        JLabel lblAnoFinal = new JLabel("Ano:");
        lblAnoFinal.setFont(new Font("Segoe UI", Font.BOLD, 11));
        lblAnoFinal.setForeground(ModernColors.DARK_GRAY);
        cmbAnoFinal = new ModernComboBox<>();
        for (int i = anoAtual - 10; i <= anoAtual; i++) {
            cmbAnoFinal.addItem(i);
        }
        cmbAnoFinal.setSelectedItem(anoAtual);
        cmbAnoFinal.setPreferredSize(new Dimension(80, 32));

        panel2.add(lblMesFinal);
        panel2.add(cmbMesFinal);
        panel2.add(lblAnoFinal);
        panel2.add(cmbAnoFinal);

        mainPanel.add(panel1);
        mainPanel.add(Box.createVerticalStrut(5));
        mainPanel.add(panel2);

        return mainPanel;
    }

    private JPanel criarPainelRelatorios() {
        RoundedPanel panel = new RoundedPanel(8, ModernColors.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTabbedPane abas = new ModernInnerTabbedPane();

        // Aba de Texto
        areaRelatorio = new JTextArea();
        areaRelatorio.setEditable(false);
        areaRelatorio.setFont(new Font("Consolas", Font.PLAIN, 11));
        areaRelatorio.setBackground(ModernColors.VERY_LIGHT_GRAY);
        areaRelatorio.setForeground(ModernColors.DARK_GRAY);
        areaRelatorio.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollTexto = new JScrollPane(areaRelatorio);
        abas.addTab("Texto", scrollTexto);

        // Aba de Tabela
        modeloTabela = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabelaRelatorio = new JTable(modeloTabela);
        tabelaRelatorio.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        tabelaRelatorio.setRowHeight(24);
        tabelaRelatorio.setBackground(ModernColors.WHITE);
        tabelaRelatorio.setGridColor(ModernColors.BORDER_GRAY);
        tabelaRelatorio.setSelectionBackground(new Color(200, 220, 255));

        JTableHeader header = tabelaRelatorio.getTableHeader();
        header.setBackground(ModernColors.BG_SECONDARY);
        header.setForeground(ModernColors.DARK_GRAY);
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));

        JScrollPane scrollTabela = new JScrollPane(tabelaRelatorio);
        abas.addTab("Tabela", scrollTabela);

        panel.add(abas, BorderLayout.CENTER);

        return panel;
    }

    private JPanel criarPainelBotoes() {
        RoundedPanel panel = new RoundedPanel(8, ModernColors.WHITE);
        panel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.setPreferredSize(new Dimension(0, 50));

        ModernButton btnExportar = new ModernButton("Exportar CSV", ModernColors.PRIMARY_BLUE);
        btnExportar.setPreferredSize(new Dimension(130, 35));
        btnExportar.addActionListener(e -> exportarCSV());

        ModernButton btnLimpar = new ModernButton("Limpar", ModernColors.TEXT_GRAY);
        btnLimpar.setPreferredSize(new Dimension(100, 35));
        btnLimpar.addActionListener(e -> limparResultado());

        panel.add(btnExportar);
        panel.add(btnLimpar);

        return panel;
    }

    private void carregarVeiculos() {
        try {
            cmbVeiculo.removeAllItems();
            cmbVeiculo.addItem(new VeiculoComboItem()); // Adiciona "Todos"
            List<Veiculo> veiculos = veiculoController.obterTodosVeiculos();
            for (Veiculo v : veiculos) {
                cmbVeiculo.addItem(new VeiculoComboItem(v));
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    // ... Métodos de geração de relatórios
    private void gerarRelatorioDespesasVeiculo() throws IOException {
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        if (itemSelecionado == null) {
            JOptionPane.showMessageDialog(this, "Selecione um veículo!", "Erro", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (itemSelecionado.isTodos()) {
            gerarRelatorioDespesasTodosVeiculos();
            return;
        }

        Veiculo veiculo = itemSelecionado.getVeiculo();
        List<Movimentacao> movimentacoes = relatoriosController.obterDespesasVeiculo(veiculo.getIdVeiculo());
        double total = relatoriosController.obterTotalDespesasVeiculo(veiculo.getIdVeiculo());

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE DESPESAS DO VEÍCULO\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");
        sb.append("Veículo: ").append(veiculo.getMarca()).append(" ").append(veiculo.getModelo()).append("\n");
        sb.append("Placa: ").append(veiculo.getPlaca()).append("\n");
        sb.append("Ano: ").append(veiculo.getFabricateYear()).append("\n");
        sb.append("Status: ").append(veiculo.getAtivo() ? "Ativo" : "Inativo").append("\n\n");

        sb.append("Data       │ Descrição            │ Tipo           │ Valor\n");
        sb.append("───────────┼──────────────────────┼────────────────┼─────────────\n");

        for (Movimentacao m : movimentacoes) {
            sb.append(String.format("%-10s │ %-20s │ %-14s │ R$ %8.2f\n",
                    m.getData(), m.getDescricao(), m.getTipo(), m.getValor()));
        }

        sb.append("───────────┴──────────────────────┴────────────────┴─────────────\n");
        sb.append(String.format("TOTAL: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaDespesasVeiculo(movimentacoes, total);

        // Marcar que um relatório foi gerado
        relatorioGerado = true;
        tipoRelatorioAtual = "Despesas por Veículo";
    }

    private void gerarRelatorioDespesasTodosVeiculos() throws IOException {
        List<Veiculo> veiculos = veiculoController.obterTodosVeiculos();
        List<Movimentacao> todasMovimentacoes = relatoriosController.obterTodasMovimentacoes();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE DESPESAS - TODOS OS VEÍCULOS\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        double totalGeral = 0.0;

        modeloTabela.setColumnIdentifiers(new String[]{"Placa", "Veículo", "Data", "Descrição", "Tipo", "Valor"});
        modeloTabela.setRowCount(0);

        for (Veiculo veiculo : veiculos) {
            List<Movimentacao> movimentacoesVeiculo = todasMovimentacoes.stream()
                    .filter(m -> m.getIdVeiculo().equals(veiculo.getIdVeiculo()))
                    .toList();

            if (!movimentacoesVeiculo.isEmpty()) {
                double totalVeiculo = movimentacoesVeiculo.stream()
                        .mapToDouble(Movimentacao::getValor)
                        .sum();

                sb.append("┌─────────────────────────────────────────────────────────────────┐\n");
                sb.append(String.format("│ Veículo: %-20s │ Placa: %-10s          │\n",
                        veiculo.getMarca() + " " + veiculo.getModelo(), veiculo.getPlaca()));
                sb.append("└─────────────────────────────────────────────────────────────────┘\n");
                sb.append("Data       │ Descrição            │ Tipo           │ Valor\n");
                sb.append("───────────┼──────────────────────┼────────────────┼─────────────\n");

                for (Movimentacao m : movimentacoesVeiculo) {
                    sb.append(String.format("%-10s │ %-20s │ %-14s │ R$ %8.2f\n",
                            m.getData(), m.getDescricao(), m.getTipo(), m.getValor()));

                    modeloTabela.addRow(new Object[]{
                            veiculo.getPlaca(),
                            veiculo.getMarca() + " " + veiculo.getModelo(),
                            m.getData(),
                            m.getDescricao(),
                            m.getTipo(),
                            String.format("R$ %.2f", m.getValor())
                    });
                }

                sb.append("                                      SUBTOTAL │ R$ ").append(String.format("%8.2f", totalVeiculo)).append("\n\n");
                totalGeral += totalVeiculo;
            }
        }

        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append(String.format("TOTAL GERAL: R$ %.2f\n", totalGeral));
        sb.append("═══════════════════════════════════════════════════════════════════════\n");

        areaRelatorio.setText(sb.toString());
        modeloTabela.addRow(new Object[]{"", "", "", "", "TOTAL GERAL", String.format("R$ %.2f", totalGeral)});
        tabelaRelatorio.setModel(modeloTabela);

        // Marcar que um relatório foi gerado
        relatorioGerado = true;
        tipoRelatorioAtual = "Despesas - Todos os Veículos";
    }

    private void preencherTabelaDespesasVeiculo(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Descrição", "Tipo", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            modeloTabela.addRow(new Object[]{
                    m.getData(),
                    m.getDescricao(),
                    m.getTipo(),
                    String.format("R$ %.2f", m.getValor())
            });
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioDespesasMes() throws IOException {
        int mes = (int) cmbMes.getSelectedItem();
        int ano = (int) cmbAno.getSelectedItem();
        String mesAno = String.format("%02d/%d", mes, ano);
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> movimentacoesMes = movimentacoes.stream()
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
                    boolean mesCorreto = mesAnoMovimentacao.equals(mesAno);

                    // Filtrar por veículo se não for "Todos"
                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return mesCorreto && m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return mesCorreto;
                })
                .toList();

        double total = movimentacoesMes.stream().mapToDouble(Movimentacao::getValor).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE DESPESAS - ").append(mesAno);
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════\n\n");

        sb.append("Data       │ Veículo          │ Descrição        │ Tipo      │ Valor\n");
        sb.append("───────────┼──────────────────┼──────────────────┼───────────┼─────────────\n");

        for (Movimentacao m : movimentacoesMes) {
            Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
            String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
            // Limitar tamanho dos campos para não sair da linha
            String veiculoFormatado = nomeVeiculo.length() > 16 ? nomeVeiculo.substring(0, 13) + "..." : nomeVeiculo;
            String descricaoFormatada = m.getDescricao().length() > 16 ? m.getDescricao().substring(0, 13) + "..." : m.getDescricao();
            String tipoFormatado = m.getTipo().length() > 9 ? m.getTipo().substring(0, 6) + "..." : m.getTipo();

            sb.append(String.format("%-10s │ %-16s │ %-16s │ %-9s │ R$ %8.2f\n",
                    m.getData(), veiculoFormatado, descricaoFormatada, tipoFormatado, m.getValor()));
        }

        sb.append("───────────┴──────────────────┴──────────────────┴───────────┴─────────────\n");
        sb.append(String.format("TOTAL DO MÊS: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaDespesasMes(movimentacoesMes, total);

        // Marcar que um relatório foi gerado
        relatorioGerado = true;
        tipoRelatorioAtual = "Despesas por Mês";
    }

    private void preencherTabelaDespesasMes(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Tipo", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        m.getTipo(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioCombustivelMes() throws IOException {
        int mes = (int) cmbMes.getSelectedItem();
        int ano = (int) cmbAno.getSelectedItem();
        String mesAno = String.format("%02d/%d", mes, ano);
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> combustivelisMes = movimentacoes.stream()
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    String mesAnoMovimentacao = dataParts[1] + "/" + dataParts[2];
                    // ID 1 = Combustivel
                    boolean mesCorreto = mesAnoMovimentacao.equals(mesAno) && m.getIdTipoDespesa().equals(1L);

                    // Filtrar por veículo se não for "Todos"
                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return mesCorreto && m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return mesCorreto;
                })
                .toList();

        double total = combustivelisMes.stream().mapToDouble(Movimentacao::getValor).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE COMBUSTÍVEL - ").append(mesAno);
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        sb.append("Data       │ Veículo              │ Descrição            │ Valor\n");
        sb.append("───────────┼──────────────────────┼──────────────────────┼─────────────\n");

        for (Movimentacao m : combustivelisMes) {
            Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
            String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
            sb.append(String.format("%-10s │ %-20s │ %-20s │ R$ %8.2f\n",
                    m.getData(), nomeVeiculo, m.getDescricao(), m.getValor()));
        }

        sb.append("───────────┴──────────────────────┴──────────────────────┴─────────────\n");
        sb.append(String.format("TOTAL DE COMBUSTÍVEL: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaCombustivelMes(combustivelisMes, total);

        // Marcar que um relatório foi gerado
        relatorioGerado = true;
        tipoRelatorioAtual = "Combustível por Mês";
    }

    private void preencherTabelaCombustivelMes(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioIPVAAno() throws IOException {
        int anoSelecionado = (int) cmbAno.getSelectedItem();
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();

        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> ipvasAno = movimentacoes.stream()
                .filter(m -> {
                    String[] dataParts = m.getData().split("/");
                    int anoMovimentacao = Integer.parseInt(dataParts[2]);
                    // ID 2 = IPVA
                    boolean anoCorreto = anoMovimentacao == anoSelecionado && m.getIdTipoDespesa().equals(2L);

                    // Filtrar por veículo se não for "Todos"
                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return anoCorreto && m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return anoCorreto;
                })
                .toList();

        double total = ipvasAno.stream().mapToDouble(Movimentacao::getValor).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE IPVA - ").append(anoSelecionado);
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════\n\n");

        sb.append("Data       │ Veículo              │ Descrição            │ Valor\n");
        sb.append("───────────┼──────────────────────┼──────────────────────┼─────────────\n");

        for (Movimentacao m : ipvasAno) {
            Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
            String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
            sb.append(String.format("%-10s │ %-20s │ %-20s │ R$ %8.2f\n",
                    m.getData(), nomeVeiculo, m.getDescricao(), m.getValor()));
        }

        sb.append("───────────┴──────────────────────┴──────────────────────┴─────────────\n");
        sb.append(String.format("TOTAL DE IPVA: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaIPVAAno(ipvasAno, total);

        // Marcar que um relatório foi gerado
        relatorioGerado = true;
        tipoRelatorioAtual = "IPVA por Ano";
    }

    private void preencherTabelaIPVAAno(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    // Relatório de veículos inativos
    private void gerarRelatorioVeiculosInativos() throws IOException {
        List<Veiculo> veiculosInativos = veiculoController.obterTodosVeiculos().stream()
                .filter(v -> !v.getAtivo())
                .toList();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE VEÍCULOS INATIVOS\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n\n");

        if (veiculosInativos.isEmpty()) {
            sb.append("Nenhum veículo inativo registrado.\n");
        } else {
            sb.append("Placa       │ Marca                │ Modelo               │ Ano   │ Tipo\n");
            sb.append("────────────┼──────────────────────┼──────────────────────┼───────┼──────────────\n");

            for (Veiculo v : veiculosInativos) {
                sb.append(String.format("%-11s │ %-20s │ %-20s │ %-5s │ %s\n",
                        v.getPlaca(), v.getMarca(), v.getModelo(), v.getFabricateYear(), v.getTipo()));
            }

            sb.append("────────────┴──────────────────────┴──────────────────────┴───────┴──────────────\n");
            sb.append(String.format("TOTAL: %d veículo(s) inativo(s)\n", veiculosInativos.size()));
        }

        areaRelatorio.setText(sb.toString());
        preencherTabelaVeiculosInativos(veiculosInativos);

        // Marcar que um relatório foi gerado
        relatorioGerado = true;
        tipoRelatorioAtual = "Veículos Inativos";
    }

    private void preencherTabelaVeiculosInativos(List<Veiculo> veiculos) {
        modeloTabela.setColumnIdentifiers(new String[]{"Placa", "Marca", "Modelo", "Ano", "Tipo"});
        modeloTabela.setRowCount(0);

        for (Veiculo v : veiculos) {
            modeloTabela.addRow(new Object[]{
                    v.getPlaca(),
                    v.getMarca(),
                    v.getModelo(),
                    v.getFabricateYear(),
                    v.getTipo()
            });
        }

        modeloTabela.addRow(new Object[]{"", "", "", "", "TOTAL: " + veiculos.size() + " veículo(s)"});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void gerarRelatorioMultas() throws IOException {
        VeiculoComboItem itemSelecionado = (VeiculoComboItem) cmbVeiculo.getSelectedItem();
        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();
        List<Movimentacao> multas = movimentacoes.stream()
                .filter(m -> {
                    // ID 6 = Multas
                    boolean isMulta = m.getIdTipoDespesa().equals(6L);

                    // Filtrar por veículo se não for "Todos"
                    if (itemSelecionado != null && !itemSelecionado.isTodos()) {
                        Veiculo veiculoSelecionado = itemSelecionado.getVeiculo();
                        return isMulta && m.getIdVeiculo().equals(veiculoSelecionado.getIdVeiculo());
                    }
                    return isMulta;
                })
                .toList();

        double total = multas.stream().mapToDouble(Movimentacao::getValor).sum();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n");
        sb.append("  RELATÓRIO DE MULTAS");
        if (itemSelecionado != null && !itemSelecionado.isTodos()) {
            Veiculo v = itemSelecionado.getVeiculo();
            sb.append(" - ").append(v.getPlaca());
        }
        sb.append("\n");
        sb.append("═══════════════════════════════════════════════════════════════════════════════\n\n");

        sb.append("Data       │ Veículo              │ Descrição            │ Valor\n");
        sb.append("───────────┼──────────────────────┼──────────────────────┼─────────────\n");

        for (Movimentacao m : multas) {
            Veiculo veiculo = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
            String nomeVeiculo = veiculo != null ? veiculo.getMarca() + " " + veiculo.getModelo() : "N/A";
            sb.append(String.format("%-10s │ %-20s │ %-20s │ R$ %8.2f\n",
                    m.getData(), nomeVeiculo, m.getDescricao(), m.getValor()));
        }

        sb.append("───────────┴──────────────────────┴──────────────────────┴─────────────\n");
        sb.append(String.format("TOTAL DE MULTAS: R$ %.2f\n", total));

        areaRelatorio.setText(sb.toString());
        preencherTabelaMultas(multas, total);

        // Marcar que um relatório foi gerado
        relatorioGerado = true;
        tipoRelatorioAtual = "Multas por Veículo";
    }

    private void preencherTabelaMultas(List<Movimentacao> movimentacoes, double total) {
        modeloTabela.setColumnIdentifiers(new String[]{"Data", "Veículo", "Descrição", "Valor"});
        modeloTabela.setRowCount(0);

        for (Movimentacao m : movimentacoes) {
            try {
                Veiculo v = veiculoController.obterVeiculoPorId(m.getIdVeiculo());
                String nomeVeiculo = v != null ? v.getMarca() + " " + v.getModelo() : "N/A";
                modeloTabela.addRow(new Object[]{
                        m.getData(),
                        nomeVeiculo,
                        m.getDescricao(),
                        String.format("R$ %.2f", m.getValor())
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        modeloTabela.addRow(new Object[]{"", "", "TOTAL", String.format("R$ %.2f", total)});
        tabelaRelatorio.setModel(modeloTabela);
    }

    private void exportarCSV() {
        // Validar se um relatório foi gerado
        if (!relatorioGerado) {
            JOptionPane.showMessageDialog(this,
                "Por favor, gere um relatório antes de exportar!\n\n" +
                "Clique em um dos botões:\n" +
                "• Despesas por Veículo\n" +
                "• Despesas por Mês\n" +
                "• Combustível por Mês\n" +
                "• IPVA por Ano\n" +
                "• Veículos Inativos\n" +
                "• Multas por Veículo",
                "Aviso",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

            // Gerar nome do arquivo baseado no tipo de relatório
            String nomeArquivo = "relatorio_" + tipoRelatorioAtual.toLowerCase()
                    .replace(" ", "_")
                    .replace("á", "a")
                    .replace("ã", "a")
                    .replace("í", "i")
                    .replace("ú", "u")
                    + ".csv";

            GeradorCSV.gerarRelatorioDespesasCSV(movimentacoes, nomeArquivo);

            // Obter caminho completo do arquivo
            java.io.File arquivo = new java.io.File(nomeArquivo);
            String caminhoCompleto = arquivo.getAbsolutePath();

            JOptionPane.showMessageDialog(this,
                "Relatório exportado com sucesso!\n\n" +
                "Tipo: " + tipoRelatorioAtual + "\n" +
                "Local: " + caminhoCompleto,
                "Sucesso",
                JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Erro ao exportar: " + e.getMessage(),
                "Erro",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void limparResultado() {
        areaRelatorio.setText("");
        modeloTabela.setRowCount(0);
        lblTitulo.setText("Relatórios");

        // Resetar controle de relatório gerado
        relatorioGerado = false;
        tipoRelatorioAtual = "";
    }

    // ========== MÉTODOS DE RELATÓRIOS DE MATRIZES ==========

    /**
     * Gera relatório da Matriz A - Quantidade de abastecimentos por Veículo/Mês
     */
    private void gerarRelatorioMatrizA() throws IOException {
        // Obter período selecionado
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        // Validar período
        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                "O período inicial não pode ser maior que o período final!",
                "Período Inválido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String relatorio = relatoriosController.gerarRelatorioMatrizAComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal);

        areaRelatorio.setText(relatorio);

        // Preencher tabela
        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();
        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);

        preencherTabelaMatriz(matrizA, veiculos, meses, "Matriz A - Abastecimentos");

        relatorioGerado = true;
        tipoRelatorioAtual = "Matriz A (Abastecimentos)";
    }

    /**
     * Gera relatório da Matriz B - Custo médio por Abastecimento/Marca
     */
    private void gerarRelatorioMatrizB() throws IOException {
        // Obter período selecionado
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        // Validar período
        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                "O período inicial não pode ser maior que o período final!",
                "Período Inválido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String relatorio = relatoriosController.gerarRelatorioMatrizBComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal);

        areaRelatorio.setText(relatorio);

        // Preencher tabela
        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();
        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);

        // Para matriz B, os rótulos das linhas são os meses
        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(marcas));
        modeloTabela.setRowCount(0);

        for (int i = 0; i < matrizB.length; i++) {
            Object[] linha = new Object[marcas.size() + 1];
            linha[0] = meses.get(i);
            for (int j = 0; j < marcas.size(); j++) {
                linha[j + 1] = String.format("R$ %.2f", matrizB[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        // Linha de total
        Object[] linhaTotal = new Object[marcas.size() + 1];
        linhaTotal[0] = "TOTAL GERAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matrizB);
        linhaTotal[marcas.size()] = String.format("R$ %.2f", total);
        for (int j = 0; j < marcas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);

        relatorioGerado = true;
        tipoRelatorioAtual = "Matriz B (Custo Médio)";
    }

    /**
     * Gera relatório da Matriz C - Gasto Total Estimado por Veículo/Marca
     */
    private void gerarRelatorioMatrizC() throws IOException {
        // Obter período selecionado
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        // Validar período
        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                "O período inicial não pode ser maior que o período final!",
                "Período Inválido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String relatorio = relatoriosController.gerarRelatorioMatrizCComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal);

        areaRelatorio.setText(relatorio);

        // Preencher tabela
        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();
        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        // Para matriz C, os rótulos das linhas são os veículos e colunas são as marcas
        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(marcas));
        modeloTabela.setRowCount(0);

        for (int i = 0; i < matrizC.length; i++) {
            Object[] linha = new Object[marcas.size() + 1];
            linha[0] = br.com.utils.MatrizRelatorios.formatarRotuloVeiculo(veiculos.get(i));
            for (int j = 0; j < marcas.size(); j++) {
                linha[j + 1] = String.format("R$ %.2f", matrizC[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        // Linha de total
        Object[] linhaTotal = new Object[marcas.size() + 1];
        linhaTotal[0] = "TOTAL GERAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matrizC);
        linhaTotal[marcas.size()] = String.format("R$ %.2f", total);
        for (int j = 0; j < marcas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);

        relatorioGerado = true;
        tipoRelatorioAtual = "Matriz C (Gasto Total)";
    }

    /**
     * Gera relatório completo com todas as matrizes
     */
    private void gerarRelatorioMatrizCompleto() throws IOException {
        // Obter período selecionado
        int mesInicial = (int) cmbMes.getSelectedItem();
        int anoInicial = (int) cmbAno.getSelectedItem();
        int mesFinal = (int) cmbMesFinal.getSelectedItem();
        int anoFinal = (int) cmbAnoFinal.getSelectedItem();

        // Validar período
        if (anoInicial * 100 + mesInicial > anoFinal * 100 + mesFinal) {
            JOptionPane.showMessageDialog(this,
                "O período inicial não pode ser maior que o período final!",
                "Período Inválido",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        String relatorio = relatoriosController.gerarRelatorioMatrizCompletoComPeriodo(
                mesInicial, anoInicial, mesFinal, anoFinal);

        areaRelatorio.setText(relatorio);

        // Para o relatório completo, mostrar a Matriz C na tabela (a mais importante)
        List<Veiculo> veiculos = relatoriosController.obterTodosVeiculos();
        List<Movimentacao> movimentacoes = relatoriosController.obterTodasMovimentacoes();

        // Filtrar movimentações por período
        List<Movimentacao> movimentacoesFiltradas = movimentacoes.stream()
                .filter(m -> br.com.utils.MatrizRelatorios.estaNoPeriodo(m, mesInicial, anoInicial, mesFinal, anoFinal))
                .toList();

        List<String> meses = br.com.utils.MatrizRelatorios.extrairMesesPorPeriodo(
                movimentacoesFiltradas, mesInicial, anoInicial, mesFinal, anoFinal);
        List<String> marcas = br.com.utils.MatrizRelatorios.extrairMarcas(veiculos);

        double[][] matrizA = br.com.utils.MatrizRelatorios.gerarMatrizA(veiculos, movimentacoesFiltradas, meses);
        double[][] matrizB = br.com.utils.MatrizRelatorios.gerarMatrizB(meses, marcas, veiculos, movimentacoesFiltradas);
        double[][] matrizC = br.com.utils.MatrizRelatorios.gerarMatrizC(matrizA, matrizB);

        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(marcas));
        modeloTabela.setRowCount(0);

        for (int i = 0; i < matrizC.length; i++) {
            Object[] linha = new Object[marcas.size() + 1];
            linha[0] = br.com.utils.MatrizRelatorios.formatarRotuloVeiculo(veiculos.get(i));
            for (int j = 0; j < marcas.size(); j++) {
                linha[j + 1] = String.format("R$ %.2f", matrizC[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        Object[] linhaTotal = new Object[marcas.size() + 1];
        linhaTotal[0] = "TOTAL GERAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matrizC);
        linhaTotal[marcas.size()] = String.format("R$ %.2f", total);
        for (int j = 0; j < marcas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);

        relatorioGerado = true;
        tipoRelatorioAtual = "Relatório Completo de Matrizes";
    }

    /**
     * Preenche a tabela com dados de uma matriz
     */
    private void preencherTabelaMatriz(double[][] matriz, List<Veiculo> veiculos, List<String> colunas, String titulo) {
        // Criar cabeçalho
        modeloTabela.setColumnIdentifiers(criarCabecalhoTabela(colunas));
        modeloTabela.setRowCount(0);

        // Preencher linhas
        for (int i = 0; i < matriz.length; i++) {
            Object[] linha = new Object[colunas.size() + 1];
            linha[0] = br.com.utils.MatrizRelatorios.formatarRotuloVeiculo(veiculos.get(i));

            for (int j = 0; j < colunas.size(); j++) {
                linha[j + 1] = String.format("%.0f", matriz[i][j]);
            }
            modeloTabela.addRow(linha);
        }

        // Linha de total
        Object[] linhaTotal = new Object[colunas.size() + 1];
        linhaTotal[0] = "TOTAL";
        double total = br.com.utils.MatrizRelatorios.calcularTotalGeral(matriz);
        linhaTotal[colunas.size()] = String.format("%.0f", total);
        for (int j = 0; j < colunas.size() - 1; j++) {
            linhaTotal[j + 1] = "";
        }
        modeloTabela.addRow(linhaTotal);

        tabelaRelatorio.setModel(modeloTabela);
    }

    /**
     * Cria cabeçalho para tabela de matriz
     */
    private String[] criarCabecalhoTabela(List<String> colunas) {
        String[] cabecalho = new String[colunas.size() + 1];
        cabecalho[0] = "Veículo/Período";
        for (int i = 0; i < colunas.size(); i++) {
            cabecalho[i + 1] = colunas.get(i);
        }
        return cabecalho;
    }
}

