package com.bancodedados.projeto.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.bancodedados.projeto.service.BibliotecaService;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "biblioteca")
public class Biblioteca {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	//cascade = CascadeType.ALL: operações que sao feitas na classe cliente afetam também a classe referenciada.
	//fetch = quando faz a buscar da biblioteca no banco, as classes referenciadas são carregadas automaticamente.
	//cascade = qualquer ação feita na Biblioteca será replicada para as vendas.

	@OneToMany(mappedBy = "biblioteca", fetch = FetchType.EAGER)
	List<Cliente> clientes = new ArrayList<Cliente>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "biblioteca")
	List<Livro> livros = new ArrayList<Livro>();

	@OneToMany(mappedBy = "biblioteca", fetch = FetchType.EAGER)
	List<Emprestimo> emprestimos = new ArrayList<Emprestimo>();

	@OneToMany(fetch = FetchType.EAGER, mappedBy = "biblioteca", cascade = CascadeType.ALL)
	List<Venda> vendas = new ArrayList<Venda>();

	@OneToOne(mappedBy = "biblioteca", cascade = CascadeType.ALL)
	Administrador administrador;

	public Biblioteca() {

	}

	public Biblioteca(String nome, String cpf, String senha, int idade) {

	}
	
	//verifica se existe um livro com o nome e autor informados
	public Livro validaLivro(String nomeDoLivro, String autor) {
		if (livros.isEmpty()) {
			return null;
		}
		for (Livro livro : livros) {
			if (livro.getTitulo().equals(nomeDoLivro) && livro.getAutor().equals(autor)) {
				return livro;
			}
		}
		return null;
	}
	
	//verifica se o cliente com o cpf informado ja esta cadastrado
	public Cliente validaCliente(String cpf) {
		for (Cliente cliente : clientes) {
			if (cliente.getCpf().equals(cpf)) {
				return cliente;
			}
		}
		return null;
	}
	
	//verifica se o livro existe e se esta disponivel
	//verifica se o cliente ja tem o emprestimo do mesmo livro
	//verifica se possui emprestimo atrasado
	//cria um emprestimo
	//atualiza o estoque do livro 
	public String emprestaLivro(Cliente c, String nomeDoLivro, LocalDate prazo, String autor) {
		Livro livro = validaLivro(nomeDoLivro, autor);
		if (livro != null && livro.getQntEstoque() > 0) {
			if (c.temEmprestimo(nomeDoLivro, autor)) {
				return "Cliente já possui empréstimo!";
			}
			if (c.emprestimoEmAtraso()) {
				return "Cliente possui empréstimos em atraso.";
			}

			Emprestimo emprestimo = new Emprestimo(c.getCpf(), c.getNome(), prazo, nomeDoLivro, autor, c, livro, null);
			emprestimos.add(emprestimo);
			c.getHistoricoDeEmprestimos().add(emprestimo);
			c.adicionarEmprestimo(emprestimo);
			livro.qntEstoque--;
			return "Empréstimo realizado com sucesso";
		}
		return "Erro: Livro não encontrado ou indisponível.";
	}

	public String cadastrarCliente(String nome, int idade, String telefone, String cpf, String senha) {
		Cliente cliente = new Cliente(nome, idade, telefone, cpf, senha);
		//verifica se o cpf do cliente ja esta cadastrado
		if (validaCliente(cpf) != null) {
			return "Cliente já cadastrado!";
		}
		clientes.add(cliente);
		return "Cadastro de cliente realizado com sucesso!";
	}

	public String devolveLivros(String cpf, String nomeDoLivro, String nomeDoAutor) {
		//é criado para percorrer a lista de emprestimos
		Iterator<Emprestimo> it = emprestimos.iterator();
		//verifica se o iterator ainda tem elementos para percorrer
		while (it.hasNext()) {
			//retorna o proximo elemento da lista e armazena
			Emprestimo e = it.next();
			if (e.cpf.equals(cpf) && e.tituloLivro.equals(nomeDoLivro) && e.autor.equals(nomeDoAutor)) {
				Livro livro = validaLivro(nomeDoLivro, nomeDoAutor);
				if (livro != null) {
					livro.qntEstoque++;
				}
				it.remove();
				return "Devolvido com sucesso";
			}
		}
		return "Erro: Empréstimo não encontrado.";
	}

	public List<Livro> exibirLivrosDisponiveis() {
		List<Livro> livrosReturn = new ArrayList<Livro>();
		System.out.println("Livros disponíveis: ");
		for (Livro l : livros) {
			if (l.getQntEstoque() > 0) {
				livrosReturn.add(l);
			}
		}
		return livrosReturn;
	}

	public String venderLivro(String cpf, String nomeDoLivro, String autor, LocalDate dataVenda) {
		Cliente c = validaCliente(cpf);
		Livro livro = validaLivro(nomeDoLivro, autor);
		//recupera a instancia da biblioteca associada
		Biblioteca biblioteca = BibliotecaService.getInstance().getBiblioteca();

		if (livro != null && livro.qntEstoque > 0) {
			Venda novaVenda = new Venda(c, livro, dataVenda, biblioteca);
			vendas.add(novaVenda);
			livro.qntEstoque--;
			c.adicionarCompra(novaVenda);

			return "Venda realizada com sucesso!";
		}
		return "Livro não disponível para venda!";
	}

	public String verificarTipoUsuario(String cpf, String senha) {
		if (administrador.getCpf().equals(cpf) && administrador.getSenha().equals(senha)) {
			return "administrador";
		}
		for (Cliente c : clientes) {
			if (c.getCpf().equals(cpf) && c.getSenha().equals(senha)) {
				return "cliente";
			}
		}
		return null;
	}
	
	public void reporLivro(Livro livro) {
		livro.qntEstoque++;
	}

	public void atualizarEstoque(Livro livro) {
		livro.qntEstoque--;
		//instancia atual da biblioteca
		BibliotecaService.salvaDados(this, livro, null);
	}

	public List<Emprestimo> recuperarEmprestimos(Cliente cliente) {
		List<Emprestimo> lista = new ArrayList<Emprestimo>();
		for (Emprestimo e : emprestimos) {
			if (e.cliente.equals(cliente))
				lista.add(e);
		}
		return lista;
	}

	public Administrador getAdministrador() {
		return administrador;
	}
	
	// garante que a relação entre os dois objetos esteja corretamente
	//configurada nos dois sentidos.
	public void setAdministrador(Administrador administrador) {
		this.administrador = administrador;
		if (administrador != null) {
			administrador.setBiblioteca(this);
		}
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<Cliente> getClientes() {
		return clientes;
	}

	public void setClientes(List<Cliente> clientes) {
		this.clientes = clientes;
	}

	public List<Livro> getLivros() {
		return livros;
	}

	public void setLivros(List<Livro> livros) {
		this.livros = livros;
	}

	public List<Emprestimo> getEmprestimos() {
		return emprestimos;
	}

	public void setEmprestimos(List<Emprestimo> emprestimos) {
		this.emprestimos = emprestimos;
	}

	public List<Venda> getVendas() {
		return vendas;
	}

	public void setVendas(List<Venda> vendas) {
		this.vendas = vendas;
	}
}