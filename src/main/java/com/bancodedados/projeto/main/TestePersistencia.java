package com.bancodedados.projeto.main;

import javax.swing.SwingUtilities;

import com.bancodedados.projeto.service.BibliotecaService;
import com.bancodedados.projeto.view.TelaCadastro;
import com.bancodedados.projeto.view.TelaLogin;

public class TestePersistencia {
	public static void main(String[] args) {

		BibliotecaService bibliotecaService = BibliotecaService.getInstance();

		if (bibliotecaService.verificarExistenciaDeDados()) {
			SwingUtilities.invokeLater(() -> {
				new TelaLogin().setVisible(true);
			});
		} else {
			SwingUtilities.invokeLater(() -> {
				new TelaCadastro().setVisible(true);
			});
		}

	}
}
