package main;

import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JPanel;
import inputs.EntradaPorTeclado;
import inputs.EntradaPorMouse;
import static main.Juego.GAME_HEIGHT;
import static main.Juego.GAME_WIDTH;

public class PanelDeJuego extends JPanel {

	private EntradaPorMouse entradaPorMouse;
	private Juego juego;

	public PanelDeJuego(Juego juego) {
		entradaPorMouse = new EntradaPorMouse(this);
		this.juego = juego;
		setPanelSize();
		addKeyListener(new EntradaPorTeclado(this));
		addMouseListener(entradaPorMouse);
		addMouseMotionListener(entradaPorMouse);
		setFocusable(true);
		requestFocusInWindow();
	}

	private void setPanelSize() {
		Dimension size = new Dimension(GAME_WIDTH, GAME_HEIGHT);
		setPreferredSize(size);
	}

	public void updateGame() {

	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		juego.render(g);
	}

	public Juego getGame() {
		return juego;
	}

}