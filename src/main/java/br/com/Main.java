package br.com;

import br.com.view.TelaPrincipal;

/**
 * Classe principal do Sistema de Gerenciamento de Frotas
 *
 * Esta é a classe de entrada da aplicação. Ela inicializa a interface gráfica
 * chamando a TelaPrincipal, que é a janela principal do sistema.
 *
 * Funcionalidades do Sistema:
 * - Cadastro e gerenciamento de veículos
 * - Registro de movimentações (despesas)
 * - Geração de relatórios e análises matriciais
 * - Exportação de dados em CSV
 *
 * @author Sidney Emanuel Oliveira
 * @version 1.0
 */
public class Main {

    /**
     * Método principal que inicia a aplicação
     *
     * @param args Argumentos da linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        // Inicia a interface gráfica do sistema
        TelaPrincipal.main(args);
    }
}