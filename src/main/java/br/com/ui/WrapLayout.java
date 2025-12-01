package br.com.ui;

import java.awt.*;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

/**
 * WrapLayout - Layout inteligente que quebra componentes automaticamente
 *
 * Esta classe estende FlowLayout para adicionar funcionalidade de quebra automática
 * de linha quando os componentes não cabem na largura do contêiner.
 *
 * Diferença do FlowLayout padrão:
 * - FlowLayout padrão: Pode criar barra de rolagem horizontal
 * - WrapLayout: Sempre quebra para nova linha, sem rolagem horizontal
 *
 * Uso típico:
 * - Exibição de cards de veículos
 * - Galerias de imagens
 * - Painéis com múltiplos botões
 *
 * @author Sidney Emanuel Oliveira
 * @version 1.0
 */
public class WrapLayout extends FlowLayout {

    // ==================== CONSTRUTORES ====================

    /**
     * Cria um WrapLayout com alinhamento centralizado e espaçamento padrão
     */
    public WrapLayout() {
        super();
    }

    /**
     * Cria um WrapLayout com alinhamento específico
     *
     * @param alinhamento Uma das constantes: LEFT, CENTER, RIGHT, LEADING ou TRAILING
     */
    public WrapLayout(int alinhamento) {
        super(alinhamento);
    }

    /**
     * Cria um WrapLayout com configurações personalizadas
     *
     * @param alinhamento Alinhamento dos componentes
     * @param espacamentoHorizontal Espaço horizontal entre componentes
     * @param espacamentoVertical Espaço vertical entre componentes
     */
    public WrapLayout(int alinhamento, int espacamentoHorizontal, int espacamentoVertical) {
        super(alinhamento, espacamentoHorizontal, espacamentoVertical);
    }

    // ==================== MÉTODOS SOBRESCRITOS ====================

    /**
     * Calcula o tamanho preferido do layout considerando quebra de linha
     *
     * @param target O contêiner que está sendo ajustado
     * @return Dimensão preferida do contêiner
     */
    @Override
    public Dimension preferredLayoutSize(Container target) {
        return calcularTamanhoLayout(target, true);
    }

    /**
     * Calcula o tamanho mínimo do layout
     *
     * @param target O contêiner que está sendo ajustado
     * @return Dimensão mínima do contêiner
     */
    @Override
    public Dimension minimumLayoutSize(Container target) {
        Dimension tamanhoMinimo = calcularTamanhoLayout(target, false);
        tamanhoMinimo.width -= (getHgap() + 1);
        return tamanhoMinimo;
    }

    // ==================== CÁLCULOS DE LAYOUT ====================

    /**
     * Calcula o tamanho do layout baseado nos componentes e quebras de linha
     *
     * Algoritmo:
     * 1. Itera sobre cada componente
     * 2. Se não couber na linha atual, cria nova linha
     * 3. Acumula altura total das linhas
     * 4. Retorna dimensão final
     *
     * @param target Contêiner alvo
     * @param usarTamanhoPreferido Se true, usa tamanho preferido; se false, usa mínimo
     * @return Dimensão calculada do layout
     */
    private Dimension calcularTamanhoLayout(Container target, boolean usarTamanhoPreferido) {
        synchronized (target.getTreeLock()) {
            // Determina a largura disponível do contêiner
            // Se a largura for 0, procura o contêiner pai
            int larguraDisponivel = obterLarguraDisponivel(target);

            // Obtém espaçamentos e margens
            int espacoHorizontal = getHgap();
            int espacoVertical = getVgap();
            Insets margens = target.getInsets();
            int margensHorizontais = margens.left + margens.right + (espacoHorizontal * 2);
            int larguraMaximaLinha = larguraDisponivel - margensHorizontais;

            // Dimensão final do contêiner
            Dimension dimensaoFinal = new Dimension(0, 0);

            // Controle da linha atual
            int larguraLinhaAtual = 0;
            int alturaLinhaAtual = 0;

            // Itera sobre cada componente
            int totalComponentes = target.getComponentCount();
            for (int i = 0; i < totalComponentes; i++) {
                Component componente = target.getComponent(i);

                if (componente.isVisible()) {
                    // Obtém dimensão do componente
                    Dimension tamanhoComponente = usarTamanhoPreferido
                        ? componente.getPreferredSize()
                        : componente.getMinimumSize();

                    // Verifica se componente cabe na linha atual
                    if (larguraLinhaAtual + tamanhoComponente.width > larguraMaximaLinha) {
                        // Não cabe: adiciona linha atual e inicia nova linha
                        adicionarLinha(dimensaoFinal, larguraLinhaAtual, alturaLinhaAtual);
                        larguraLinhaAtual = 0;
                        alturaLinhaAtual = 0;
                    }

                    // Adiciona espaço horizontal entre componentes (exceto o primeiro)
                    if (larguraLinhaAtual != 0) {
                        larguraLinhaAtual += espacoHorizontal;
                    }

                    // Adiciona componente à linha atual
                    larguraLinhaAtual += tamanhoComponente.width;
                    alturaLinhaAtual = Math.max(alturaLinhaAtual, tamanhoComponente.height);
                }
            }

            // Adiciona a última linha
            adicionarLinha(dimensaoFinal, larguraLinhaAtual, alturaLinhaAtual);

            // Adiciona margens ao tamanho final
            dimensaoFinal.width += margensHorizontais;
            dimensaoFinal.height += margens.top + margens.bottom + espacoVertical * 2;

            // Ajuste especial para JScrollPane
            Container scrollPane = SwingUtilities.getAncestorOfClass(JScrollPane.class, target);
            if (scrollPane != null && target.isValid()) {
                dimensaoFinal.width -= (espacoHorizontal + 1);
            }

            return dimensaoFinal;
        }
    }

    /**
     * Obtém a largura disponível do contêiner, procurando no contêiner pai se necessário
     */
    private int obterLarguraDisponivel(Container target) {
        int largura = target.getSize().width;
        Container container = target;

        // Procura contêiner pai com largura definida
        while (container.getSize().width == 0 && container.getParent() != null) {
            container = container.getParent();
        }

        largura = container.getSize().width;

        // Se ainda não tiver largura, usa valor máximo
        if (largura == 0) {
            largura = Integer.MAX_VALUE;
        }

        return largura;
    }

    /**
     * Adiciona uma linha ao cálculo da dimensão final
     *
     * Atualiza a largura máxima e altura total considerando
     * a linha que está sendo adicionada
     *
     * @param dimensaoFinal Dimensão sendo calculada
     * @param larguraLinha Largura da linha a adicionar
     * @param alturaLinha Altura da linha a adicionar
     */
    private void adicionarLinha(Dimension dimensaoFinal, int larguraLinha, int alturaLinha) {
        // Atualiza largura máxima
        dimensaoFinal.width = Math.max(dimensaoFinal.width, larguraLinha);

        // Adiciona espaço vertical entre linhas (se não for a primeira)
        if (dimensaoFinal.height > 0) {
            dimensaoFinal.height += getVgap();
        }

        // Adiciona altura da linha
        dimensaoFinal.height += alturaLinha;
    }
}

