package br.com.ui;

import java.awt.*;

/**
 * Paleta de cores moderna e centralizada
 */
public class ModernColors {
    // Cores principais
    public static final Color PRIMARY_BLUE = new Color(45, 140, 255);      // #2D8CFF
    public static final Color ACCENT_BLUE = new Color(33, 150, 243);       // #2196F3
    public static final Color LIGHT_BLUE = new Color(232, 245, 255);       // #E8F5FF

    // Cores de status
    public static final Color SUCCESS_GREEN = new Color(68, 199, 103);     // #44C767
    public static final Color DANGER_RED = new Color(231, 76, 60);         // #E74C3C
    public static final Color WARNING_ORANGE = new Color(255, 152, 0);     // #FF9800
    public static final Color INFO_BLUE = new Color(52, 168, 224);         // #34A8E0

    // Cores neutras
    public static final Color WHITE = new Color(255, 255, 255);            // #FFFFFF
    public static final Color LIGHT_GRAY = new Color(245, 246, 248);       // #F5F6F8
    public static final Color BORDER_GRAY = new Color(225, 225, 225);      // #E1E1E1
    public static final Color TEXT_GRAY = new Color(120, 120, 120);        // #787878
    public static final Color DARK_GRAY = new Color(80, 80, 80);           // #505050
    public static final Color VERY_LIGHT_GRAY = new Color(250, 250, 250);  // #FAFAFA

    // Backgrounds
    public static final Color BG_PRIMARY = new Color(249, 250, 251);       // #F9FAFB
    public static final Color BG_SECONDARY = new Color(245, 246, 248);     // #F5F6F8

    // Shadows
    public static final Color SHADOW = new Color(0, 0, 0, 12);
    public static final Color SHADOW_LIGHT = new Color(0, 0, 0, 8);

    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
}

