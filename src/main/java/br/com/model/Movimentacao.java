package br.com.model;

public class Movimentacao {
    private Long idMovimentacao;
    private Long idVeiculo;
    private Long idTipoDespesa;
    private String descricao;
    private String data;
    private double valor;
    private String tipo;

    public Movimentacao() {
    }

    public Movimentacao(Long idMovimentacao, Long idVeiculo, Long idTipoDespesa, String descricao, String data, double valor, String tipo) {
        this.idMovimentacao = idMovimentacao;
        this.idVeiculo = idVeiculo;
        this.idTipoDespesa = idTipoDespesa;
        this.descricao = descricao;
        this.data = data;
        this.valor = valor;
        this.tipo = tipo;
    }

    // Getters e Setters
    public Long getIdMovimentacao() {
        return idMovimentacao;
    }

    public void setIdMovimentacao(Long idMovimentacao) {
        this.idMovimentacao = idMovimentacao;
    }

    public Long getIdVeiculo() {
        return idVeiculo;
    }

    public void setIdVeiculo(Long idVeiculo) {
        this.idVeiculo = idVeiculo;
    }

    public Long getIdTipoDespesa() {
        return idTipoDespesa;
    }

    public void setIdTipoDespesa(Long idTipoDespesa) {
        this.idTipoDespesa = idTipoDespesa;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    @Override
    public String toString() {
        return "Movimentacao{" +
                "idMovimentacao=" + idMovimentacao +
                ", idVeiculo=" + idVeiculo +
                ", idTipoDespesa=" + idTipoDespesa +
                ", descricao='" + descricao + '\'' +
                ", data='" + data + '\'' +
                ", valor=" + valor +
                ", tipo='" + tipo + '\'' +
                '}';
    }
}

