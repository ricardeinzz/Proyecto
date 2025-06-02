package gamestates;

/**
 * Enum que representa los distintos estados del juego.
 * Se utiliza para gestionar en qué parte del juego se encuentra el jugador.
 */
public enum Gamestate {

	JUGANDO,             // Estado en el que se está jugando activamente.
	MENU,                // Estado del menú principal.
	OPCIONES,            // Estado de opciones/configuración.
	SALIR,               // Estado para cerrar o salir del juego.
	CREDITOS,            // Estado de pantalla de créditos.
	SELECIONAR_JUGADOR_; // Estado para seleccionar el personaje/jugador (nota: el guion bajo final parece un error tipográfico).

	// Variable estática que mantiene el estado actual del juego.
	public static Gamestate estado = MENU; // Se inicia en el menú por defecto.
}
