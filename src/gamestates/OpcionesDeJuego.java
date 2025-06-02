package gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import main.Juego;
import ui.OpcionesDeAudio;
import ui.BotonDePausa;
import ui.BotonesUrm;
import utilz.LoadSave;
import static utilz.Constantes.UI.BotonUrm.*;

public class OpcionesDeJuego extends Estados implements MetodosDeEstado {

	private OpcionesDeAudio opcionesDeAudio;
	private BufferedImage backgroundImg, optionsBackgroundImg;
	private int bgX, bgY, bgW, bgH;
	private BotonesUrm menuB;

	public OpcionesDeJuego(Juego juego) {
		super(juego);
		loadImgs();
		loadButton();
		opcionesDeAudio = juego.getAudioOptions();
	}

	private void loadButton() {
		int menuX = (int) (387 * Juego.SCALE);
		int menuY = (int) (325 * Juego.SCALE);

		menuB = new BotonesUrm(menuX, menuY, URM_TAMANO, URM_TAMANO, 2);
	}

	private void loadImgs() {
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.MENU_FONDO_IMG);
		optionsBackgroundImg = LoadSave.GetSpriteAtlas(LoadSave.OPCIONES_MENU);

		bgW = (int) (optionsBackgroundImg.getWidth() * Juego.SCALE);
		bgH = (int) (optionsBackgroundImg.getHeight() * Juego.SCALE);
		bgX = Juego.GAME_WIDTH / 2 - bgW / 2;
		bgY = (int) (33 * Juego.SCALE);
	}

	@Override
	public void update() {
		menuB.update();
		opcionesDeAudio.update();
	}

	@Override
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
		g.drawImage(optionsBackgroundImg, bgX, bgY, bgW, bgH, null);

		menuB.draw(g);
		opcionesDeAudio.draw(g);
	}

	public void mouseDragged(MouseEvent e) {
		opcionesDeAudio.mouseDragged(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (isIn(e, menuB)) {
			menuB.setMousePressed(true);
		} else
			opcionesDeAudio.mousePressed(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (isIn(e, menuB)) {
			if (menuB.isMousePressed())
				Gamestate.estado = Gamestate.MENU;
		} else
			opcionesDeAudio.mouseReleased(e);
		menuB.resetBools();
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		menuB.setMouseOver(false);

		if (isIn(e, menuB))
			menuB.setMouseOver(true);
		else
			opcionesDeAudio.mouseMoved(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ESCAPE)
			Gamestate.estado = Gamestate.MENU;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	private boolean isIn(MouseEvent e, BotonDePausa b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

}
