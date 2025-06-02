package ui;

import static utilz.Constantes.UI.BotonPausa.TAMANO_SONIDO;
import static utilz.Constantes.UI.BotonVolumen.SLIDER_ANCHO;
import static utilz.Constantes.UI.BotonVolumen.VOLUMEN_ALTO;

import java.awt.Graphics;
import java.awt.event.MouseEvent;

import main.Juego;

public class OpcionesDeAudio {

	private BotonesDeVolumen botonesDeVolumen;
	private BotonDeSonido musicButton, sfxButton;

	private Juego juego;

	public OpcionesDeAudio(Juego juego) {
		this.juego = juego;
		createSoundButtons();
		createVolumeButton();
	}

	private void createVolumeButton() {
		int vX = (int) (309 * Juego.SCALE);
		int vY = (int) (278 * Juego.SCALE);
		botonesDeVolumen = new BotonesDeVolumen(vX, vY, SLIDER_ANCHO, VOLUMEN_ALTO);
	}

	private void createSoundButtons() {
		int soundX = (int) (450 * Juego.SCALE);
		int musicY = (int) (140 * Juego.SCALE);
		int sfxY = (int) (186 * Juego.SCALE);
		musicButton = new BotonDeSonido(soundX, musicY, TAMANO_SONIDO, TAMANO_SONIDO);
		sfxButton = new BotonDeSonido(soundX, sfxY, TAMANO_SONIDO, TAMANO_SONIDO);
	}

	public void update() {
		musicButton.update();
		sfxButton.update();

		botonesDeVolumen.update();
	}

	public void draw(Graphics g) {
		// Sound buttons
		musicButton.draw(g);
		sfxButton.draw(g);

		// Volume Button
		botonesDeVolumen.draw(g);
	}

	public void mouseDragged(MouseEvent e) {
		if (botonesDeVolumen.isMousePressed()) {
			float valueBefore = botonesDeVolumen.getFloatValue();
			botonesDeVolumen.changeX(e.getX());
			float valueAfter = botonesDeVolumen.getFloatValue();
			if (valueBefore != valueAfter)
				juego.getAudioPlayer().setVolumen(valueAfter);
		}
	}

	public void mousePressed(MouseEvent e) {
		if (isIn(e, musicButton))
			musicButton.setMousePressed(true);
		else if (isIn(e, sfxButton))
			sfxButton.setMousePressed(true);
		else if (isIn(e, botonesDeVolumen))
			botonesDeVolumen.setMousePressed(true);
	}

	public void mouseReleased(MouseEvent e) {
		if (isIn(e, musicButton)) {
			if (musicButton.isMousePressed()) {
				musicButton.setMuted(!musicButton.isMuted());
				juego.getAudioPlayer().toggleSongMute();
			}

		} else if (isIn(e, sfxButton)) {
			if (sfxButton.isMousePressed()) {
				sfxButton.setMuted(!sfxButton.isMuted());
				juego.getAudioPlayer().toggleEffectMute();
			}
		}

		musicButton.resetBools();
		sfxButton.resetBools();

		botonesDeVolumen.resetBools();
	}

	public void mouseMoved(MouseEvent e) {
		musicButton.setMouseOver(false);
		sfxButton.setMouseOver(false);

		botonesDeVolumen.setMouseOver(false);

		if (isIn(e, musicButton))
			musicButton.setMouseOver(true);
		else if (isIn(e, sfxButton))
			sfxButton.setMouseOver(true);
		else if (isIn(e, botonesDeVolumen))
			botonesDeVolumen.setMouseOver(true);
	}

	private boolean isIn(MouseEvent e, BotonDePausa b) {
		return b.getBounds().contains(e.getX(), e.getY());
	}

}
