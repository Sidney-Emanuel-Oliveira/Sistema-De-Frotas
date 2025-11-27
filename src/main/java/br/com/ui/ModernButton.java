package br.com.ui;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Botão moderno com bordas arredondadas e efeitos de hover
 */
public class ModernButton extends JButton {
    private Color primaryColor;
    private Color hoverColor;
    private Color pressedColor;
    private int arcRadius = 8;
    private boolean isHovered = false;

    public ModernButton(String text, Color primaryColor) {
        super(text);
        this.primaryColor = primaryColor;
        this.hoverColor = brightColor(primaryColor, 1.1f);
        this.pressedColor = brightColor(primaryColor, 0.9f);

        setOpaque(false);
        setContentAreaFilled(false);
        setBorder(new EmptyBorder(8, 16, 8, 16));
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setForeground(Color.WHITE);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                isHovered = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                isHovered = false;
                repaint();
            }
        });
    }

    private Color brightColor(Color color, float factor) {
        return new Color(
            Math.min(255, (int) (color.getRed() * factor)),
            Math.min(255, (int) (color.getGreen() * factor)),
            Math.min(255, (int) (color.getBlue() * factor))
        );
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Determinar cor baseado no estado
        Color bgColor = primaryColor;
        if (getModel().isPressed()) {
            bgColor = pressedColor;
        } else if (isHovered) {
            bgColor = hoverColor;
        }

        // Desenhar fundo arredondado
        g2d.setColor(bgColor);
        g2d.fillRoundRect(0, 0, getWidth(), getHeight(), arcRadius, arcRadius);

        // Desenhar texto
        FontMetrics fm = g2d.getFontMetrics();
        int stringX = (getWidth() - fm.stringWidth(getText())) / 2;
        int stringY = ((getHeight() - fm.getHeight()) / 2) + fm.getAscent();

        g2d.setColor(getForeground());
        g2d.setFont(getFont());
        g2d.drawString(getText(), stringX, stringY);
    }

    public void setArcRadius(int radius) {
        this.arcRadius = radius;
        repaint();
    }
}

