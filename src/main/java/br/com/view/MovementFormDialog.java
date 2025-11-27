package br.com.view;

import br.com.controller.MovimentacaoController;
import br.com.controller.VeiculoController;
import br.com.controller.TipoDespesaController;
import br.com.model.Movimentacao;
import br.com.model.Veiculo;
import br.com.model.TipoDespesa;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Diálogo moderno para cadastro/edição de movimentações
 */
public class MovementFormDialog extends JDialog {
    private MovimentacaoController movimentacaoController;
    private VeiculoController veiculoController;
    private TipoDespesaController tipoDespesaController;
    private Movimentacao movimentacaoEdicao;
    private Runnable onSuccess;

    private JComboBox<Veiculo> cmbVeiculo;
    private JComboBox<TipoDespesa> cmbTipoDespesa;
    private JTextField txtDescricao;
    private JTextField txtValor;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Segoe UI", Font.BOLD, 13);
    private static final Font FIELD_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public MovementFormDialog(JFrame owner, Movimentacao movimentacaoEdicao) {
        super(owner, movimentacaoEdicao != null ? "Editar Movimentação" : "Nova Movimentação", true);
        this.movimentacaoController = new MovimentacaoController();
        this.veiculoController = new VeiculoController();
        this.tipoDespesaController = new TipoDespesaController();
        this.movimentacaoEdicao = movimentacaoEdicao;

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        setSize(520, 580);
        setLocationRelativeTo(owner);
        setResizable(false);

        initializeComponents();
    }

    private void initializeComponents() {
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(ModernColors.BG_PRIMARY);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // Header Panel
        JPanel headerPanel = criarPainelCabecalho();
        contentPanel.add(headerPanel, BorderLayout.NORTH);

        // Main Panel com formulário
        JPanel mainPanel = new RoundedPanel(12, ModernColors.WHITE);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(20, 20, 20, 20),
            BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE.brighter(), 2)
        ));

        // Painel de formulário com scroll
        JPanel formPanel = criarPainelFormulario();
        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Painel de botões
        JPanel buttonPanel = criarPainelBotoes();
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Wrapper para o mainPanel com margem
        JPanel wrapperPanel = new JPanel(new BorderLayout());
        wrapperPanel.setBackground(ModernColors.BG_PRIMARY);
        wrapperPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 20, 20));
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);

        contentPanel.add(wrapperPanel, BorderLayout.CENTER);

        add(contentPanel);
    }

    private JPanel criarPainelCabecalho() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(ModernColors.PRIMARY_BLUE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Ícone da movimentação
        ImageIcon icon;
        if (movimentacaoEdicao != null) {
            try {
                TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacaoEdicao.getIdTipoDespesa());
                icon = IconLoader.loadIconForExpenseType(tipo != null ? tipo.getDescricao() : "Combustível", 40, 40);
            } catch (IOException e) {
                icon = IconLoader.loadCombustivelIcon(40, 40);
            }
        } else {
            icon = IconLoader.loadCombustivelIcon(40, 40);
        }

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15));

        // Título
        String titulo = movimentacaoEdicao != null ? "Editar Movimentação" : "Nova Movimentação";
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(TITLE_FONT);
        lblTitulo.setForeground(Color.WHITE);

        // Subtítulo
        String subtitulo = movimentacaoEdicao != null
            ? "Atualize os dados da movimentação"
            : "Registre uma nova despesa do veículo";
        JLabel lblSubtitulo = new JLabel(subtitulo);
        lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        lblSubtitulo.setForeground(new Color(255, 255, 255, 200));

        // Panel de textos
        JPanel textoPanel = new JPanel(new GridLayout(2, 1, 0, 3));
        textoPanel.setOpaque(false);
        textoPanel.add(lblTitulo);
        textoPanel.add(lblSubtitulo);

        // Panel esquerdo com ícone e texto
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel);
        leftPanel.add(textoPanel);

        headerPanel.add(leftPanel, BorderLayout.WEST);

        return headerPanel;
    }

    private JPanel criarPainelFormulario() {
        JPanel mainFormPanel = new JPanel();
        mainFormPanel.setLayout(new BoxLayout(mainFormPanel, BoxLayout.Y_AXIS));
        mainFormPanel.setOpaque(false);
        mainFormPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Seção: Informações da Movimentação
        JLabel secaoInfo = new JLabel("Informações da Movimentação");
        secaoInfo.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secaoInfo.setForeground(ModernColors.PRIMARY_BLUE);
        secaoInfo.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainFormPanel.add(secaoInfo);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Veículo
        JPanel veiculoPanel = new JPanel(new BorderLayout(8, 5));
        veiculoPanel.setOpaque(false);
        veiculoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        veiculoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblVeiculo = new JLabel("Veículo:");
        lblVeiculo.setFont(LABEL_FONT);
        lblVeiculo.setForeground(ModernColors.DARK_GRAY);
        veiculoPanel.add(lblVeiculo, BorderLayout.NORTH);

        cmbVeiculo = new JComboBox<>();
        cmbVeiculo.setFont(FIELD_FONT);
        cmbVeiculo.setBackground(Color.WHITE);
        cmbVeiculo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernColors.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        carregarVeiculos();
        veiculoPanel.add(cmbVeiculo, BorderLayout.CENTER);

        mainFormPanel.add(veiculoPanel);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Tipo de Despesa
        JPanel tipoPanel = new JPanel(new BorderLayout(8, 5));
        tipoPanel.setOpaque(false);
        tipoPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        tipoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblTipo = new JLabel("Tipo de Despesa:");
        lblTipo.setFont(LABEL_FONT);
        lblTipo.setForeground(ModernColors.DARK_GRAY);
        tipoPanel.add(lblTipo, BorderLayout.NORTH);

        cmbTipoDespesa = new JComboBox<>();
        cmbTipoDespesa.setFont(FIELD_FONT);
        cmbTipoDespesa.setBackground(Color.WHITE);
        cmbTipoDespesa.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernColors.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        carregarTiposDespesa();
        tipoPanel.add(cmbTipoDespesa, BorderLayout.CENTER);

        mainFormPanel.add(tipoPanel);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Seção: Detalhes
        JLabel secaoDetalhes = new JLabel("Detalhes");
        secaoDetalhes.setFont(new Font("Segoe UI", Font.BOLD, 14));
        secaoDetalhes.setForeground(ModernColors.PRIMARY_BLUE);
        secaoDetalhes.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainFormPanel.add(secaoDetalhes);
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 15)));

        // Descrição
        mainFormPanel.add(criarCampoFormulario("Descrição:", "txtDescricao"));
        mainFormPanel.add(Box.createRigidArea(new Dimension(0, 12)));

        // Valor
        mainFormPanel.add(criarCampoFormulario("Valor (R$):", "txtValor"));

        // Carregar dados se estiver editando
        if (movimentacaoEdicao != null) {
            try {
                Veiculo veiculo = veiculoController.obterVeiculoPorId(movimentacaoEdicao.getIdVeiculo());
                if (veiculo != null) cmbVeiculo.setSelectedItem(veiculo);

                TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacaoEdicao.getIdTipoDespesa());
                if (tipo != null) cmbTipoDespesa.setSelectedItem(tipo);

                txtDescricao.setText(movimentacaoEdicao.getDescricao());
                txtValor.setText(String.valueOf(movimentacaoEdicao.getValor()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return mainFormPanel;
    }

    private JPanel criarCampoFormulario(String labelText, String fieldName) {
        JPanel panel = new JPanel(new BorderLayout(8, 5));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel label = new JLabel(labelText);
        label.setFont(LABEL_FONT);
        label.setForeground(ModernColors.DARK_GRAY);
        panel.add(label, BorderLayout.NORTH);

        JTextField field = criarCampoTexto();
        panel.add(field, BorderLayout.CENTER);

        // Armazenar referência ao campo
        switch (fieldName) {
            case "txtDescricao" -> txtDescricao = field;
            case "txtValor" -> txtValor = field;
        }

        return panel;
    }

    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        ModernButton btnCancelar = new ModernButton("Cancelar", ModernColors.TEXT_GRAY);
        btnCancelar.setPreferredSize(new Dimension(120, 42));
        btnCancelar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnCancelar.addActionListener(e -> dispose());

        ModernButton btnSalvar = new ModernButton("Salvar", ModernColors.PRIMARY_BLUE);
        btnSalvar.setPreferredSize(new Dimension(120, 42));
        btnSalvar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btnSalvar.addActionListener(e -> salvar());

        panel.add(btnCancelar);
        panel.add(btnSalvar);

        return panel;
    }

    private JTextField criarCampoTexto() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernColors.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));

        // Adicionar efeito de foco
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE, 2),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ModernColors.BORDER_GRAY, 1),
                    BorderFactory.createEmptyBorder(10, 12, 10, 12)
                ));
            }
        });

        return field;
    }

    private void carregarVeiculos() {
        try {
            List<Veiculo> veiculos = veiculoController.obterTodosVeiculos();
            for (Veiculo v : veiculos) {
                cmbVeiculo.addItem(v);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void carregarTiposDespesa() {
        try {
            List<TipoDespesa> tipos = tipoDespesaController.obterTodosTipos();
            for (TipoDespesa t : tipos) {
                cmbTipoDespesa.addItem(t);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar tipos", "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void salvar() {
        try {
            Veiculo veiculoSelecionado = (Veiculo) cmbVeiculo.getSelectedItem();
            TipoDespesa tipoSelecionado = (TipoDespesa) cmbTipoDespesa.getSelectedItem();

            if (veiculoSelecionado == null || tipoSelecionado == null) {
                JOptionPane.showMessageDialog(this,
                    "Selecione veículo e tipo de despesa!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            String descricao = txtDescricao.getText().trim();
            String valor = txtValor.getText().trim();

            if (descricao.isEmpty() || valor.isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Preencha todos os campos!",
                    "Erro",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Gerar data automática no formato dd/MM/yyyy
            String data = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));

            if (movimentacaoEdicao != null) {
                movimentacaoController.atualizarMovimentacao(
                    movimentacaoEdicao.getIdMovimentacao(),
                    veiculoSelecionado.getIdVeiculo(),
                    tipoSelecionado.getIdTipoDespesa(),
                    descricao,
                    data,
                    valor,
                    tipoSelecionado.getDescricao()
                );
                JOptionPane.showMessageDialog(this,
                    "Movimentação atualizada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                movimentacaoController.salvarMovimentacao(
                    veiculoSelecionado.getIdVeiculo(),
                    tipoSelecionado.getIdTipoDespesa(),
                    descricao,
                    data,
                    valor,
                    tipoSelecionado.getDescricao()
                );
                JOptionPane.showMessageDialog(this,
                    "Movimentação cadastrada com sucesso!",
                    "Sucesso",
                    JOptionPane.INFORMATION_MESSAGE);
            }

            if (onSuccess != null) onSuccess.run();
            dispose();
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Erro de Validação", JOptionPane.ERROR_MESSAGE);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao salvar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void setCallback(Runnable callback) {
        this.onSuccess = callback;
    }
}

