package br.com.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Painel customizado com bordas arredondadas e sombra leve
 */
public class RoundedPanel extends JPanel {
    private int arcRadius = 12;
    private Color shadowColor = new Color(0, 0, 0, 15);
    private int shadowSize = 4;
    private Color backgroundColor = Color.WHITE;

    public RoundedPanel() {
        this.setOpaque(false);
        this.setLayout(null);
    }

    public RoundedPanel(LayoutManager layout) {
        this.setOpaque(false);
        this.setLayout(layout);
    }

    public RoundedPanel(int arcRadius) {
        this();
        this.arcRadius = arcRadius;
    }

    public RoundedPanel(int arcRadius, Color backgroundColor) {
        this();
        this.arcRadius = arcRadius;
        this.backgroundColor = backgroundColor;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Desenhar sombra
        g2d.setColor(shadowColor);
        g2d.fillRoundRect(shadowSize, shadowSize,
            getWidth() - shadowSize * 2, getHeight() - shadowSize * 2,
            arcRadius, arcRadius);

        // Desenhar fundo arredondado
        g2d.setColor(backgroundColor);
        g2d.fillRoundRect(0, 0, getWidth() - shadowSize, getHeight() - shadowSize,
            arcRadius, arcRadius);

        super.paintComponent(g);
    }

    public void setBackgroundColor(Color color) {
        this.backgroundColor = color;
        repaint();
    }

    public void setArcRadius(int arcRadius) {
        this.arcRadius = arcRadius;
        repaint();
    }

    public void setShadowSize(int shadowSize) {
        this.shadowSize = shadowSize;
        repaint();
    }
}

