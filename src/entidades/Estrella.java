package entidades; // Paquete que contiene todas las entidades del juego, como enemigos o personajes.

import static utilz.Constantes.ConstanteEnemigos.*; // Constantes específicas del enemigo tipo Estrella.
import static utilz.Constantes.Dialogo.*; // Constantes para mostrar elementos visuales como diálogos.
import static utilz.MetodosDeAyuda.PuedeMoverAqui; // Método que verifica si una posición es transitable.
import static utilz.MetodosDeAyuda.EsPiso; // Método que verifica si hay piso debajo de una entidad.
import static utilz.Constantes.Direciones.*; // Constantes de dirección (IZQUIERDA, DERECHA, etc).

import gamestates.Jugando; // Estado actual del juego, con acceso al jugador y demás elementos.

/**
 * Clase que representa al enemigo tipo Estrella.
 * Tiene un comportamiento especial de rodar cuando ataca.
 */
public class Estrella extends Enemigos {

	private boolean preRoll = true; // Indica si está en la fase previa al rodar (preparación de ataque).
	private int tickdesdeElUltimoDmgPlayer; // Ticks desde el último daño causado al jugador.
	private int tickDespuesRollEnIdle; // Contador para saber cuánto tiempo lleva en estado IDLE luego de rodar.
	private int duracionRollTick, duracionRoll = 300; // Contadores para controlar la duración del rodado.

	// Constructor de la estrella con posición inicial.
	public Estrella(float x, float y) {
		super(x, y, ESTRELLA_ANCHO, ESTRELLA_ALTO, ESTRELLA); // Inicializa con tamaño y tipo del enemigo.
		inicializarCajaColision(17, 21); // Define su caja de colisión para detectar obstáculos.
	}

	// Método que se llama cada frame del juego para actualizar el estado del enemigo.
	public void actualizar(int[][] lvlData, Jugando jugando) {
		actualizarComportamiento(lvlData, jugando); // Aplica la lógica de comportamiento.
		updateAnimationTick(); // Avanza el frame de la animación.
	}

	// Método que define el comportamiento del enemigo según su estado y entorno.
	private void actualizarComportamiento(int[][] lvlData, Jugando jugando) {
	    Jugador jugador1 = jugando.getPlayer();
	    Jugador jugador2 = jugando.getPlayer2();

	    if (primeraActualizacion)
	        firstUpdateCheck(lvlData);

	    if (enAire) {
	        inAirChecks(lvlData, jugando);
	    } else {
	        switch (estado) {
	            case IDLE:
	                preRoll = true;
	                if (tickDespuesRollEnIdle >= 120) {
	                    if (EsPiso(cajaColision, lvlData))
	                        cambiarEstado(CORRIENDO);
	                    else
	                        enAire = true;

	                    tickDespuesRollEnIdle = 0;
	                    tickdesdeElUltimoDmgPlayer = 60;
	                } else {
	                    tickDespuesRollEnIdle++;
	                }
	                break;

	            case CORRIENDO:
	                if (canSeePlayer(lvlData, jugador1, jugador2)) {
	                    cambiarEstado(ATACANDO);

	                    //Gira hacia el jugador más cercano
	                    float d1 = Math.abs(jugador1.cajaColision.x - cajaColision.x);
	                    float d2 = Math.abs(jugador2.cajaColision.x - cajaColision.x);
	                    Jugador objetivo = (d1 < d2) ? jugador1 : jugador2;
	                    setWalkDir(objetivo);
	                }
	                mover(lvlData, jugando);
	                break;

	            case ATACANDO:
	                if (preRoll) {
	                    if (indiceAnimacion >= 3)
	                        preRoll = false;
	                } else {
	                    mover(lvlData, jugando);

	                    //Revisa colisión contra ambos jugadores
	                    checkDmgAlPlayer(jugador1);
	                    checkDmgAlPlayer(jugador2);

	                    checkRollOver(jugando);
	                }
	                break;

	            case HIT:
	                if (indiceAnimacion <= GetSpriteAmount(tipoEnemigo, estado) - 2)
	                    retroceder(direccionRetroceso, lvlData, 2f);

	                actualizarDesplazamientoRetroceso();
	                tickDespuesRollEnIdle = 120;
	                break;
	        }
	    }
	}


	// Verifica si está colisionando con el jugador para hacerle daño.
	private void checkDmgAlPlayer(Jugador jugador) {
		if (cajaColision.intersects(jugador.obtenerCajaColision())) // Si colisiona con el jugador...
			if (tickdesdeElUltimoDmgPlayer >= 60) { // Y ha pasado suficiente tiempo desde el último daño...
				tickdesdeElUltimoDmgPlayer = 0;
				jugador.cambiarVida(-GetEnemyDmg(tipoEnemigo), this); // Resta vida al jugador.
			} else
				tickdesdeElUltimoDmgPlayer++; // Si no, espera más ticks antes de volver a hacer daño.
	}

	// Verifica si ha rodado por suficiente tiempo y debe detenerse.
	private void checkRollOver(Jugando jugando) {
		duracionRollTick++;
		if (duracionRollTick >= duracionRoll) {
			rollOver(jugando);
			duracionRollTick = 0;
		}
	}

	// Lógica de movimiento de la estrella, diferente si está atacando.
	protected void mover(int[][] lvlData, Jugando jugando) {
		float xSpeed = 0;

		// Define la velocidad según la dirección.
		if (dirCaminar == IZQUIERDA)
			xSpeed = -velocidadDeCaminar;
		else
			xSpeed = velocidadDeCaminar;

		// Si está atacando, se mueve al doble de velocidad.
		if (estado == ATACANDO)
			xSpeed *= 2;

		// Verifica si puede moverse al nuevo lugar sin chocar.
		if (PuedeMoverAqui(cajaColision.x + xSpeed, cajaColision.y, cajaColision.width, cajaColision.height, lvlData))
			if (EsPiso(cajaColision, xSpeed, lvlData)) {
				cajaColision.x += xSpeed; // Se mueve horizontalmente.
				return;
			}

		// Si no puede avanzar mientras ataca, se detiene y muestra símbolo de pregunta.
		if (estado == ATACANDO) {
			rollOver(jugando);
			duracionRollTick = 0;
		}

		changeWalkDir(); // Cambia la dirección del movimiento si choca con algo.
	}

	// Detiene el rodado y vuelve al estado IDLE. Muestra un globo de pregunta como "¿dónde está el jugador?".
	private void rollOver(Jugando jugando) {
		cambiarEstado(IDLE);
		jugando.addDialogue((int) cajaColision.x, (int) cajaColision.y, PREGUNTA);
	}

	// Define hacia qué dirección caminará la estrella (izquierda o derecha).
	private void setWalkDir(Jugador jugador) {
		if (jugador.obtenerCajaColision().x > cajaColision.x)
			dirCaminar = DERECHA;
		else
			dirCaminar = IZQUIERDA;
	}
}

