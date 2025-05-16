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
@Table(name = "emprestimos")
public class Emprestimo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	String cpf;

	@Column(nullable = false)
	String nomeDoCliente;

	@Column(name = "vencimento_do_emprestimo", columnDefinition = "DATE", nullable = false)
	LocalDate vencimentoDoEmprestimo;

	@Column(nullable = false)
	String tituloLivro;

	@Column(nullable = false)
	String autor;
	
	//indica que a relação não pode ser nula
	@ManyToOne(optional = false)
	@JoinColumn(name = "cliente_fk")
	Cliente cliente;

	@ManyToOne(optional = false)
	@JoinColumn(name = "livro_fk")
	Livro livro;

	@ManyToOne(optional = false)
	@JoinColumn(name = "biblioteca_fk")
	Biblioteca biblioteca;

	public Emprestimo() {

	}

	public Emprestimo(String cpf, String nomeDoClinte, LocalDate vencimentoDoEmprestimo, String nomeLivro, String autor,
			Cliente cliente, Livro livro, Biblioteca biblioteca) {
		this.cpf = cpf;
		this.nomeDoCliente = nomeDoClinte;
		this.vencimentoDoEmprestimo = vencimentoDoEmprestimo;
		this.tituloLivro = nomeLivro;
		this.autor = autor;
		this.cliente = cliente;
		this.biblioteca = biblioteca;
		this.livro = livro;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getNomeDoCliente() {
		return nomeDoCliente;
	}

	public void setNomeDoCliente(String nomeDoCliente) {
		this.nomeDoCliente = nomeDoCliente;
	}

	public LocalDate getVencimentoDoEmprestimo() {
		return vencimentoDoEmprestimo;
	}

	public void setVencimentoDoEmprestimo(LocalDate vencimentoDoEmprestimo) {
		this.vencimentoDoEmprestimo = vencimentoDoEmprestimo;
	}

	public String getTituloLivro() {
		return tituloLivro;
	}

	public void setTituloLivro(String tituloLivro) {
		this.tituloLivro = tituloLivro;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
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

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}
}
