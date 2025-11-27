package br.com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;

/**
 * TabbedPane customizado para as abas de Texto/Tabela dentro de painéis
 */
public class ModernInnerTabbedPane extends JTabbedPane {

    public ModernInnerTabbedPane() {
        super();
        setUI(new ModernInnerTabbedPaneUI());
        setFont(new Font("Segoe UI", Font.BOLD, 12));
        setBackground(ModernColors.WHITE);
        setForeground(ModernColors.DARK_GRAY);
        setBorder(BorderFactory.createEmptyBorder());
    }

    /**
     * UI customizado para as abas internas
     */
    private static class ModernInnerTabbedPaneUI extends BasicTabbedPaneUI {

        @Override
        protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex,
                                          int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                // Aba selecionada - Branco com fundo azul suave
                g2d.setColor(ModernColors.PRIMARY_BLUE);
                g2d.fillRect(x, y, w, h - 2);
            } else {
                // Aba não selecionada - Transparente
                g2d.setColor(ModernColors.BG_SECONDARY);
                g2d.fillRect(x, y, w, h - 2);
            }
        }

        @Override
        protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex,
                                      int x, int y, int w, int h, boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (isSelected) {
                // Borda inferior para aba selecionada
                g2d.setColor(ModernColors.PRIMARY_BLUE);
                g2d.setStroke(new BasicStroke(3));
                g2d.drawLine(x, y + h - 2, x + w, y + h - 2);
            } else {
                // Borda sutil
                g2d.setColor(ModernColors.BORDER_GRAY);
                g2d.setStroke(new BasicStroke(1));
                g2d.drawLine(x, y + h - 2, x + w, y + h - 2);
            }
        }

        @Override
        protected void paintText(Graphics g, int tabPlacement, Font font, FontMetrics metrics,
                                 int tabIndex, String title, Rectangle textRect,
                                 boolean isSelected) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            g2d.setFont(font);

            if (isSelected) {
                g2d.setColor(Color.WHITE);
            } else {
                g2d.setColor(ModernColors.TEXT_GRAY);
            }

            int x = textRect.x + 8;
            int y = textRect.y + metrics.getAscent();
            g2d.drawString(title, x, y);
        }

        @Override
        protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
            return fontHeight + 14;
        }

        @Override
        protected int calculateTabWidth(int tabPlacement, int tabIndex, FontMetrics metrics) {
            return super.calculateTabWidth(tabPlacement, tabIndex, metrics) + 25;
        }
    }
}

