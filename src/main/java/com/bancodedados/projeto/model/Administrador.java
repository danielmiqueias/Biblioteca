package com.bancodedados.projeto.model;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "administrador")
public class Administrador extends User {

	@OneToOne
	@JoinColumn(name = "biblioteca_fk", nullable = false, unique = true)
	Biblioteca biblioteca;

	public Administrador() {

	}

	public Administrador(String nome, String cpf, String senha, int idade, Biblioteca biblioteca) {
		super(nome, cpf, senha, idade);
		this.biblioteca = biblioteca;
		//para garantir que a biblioteca conheça o seu administrador.
		if (biblioteca != null) {
			biblioteca.setAdministrador(this);
		}
	}
	
	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}
}
