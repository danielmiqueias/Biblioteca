package com.bancodedados.projeto.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.bancodedados.projeto.service.BibliotecaService;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "clientes")
public class Cliente extends User {

	@Column(nullable = false)
	String telefone;
	
	//cascade = operações que sao feitas na classe cliente afetam também a classe referenciada.
	//orphanRemoval = se remover um empréstimo da lista, ele é apagado do banco.
	//fetch =  ao buscar a classe cliente no banco, a classe referenciada é carregada automaticamente.

	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
	List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();
	
	//só carrega quando for acessado no código
	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
	List<Emprestimo> historicoDeEmprestimos = new ArrayList<Emprestimo>();

	@OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	List<Venda> compras = new ArrayList<Venda>();

	@ManyToOne
	@JoinColumn(name = "biblioteca_fk")
	Biblioteca biblioteca;

	public Cliente(String nome, int idade, String telefone, String cpf, String senha) {
		super(nome, cpf, senha, idade);
		this.telefone = telefone;
	}

	public Cliente() {

	}
	
	//verifica se o cliente tem o emprestimo do livro com o autor 
	public boolean temEmprestimo(String nomeDoLivro, String autor) {
		for (Emprestimo e : emprestimos) {
			if (e != null) {
				if (e.tituloLivro.equalsIgnoreCase(nomeDoLivro) && e.autor.equals(autor)) {
					return true;
				}
			}
		}
		return false;
	}

	public boolean emprestimoEmAtraso() {
		for (Emprestimo e : emprestimos) {
			if (e.vencimentoDoEmprestimo.isBefore(LocalDate.now())) {
				return true;
			}
		}
		return false;
	}
	
	//verifica se o cliente já possui quatro emprestimos
	public String adicionarEmprestimo(Emprestimo emprestimo) {
		if (emprestimos.size() < 4) {
			emprestimos.add(emprestimo);
			BibliotecaService.salvaDados(biblioteca, emprestimos, null);
			//BibliotecaService.salvaDados(biblioteca, emprestimos);
			return "Novo emprestimo adicionado";
		}
		return "Erro! Já possui quatro emprestimos.";
	}

	public void adicionarCompra(Venda venda) {
		compras.add(venda);
	}

	public String getTelefone() {
		return telefone;
	}

	public void setTelefone(String telefone) {
		this.telefone = telefone;
	}

	public List<Emprestimo> getHistoricoDeEmprestimos() {
		return historicoDeEmprestimos;
	}

	public List<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(List<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}

	public void setHistoricoDeEmprestimos(List<Emprestimo> historicoDeEmprestimos) {
		this.historicoDeEmprestimos = historicoDeEmprestimos;
	}

	public List<Venda> getCompras() {
		return compras;
	}

	public void setCompras(List<Venda> compras) {
		this.compras = compras;
	}

	public Biblioteca getBiblioteca() {
		return biblioteca;
	}

	public void setBiblioteca(Biblioteca biblioteca) {
		this.biblioteca = biblioteca;
	}


}
