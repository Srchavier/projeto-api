package com.algaworks.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.algaworks.api.model.Lancamento;
import com.algaworks.api.repository.lancamento.LancamentoRepositoryQuery;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>, LancamentoRepositoryQuery {

}
