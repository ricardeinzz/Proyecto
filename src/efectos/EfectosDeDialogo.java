package efectos;

import static utilz.Constantes.ANI_VELOCIDAD; // Velocidad general de las animaciones.
import static utilz.Constantes.Dialogo.*; // Constantes relacionadas con los tipos de diálogos (¡!, ¿?, etc).

/**
 * Clase que representa un efecto visual de diálogo que aparece sobre entidades,
 * como una exclamación, interrogación u otro símbolo animado.
 */
public class EfectosDeDialogo {

	private int x, y, type; // Posición (x, y) donde se muestra el efecto y tipo de símbolo.
	private int indiceAnimacion, contadorAnimacion; // Controlan la animación del efecto.
	private boolean active = true; // Indica si el efecto está activo y debe mostrarse.

	// Constructor: inicializa el efecto con posición y tipo.
	public EfectosDeDialogo(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;
	}

	// Método que actualiza la animación del efecto.
	public void update() {
		contadorAnimacion++; // Aumenta el contador de tiempo de animación.

		if (contadorAnimacion >= ANI_VELOCIDAD) { // Si se alcanzó el tiempo para cambiar de frame...
			contadorAnimacion = 0; // Reinicia el contador.
			indiceAnimacion++; // Pasa al siguiente frame.

			if (indiceAnimacion >= GetSpriteAmount(type)) {
				// Si ya terminó la animación (último frame), se desactiva.
				active = false;
				indiceAnimacion = 0; // Reinicia la animación por si se vuelve a usar.
			}
		}
	}

	// Método para desactivar manualmente el efecto.
	public void deactive() {
		active = false;
	}

	// Reinicia el efecto en una nueva posición, reactivándolo.
	public void reset(int x, int y) {
		this.x = x;
		this.y = y;
		active = true;
	}

	// Devuelve el índice actual de la animación.
	public int obtenerIndiceAnimacion() {
		return indiceAnimacion;
	}

	// Devuelve la posición X.
	public int getX() {
		return x;
	}

	// Devuelve la posición Y.
	public int getY() {
		return y;
	}

	// Devuelve el tipo de diálogo (exclamación, interrogación, etc).
	public int getType() {
		return type;
	}

	// Devuelve si el efecto está activo (visible) o no.
	public boolean isActive() {
		return active;
	}
}

