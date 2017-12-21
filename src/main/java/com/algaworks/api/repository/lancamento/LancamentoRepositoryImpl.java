package com.algaworks.api.repository.lancamento;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.algaworks.api.model.Categoria_;
import com.algaworks.api.model.Lancamento;
import com.algaworks.api.model.Lancamento_;
import com.algaworks.api.model.Pessoa_;
import com.algaworks.api.repository.filter.LancamentoFilter;
import com.algaworks.api.repository.projection.ResumoLancamento;

public class LancamentoRepositoryImpl implements LancamentoRepositoryQuery {

	@PersistenceContext
	private EntityManager manager;

	@Override
	public Page<Lancamento> filtrar(LancamentoFilter lancamentoFilter, Pageable pageable) {

		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<Lancamento> criteria = builder.createQuery(Lancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		// criar retriçoes
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<Lancamento> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	@Override
	public Page<ResumoLancamento> resumir(LancamentoFilter lancamentoFilter, Pageable pageable) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();
		CriteriaQuery<ResumoLancamento> criteria = builder.createQuery(ResumoLancamento.class);
		Root<Lancamento> root = criteria.from(Lancamento.class);

		criteria.select(builder.construct(ResumoLancamento.class, root.get(Lancamento_.codigo),
				root.get(Lancamento_.descricao), root.get(Lancamento_.dataVencimento),
				root.get(Lancamento_.dataPagamento), root.get(Lancamento_.valor), root.get(Lancamento_.tipo),
				root.get(Lancamento_.categoria).get(Categoria_.nome), root.get(Lancamento_.pessoa).get(Pessoa_.nome)));

		// criar retriçoes
		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);
		criteria.where(predicates);

		TypedQuery<ResumoLancamento> query = manager.createQuery(criteria);
		adicionarRestricoesDePaginacao(query, pageable);

		return new PageImpl<>(query.getResultList(), pageable, total(lancamentoFilter));
	}

	private Predicate[] criarRestricoes(LancamentoFilter lancamentoFilter, CriteriaBuilder builder,
			Root<Lancamento> root) {

		List<Predicate> predicate = new ArrayList<>();

		if (!org.springframework.util.StringUtils.isEmpty(lancamentoFilter.getDescricao())) {
			predicate.add(builder.like(builder.lower(root.get(Lancamento_.descricao)),
					"%" + lancamentoFilter.getDescricao().toLowerCase() + "%"));
		}

		if (lancamentoFilter.getDataVencimentoDe() != null) {

			predicate.add(builder.greaterThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoDe()));
		}

		if (lancamentoFilter.getDataVencimentoAte() != null) {
			predicate.add(builder.lessThanOrEqualTo(root.get(Lancamento_.dataVencimento),
					lancamentoFilter.getDataVencimentoAte()));
		}
		return predicate.toArray(new Predicate[predicate.size()]);
	}

	private void adicionarRestricoesDePaginacao(TypedQuery<?> query, Pageable pageable) {
		int paginaAtual = pageable.getPageNumber();
		int totalDeRegristoPorPagina = pageable.getPageSize();
		int primeiroRegistroDaPagina = paginaAtual * totalDeRegristoPorPagina;

		query.setFirstResult(primeiroRegistroDaPagina);
		query.setMaxResults(totalDeRegristoPorPagina);
	}

	private Long total(LancamentoFilter lancamentoFilter) {
		CriteriaBuilder builder = manager.getCriteriaBuilder();// cria a criteria
		CriteriaQuery<Long> criteria = builder.createQuery(Long.class);// informa de que tipo e a query
		Root<Lancamento> root = criteria.from(Lancamento.class);// query vai ser qual classe

		Predicate[] predicates = criarRestricoes(lancamentoFilter, builder, root);// cria as retriçoes
		criteria.where(predicates);// passa as retriçoes

		criteria.select(builder.count(root));// faz o select na classe
		return manager.createQuery(criteria).getSingleResult();// passa o resultado
	}

}
