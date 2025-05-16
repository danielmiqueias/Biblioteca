package com.bancodedados.projeto.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.bancodedados.projeto.model.Administrador;
import com.bancodedados.projeto.model.Biblioteca;
import com.bancodedados.projeto.model.Cliente;
import com.bancodedados.projeto.model.Emprestimo;
import com.bancodedados.projeto.model.Livro;
import com.bancodedados.projeto.model.User;
import com.bancodedados.projeto.model.Venda;
import com.bancodedados.projeto.service.BibliotecaService;

public class BibliotecaMenu extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private User user;
	private BibliotecaService bibliotecaService;

	// construtor
	public BibliotecaMenu(Biblioteca biblioteca, User user) {
		this.user = user;
		this.bibliotecaService = new BibliotecaService();

		setTitle("Menu Biblioteca");
		setSize(600, 550);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);

		exibirMenuPorUsuario(user, biblioteca);
	}

	public void exibirMenuPorUsuario(Object usuario, Biblioteca biblioteca) {

		// dimensões
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 10, 8, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		JButton btnEmprestimo = new JButton("Fazer Empréstimo");
		JButton btnVenda = new JButton("Comprar");
		JButton btnDevolverLivro = new JButton("Devolver");
		JButton btnEmprestimos = new JButton("Meus Empréstimos");
		JButton btnExibirLivros = new JButton("Exibir Livros Disponíveis");
		JButton btnSair = new JButton("Voltar");
		JButton btnCadastrarLivro = new JButton("Cadastrar Livro");
		JButton btnVendas = new JButton("Vendas");
		JButton btnTodosOsEmprestimos = new JButton("Lista de emprestimos");

		JButton[] todosBotoes = { btnVendas, btnEmprestimo, btnVenda, btnDevolverLivro, btnEmprestimos, btnExibirLivros,
				btnSair, btnCadastrarLivro };

		for (JButton botao : todosBotoes) {
			botao.setPreferredSize(new Dimension(250, 25));
			botao.setVisible(false);
		}

		String tipo = verificarTipoUsuario(usuario);

		if ("administrador".equals(tipo)) {
			btnCadastrarLivro.setVisible(true);
			btnExibirLivros.setVisible(true);
			btnSair.setVisible(true);
			btnVendas.setVisible(true);
			btnTodosOsEmprestimos.setVisible(true);
			adicionarBotoes(panel, gbc, 0, btnCadastrarLivro, btnExibirLivros, btnVendas, btnTodosOsEmprestimos ,btnSair);
		} else if ("cliente".equals(tipo)) {
			btnEmprestimo.setVisible(true);
			btnVenda.setVisible(true);
			btnDevolverLivro.setVisible(true);
			btnEmprestimos.setVisible(true);
			btnExibirLivros.setVisible(true);
			btnSair.setVisible(true);
			adicionarBotoes(panel, gbc, 0, btnEmprestimo, btnVenda, btnDevolverLivro, btnEmprestimos, btnExibirLivros,
					btnSair);
		}
		btnTodosOsEmprestimos.addActionListener(e -> {
			dispose();
			new TelaTodosOsEmprestimos((Administrador) usuario);
		});
		
		btnVendas.addActionListener(e -> {
			dispose();
			new TelaVendas((Administrador) usuario);
		});

		btnCadastrarLivro.addActionListener(e -> {
			dispose();
			new TelaCadastroLivro();
		});
		btnEmprestimo.addActionListener(e -> {
			dispose();
			new TelaEmprestimoLivro((Cliente) usuario);
		});
		btnVenda.addActionListener(e -> {
			dispose();
			new TelaVendaLivro((Cliente) usuario);
		});

		btnDevolverLivro.addActionListener(e -> new TelaDevolverLivro((Cliente) usuario));

		btnExibirLivros.addActionListener(e -> {
			dispose();
			new TelaExibirLivros(this);
		});
		btnEmprestimos.addActionListener(e -> {
			if (user instanceof Cliente cliente) {
				dispose();
				new TelaEmprestimosEmAberto(cliente);
			}
		});
		btnSair.addActionListener(e -> {
			dispose();
			new TelaLogin();

		});

		getContentPane().add(panel);
		setVisible(true);

	}

	private void adicionarBotoes(JPanel panel, GridBagConstraints gbc, int linhaInicial, JButton... botoes) {
		gbc.gridwidth = 2;
		for (int i = 0; i < botoes.length; i++) {
			gbc.gridx = 0;
			gbc.gridy = linhaInicial + i;
			panel.add(botoes[i], gbc);
		}
	}

	public static String verificarTipoUsuario(Object usuario) {
		if (usuario instanceof Administrador)
			return "administrador";
		if (usuario instanceof Cliente)
			return "cliente";
		return "desconhecido";
	}

	class TelaEmprestimoLivro extends JFrame {

		private static final long serialVersionUID = 1L;

		private TelaEmprestimoLivro(Cliente cliente) {

			setTitle("Realizar Empréstimo");
			setSize(600, 400);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(8, 10, 8, 10);
			gbc.fill = GridBagConstraints.HORIZONTAL;

			String[] rotulos = { "Título do Livro", "Autor", "Data: (yyyy-MM-dd)" };

			JTextField[] campos = new JTextField[rotulos.length];
			for (int i = 0; i < rotulos.length; i++) {
				JLabel label = new JLabel(rotulos[i] + ":");
				campos[i] = new JTextField();
				campos[i].setPreferredSize(new Dimension(300, 25));

				gbc.gridx = 0;
				gbc.gridy = i;
				panel.add(label, gbc);

				gbc.gridx = 1;
				panel.add(campos[i], gbc);
			}

			JButton btnConfirmar = new JButton("Confirmar");
			btnConfirmar.setPreferredSize(new Dimension(150, 30));
			JButton btnVoltar = new JButton("Voltar");
			btnVoltar.setPreferredSize(new Dimension(150, 30));

			JPanel btnPanel = new JPanel((LayoutManager) new FlowLayout(FlowLayout.CENTER));
			btnPanel.add(btnConfirmar);
			btnPanel.add(btnVoltar);

			gbc.gridx = 0;
			gbc.gridy = rotulos.length;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.CENTER;
			panel.add(btnPanel, gbc);

			btnVoltar.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent e) {
					dispose();
					new BibliotecaMenu(BibliotecaService.getInstance().getBiblioteca(), user);
				}
			});

			btnConfirmar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Biblioteca biblioteca = BibliotecaService.getInstance().getBiblioteca();
						String cpf = cliente.getCpf();
						String titulo = campos[0].getText().trim();
						String autor = campos[1].getText().trim();
						String dataStr = campos[2].getText().trim();

						if (titulo.isEmpty() || autor.isEmpty() || dataStr.isEmpty()) {
							JOptionPane.showMessageDialog(TelaEmprestimoLivro.this,
									"Por favor, preencha todos os campos.");
							return;
						}

						Livro livro = bibliotecaService.buscarLivroPorTitulo(titulo);
						if (livro == null) {
							JOptionPane.showMessageDialog(TelaEmprestimoLivro.this, "Livro não encontrado.");
							return;
						}

						LocalDate prazo = LocalDate.parse(dataStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
						String mensagem = bibliotecaService.cadastrarEmprestimo(cpf, cliente.getNome(), prazo, titulo,
								autor, biblioteca);

						JOptionPane.showMessageDialog(TelaEmprestimoLivro.this, mensagem);
						dispose();
						new BibliotecaMenu(biblioteca, cliente);

					} catch (DateTimeParseException ex) {
						JOptionPane.showMessageDialog(TelaEmprestimoLivro.this,
								"Data inválida. Use o formato yyyy-MM-dd.");
					}
				}
			});

			add(panel);
			setVisible(true);

		}
	}

	class TelaVendaLivro extends JFrame {

		private static final long serialVersionUID = 1L;

		public TelaVendaLivro(Cliente cliente) {
			setTitle("Realizar Venda");
			setSize(400, 250);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			JPanel panel = new JPanel(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(10, 10, 10, 10);
			gbc.fill = GridBagConstraints.HORIZONTAL;

			JLabel lblLivro = new JLabel("Título do Livro:");
			JTextField txtLivro = new JTextField(20);

			JLabel lblAutor = new JLabel("Autor:");
			JTextField txtAutor = new JTextField(20);

			JButton btnConfirmar = new JButton("Confirmar Venda");

			gbc.gridx = 0;
			gbc.gridy = 1;
			panel.add(lblLivro, gbc);
			gbc.gridx = 1;
			panel.add(txtLivro, gbc);

			gbc.gridx = 0;
			gbc.gridy = 2;
			panel.add(lblAutor, gbc);
			gbc.gridx = 1;
			panel.add(txtAutor, gbc);

			gbc.gridx = 0;
			gbc.gridy = 3;
			gbc.gridwidth = 1;
			gbc.anchor = GridBagConstraints.CENTER;
			panel.add(btnConfirmar, gbc);

			btnConfirmar.addActionListener(e -> {
				BibliotecaService biblioteca = BibliotecaService.getInstance();
				String cpf = cliente.getCpf();
				String titulo = txtLivro.getText();
				String autor = txtAutor.getText();
				String mensagem = biblioteca.vendeLivro(cpf, titulo, autor, LocalDate.now());
				JOptionPane.showMessageDialog(this, mensagem);
				dispose();
				new BibliotecaMenu(biblioteca.getBiblioteca(), user);
			});

			add(panel);
			setVisible(true);
		}
	}

	class TelaExibirLivros extends JFrame {

		private static final long serialVersionUID = 1L;

		public TelaExibirLivros(BibliotecaMenu bibliotecaMenu) {
			setTitle("Livros Cadastrados");
			setSize(600, 400);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(textArea);
			Biblioteca biblioteca = BibliotecaService.getInstance().getBiblioteca();

			List<Livro> lista = biblioteca.exibirLivrosDisponiveis();
			if (lista.isEmpty()) {
				textArea.setText("Nenhum livro cadastrado.");
			} else {
				for (Livro livro : lista) {
					textArea.append("Título: " + livro.getTitulo() + "\nAutor: " + livro.getAutor() + "\nGênero: "
							+ livro.getGenero() + "\nPreço: R$" + livro.getValor() + "\nQuantidade: "
							+ livro.getQntEstoque() + "\n\n");
				}
			}
			JButton btnVoltar = new JButton("Voltar");
			btnVoltar.addActionListener(e -> {
				dispose();
				new BibliotecaMenu(BibliotecaService.getInstance().getBiblioteca(), user);
			});

			JPanel panelInferior = new JPanel();
			panelInferior.add(btnVoltar);

			getContentPane().add(scrollPane, BorderLayout.CENTER);
			getContentPane().add(panelInferior, BorderLayout.SOUTH);

			setVisible(true);
		}
	}

	class TelaCadastroLivro extends JFrame {

		private static final long serialVersionUID = 1L;

		public TelaCadastroLivro() {
			setTitle("Cadastrar Livro");
			setSize(600, 400);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(8, 10, 8, 10);
			gbc.fill = GridBagConstraints.HORIZONTAL;

			String[] rotulos = { "Título", "Autor", "Gênero", "Preço", "Quantidade" };
			JTextField[] campos = new JTextField[rotulos.length];

			for (int i = 0; i < rotulos.length; i++) {
				JLabel label = new JLabel(rotulos[i] + ":");
				campos[i] = new JTextField();
				campos[i].setPreferredSize(new Dimension(300, 25));

				gbc.gridx = 0;
				gbc.gridy = i;
				panel.add(label, gbc);

				gbc.gridx = 1;
				panel.add(campos[i], gbc);
			}

			JButton btnSalvar = new JButton("Salvar");
			gbc.gridx = 0;
			gbc.gridy = rotulos.length;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.CENTER;
			panel.add(btnSalvar, gbc);

			btnSalvar.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					try {
						Biblioteca biblioteca = BibliotecaService.getInstance().getBiblioteca();
						String titulo = campos[0].getText();
						String autor = campos[1].getText();
						String genero = campos[2].getText();
						Float valor = Float.parseFloat(campos[3].getText());
						int quantidade = Integer.parseInt(campos[4].getText());
						BibliotecaService b = BibliotecaService.getInstance();
						b.cadastrarLivro(autor, valor, genero, quantidade, titulo);
						dispose();
						new BibliotecaMenu(biblioteca, biblioteca.getAdministrador());
					} catch (NumberFormatException ex) {
						JOptionPane.showMessageDialog(TelaCadastroLivro.this,
								"Erro nos campos numéricos: " + ex.getMessage());
					}
				}
			});

			add(panel);
			setVisible(true);
		}
	}

	class TelaEmprestimosEmAberto extends JFrame {

		private static final long serialVersionUID = 1L;

		public TelaEmprestimosEmAberto(Cliente cliente) {
			setTitle("Empréstimos");
			setSize(600, 400);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(textArea);
			Biblioteca biblioteca = BibliotecaService.getInstance().getBiblioteca();

			// Carrega o cliente atualizado com o histórico de empréstimos
			Cliente clienteAtualizado = BibliotecaService.getInstance().buscarClienteCpf(cliente.getCpf());

			// Inicializa o histórico de empréstimos
			List<Emprestimo> historico = clienteAtualizado.getHistoricoDeEmprestimos();

			if (historico.isEmpty()) {
				textArea.setText("Nenhum empréstimo.");
			} else {
				for (Emprestimo emp : historico) {
					textArea.append("Livro: " + emp.getLivro().getTitulo() + " | Vencimento: "
							+ emp.getVencimentoDoEmprestimo() + " \n");
				}
			}

			add(scrollPane);
			setVisible(true);

			JButton btnVoltar = new JButton("Voltar");
			btnVoltar.addActionListener(e -> {
				dispose();
				new BibliotecaMenu(biblioteca, clienteAtualizado);
			});

			JPanel panelInferior = new JPanel();
			panelInferior.add(btnVoltar);
			getContentPane().add(panelInferior, BorderLayout.SOUTH);
		}

	}

	class TelaDevolverLivro extends JFrame {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TelaDevolverLivro(Cliente cliente) {
			setTitle("Devolver Livro");
			setSize(500, 250);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);

			JPanel panel = new JPanel();
			panel.setLayout(new GridBagLayout());
			GridBagConstraints gbc = new GridBagConstraints();
			gbc.insets = new Insets(8, 10, 8, 10);
			gbc.fill = GridBagConstraints.HORIZONTAL;

			JLabel lblTitulo = new JLabel("Título:");
			JTextField txtTitulo = new JTextField();
			txtTitulo.setPreferredSize(new Dimension(300, 25));

			JLabel lblAutor = new JLabel("Autor:");
			JTextField txtAutor = new JTextField();
			txtAutor.setPreferredSize(new Dimension(300, 25));

			gbc.gridx = 0;
			gbc.gridy = 0;
			panel.add(lblTitulo, gbc);
			gbc.gridx = 1;
			panel.add(txtTitulo, gbc);

			gbc.gridx = 0;
			gbc.gridy = 1;
			panel.add(lblAutor, gbc);
			gbc.gridx = 1;
			panel.add(txtAutor, gbc);

			JButton btnDevolver = new JButton("Devolver");
			gbc.gridx = 0;
			gbc.gridy = 2;
			gbc.gridwidth = 2;
			gbc.anchor = GridBagConstraints.CENTER;
			panel.add(btnDevolver, gbc);

			btnDevolver.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					String titulo = txtTitulo.getText().trim();
					String autor = txtAutor.getText().trim();

					if (titulo.isEmpty()) {
						JOptionPane.showMessageDialog(TelaDevolverLivro.this, "Título do livro não informado.", "Erro",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					if (autor.isEmpty()) {
						JOptionPane.showMessageDialog(TelaDevolverLivro.this, "Autor do livro não informado.", "Erro",
								JOptionPane.ERROR_MESSAGE);
						return;
					}

					try {
						BibliotecaService.getInstance().devolverLivro(titulo, autor, cliente);
						JOptionPane.showMessageDialog(TelaDevolverLivro.this, "Livro devolvido com sucesso!");
						dispose();
					} catch (Exception ex) {
						JOptionPane.showMessageDialog(TelaDevolverLivro.this,
								"Erro ao devolver livro: " + ex.getMessage(), "Erro", JOptionPane.ERROR_MESSAGE);
					}
				}
			});

			add(panel);
			setVisible(true);
		}
	}

	class TelaVendas extends JFrame {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		public TelaVendas(Administrador administrador) {

			setTitle("Vendas");
			setSize(500, 250);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(textArea);
			Biblioteca biblioteca = BibliotecaService.getInstance().getBiblioteca();

			List<Venda> lista = biblioteca.getVendas();
			if (lista.isEmpty()) {
				textArea.setText("Nenhuma venda.");
			} else {
				for (Venda venda : lista) {
					textArea.append("ID: " + venda.getId() + "\nComprador: " + venda.clienteToString() + "\n Data:"
							+ venda.getDataVenda() + "\nLivro: " + venda.livroToString() + "\nValor: "
							+ venda.getLivro().getValor() + "\n\n");
				}
			}
			JButton btnVoltar = new JButton("Voltar");
			btnVoltar.addActionListener(e -> {
				dispose();
				new BibliotecaMenu(BibliotecaService.getInstance().getBiblioteca(), user);
			});

			JPanel panelInferior = new JPanel();
			panelInferior.add(btnVoltar);

			getContentPane().add(scrollPane, BorderLayout.CENTER);
			getContentPane().add(panelInferior, BorderLayout.SOUTH);

			setVisible(true);

		}
	}
	class TelaTodosOsEmprestimos extends JFrame{

		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		public TelaTodosOsEmprestimos(Administrador administrador) {
			


			setTitle("Todos os emprestimos");
			setSize(500, 250);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			JTextArea textArea = new JTextArea();
			textArea.setEditable(false);
			JScrollPane scrollPane = new JScrollPane(textArea);
			Biblioteca biblioteca = BibliotecaService.getInstance().getBiblioteca();

			List<Emprestimo> emprestimos = biblioteca.getEmprestimos();
			if (emprestimos.isEmpty()) {
				textArea.setText("Nenhum Emprestimo.");
			} else {
				for (Emprestimo e : emprestimos) {
					textArea.append("ID: " + e.getId() + "\nTitulo: " + e.getTituloLivro() + "\n Autor:"
							+ e.getAutor() + "\nData: " + e.getVencimentoDoEmprestimo()+"\n\n");
				}
			}
		
			JButton btnVoltar = new JButton("Voltar");
			btnVoltar.addActionListener(e -> {
				dispose();
				new BibliotecaMenu(BibliotecaService.getInstance().getBiblioteca(), user);
			});

			JPanel panelInferior = new JPanel();
			panelInferior.add(btnVoltar);

			getContentPane().add(scrollPane, BorderLayout.CENTER);
			getContentPane().add(panelInferior, BorderLayout.SOUTH);

			setVisible(true);
		}	
	}
}
