package br.com.view;

import br.com.controller.TipoDespesaController;
import br.com.model.TipoDespesa;
import br.com.ui.ModernTabbedPane;
import br.com.ui.ModernColors;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TelaPrincipal extends JFrame {
    private JTabbedPane abas;
    private TipoDespesaController tipoDespesaController;

    public TelaPrincipal() {
        setTitle("Sistema de Controle de Frotas - GynLog");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 750);
        setLocationRelativeTo(null);
        setResizable(true);
        setDefaultLookAndFeelDecorated(true);

        // Background moderno
        getContentPane().setBackground(ModernColors.BG_PRIMARY);

        // Inicializar controller e tipos de despesa pré-cadastrados
        tipoDespesaController = new TipoDespesaController();
        inicializarTiposDespesaPadrao();

        // Menu
        criarMenuBar();

        // Abas (sem "Tipos de Despesas")
        abas = new ModernTabbedPane();
        abas.addTab("Veículos", new TelaCadastroVeiculo());
        abas.addTab("Movimentações", new TelaMovimentacao());
        abas.addTab("Relatórios", new TelaRelatorios());
        abas.addTab("Sobre", new TelaAbout());

        add(abas);
    }

    private void inicializarTiposDespesaPadrao() {
        try {
            List<TipoDespesa> tipos = tipoDespesaController.obterTodosTipos();
            if (tipos.isEmpty()) {
                // Inserir tipos de despesa padrão
                tipoDespesaController.salvarTipoDespesa("Combustível");
                tipoDespesaController.salvarTipoDespesa("Seguro");
                tipoDespesaController.salvarTipoDespesa("Lavagem");
                tipoDespesaController.salvarTipoDespesa("Manutenção");
                tipoDespesaController.salvarTipoDespesa("IPVA");
                tipoDespesaController.salvarTipoDespesa("Multa");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void criarMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        menuBar.setBackground(Color.WHITE);
        menuBar.setForeground(ModernColors.DARK_GRAY);
        menuBar.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernColors.BORDER_GRAY));

        // Menu Arquivo
        JMenu menuArquivo = new JMenu("Arquivo");
        menuArquivo.setForeground(ModernColors.DARK_GRAY);
        menuArquivo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JMenuItem itemSair = new JMenuItem("Sair");
        itemSair.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemSair.addActionListener(e -> System.exit(0));
        menuArquivo.add(itemSair);

        // Menu Ajuda
        JMenu menuAjuda = new JMenu("Ajuda");
        menuAjuda.setForeground(ModernColors.DARK_GRAY);
        menuAjuda.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        JMenuItem itemSobre = new JMenuItem("Sobre");
        itemSobre.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemSobre.addActionListener(e -> JOptionPane.showMessageDialog(this,
                "Sistema de Controle de Frotas v2.0\nDesenvolvido para GynLog",
                "Sobre", JOptionPane.INFORMATION_MESSAGE));
        menuAjuda.add(itemSobre);

        menuBar.add(menuArquivo);
        menuBar.add(menuAjuda);

        setJMenuBar(menuBar);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal frame = new TelaPrincipal();
            frame.setVisible(true);
        });
    }
}

