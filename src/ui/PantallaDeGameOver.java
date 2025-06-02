package ui;

import static utilz.Constantes.UI.BotonUrm.URM_TAMANO;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Jugando;
import main.Juego;
import utilz.LoadSave;

public class PantallaDeGameOver {

	private Jugando jugando;
	private BufferedImage img;
	private int imgX, imgY, imgW, imgH;
	private BotonesUrm menu, play;

	public PantallaDeGameOver(Jugando jugando) {
		this.jugando = jugando;
		createImg();
		createButtons();
	}

	private void createButtons() {
		int menuX = (int) (335 * Juego.SCALE);
		int playX = (int) (440 * Juego.SCALE);
		int y = (int) (195 * Juego.SCALE);
		play = new BotonesUrm(playX, y, URM_TAMANO, URM_TAMANO, 0);
		menu = new BotonesUrm(menuX, y, URM_TAMANO, URM_TAMANO, 2);

	}

	private void createImg() {
		img = LoadSave.GetSpriteAtlas(LoadSave.PANTALLA_MUERTE);
		imgW = (int) (img.getWidth() * Juego.SCALE);
		imgH = (int) (img.getHeight() * Juego.SCALE);
		imgX = Juego.GAME_WIDTH / 2 - imgW / 2;
		imgY = (int) (100 * Juego.SCALE);

	}

	public void draw(Graphics g) {
		g.setColor(new Color(0, 0, 0, 200));
		g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);

		g.drawImage(img, imgX, imgY, imgW, imgH, null);

		menu.draw(g);
		play.draw(g);
	}

	public void update() {
		menu.update();
		play.update();
	}

	private boolean isIn(BotonesUrm b, MouseEvent e) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

	public void mouseMoved(MouseEvent e) {
		play.setMouseOver(false);
		menu.setMouseOver(false);

		if (isIn(menu, e))
			menu.setMouseOver(true);
		else if (isIn(play, e))
			play.setMouseOver(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(menu, e)) {
			if (menu.isMousePressed()) {
				jugando.resetAll();
				jugando.setGamestate(Gamestate.MENU);
			}
		} else if (isIn(play, e))
			if (play.isMousePressed()) {
				jugando.resetAll();
				jugando.getGame().getAudioPlayer().setLevelSong(jugando.getLevelManager().getLevelIndex());
			}

		menu.resetBools();
		play.resetBools();
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(menu, e))
			menu.setMousePressed(true);
		else if (isIn(play, e))
			play.setMousePressed(true);
	}

}
