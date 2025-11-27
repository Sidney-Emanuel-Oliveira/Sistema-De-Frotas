package br.com.ui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxUI;
import java.awt.*;

/**
 * ComboBox moderno com cores vibrantes
 */
public class ModernComboBox<T> extends JComboBox<T> {

    public ModernComboBox() {
        super();
        personalizarComboBox();
    }

    public ModernComboBox(ComboBoxModel<T> aModel) {
        super(aModel);
        personalizarComboBox();
    }

    private void personalizarComboBox() {
        setFont(new Font("Segoe UI", Font.PLAIN, 11));
        setBackground(ModernColors.WHITE);
        setForeground(ModernColors.DARK_GRAY);
        setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(ModernColors.BORDER_GRAY, 1),
            BorderFactory.createEmptyBorder(6, 8, 6, 8)
        ));
        setPreferredSize(new Dimension(150, 32));

        // Renderizador customizado
        setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                         boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if (isSelected) {
                    setBackground(ModernColors.PRIMARY_BLUE);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(ModernColors.DARK_GRAY);
                }

                setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
                return c;
            }
        });
    }
}

