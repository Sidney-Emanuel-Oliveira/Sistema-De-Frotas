package br.com.controller;

import br.com.dao.MovimentacaoDAO;
import br.com.model.Movimentacao;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.util.List;

public class MovimentacaoController {
    private MovimentacaoDAO movimentacaoDAO;

    public MovimentacaoController() {
        this.movimentacaoDAO = new MovimentacaoDAO();
    }

    public void salvarMovimentacao(Long idVeiculo, Long idTipoDespesa, String descricao,
                                  String data, String valor, String tipo) throws IOException {
        // Validações
        if (!Validacoes.validarCampoVazio(descricao) ||
            !Validacoes.validarData(data) ||
            !Validacoes.validarValor(valor) ||
            idVeiculo == null ||
            idTipoDespesa == null) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        Long novoId = movimentacaoDAO.obterProximoId();
        double valorParsed = Double.parseDouble(valor.replace(",", "."));
        Movimentacao movimentacao = new Movimentacao(novoId, idVeiculo, idTipoDespesa,
                                                      descricao, data, valorParsed, tipo);
        movimentacaoDAO.salvar(movimentacao);
    }

    public void atualizarMovimentacao(Long id, Long idVeiculo, Long idTipoDespesa, String descricao,
                                     String data, String valor, String tipo) throws IOException {
        if (!Validacoes.validarCampoVazio(descricao) ||
            !Validacoes.validarData(data) ||
            !Validacoes.validarValor(valor) ||
            idVeiculo == null ||
            idTipoDespesa == null) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        double valorParsed = Double.parseDouble(valor.replace(",", "."));
        Movimentacao movimentacao = new Movimentacao(id, idVeiculo, idTipoDespesa,
                                                      descricao, data, valorParsed, tipo);
        movimentacaoDAO.salvar(movimentacao);
    }

    public List<Movimentacao> obterTodasMovimentacoes() throws IOException {
        return movimentacaoDAO.obterTodos();
    }

    public List<Movimentacao> obterMovimentacoesPorVeiculo(Long idVeiculo) throws IOException {
        return movimentacaoDAO.obterPorVeiculo(idVeiculo);
    }

    public List<Movimentacao> obterMovimentacoesPorTipo(String tipo) throws IOException {
        return movimentacaoDAO.obterPorTipo(tipo);
    }

    public Movimentacao obterMovimentacaoPorId(Long id) throws IOException {
        return movimentacaoDAO.obterPorId(id);
    }

    public void deletarMovimentacao(Long id) throws IOException {
        movimentacaoDAO.deletar(id);
    }
}

