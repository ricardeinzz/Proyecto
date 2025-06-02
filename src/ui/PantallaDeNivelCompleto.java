package ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Jugando;
import main.Juego;
import utilz.LoadSave;
import static utilz.Constantes.UI.BotonUrm.*;

public class PantallaDeNivelCompleto {

	private Jugando jugando;
	private BotonesUrm menu, next;
	private BufferedImage img;
	private int bgX, bgY, bgW, bgH;

	public PantallaDeNivelCompleto(Jugando jugando) {
		this.jugando = jugando;
		initImg();
		initButtons();
	}

	private void initButtons() {
		int menuX = (int) (330 * Juego.SCALE);
		int nextX = (int) (445 * Juego.SCALE);
		int y = (int) (195 * Juego.SCALE);
		next = new BotonesUrm(nextX, y, URM_TAMANO, URM_TAMANO, 0);
		menu = new BotonesUrm(menuX, y, URM_TAMANO, URM_TAMANO, 2);
	}

	private void initImg() {
		img = LoadSave.GetSpriteAtlas(LoadSave.COMPLETA_IMG);
		bgW = (int) (img.getWidth() * Juego.SCALE);
		bgH = (int) (img.getHeight() * Juego.SCALE);
		bgX = Juego.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int) (75 * Juego.SCALE);
	}

	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);

		g.drawImage(img, bgX, bgY, bgW, bgH, null);
		next.draw(g);
		menu.draw(g);
	}

	public void update() {
		next.update();
		menu.update();
	}

	private boolean isIn(BotonesUrm b, MouseEvent e) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		next.setMouseOver(false);
		menu.setMouseOver(false);

		if (isIn(menu, e))
			menu.setMouseOver(true);
		else if (isIn(next, e))
			next.setMouseOver(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(menu, e)) {
			if (menu.isMousePressed()) {
				jugando.resetAll();
				jugando.setGamestate(Gamestate.MENU);
			}
		} else if (isIn(next, e))
			if (next.isMousePressed()) {
				jugando.loadNextLevel();
				jugando.getGame().getAudioPlayer().setLevelSong(jugando.getLevelManager().getLevelIndex());
			}

		menu.resetBools();
		next.resetBools();
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(menu, e))
			menu.setMousePressed(true);
		else if (isIn(next, e))
			next.setMousePressed(true);
	}

}
