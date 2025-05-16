package com.bancodedados.projeto.service;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import javax.swing.JOptionPane;

import org.hibernate.Hibernate;

import com.bancodedados.projeto.model.Administrador;
import com.bancodedados.projeto.model.Biblioteca;
import com.bancodedados.projeto.model.Cliente;
import com.bancodedados.projeto.model.Emprestimo;
import com.bancodedados.projeto.model.Livro;
import com.bancodedados.projeto.model.Venda;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;

public class BibliotecaService {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("bibliotecaPU");
	private Biblioteca biblioteca;
	private static BibliotecaService instance;

	public BibliotecaService() {
		biblioteca = getBiblioteca();
	}

	public void salvarAdministrador(Administrador administrador, Biblioteca biblioteca) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();
			Biblioteca bibliotecaExistente = em.find(Biblioteca.class, biblioteca.getId());
			/*if (bibliotecaExistente == null) {
				em.persist(biblioteca);
				bibliotecaExistente = biblioteca;
			}*/
			administrador.setBiblioteca(bibliotecaExistente);
			bibliotecaExistente.setAdministrador(administrador);
			em.persist(administrador);
			em.merge(bibliotecaExistente);
			tx.commit();

		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			e.printStackTrace();
		} finally {
			em.close();
		}
	}

	public Cliente buscarClienteCpf(String cpf) {
		EntityManager em = emf.createEntityManager();
		try {
			Cliente cliente = em.createQuery("SELECT c FROM Cliente c WHERE c.cpf = :cpf", Cliente.class)
					.setParameter("cpf", cpf).getSingleResult();

			// Inicializa as listas necessárias dentro da sessão
			Hibernate.initialize(cliente.getEmprestimos());
			Hibernate.initialize(cliente.getHistoricoDeEmprestimos());
			Hibernate.initialize(cliente.getCompras()); // opcional, se precisar

			return cliente;
		} catch (NoResultException e) {
			return null;
		} finally {
			em.close();
		}
	}

	public String cadastrarCliente(String nome, int idade, String telefone, String cpf, String senha) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			TypedQuery<Cliente> query = em.createQuery("SELECT c FROM Cliente c WHERE c.cpf = :cpf", Cliente.class);
			query.setParameter("cpf", cpf);

			List<Cliente> existentes = query.getResultList();
			if (!existentes.isEmpty()) {
				return "Cliente já cadastrado!";
			}

			biblioteca = getBiblioteca();

			if (!nome.isEmpty() || idade > 18 && idade < 100 || !telefone.isEmpty() || !cpf.isEmpty()
					|| !senha.isEmpty()) {

				Cliente cliente = new Cliente(nome, idade, telefone, cpf, senha);
				cliente.setBiblioteca(biblioteca);
				biblioteca.getClientes().add(cliente);

				// persistir novo cliente
				em.persist(cliente);

				// atualizar relação com biblioteca
				em.merge(biblioteca);

				tx.commit();
				return "Cadastro de cliente realizado com sucesso!";
			}

		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			em.close();
		}
		return "Erro ao cadastrar cliente!";
	}

	public String cadastrarLivro(String autor, float valor, String genero, int qntEstoque, String titulo) {

		Livro livroExistente = buscarLivroPorTitulo(titulo);
		if (livroExistente != null) {
			return "Livro já cadastrado!";
		}

		Livro livro = new Livro(autor, valor, genero, qntEstoque, titulo);
		
		biblioteca = getBiblioteca();
		biblioteca.getLivros().add(livro);
		salvaDados(biblioteca, livro, null);
		return "Cadastro de livro realizado com sucesso!";

	}

	public Livro buscarLivroPorTitulo(String titulo) {
		EntityManager em = emf.createEntityManager();
		try {
			biblioteca = getBiblioteca();
			Hibernate.initialize(biblioteca.getLivros());
			for (Livro l : biblioteca.getLivros()) {
				if (l.getTitulo().equalsIgnoreCase(titulo)) {
					return l;
				}
			}
			return null;
		} finally {
			em.close();
		}
	}

	public String cadastrarEmprestimo(String cpf, String nomeDoCliente, LocalDate vencimentoDoEmprestimo,
			String nomeLivro, String autor, Biblioteca biblioteca) {
		EntityManager em = emf.createEntityManager();
		try {
			em.getTransaction().begin();

			Cliente cliente = buscarClienteCpf(cpf);
			Livro livro = buscarLivroPorTitulo(nomeLivro);

			if (cliente == null)
				return "Cliente não encontrado!";
			if (livro == null)
				return "Livro não encontrado!";

			if (cliente.temEmprestimo(nomeLivro, autor)) {
				return "Livro já está emprestado!";
			}

			Emprestimo emprestimo = new Emprestimo(cpf, nomeDoCliente, vencimentoDoEmprestimo, nomeLivro, autor,
					cliente, livro, biblioteca);
			String resultado = cliente.adicionarEmprestimo(emprestimo);
			if (!resultado.equals("Novo emprestimo adicionado")) {
				return "Limite de emprestimos alcançado";
			}

			biblioteca.atualizarEstoque(livro);

			em.persist(emprestimo);

			em.getTransaction().commit();
			return "Empréstimo realizado com sucesso!";

		} catch (Exception e) {
			if (em.getTransaction().isActive())
				em.getTransaction().rollback();
			e.printStackTrace();
			return "Erro ao cadastrar empréstimo.";
		} finally {
			em.close();
		}
	}

	public boolean verificarExistenciaDeDados() {
		EntityManager em = emf.createEntityManager();
		try {
			TypedQuery<Administrador> queryAdm = em.createQuery("SELECT a FROM Administrador a", Administrador.class);
			List<Administrador> admin = queryAdm.getResultList();
			return !admin.isEmpty();
		} finally {
			em.close();
		}
	}

	public Biblioteca getBiblioteca() {
		EntityManager em = emf.createEntityManager();
		try {
			List<Biblioteca> resultado = em.createQuery("SELECT b FROM Biblioteca b", Biblioteca.class).setMaxResults(1)
					.getResultList();
			if (!resultado.isEmpty()) {
				return resultado.get(0);
			} else {
				return null;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			em.close();
		}
	}

	public static BibliotecaService getInstance() {
		if (instance == null) {
			instance = new BibliotecaService();
		}
		return instance;
	}

	public String vendeLivro(String cpf, String titulo, String autor, LocalDate data) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			TypedQuery<Cliente> queryCliente = em.createQuery("SELECT c FROM Cliente c WHERE c.cpf = :cpf",
					Cliente.class);
			queryCliente.setParameter("cpf", cpf);
			Cliente c = queryCliente.getSingleResult();

			TypedQuery<Livro> queryLivro = em
					.createQuery("SELECT l FROM Livro l WHERE l.titulo = :titulo AND l.autor = :autor", Livro.class);
			queryLivro.setParameter("titulo", titulo);
			queryLivro.setParameter("autor", autor);
			Livro livro = queryLivro.getSingleResult();

			if (livro.getQntEstoque() <= 0) {
				return "Livro não disponível para venda!";
			}

			Biblioteca biblioteca = em.find(Biblioteca.class, getBiblioteca().getId());

			Venda novaVenda = new Venda(c, livro, data, biblioteca);
			em.persist(novaVenda);

			biblioteca.atualizarEstoque(livro);
			em.merge(livro);

			tx.commit();
			return "Venda realizada com sucesso!";
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
			return "Erro ao realizar venda!";
		} finally {
			em.close();
		}

	}

	public void devolverLivro(String titulo, String autor, Cliente cliente) {
		Biblioteca biblioteca = getBiblioteca();
		Livro livro = biblioteca.validaLivro(titulo, autor);
		if (livro != null) {
			removerEmprestimo(livro, cliente);
		}

	}

	public boolean removerEmprestimo(Livro livro, Cliente cliente) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			Cliente clienteGerenciado = em.find(Cliente.class, cliente.getId());

			Emprestimo emprestimoParaRemover = null;
			for (Emprestimo e : clienteGerenciado.getEmprestimos()) {
				if (e.getAutor().equals(livro.getAutor()) && e.getTituloLivro().equals(livro.getTitulo())) {
					emprestimoParaRemover = e;
					break;
				}
			}

			if (emprestimoParaRemover != null) {
				clienteGerenciado.getEmprestimos().remove(emprestimoParaRemover);
				Livro livroGerenciado = em.find(Livro.class, livro.getId());
				livroGerenciado.setQntEstoque(livroGerenciado.getQntEstoque() + 1);
			
				em.remove(em.contains(emprestimoParaRemover) ? emprestimoParaRemover : em.merge(emprestimoParaRemover));

				tx.commit();
				return true;
			} else {
				JOptionPane.showMessageDialog(null, "Empréstimo não encontrado");
				tx.rollback();
				return false;
			}

		} catch (Exception e) {
			if (tx.isActive())
				tx.rollback();
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Erro");
			return false;
		} finally {
			em.close();
		}
	}

	public static void salvaDados(Biblioteca biblioteca, Object object, Object object2) {
		EntityManager em = emf.createEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();

			if (object instanceof Cliente) {
				Cliente cliente = (Cliente) object;
				Cliente clienteGerenciado = em.find(Cliente.class, cliente.getId());

				if (clienteGerenciado != null) {
					Emprestimo emprestimoParaRemover = null;
					for (Emprestimo emp : clienteGerenciado.getEmprestimos()) {
						if (emp.getId().equals(((Emprestimo) object2).getId())) {
							emprestimoParaRemover = emp;
							break;
						}
					}

					if (emprestimoParaRemover != null) {
						clienteGerenciado.getEmprestimos().remove(emprestimoParaRemover);
					}

					clienteGerenciado.setHistoricoDeEmprestimos(cliente.getHistoricoDeEmprestimos());
					clienteGerenciado.setCompras(cliente.getCompras());

					em.merge(clienteGerenciado);
				} else {
					em.persist(cliente);
				}
			} else if (isNewEntity(object)) {
				em.persist(object);
			} else {
				em.merge(object);
			}

			em.merge(biblioteca); 

			tx.commit();
		} catch (Exception e) {
			if (tx.isActive()) {
				tx.rollback();
			}
			e.printStackTrace();
		} finally {
			em.close();
		}
	}

	private static boolean isNewEntity(Object entity) {
		try {
			Method getId = entity.getClass().getMethod("getId");
			Object id = getId.invoke(entity);
			return id == null;
		} catch (Exception e) {
			return false;
		}
	}

}
