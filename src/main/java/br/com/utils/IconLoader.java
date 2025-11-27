package br.com.utils;

import javax.swing.*;
import java.awt.*;
import java.net.URL;
import java.io.File;

/**
 * Utilitário para carregar ícones de imagens PNG
 */
public class IconLoader {
    private static final String ICONS_PATH = "src/main/resources/icons/";
    private static final String RESOURCE_PATH = "/icons/";

    /**
     * Carrega um ícone como ImageIcon
     */
    public static ImageIcon loadIcon(String iconName, int width, int height) {
        try {
            // Tentar carrer do classpath (quando compilado)
            URL resource = IconLoader.class.getResource(RESOURCE_PATH + iconName);
            if (resource != null) {
                ImageIcon icon = new ImageIcon(resource);
                // Redimensionar a imagem
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone do classpath: " + iconName);
        }

        // Fallback: tentar carregar do diretório direto
        try {
            File iconFile = new File(ICONS_PATH + iconName);
            if (iconFile.exists()) {
                ImageIcon icon = new ImageIcon(iconFile.getAbsolutePath());
                // Redimensionar a imagem
                Image scaledImage = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(scaledImage);
            }
        } catch (Exception e) {
            System.err.println("Erro ao carregar ícone do arquivo: " + iconName);
        }

        // Se não conseguir carregar, retornar um ícone vazio
        return new ImageIcon();
    }

    /**
     * Carrega ícone de carro
     */
    public static ImageIcon loadCarIcon(int width, int height) {
        return loadIcon("—Pngtree—vector car icon_3989896.png", width, height);
    }

    /**
     * Carrega ícone de caminhão
     */
    public static ImageIcon loadTruckIcon(int width, int height) {
        return loadIcon("—Pngtree—black line drawing delivery truck_4467470.png", width, height);
    }

    /**
     * Carrega ícone de moto
     */
    public static ImageIcon loadMotoIcon(int width, int height) {
        return loadIcon("moto.png", width, height);
    }

    /**
     * Carrega ícone de van
     */
    public static ImageIcon loadVanIcon(int width, int height) {
        return loadIcon("delivery-truck.png", width, height);
    }

    /**
     * Carrega ícone de combustível (bomba de gasolina)
     */
    public static ImageIcon loadCombustivelIcon(int width, int height) {
        return loadIcon("bomba-de-gasolina.png", width, height);
    }

    /**
     * Carrega ícone de seguro (escudo)
     */
    public static ImageIcon loadSeguroIcon(int width, int height) {
        return loadIcon("escudo-seguro.png", width, height);
    }

    /**
     * Carrega ícone de IPVA
     */
    public static ImageIcon loadIPVAIcon(int width, int height) {
        return loadIcon("pngwing.com.png", width, height);
    }

    /**
     * Carrega ícone de lavagem
     */
    public static ImageIcon loadLavagemIcon(int width, int height) {
        return loadIcon("pngegg.png", width, height);
    }

    /**
     * Carrega ícone de manutenção
     */
    public static ImageIcon loadManutencaoIcon(int width, int height) {
        return loadIcon("manutencao.png", width, height);
    }

    /**
     * Carrega ícone de busca (motor de busca)
     */
    public static ImageIcon loadSearchIcon(int width, int height) {
        return loadIcon("motor-de-busca.png", width, height);
    }

    /**
     * Carrega ícone baseado no tipo de veículo
     */
    public static ImageIcon loadIconForType(String tipo, int width, int height) {
        if (tipo == null) return new ImageIcon();

        String lowerType = tipo.toLowerCase();
        if (lowerType.contains("carro") || lowerType.contains("car")) {
            return loadCarIcon(width, height);
        } else if (lowerType.contains("moto")) {
            return loadMotoIcon(width, height);
        } else if (lowerType.contains("caminhão") || lowerType.contains("truck")) {
            return loadTruckIcon(width, height);
        } else if (lowerType.contains("van")) {
            return loadVanIcon(width, height);
        }

        return new ImageIcon();
    }

    /**
     * Carrega ícone baseado no tipo de despesa/movimentação
     */
    public static ImageIcon loadIconForExpenseType(String tipo, int width, int height) {
        if (tipo == null) return new ImageIcon();

        String lowerType = tipo.toLowerCase();
        if (lowerType.contains("combustível") || lowerType.contains("combustivel")) {
            return loadCombustivelIcon(width, height);
        } else if (lowerType.contains("seguro")) {
            return loadSeguroIcon(width, height);
        } else if (lowerType.contains("ipva")) {
            return loadIPVAIcon(width, height);
        } else if (lowerType.contains("lavagem")) {
            return loadLavagemIcon(width, height);
        } else if (lowerType.contains("manutenção") || lowerType.contains("manutencao")) {
            return loadManutencaoIcon(width, height);
        } else if (lowerType.contains("multa")) {
            return loadIcon("notas.png", width, height);
        }

        return new ImageIcon();
    }
}

