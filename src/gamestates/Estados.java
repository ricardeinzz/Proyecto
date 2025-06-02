package gamestates;

import java.awt.event.MouseEvent;

import audio.AudioJugador;
import main.Juego;
import ui.BotonesDeMenu;

/**
 * Clase base para los diferentes estados del juego (MENÚ, JUGANDO, CRÉDITOS, etc.).
 * Provee funciones comunes como cambiar el estado o detectar clics sobre botones.
 */
public class Estados {

	protected Juego juego; // Referencia al objeto principal del juego.

	// Constructor: se recibe el juego actual para tener acceso global desde los estados.
	public Estados(Juego juego) {
		this.juego = juego;
	}

	/**
	 * Verifica si el MouseEvent ocurrió dentro de los límites del botón dado.
	 * @param e Evento del mouse.
	 * @param mb Botón del menú a verificar.
	 * @return true si el mouse está dentro del botón, false si no.
	 */
	public boolean isIn(MouseEvent e, BotonesDeMenu mb) {
		return mb.getBounds().contains(e.getX(), e.getY());
	}

	/**
	 * Devuelve el objeto principal del juego.
	 */
	public Juego getGame() {
		return juego;
	}

	/**
	 * Cambia el estado del juego (por ejemplo, de MENU a JUGANDO) y
	 * actualiza la música correspondiente según el nuevo estado.
	 * 
	 * @param estado Nuevo estado a establecer.
	 */
	@SuppressWarnings("incomplete-switch") // El switch no incluye todos los posibles estados.
	public void setGamestate(Gamestate estado) {
		switch (estado) {
			case MENU -> juego.getAudioPlayer().reproducirCancion(AudioJugador.MENU_1); // Música del menú
			case JUGANDO -> juego.getAudioPlayer().setLevelSong(juego.getPlaying().getLevelManager().getLevelIndex()); // Música del nivel actual
		}

		Gamestate.estado = estado; // Establece el nuevo estado global.
	}
}
