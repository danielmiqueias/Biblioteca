package com.bancodedados.projeto.model;

import com.bancodedados.projeto.service.BibliotecaService;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "livros")
public class Livro {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	String autor;

	@Column(nullable = false)
	float valor;

	@Column(nullable = false)
	String genero;

	@Column(nullable = false)
	int qntEstoque;

	@Column(nullable = false)
	String titulo;

	@ManyToOne
	@JoinColumn(name = "biblioteca_fk")
	Biblioteca biblioteca;

	public Livro() {

	}

	public Livro(String autor, float valor, String genero, int qntEstoque, String titulo) {
		this.autor = autor;
		this.valor = valor;
		this.genero = genero;
		this.qntEstoque = qntEstoque;
		this.titulo = titulo;
		biblioteca = BibliotecaService.getInstance().getBiblioteca();

	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public float getValor() {
		return valor;
	}

	public void setValor(float valor) {
		this.valor = valor;
	}

	public String getGenero() {
		return genero;
	}

	public void setGenero(String genero) {
		this.genero = genero;
	}

	public int getQntEstoque() {
		return qntEstoque;
	}

	public void setQntEstoque(int qntEstoque) {
		this.qntEstoque = qntEstoque;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
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
