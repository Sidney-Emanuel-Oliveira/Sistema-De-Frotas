package br.com.ui.components;

import br.com.model.Veiculo;
import br.com.ui.ModernColors;
import br.com.ui.RoundedPanel;
import br.com.ui.ModernButton;
import br.com.utils.IconLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Card moderno para exibição de veículos
 */
public class VehicleCard extends RoundedPanel {
    private Veiculo veiculo;
    private List<VehicleCardListener> listeners = new ArrayList<>();
    private boolean isHovered = false;
    private static final Color BORDER_COLOR = new Color(66, 153, 225); // Azul suave
    private static final Color HOVER_COLOR = new Color(235, 245, 255); // Azul clarinho bem sutil
    private static final Color HOVER_BORDER_COLOR = new Color(66, 153, 225); // Mesmo azul suave no hover

    public interface VehicleCardListener {
        void onEditClicked(Veiculo veiculo);
        void onDeleteClicked(Veiculo veiculo);
        void onCardClicked(Veiculo veiculo);
    }

    public VehicleCard(Veiculo veiculo) {
        super(12, ModernColors.WHITE);
        this.veiculo = veiculo;
        setPreferredSize(new Dimension(280, 200));
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        setOpaque(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Adicionar mouse listener ao card inteiro
        MouseAdapter mouseAdapter = new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                setBackgroundColor(HOVER_COLOR);
                repaint();
                revalidate();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                setBackgroundColor(ModernColors.WHITE);
                repaint();
                revalidate();
            }

            @Override
            public void mouseClicked(MouseEvent e) {
                notifyCardClicked();
            }
        };

        addMouseListener(mouseAdapter);
        addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (!isHovered) {
                    isHovered = true;
                    setBackgroundColor(HOVER_COLOR);
                    repaint();
                }
            }
        });

        initializeComponents();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int arcRadius = 12;
        int shadowSize = 4;

        // Desenhar bordinha azul sutil e elegante
        if (isHovered) {
            g2d.setColor(HOVER_BORDER_COLOR);
            g2d.setStroke(new BasicStroke(3f)); // 3px no hover
        } else {
            g2d.setColor(BORDER_COLOR);
            g2d.setStroke(new BasicStroke(2.5f)); // 2.5px normal
        }
        g2d.drawRoundRect(2, 2, getWidth() - shadowSize - 4, getHeight() - shadowSize - 4,
            arcRadius, arcRadius);
    }

    private void initializeComponents() {
        // Painel superior - Ícone + Info principal
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setOpaque(false);

        // Ícone do veículo - Carregar imagem PNG real
        ImageIcon icon = IconLoader.loadIconForType(veiculo.getTipo(), 48, 48);
        JLabel iconLabel = new JLabel(icon, JLabel.CENTER);
        iconLabel.setHorizontalAlignment(JLabel.CENTER);
        iconLabel.setVerticalAlignment(JLabel.CENTER);
        topPanel.add(iconLabel, BorderLayout.WEST);

        // Informações principais
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setOpaque(false);

        JLabel placaLabel = new JLabel(veiculo.getPlaca() + " • " + veiculo.getTipo());
        placaLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        placaLabel.setForeground(ModernColors.DARK_GRAY);

        JLabel modeloLabel = new JLabel(veiculo.getMarca() + " " + veiculo.getModelo() + " • " + veiculo.getFabricateYear());
        modeloLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        modeloLabel.setForeground(ModernColors.TEXT_GRAY);

        infoPanel.add(placaLabel);
        infoPanel.add(modeloLabel);

        topPanel.add(infoPanel, BorderLayout.CENTER);
        add(topPanel, BorderLayout.NORTH);

        // Painel do meio - Status
        JLabel statusLabel = new JLabel();
        statusLabel.setHorizontalAlignment(JLabel.LEFT);
        statusLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        statusLabel.setOpaque(true);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));

        if (veiculo.getAtivo()) {
            statusLabel.setText("● Ativo");
            statusLabel.setBackground(ModernColors.SUCCESS_GREEN);
        } else {
            statusLabel.setText("● Inativo");
            statusLabel.setBackground(ModernColors.TEXT_GRAY);
        }
        statusLabel.setForeground(ModernColors.WHITE);

        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statusPanel.setOpaque(false);
        statusPanel.add(statusLabel);

        add(statusPanel, BorderLayout.CENTER);

        // Painel inferior - Botões
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        buttonPanel.setOpaque(false);

        ModernButton editBtn = new ModernButton("Editar", ModernColors.PRIMARY_BLUE);
        editBtn.setPreferredSize(new Dimension(75, 30));
        editBtn.addActionListener(e -> notifyEditClicked());

        ModernButton deleteBtn = new ModernButton("Excluir", ModernColors.DANGER_RED);
        deleteBtn.setPreferredSize(new Dimension(75, 30));
        deleteBtn.addActionListener(e -> notifyDeleteClicked());

        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void addListener(VehicleCardListener listener) {
        listeners.add(listener);
    }

    private void notifyEditClicked() {
        for (VehicleCardListener listener : listeners) {
            listener.onEditClicked(veiculo);
        }
    }

    private void notifyDeleteClicked() {
        for (VehicleCardListener listener : listeners) {
            listener.onDeleteClicked(veiculo);
        }
    }

    private void notifyCardClicked() {
        for (VehicleCardListener listener : listeners) {
            listener.onCardClicked(veiculo);
        }
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }
}

