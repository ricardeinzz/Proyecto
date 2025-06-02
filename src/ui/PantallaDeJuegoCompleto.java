package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Jugando;
import main.Juego;
import utilz.LoadSave;

public class PantallaDeJuegoCompleto {
	private Jugando jugando;
	private BufferedImage img;
	private BotonesDeMenu quit, credit;
	private int imgX, imgY, imgW, imgH;

	public PantallaDeJuegoCompleto(Jugando jugando) {
		this.jugando = jugando;
		createImg();
		createButtons();
	}

	private void createButtons() {
		quit = new BotonesDeMenu(Juego.GAME_WIDTH / 2, (int) (270 * Juego.SCALE), 2, Gamestate.MENU);
		credit = new BotonesDeMenu(Juego.GAME_WIDTH / 2, (int) (200 * Juego.SCALE), 3, Gamestate.CREDITOS);
	}

	private void createImg() {
		img = LoadSave.GetSpriteAtlas(LoadSave.JUEGO_COMPLETO);
		imgW = (int) (img.getWidth() * Juego.SCALE);
		imgH = (int) (img.getHeight() * Juego.SCALE);
		imgX = Juego.GAME_WIDTH / 2 - imgW / 2;
		imgY = (int) (100 * Juego.SCALE);

	}

	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);

		g.drawImage(img, imgX, imgY, imgW, imgH, null);

		credit.draw(g);
		quit.draw(g);
	}

	public void update() {
		credit.update();
		quit.update();
	}

	private boolean isIn(BotonesDeMenu b, MouseEvent e) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		credit.setMouseOver(false);
		quit.setMouseOver(false);

		if (isIn(quit, e))
			quit.setMouseOver(true);
		else if (isIn(credit, e))
			credit.setMouseOver(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(quit, e)) {
			if (quit.isMousePressed()) {
				jugando.resetAll();
				jugando.resetGameCompleted();
				jugando.setGamestate(Gamestate.MENU);

			}
		} else if (isIn(credit, e))
			if (credit.isMousePressed()) {
				jugando.resetAll();
				jugando.resetGameCompleted();
				jugando.setGamestate(Gamestate.CREDITOS);
			}

		quit.resetBools();
		credit.resetBools();
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(quit, e))
			quit.setMousePressed(true);
		else if (isIn(credit, e))
			credit.setMousePressed(true);
	}
}
