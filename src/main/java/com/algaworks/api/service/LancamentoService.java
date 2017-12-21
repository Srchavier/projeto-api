package com.algaworks.api.service;

import java.util.Optional;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.algaworks.api.model.Lancamento;
import com.algaworks.api.model.Pessoa;
import com.algaworks.api.repository.LancamentoRepository;
import com.algaworks.api.repository.PessoaRepository;
import com.algaworks.api.service.exception.PessoaInxistenteOuInativaExcetion;

@Service
public class LancamentoService {

	@Autowired
	private PessoaRepository pessoaRepository;

	@Autowired
	private LancamentoRepository lancamentoRepository;

	public Lancamento salvar(Lancamento lancamento) {
		Pessoa pessoa = pessoaRepository.findOne(Optional.ofNullable(lancamento.getPessoa().getCodigo()).orElse(0L));
		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInxistenteOuInativaExcetion();
		}

		return lancamentoRepository.save(lancamento);
	}

	public Lancamento atualizar(Long codigo, Lancamento lancamento) {
		Lancamento lancamentoSalvo = buscarLancamentoExistente(codigo);
		if (!lancamento.getPessoa().equals(lancamentoSalvo.getPessoa())) {
			validarPessoa(lancamento);
		}

		BeanUtils.copyProperties(lancamento, lancamentoSalvo, "codigo");

		return lancamentoRepository.save(lancamentoSalvo);
	}

	private void validarPessoa(Lancamento lancamento) {
		Pessoa pessoa = null;

		if (lancamento.getPessoa().getCodigo() != null) {
			pessoa = pessoaRepository.findOne(lancamento.getPessoa().getCodigo());
		}

		if (pessoa == null || pessoa.isInativo()) {
			throw new PessoaInxistenteOuInativaExcetion();
		}

	}

	private Lancamento buscarLancamentoExistente(Long codigo) {

		Lancamento lancamentoSalvo = lancamentoRepository.findOne(codigo);
		if (lancamentoSalvo == null) {
			throw new IllegalArgumentException();
		}

		return lancamentoSalvo;
	}

}
