package br.com.view;

import br.com.controller.VeiculoController;
import br.com.model.Veiculo;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.ui.WrapLayout;
import br.com.ui.components.VehicleCard;
import br.com.ui.components.VehicleDetailsPanel;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.List;

public class TelaCadastroVeiculo extends JPanel {
    private VeiculoController controller;
    private JPanel mainPanel;
    private JScrollPane scrollCardsPanel;
    private JPanel detailsPanel;
    private JButton btnNovoVeiculo;
    private JTextField txtPesquisaPlaca;
    private JPanel cardsPanel;

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.PLAIN, 13);

    public TelaCadastroVeiculo() {
        controller = new VeiculoController();
        setLayout(new BorderLayout());
        setBackground(ModernColors.BG_PRIMARY);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(ModernColors.BG_PRIMARY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel headerPanel = criarPainelCabecalho();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        scrollCardsPanel = criarPainelCards();
        mainPanel.add(scrollCardsPanel, BorderLayout.CENTER);

        detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setBackground(ModernColors.BG_PRIMARY);
        detailsPanel.setVisible(false);

        CardLayout layout = new CardLayout();
        JPanel container = new JPanel(layout);
        container.add(mainPanel, "lista");
        container.add(detailsPanel, "detalhes");

        add(container, BorderLayout.CENTER);
    }

    private JPanel criarPainelCabecalho() {
        RoundedPanel panel = new RoundedPanel(8, ModernColors.WHITE);
        panel.setLayout(new BorderLayout(20, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        panel.setPreferredSize(new Dimension(0, 80));

        // Esquerda: Título
        JLabel titulo = new JLabel("Veículos");
        titulo.setFont(TITLE_FONT);
        titulo.setForeground(ModernColors.DARK_GRAY);

        // Centro: Barra de pesquisa - Melhorada e mais destacada
        JPanel pesquisaPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
        pesquisaPanel.setOpaque(false);

        // Carregar ícone de busca
        ImageIcon searchIcon = IconLoader.loadSearchIcon(24, 24);
        JLabel iconPesquisa = new JLabel(searchIcon);

        JLabel lblPesquisa = new JLabel("Pesquisar por Placa:");
        lblPesquisa.setFont(new Font("Segoe UI", Font.BOLD, 14));
        lblPesquisa.setForeground(ModernColors.DARK_GRAY);

        txtPesquisaPlaca = new JTextField();
        txtPesquisaPlaca.setPreferredSize(new Dimension(250, 38));
        txtPesquisaPlaca.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        txtPesquisaPlaca.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernColors.PRIMARY_BLUE, 2),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        txtPesquisaPlaca.setBackground(Color.WHITE);
        txtPesquisaPlaca.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
                public void keyReleased(java.awt.event.KeyEvent e) {
                filtrarVeiculosPorPlaca();
            }
        });

        pesquisaPanel.add(iconPesquisa);
        pesquisaPanel.add(lblPesquisa);
        pesquisaPanel.add(txtPesquisaPlaca);

        // Direita: Botão Novo
        btnNovoVeiculo = new ModernButton("+ Novo", ModernColors.PRIMARY_BLUE);
        btnNovoVeiculo.setPreferredSize(new Dimension(120, 40));
        btnNovoVeiculo.addActionListener(e -> abrirDialogNovoVeiculo());

        panel.add(titulo, BorderLayout.WEST);
        panel.add(pesquisaPanel, BorderLayout.CENTER);
        panel.add(btnNovoVeiculo, BorderLayout.EAST);

        return panel;
    }

    private JScrollPane criarPainelCards() {
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new WrapLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(ModernColors.BG_PRIMARY);

        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBackground(ModernColors.BG_PRIMARY);
        scrollPane.getViewport().setBackground(ModernColors.BG_PRIMARY);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

        carregarCardsVeiculos(cardsPanel);

        return scrollPane;
    }

    private void carregarCardsVeiculos(JPanel cardsPanel) {
        cardsPanel.removeAll();
        try {
            List<Veiculo> veiculos = controller.obterTodosVeiculos();

            if (veiculos.isEmpty()) {
                JLabel emptyLabel = new JLabel("Nenhum veículo cadastrado");
                emptyLabel.setFont(SUBTITLE_FONT);
                emptyLabel.setForeground(ModernColors.TEXT_GRAY);
                cardsPanel.add(emptyLabel);
            } else {
                for (Veiculo v : veiculos) {
                    VehicleCard card = new VehicleCard(v);
                    card.addListener(new VehicleCard.VehicleCardListener() {
                        @Override
                        public void onEditClicked(Veiculo veiculo) {
                            abrirDialogEditarVeiculo(veiculo);
                        }

                        @Override
                        public void onDeleteClicked(Veiculo veiculo) {
                            confirmarDelecao(veiculo);
                        }

                        @Override
                        public void onCardClicked(Veiculo veiculo) {
                            exibirDetalhesVeiculo(veiculo);
                        }
                    });
                    cardsPanel.add(card);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao carregar veículos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }

    private void exibirDetalhesVeiculo(Veiculo veiculo) {
        detailsPanel.removeAll();

        VehicleDetailsPanel detailsView = new VehicleDetailsPanel(veiculo);
        detailsView.setOnBackCallback(() -> {
            Component comp = scrollCardsPanel.getViewport().getView();
            if (comp instanceof JPanel) {
                carregarCardsVeiculos((JPanel) comp);
            }
            detailsPanel.removeAll();
            detailsPanel.setVisible(false);
            mainPanel.setVisible(true);
            revalidate();
            repaint();
        });
        detailsView.setOnEditCallback(() -> {
            abrirDialogEditarVeiculo(veiculo);
        });

        detailsPanel.add(detailsView, BorderLayout.CENTER);
        detailsPanel.setVisible(true);
        mainPanel.setVisible(false);
        revalidate();
        repaint();
    }

    private void abrirDialogNovoVeiculo() {
        VehicleFormDialog dialog = new VehicleFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), null);
        dialog.setCallback(() -> {
            Component comp = scrollCardsPanel.getViewport().getView();
            if (comp instanceof JPanel) {
                carregarCardsVeiculos((JPanel) comp);
            }
        });
        dialog.setVisible(true);
    }

    private void abrirDialogEditarVeiculo(Veiculo veiculo) {
        VehicleFormDialog dialog = new VehicleFormDialog((JFrame) SwingUtilities.getWindowAncestor(this), veiculo);
        dialog.setCallback(() -> {
            if (detailsPanel.isVisible()) {
                detailsPanel.removeAll();
                detailsPanel.setVisible(false);
                mainPanel.setVisible(true);
            }
            Component comp = scrollCardsPanel.getViewport().getView();
            if (comp instanceof JPanel) {
                carregarCardsVeiculos((JPanel) comp);
            }
            revalidate();
            repaint();
        });
        dialog.setVisible(true);
    }

    private void confirmarDelecao(Veiculo veiculo) {
        int resultado = JOptionPane.showConfirmDialog(this,
                "Deseja deletar o veículo " + veiculo.getPlaca() + "?",
                "Confirmar Exclusão",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (resultado == JOptionPane.YES_OPTION) {
            try {
                controller.deletarVeiculo(veiculo.getIdVeiculo());
                JOptionPane.showMessageDialog(this, "Veículo deletado com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
                Component comp = scrollCardsPanel.getViewport().getView();
                if (comp instanceof JPanel) {
                    carregarCardsVeiculos((JPanel) comp);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Erro ao deletar: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void filtrarVeiculosPorPlaca() {
        String placaFiltro = txtPesquisaPlaca.getText().trim().toUpperCase();
        cardsPanel.removeAll();

        try {
            List<Veiculo> veiculos = controller.obterTodosVeiculos();
            List<Veiculo> veiculosFiltrados;

            if (placaFiltro.isEmpty()) {
                veiculosFiltrados = veiculos;
            } else {
                veiculosFiltrados = veiculos.stream()
                        .filter(v -> v.getPlaca().toUpperCase().contains(placaFiltro))
                        .toList();
            }

            if (veiculosFiltrados.isEmpty()) {
                JLabel emptyLabel = new JLabel("Nenhum veículo encontrado com a placa: " + placaFiltro);
                emptyLabel.setFont(SUBTITLE_FONT);
                emptyLabel.setForeground(ModernColors.TEXT_GRAY);
                cardsPanel.add(emptyLabel);
            } else {
                for (Veiculo v : veiculosFiltrados) {
                    VehicleCard card = new VehicleCard(v);
                    card.addListener(new VehicleCard.VehicleCardListener() {
                        @Override
                        public void onEditClicked(Veiculo veiculo) {
                            abrirDialogEditarVeiculo(veiculo);
                        }

                        @Override
                        public void onDeleteClicked(Veiculo veiculo) {
                            confirmarDelecao(veiculo);
                        }

                        @Override
                        public void onCardClicked(Veiculo veiculo) {
                            exibirDetalhesVeiculo(veiculo);
                        }
                    });
                    cardsPanel.add(card);
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Erro ao filtrar veículos: " + e.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
        }

        cardsPanel.revalidate();
        cardsPanel.repaint();
    }
}

