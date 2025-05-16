package com.bancodedados.projeto.view;

import javax.swing.*;
import com.bancodedados.projeto.model.Administrador;
import com.bancodedados.projeto.model.Cliente;
import com.bancodedados.projeto.service.*;
import com.bancodedados.projeto.model.Biblioteca;

import java.awt.*;
import java.awt.event.*;

public class TelaLogin extends JFrame {

	private static final long serialVersionUID = 1L;

	//construtor
	public TelaLogin() {
		setTitle("Login");
		setSize(400, 250);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);

		JPanel panel = new JPanel(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(10, 10, 10, 10);
		gbc.fill = GridBagConstraints.HORIZONTAL;

//		pede as informações do usuario
		JLabel lblCpf = new JLabel("CPF:");
		JTextField txtCpf = new JTextField();
		txtCpf.setPreferredSize(new Dimension(200, 25));

		JLabel lblSenha = new JLabel("Senha:");
		JPasswordField txtSenha = new JPasswordField();
		txtSenha.setPreferredSize(new Dimension(200, 25));

		JButton btnEntrar = new JButton("Entrar");
		JButton btnCadastrar = new JButton("Cadastrar");

//		linhas e tabelas		
		gbc.gridx = 0;
		gbc.gridy = 0;
		panel.add(lblCpf, gbc);
		gbc.gridx = 1;
		panel.add(txtCpf, gbc);

		gbc.gridx = 0;
		gbc.gridy = 1;
		panel.add(lblSenha, gbc);
		gbc.gridx = 1;
		panel.add(txtSenha, gbc);

		gbc.gridx = 1;
		gbc.gridy = 2;
		gbc.gridwidth = 1;
		gbc.anchor = GridBagConstraints.CENTER;
		btnEntrar.setPreferredSize(new Dimension(200, 30));
		panel.add(btnEntrar, gbc);
		
		gbc.gridx = 1;
		gbc.gridy = 3;
		gbc.anchor = GridBagConstraints.CENTER;
		btnCadastrar.setPreferredSize(new Dimension(200, 30));
		panel.add(btnCadastrar, gbc);

		add(panel);

		
		btnEntrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String cpf = txtCpf.getText();
				String senha = new String(txtSenha.getPassword());

				BibliotecaService service = new BibliotecaService();
				Biblioteca biblioteca = service.getBiblioteca();

				Administrador admin = service.getBiblioteca().getAdministrador();
				if (admin != null && admin.getSenha().equals(senha)) {
					dispose();
					new BibliotecaMenu(biblioteca, admin);
					return;
				}

				Cliente cliente = service.buscarClienteCpf(cpf);
				if (cliente != null && cliente.getSenha().equals(senha)) {
					dispose();
					new BibliotecaMenu(biblioteca, cliente); 
					return;
				}

				JOptionPane.showMessageDialog(null, "CPF ou senha incorretos.");
			}
		});
		btnCadastrar.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				
				new TelaCadastro(); 
			}
		});

		setVisible(true);
	}
}

