package com.algaworks.api.service;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import com.algaworks.api.model.Pessoa;
import com.algaworks.api.repository.PessoaRepository;

@Service
public class PessoaService {

	@Autowired
	private PessoaRepository pessoaRepository;

	public Pessoa atualizar(Long codigo, Pessoa pessoa) {

		Pessoa pessoaSalvar = buscaPessoaPeloCodigo(codigo);
		BeanUtils.copyProperties(pessoa, pessoaSalvar, "codigo");
		return pessoaRepository.save(pessoaSalvar);
	}

	public void atualizarPropriedadeAtivo(Long codigo, Boolean ativo) {
		Pessoa pessoaSalvar = buscaPessoaPeloCodigo(codigo);
		pessoaSalvar.setAtivo(ativo);
		pessoaRepository.save(pessoaSalvar);
	}

	public Pessoa buscaPessoaPeloCodigo(Long codigo) {
		Pessoa pessoaSalvar = pessoaRepository.findOne(codigo);
		if (pessoaSalvar == null) {
			throw new EmptyResultDataAccessException(1);
		}
		return pessoaSalvar;
	}
}
