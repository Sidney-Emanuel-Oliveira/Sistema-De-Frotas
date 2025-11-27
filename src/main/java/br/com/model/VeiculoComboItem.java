package br.com.model;

/**
 * Classe wrapper para permitir adicionar "Todos" no ComboBox de veículos
 */
public class VeiculoComboItem {
    private final Veiculo veiculo;
    private final String texto;
    private final boolean isTodos;

    // Construtor para "Todos"
    public VeiculoComboItem() {
        this.veiculo = null;
        this.texto = "Todos";
        this.isTodos = true;
    }

    // Construtor para veículo específico
    public VeiculoComboItem(Veiculo veiculo) {
        this.veiculo = veiculo;
        this.texto = veiculo.getPlaca() + " - " + veiculo.getMarca() + " " + veiculo.getModelo();
        this.isTodos = false;
    }

    public Veiculo getVeiculo() {
        return veiculo;
    }

    public boolean isTodos() {
        return isTodos;
    }

    @Override
    public String toString() {
        return texto;
    }
}

