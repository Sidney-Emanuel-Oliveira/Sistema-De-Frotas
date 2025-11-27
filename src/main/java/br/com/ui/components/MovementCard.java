package br.com.ui.components;

import br.com.model.Movimentacao;
import br.com.model.TipoDespesa;
import br.com.controller.TipoDespesaController;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Card moderno para exibição de movimentações/despesas
 * Design elegante com ícone, informações e botões
 */
public class MovementCard extends RoundedPanel {
    private Movimentacao movimentacao;
    private List<MovementCardListener> listeners = new ArrayList<>();
    private TipoDespesaController tipoDespesaController;

    public interface MovementCardListener {
        void onEditClicked(Movimentacao mov);
        void onDeleteClicked(Movimentacao mov);
        void onCardClicked(Movimentacao mov);
    }

    public MovementCard(Movimentacao movimentacao) {
        super(12, ModernColors.WHITE);
        this.movimentacao = movimentacao;
        this.tipoDespesaController = new TipoDespesaController();

        setLayout(new BorderLayout(15, 0));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setPreferredSize(new Dimension(900, 120));
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));

        initializeComponents();

        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (!e.getComponent().equals(getButtonPanel())) {
                    notifyCardClicked();
                }
            }
        });
    }

    private void initializeComponents() {
        // Painel esquerdo - Ícone
        JPanel iconPanel = criarPainelIcone();
        add(iconPanel, BorderLayout.WEST);

        // Painel central - Informações
        JPanel infoPanel = criarPainelInformacoes();
        add(infoPanel, BorderLayout.CENTER);

        // Painel direito - Botões
        JPanel buttonPanel = criarPainelBotoes();
        add(buttonPanel, BorderLayout.EAST);
    }

    private JPanel criarPainelIcone() {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Desenhar fundo arredondado
                Color bgColor = getBackgroundColor();
                g2d.setColor(bgColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);

                super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(100, 80));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        // Carregar imagem PNG real baseada no tipo de despesa
        String tipoName = getTipoDespesaInfo();
        ImageIcon icon = IconLoader.loadIconForExpenseType(tipoName, 60, 60);
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalAlignment(JLabel.CENTER);
        panel.add(iconLabel, BorderLayout.CENTER);

        return panel;
    }


    private JPanel criarPainelInformacoes() {
        JPanel panel = new JPanel(new GridLayout(3, 1, 0, 8));
        panel.setOpaque(false);

        // Linha 1: Veículo + Tipo
        JPanel linha1 = new JPanel(new BorderLayout(10, 0));
        linha1.setOpaque(false);

        JLabel veiculoLabel = new JLabel(getVeiculoInfo());
        veiculoLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        veiculoLabel.setForeground(ModernColors.DARK_GRAY);

        JLabel tipoLabel = new JLabel("• " + getTipoDespesaInfo());
        tipoLabel.setFont(new Font("Segoe UI", Font.BOLD, 12));
        tipoLabel.setForeground(getColorForType());

        linha1.add(veiculoLabel, BorderLayout.WEST);
        linha1.add(tipoLabel, BorderLayout.CENTER);

        // Linha 2: Data + Descrição
        JPanel linha2 = new JPanel(new BorderLayout(10, 0));
        linha2.setOpaque(false);

        JLabel dataLabel = new JLabel(movimentacao.getData());
        dataLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dataLabel.setForeground(ModernColors.TEXT_GRAY);

        JLabel descLabel = new JLabel("• " + movimentacao.getDescricao());
        descLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        descLabel.setForeground(new Color(140, 140, 140));

        linha2.add(dataLabel, BorderLayout.WEST);
        linha2.add(descLabel, BorderLayout.CENTER);

        // Linha 3: Valor
        JPanel linha3 = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        linha3.setOpaque(false);

        JLabel valorLabel = new JLabel("R$ " + String.format("%.2f", movimentacao.getValor()));
        valorLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        valorLabel.setForeground(ModernColors.PRIMARY_BLUE);

        linha3.add(valorLabel);

        panel.add(linha1);
        panel.add(linha2);
        panel.add(linha3);

        return panel;
    }

    private JPanel criarPainelBotoes() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 20));
        panel.setOpaque(false);

        ModernButton btnEditar = new ModernButton("Editar", ModernColors.PRIMARY_BLUE);
        btnEditar.setPreferredSize(new Dimension(75, 32));
        btnEditar.addActionListener(e -> notifyEditClicked());

        ModernButton btnExcluir = new ModernButton("Excluir", ModernColors.DANGER_RED);
        btnExcluir.setPreferredSize(new Dimension(75, 32));
        btnExcluir.addActionListener(e -> notifyDeleteClicked());

        panel.add(btnEditar);
        panel.add(btnExcluir);

        return panel;
    }

    private JPanel getButtonPanel() {
        Component[] components = getComponents();
        for (Component comp : components) {
            if (comp instanceof JPanel) {
                JPanel p = (JPanel) comp;
                if (p.getComponentCount() > 0 && p.getComponent(0) instanceof ModernButton) {
                    return p;
                }
            }
        }
        return null;
    }

    private Color getBackgroundColor() {
        try {
            TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacao.getIdTipoDespesa());
            if (tipo != null) {
                String desc = tipo.getDescricao().toLowerCase();
                if (desc.contains("combustível") || desc.contains("combustivel")) return new Color(255, 243, 224);   // Laranja claro
                if (desc.contains("seguro")) return new Color(232, 245, 233);        // Verde claro
                if (desc.contains("lavagem")) return new Color(225, 245, 254);       // Azul claro
                if (desc.contains("manutenção") || desc.contains("manutencao")) return new Color(243, 229, 245);    // Roxo claro
                if (desc.contains("ipva")) return new Color(255, 249, 196);          // Amarelo claro
                if (desc.contains("multa")) return new Color(251, 235, 235);         // Vermelho claro
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ModernColors.LIGHT_BLUE;
    }

    private String getVeiculoInfo() {
        // Formato: Marca Modelo (Placa)
        return movimentacao.getDescricao().length() > 30
            ? movimentacao.getDescricao().substring(0, 27) + "..."
            : movimentacao.getDescricao();
    }

    private String getTipoDespesaInfo() {
        try {
            TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacao.getIdTipoDespesa());
            if (tipo != null) {
                return tipo.getDescricao();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "Despesa";
    }

    private Color getColorForType() {
        try {
            TipoDespesa tipo = tipoDespesaController.obterTipoPorId(movimentacao.getIdTipoDespesa());
            if (tipo != null) {
                String desc = tipo.getDescricao().toLowerCase();
                if (desc.contains("combustível") || desc.contains("combustivel")) return new Color(255, 152, 0);      // Laranja
                if (desc.contains("seguro")) return new Color(76, 175, 80);          // Verde
                if (desc.contains("lavagem")) return new Color(33, 150, 243);        // Azul
                if (desc.contains("manutenção") || desc.contains("manutencao")) return new Color(156, 39, 176);     // Roxo
                if (desc.contains("ipva")) return new Color(255, 193, 7);            // Amarelo
                if (desc.contains("multa")) return new Color(244, 67, 54);           // Vermelho
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ModernColors.PRIMARY_BLUE;
    }

    public void addListener(MovementCardListener listener) {
        listeners.add(listener);
    }

    private void notifyEditClicked() {
        for (MovementCardListener listener : listeners) {
            listener.onEditClicked(movimentacao);
        }
    }

    private void notifyDeleteClicked() {
        for (MovementCardListener listener : listeners) {
            listener.onDeleteClicked(movimentacao);
        }
    }

    private void notifyCardClicked() {
        for (MovementCardListener listener : listeners) {
            listener.onCardClicked(movimentacao);
        }
    }

    public Movimentacao getMovimentacao() {
        return movimentacao;
    }
}

