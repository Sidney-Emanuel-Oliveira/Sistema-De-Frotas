package br.com.view;

import br.com.controller.TipoDespesaController;
import br.com.model.TipoDespesa;
import br.com.ui.ModernTabbedPane;
import br.com.ui.ModernColors;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

/**
 * TelaPrincipal - Janela principal do Sistema de Gerenciamento de Frotas
 *
 * Esta classe é responsável por:
 * - Criar a interface principal com abas de navegação
 * - Inicializar os tipos de despesa padrão do sistema
 * - Gerenciar a barra de menu (Arquivo e Ajuda)
 * - Organizar as diferentes seções do sistema em abas
 *
 * Estrutura das Abas:
 * 1. Veículos - Cadastro e gerenciamento de veículos
 * 2. Movimentações - Registro de despesas e movimentações
 * 3. Relatórios - Visualização e exportação de relatórios
 * 4. Sobre - Informações sobre o sistema
 *
 * @author Sidney Emanuel Oliveira
 * @version 1.0
 */
public class TelaPrincipal extends JFrame {

    // ==================== ATRIBUTOS ====================

    /** Painel de abas principal do sistema */
    private JTabbedPane abas;

    /** Controller para gerenciar tipos de despesa */
    private TipoDespesaController tipoDespesaController;

    // ==================== CONSTANTES ====================

    /** Largura padrão da janela */
    private static final int LARGURA_JANELA = 1200;

    /** Altura padrão da janela */
    private static final int ALTURA_JANELA = 750;

    /** Título da aplicação */
    private static final String TITULO_SISTEMA = "Sistema de Controle de Frotas - GynLog";

    // ==================== CONSTRUTOR ====================

    /**
     * Construtor da janela principal
     * Inicializa todos os componentes visuais e dados do sistema
     */
    public TelaPrincipal() {
        // Configurações básicas da janela
        configurarJanela();

        // Inicializar dados padrão do sistema
        inicializarSistema();

        // Criar interface gráfica
        construirInterface();
    }

    // ==================== MÉTODOS DE INICIALIZAÇÃO ====================

    /**
     * Configura as propriedades básicas da janela principal
     */
    private void configurarJanela() {
        setTitle(TITULO_SISTEMA);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(LARGURA_JANELA, ALTURA_JANELA);
        setLocationRelativeTo(null); // Centraliza na tela
        setResizable(true);
        setDefaultLookAndFeelDecorated(true);

        // Define cor de fundo moderna
        getContentPane().setBackground(ModernColors.BG_PRIMARY);
    }

    /**
     * Inicializa o sistema carregando dados padrão
     */
    private void inicializarSistema() {
        tipoDespesaController = new TipoDespesaController();
        inicializarTiposDespesaPadrao();
    }

    /**
     * Constrói a interface gráfica completa
     */
    private void construirInterface() {
        // Cria barra de menu
        criarMenuBar();

        // Cria sistema de abas
        criarAbas();
    }

    /**
     * Cria e configura o sistema de abas principais
     */
    private void criarAbas() {
        abas = new ModernTabbedPane();

        // Adiciona cada aba com sua respectiva tela
        abas.addTab("Veículos", new TelaCadastroVeiculo());
        abas.addTab("Movimentações", new TelaMovimentacao());
        abas.addTab("Relatórios", new TelaRelatorios());
        abas.addTab("Sobre", new TelaAbout());

        add(abas);
    }

    /**
     * Inicializa os tipos de despesa padrão do sistema
     *
     * Tipos cadastrados automaticamente:
     * - Combustível: Para abastecimentos
     * - Seguro: Seguros obrigatório e facultativo
     * - Lavagem: Lavagens e limpezas
     * - Manutenção: Reparos e manutenções
     * - IPVA: Imposto sobre veículos
     * - Multa: Multas de trânsito
     *
     * Este método só cadastra se não existirem tipos no sistema
     */
    private void inicializarTiposDespesaPadrao() {
        try {
            // Verifica se já existem tipos cadastrados
            List<TipoDespesa> tiposExistentes = tipoDespesaController.obterTodosTipos();

            // Se não houver tipos, cadastra os padrões
            if (tiposExistentes.isEmpty()) {
                cadastrarTiposPadrao();
            }
        } catch (IOException erro) {
            exibirErro("Erro ao inicializar tipos de despesa", erro);
        }
    }

    /**
     * Cadastra os tipos de despesa padrão no sistema
     */
    private void cadastrarTiposPadrao() throws IOException {
        String[] tiposPadrao = {
            "Combustível",
            "Seguro",
            "Lavagem",
            "Manutenção",
            "IPVA",
            "Multa"
        };

        for (String tipo : tiposPadrao) {
            tipoDespesaController.salvarTipoDespesa(tipo);
        }
    }

    /**
     * Exibe mensagem de erro para o usuário
     */
    private void exibirErro(String mensagem, Exception erro) {
        System.err.println(mensagem + ": " + erro.getMessage());
        erro.printStackTrace();
    }

    // ==================== CRIAÇÃO DA INTERFACE ====================

    /**
     * Cria a barra de menu principal do sistema
     *
     * Menus disponíveis:
     * - Arquivo: Opções de saída do sistema
     * - Ajuda: Informações sobre o sistema
     */
    private void criarMenuBar() {
        JMenuBar barraMenu = criarBarraMenu();

        // Adiciona os menus
        barraMenu.add(criarMenuArquivo());
        barraMenu.add(criarMenuAjuda());

        setJMenuBar(barraMenu);
    }

    /**
     * Cria e estiliza a barra de menu
     */
    private JMenuBar criarBarraMenu() {
        JMenuBar barraMenu = new JMenuBar();
        barraMenu.setBackground(Color.WHITE);
        barraMenu.setForeground(ModernColors.DARK_GRAY);
        barraMenu.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, ModernColors.BORDER_GRAY));
        return barraMenu;
    }

    /**
     * Cria o menu "Arquivo" com opções de sistema
     */
    private JMenu criarMenuArquivo() {
        JMenu menuArquivo = new JMenu("Arquivo");
        estilizarMenu(menuArquivo);

        // Item: Sair
        JMenuItem itemSair = new JMenuItem("Sair");
        estilizarMenuItem(itemSair);
        itemSair.addActionListener(evento -> encerrarSistema());

        menuArquivo.add(itemSair);
        return menuArquivo;
    }

    /**
     * Cria o menu "Ajuda" com informações do sistema
     */
    private JMenu criarMenuAjuda() {
        JMenu menuAjuda = new JMenu("Ajuda");
        estilizarMenu(menuAjuda);

        JMenuItem itemSobre = new JMenuItem("Sobre");
        estilizarMenuItem(itemSobre);
        itemSobre.addActionListener(evento -> exibirSobre());

        menuAjuda.add(itemSobre);
        return menuAjuda;
    }

    // ==================== MÉTODOS AUXILIARES ====================

    /**
     * Aplica estilização padrão a um menu
     */
    private void estilizarMenu(JMenu menu) {
        menu.setForeground(ModernColors.DARK_GRAY);
        menu.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    }

    /**
     * Aplica estilização padrão a um item de menu
     */
    private void estilizarMenuItem(JMenuItem item) {
        item.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    }

    /**
     * Encerra o sistema de forma controlada
     */
    private void encerrarSistema() {
        System.exit(0);
    }

    /**
     * Exibe a janela "Sobre" com informações do sistema
     */
    private void exibirSobre() {
        String mensagem = "Sistema de Controle de Frotas v2.0\n" +
                         "Desenvolvido para GynLog\n" +
                         "Gerenciamento completo de frotas de veículos";

        JOptionPane.showMessageDialog(
            this,
            mensagem,
            "Sobre o Sistema",
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    // ==================== MÉTODO PRINCIPAL ====================

    /**
     * Método principal que inicia a aplicação
     * Usa SwingUtilities.invokeLater para garantir thread-safety
     *
     * @param args Argumentos da linha de comando
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TelaPrincipal telaPrincipal = new TelaPrincipal();
            telaPrincipal.setVisible(true);
        });
    }
}

