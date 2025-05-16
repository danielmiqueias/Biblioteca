package com.bancodedados.projeto.view;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.bancodedados.projeto.model.Administrador;
import com.bancodedados.projeto.model.Biblioteca;
import com.bancodedados.projeto.model.Cliente;
import com.bancodedados.projeto.service.BibliotecaService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;

public class TelaCadastro extends JFrame {

	private static EntityManagerFactory emf = Persistence.createEntityManagerFactory("bibliotecaPU");

	private static final long serialVersionUID = 1L;

	// construtor
	public TelaCadastro() {
		// dimensões
		setTitle("Cadastro de Novo Usuário");
		setSize(600, 500);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);

		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(8, 10, 8, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		String[] rotulos = { "Nome", "CPF", "Idade", "Telefone", "Senha" };
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
					BibliotecaService service = new BibliotecaService();

					String nome = campos[0].getText();
					String cpf = campos[1].getText();
					int idade = Integer.parseInt(campos[2].getText());
					String telefone = campos[3].getText();
					String senha = campos[4].getText();
					BibliotecaService bibliotecaService = new BibliotecaService();
					int opcao = 0;
					if (bibliotecaService.verificarExistenciaDeDados()) {
						opcao = 1;
					}

					if (opcao == 0) {
						Biblioteca biblioteca = bibliotecaService.getBiblioteca();
						if (biblioteca == null) {
							biblioteca = new Biblioteca();
							EntityManager em = emf.createEntityManager();
							em.getTransaction().begin();
							em.persist(biblioteca);
							em.getTransaction().commit();
							em.close();
						}

						Administrador admin = new Administrador(nome, cpf, senha, idade, biblioteca);
						service.salvarAdministrador(admin, biblioteca);
						JOptionPane.showMessageDialog(TelaCadastro.this, "Administrador cadastrado com sucesso.");
						dispose();
						new BibliotecaMenu(biblioteca, admin);

					} else {
						String mensagem = service.cadastrarCliente(nome, idade, telefone, cpf, senha);
						JOptionPane.showMessageDialog(TelaCadastro.this, mensagem);
						if (mensagem.equals("Cadastro de cliente realizado com sucesso!")) {
							Cliente cliente = service.buscarClienteCpf(cpf);
							dispose();
							new BibliotecaMenu(bibliotecaService.getBiblioteca(), cliente);
						}
					}

				} catch (NumberFormatException ex) {
					JOptionPane.showMessageDialog(TelaCadastro.this, "Erro de número: " + ex.getMessage());
				} catch (Exception ex) {
					ex.printStackTrace();
					JOptionPane.showMessageDialog(TelaCadastro.this, "Erro ao cadastrar usuário.");
				}
			}
		});

		add(panel);
		setVisible(true);
	}
}
