package br.com.controller;

import br.com.dao.VeiculoDAO;
import br.com.model.Veiculo;
import br.com.utils.Validacoes;

import java.io.IOException;
import java.util.List;

public class VeiculoController {
    private VeiculoDAO veiculoDAO;

    public VeiculoController() {
        this.veiculoDAO = new VeiculoDAO();
    }

    public void salvarVeiculo(String placa, String marca, String modelo, String ano, Boolean ativo, String tipo) throws IOException {
        // Validações
        if (!Validacoes.validarCampoVazio(placa) ||
            !Validacoes.validarCampoVazio(marca) ||
            !Validacoes.validarCampoVazio(modelo) ||
            !Validacoes.validarAno(ano)) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        // Verificar duplicação de placa
        Veiculo existente = veiculoDAO.obterPorPlaca(placa);
        if (existente != null) {
            throw new IllegalArgumentException("Placa já cadastrada!");
        }

        // Criar novo veiculo
        Long novoId = veiculoDAO.obterProximoId();
        Veiculo veiculo = new Veiculo(novoId, placa, marca, modelo, ano, ativo, tipo);
        veiculoDAO.salvar(veiculo);
    }

    public void atualizarVeiculo(Long id, String placa, String marca, String modelo, String ano, Boolean ativo, String tipo) throws IOException {
        if (!Validacoes.validarCampoVazio(placa) ||
            !Validacoes.validarCampoVazio(marca) ||
            !Validacoes.validarCampoVazio(modelo) ||
            !Validacoes.validarAno(ano)) {
            throw new IllegalArgumentException("Campos inválidos ou vazios!");
        }

        // Verificar se placa já existe em outro veiculo
        Veiculo existente = veiculoDAO.obterPorPlaca(placa);
        if (existente != null && !existente.getIdVeiculo().equals(id)) {
            throw new IllegalArgumentException("Placa já cadastrada por outro veículo!");
        }

        Veiculo veiculo = new Veiculo(id, placa, marca, modelo, ano, ativo, tipo);
        veiculoDAO.salvar(veiculo);
    }

    public List<Veiculo> obterTodosVeiculos() throws IOException {
        return veiculoDAO.obterTodos();
    }

    public List<Veiculo> obterVeiculosAtivos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(Veiculo::getAtivo)
                .toList();
    }

    public List<Veiculo> obterVeiculosInativos() throws IOException {
        List<Veiculo> veiculos = veiculoDAO.obterTodos();
        return veiculos.stream()
                .filter(v -> !v.getAtivo())
                .toList();
    }

    public Veiculo obterVeiculoPorId(Long id) throws IOException {
        return veiculoDAO.obterPorId(id);
    }

    public void deletarVeiculo(Long id) throws IOException {
        veiculoDAO.deletar(id);
    }
}

