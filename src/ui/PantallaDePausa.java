package ui;

import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import gamestates.Gamestate;
import gamestates.Jugando;
import main.Juego;
import utilz.LoadSave;
import static utilz.Constantes.UI.BotonUrm.*;

public class PantallaDePausa {

	private Jugando jugando;
	private BufferedImage backgroundImg;
	private int bgX, bgY, bgW, bgH;
	private OpcionesDeAudio opcionesDeAudio;
	private BotonesUrm menuB, replayB, unpauseB;

	public PantallaDePausa(Jugando jugando) {
		this.jugando = jugando;
		loadBackground();
		opcionesDeAudio = jugando.getGame().getAudioOptions();
		createUrmButtons();
	}

	private void createUrmButtons() {
		int menuX = (int) (313 * Juego.SCALE);
		int replayX = (int) (387 * Juego.SCALE);
		int unpauseX = (int) (462 * Juego.SCALE);
		int bY = (int) (325 * Juego.SCALE);

		menuB = new BotonesUrm(menuX, bY, URM_TAMANO, URM_TAMANO, 2);
		replayB = new BotonesUrm(replayX, bY, URM_TAMANO, URM_TAMANO, 1);
		unpauseB = new BotonesUrm(unpauseX, bY, URM_TAMANO, URM_TAMANO, 0);
	}

	private void loadBackground() {
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.FONDO_PAUSA);
		bgW = (int) (backgroundImg.getWidth() * Juego.SCALE);
		bgH = (int) (backgroundImg.getHeight() * Juego.SCALE);
		bgX = Juego.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int) (25 * Juego.SCALE);
	}

	public void update() {

		menuB.update();
		replayB.update();
		unpauseB.update();

		opcionesDeAudio.update();

	}

	public void draw(Graphics g) {
		// Background
		g.drawImage(backgroundImg, bgX, bgY, bgW, bgH, null);

		// UrmButtons
		menuB.draw(g);
		replayB.draw(g);
		unpauseB.draw(g);

		opcionesDeAudio.draw(g);

	}

	public void mouseDragged(MouseEvent e) {
		opcionesDeAudio.mouseDragged(e);
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(e, menuB))
			menuB.setMousePressed(true);
		else if (isIn(e, replayB))
			replayB.setMousePressed(true);
		else if (isIn(e, unpauseB))
			unpauseB.setMousePressed(true);
		else
			opcionesDeAudio.mousePressed(e);
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(e, menuB)) {
			if (menuB.isMousePressed()) {
				jugando.resetAll();
				jugando.setGamestate(Gamestate.MENU);
				jugando.unpauseGame();
			}
		} else if (isIn(e, replayB)) {
			if (replayB.isMousePressed()) {
				jugando.resetAll();
				jugando.unpauseGame();
			}
		} else if (isIn(e, unpauseB)) {
			if (unpauseB.isMousePressed())
				jugando.unpauseGame();
		} else
			opcionesDeAudio.mouseReleased(e);

		menuB.resetBools();
		replayB.resetBools();
		unpauseB.resetBools();

	}

	public void mouseMoved(MouseEvent e) {
		menuB.setMouseOver(false);
		replayB.setMouseOver(false);
		unpauseB.setMouseOver(false);

		if (isIn(e, menuB))
			menuB.setMouseOver(true);
		else if (isIn(e, replayB))
			replayB.setMouseOver(true);
		else if (isIn(e, unpauseB))
			unpauseB.setMouseOver(true);
		else
			opcionesDeAudio.mouseMoved(e);
	}

	private boolean isIn(MouseEvent e, BotonDePausa b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

}
