package com.bancodedados.projeto.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "venda")
public class Venda {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	LocalDate dataVenda;
	
	@Column(nullable = false)
	float valorDaVenda;

	@ManyToOne
	@JoinColumn(name = "cliente_fk", nullable = false)
	Cliente cliente;

	@ManyToOne
	@JoinColumn(name = "livro_fk", nullable = false)
	Livro livro;

	@ManyToOne
	@JoinColumn(name = "biblioteca_fk")
	Biblioteca biblioteca;

	public Venda(Cliente cliente, Livro livro, LocalDate dataVenda, Biblioteca biblioteca) {
		this.cliente = cliente;
		this.dataVenda = dataVenda;
		this.livro = livro;
		this.biblioteca = biblioteca;
		valorDaVenda = livro.getValor();

	}
	
	public Venda() {
		this.dataVenda = LocalDate.now();
	}
	
	public LocalDate getDataVenda() {
		return dataVenda;
	}

	public void setDataVenda(LocalDate dataVenda) {
		this.dataVenda = dataVenda;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public Livro getLivro() {
		return livro;
	}

	public void setLivro(Livro livro) {
		this.livro = livro;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;

	}
	public String clienteToString() {
		return cliente.getNome();
	}
	public String livroToString() {
		return livro.getTitulo();
	}

}
