package entidades; // Paquete que contiene las entidades del juego, como enemigos y el jugador.

import static utilz.Constantes.Dialogo.*; // Importa símbolos visuales de diálogo (como exclamación).
import static utilz.Constantes.Direciones.IZQUIERDA; // Dirección usada para el movimiento.
import static utilz.Constantes.ConstanteEnemigos.*; // Constantes específicas del enemigo Tiburon.
import static utilz.MetodosDeAyuda.PuedeMoverAqui; // Verifica si una posición es transitable.
import static utilz.MetodosDeAyuda.EsPiso; // Verifica si hay suelo debajo de una entidad.

import gamestates.Jugando; // Clase que representa el estado del juego actual (nivel, jugador, etc).

/**
 * Clase que representa al enemigo tipo Tiburón.
 * Tiene lógica de persecución y ataque rápido cuando ve al jugador.
 */
public class Tiburon extends Enemigos {

	// Constructor: inicializa la posición, tamaño y tipo del tiburón.
	public Tiburon(float x, float y) {
		super(x, y, TIBURON_ANCHO, TIBURON_ALTO, TIBURON); // Llama al constructor de la clase Enemigos.
		inicializarCajaColision(18, 22); // Define el área de colisión física.
		initAttackBox(20, 20, 20); // Define el área de ataque del enemigo.
	}

	// Método principal que se llama cada frame para actualizar al enemigo.
	public void actualizar(int[][] lvlData, Jugando jugando) {
		actualizarComportamiento(lvlData, jugando); // Aplica la lógica de comportamiento del tiburón.
		updateAnimationTick(); // Actualiza el frame actual de animación.
		updateAttackBoxFlip(); // Ajusta la dirección de la caja de ataque.
	}

	// Lógica de comportamiento según el estado actual y entorno.
	private void actualizarComportamiento(int[][] lvlData, Jugando jugando) {
		 Jugador jugador1=jugando.getPlayer();
	     Jugador jugador2=jugando.getPlayer2();
		if (primeraActualizacion)
			firstUpdateCheck(lvlData); // Determina si está en el aire o no al aparecer.

		if (enAire)
			inAirChecks(lvlData, jugando); // Si está en el aire, aplica la lógica de caída.
		else {
			switch (estado) {

				case IDLE: // Estado quieto, esperando.
					if (EsPiso(cajaColision, lvlData)) // Si está sobre una plataforma...
						cambiarEstado(CORRIENDO); // Empieza a correr.
					else
						enAire = true; // Si no hay piso, entra en estado de caída.
					break;

				case CORRIENDO: // Estado de movimiento horizontal.
					if (canSeePlayer(lvlData, jugador1,jugador2)) { // Si ve al jugador...
						turnTowardsPlayer(jugador1,jugador2); // Se gira hacia él.
						if (isPlayerCloseForAttack(jugador1,jugador2)) // Si está cerca...
							cambiarEstado(ATACANDO); // Comienza a atacar.
					}

					move(lvlData); // Se desplaza horizontalmente.
					break;

				case ATACANDO: // Estado de ataque.
					if (indiceAnimacion == 0)
						ataqueChecked = false; // Al iniciar la animación, reinicia el chequeo de ataque.

					else if (indiceAnimacion == 3) { // Frame específico donde ocurre el ataque.
						if (!ataqueChecked)
							checkPlayerHit(cajaAtaque, jugador1,jugador2); // Verifica si golpea al jugador.

						atacarEnMovimiento(lvlData, jugando); // Movimiento agresivo hacia el jugador.
					}
					break;

				case HIT: // Estado de recibir daño.
					if (indiceAnimacion <= GetSpriteAmount(tipoEnemigo, estado) - 2)
						retroceder(direccionRetroceso, lvlData, 2f); // Retrocede al ser golpeado.

					actualizarDesplazamientoRetroceso(); // Ajusta el desplazamiento por el impacto.
					break;
			}
		}
	}

	// Movimiento especial durante el ataque: se lanza rápidamente hacia adelante.
	protected void atacarEnMovimiento(int[][] lvlData, Jugando jugando) {
		float xSpeed = 0;

		// Define dirección del movimiento según a dónde esté mirando.
		if (dirCaminar == IZQUIERDA)
			xSpeed = -velocidadDeCaminar;
		else
			xSpeed = velocidadDeCaminar;

		// Multiplica la velocidad por 4 para simular un embiste.
		if (PuedeMoverAqui(cajaColision.x + xSpeed * 4, cajaColision.y, cajaColision.width, cajaColision.height, lvlData))
			if (EsPiso(cajaColision, xSpeed * 4, lvlData)) {
				cajaColision.x += xSpeed * 4; // Se mueve rápidamente hacia adelante.
				return;
			}

		// Si no puede avanzar (chocó con algo), vuelve a estado IDLE y muestra exclamación.
		cambiarEstado(IDLE);
		jugando.addDialogue((int) cajaColision.x, (int) cajaColision.y, EXCLAMACION);
	}
}
