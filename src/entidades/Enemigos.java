package entidades;

import static utilz.Constantes.ConstanteEnemigos.*;
import static utilz.MetodosDeAyuda.*;
import static utilz.Constantes.Direciones.*;
import static utilz.Constantes.*;

import java.awt.geom.Rectangle2D;

import gamestates.Jugando;
import main.Juego;

/**
 * Clase base abstracta para todos los enemigos del juego.
 * Hereda comportamiento general de la clase Entidad.
 */
public abstract class Enemigos extends Entidad {
	protected Jugando jugando;

	// Tipo de enemigo (por ejemplo: cangrejo, tiburón, estrella)
	protected int tipoEnemigo;

	// Bandera para saber si es la primera vez que se actualiza
	protected boolean primeraActualizacion = true;

	// Dirección de movimiento actual del enemigo
	protected int dirCaminar = IZQUIERDA;

	// Celda vertical donde se encuentra el enemigo (para ver si el jugador está a la misma altura)
	protected int celdaY;

	// Distancia desde la cual puede atacar
	protected float distanciaAtaque = Juego.TILES_SIZE;

	// Si el enemigo está activo o ha sido eliminado
	protected boolean activo = true;

	// Controla si ya se revisó el ataque en el frame actual
	protected boolean ataqueChecked;

	// Offset horizontal de la caja de ataque respecto al enemigo
	protected int attackBoxOffsetX;

	// Constructor: inicializa posición, tamaño, tipo y atributos básicos
	public Enemigos(float x, float y, int ancho, int alto, int tipoEnemigo) {
		super(x, y, ancho, alto);
		this.tipoEnemigo = tipoEnemigo;

		vidaMaxima = GetMaxHealth(tipoEnemigo);
		vidaActual = vidaMaxima;
		velocidadDeCaminar = Juego.SCALE * 0.35f;
	}

	// Actualiza la posición de la caja de ataque según la posición del enemigo
	protected void actualizarAttackBox() {
		cajaAtaque.x = cajaColision.x - attackBoxOffsetX;
		cajaAtaque.y = cajaColision.y;
	}

	// Ajusta la caja de ataque cuando el enemigo mira hacia la derecha
	protected void updateAttackBoxFlip() {
		if (dirCaminar == DERECHA)
			cajaAtaque.x = cajaColision.x + cajaColision.width;
		else
			cajaAtaque.x = cajaColision.x - attackBoxOffsetX;

		cajaAtaque.y = cajaColision.y;
	}

	// Inicializa la caja de ataque con su tamaño y desplazamiento
	protected void initAttackBox(int w, int h, int attackBoxOffsetX) {
		cajaAtaque = new Rectangle2D.Float(x, y, (int) (w * Juego.SCALE), (int) (h * Juego.SCALE));
		this.attackBoxOffsetX = (int) (Juego.SCALE * attackBoxOffsetX);
	}

	// Verifica si el enemigo comienza en el aire
	protected void firstUpdateCheck(int[][] lvlData) {
		if (!EstaEntidadEnPiso(cajaColision, lvlData))
			enAire = true;
		primeraActualizacion = false;
	}

	// Comportamiento del enemigo cuando está en el aire (caída, daño por agua, etc.)
	protected void inAirChecks(int[][] lvlData, Jugando jugando) {
		if (estado != HIT && estado != MUERTO) {
			updateInAir(lvlData);
			jugando.getObjectManager().checkSpikesTouched(this);
			if (EntidadEstaEnAgua(cajaColision, lvlData))
				hurt(vidaMaxima); // muere automáticamente si cae al agua
		}
	}

	// Aplica la gravedad al enemigo cuando está en el aire
	protected void updateInAir(int[][] lvlData) {
		if (PuedeMoverAqui(cajaColision.x, cajaColision.y + velocidadAire, cajaColision.width, cajaColision.height, lvlData)) {
			cajaColision.y += velocidadAire;
			velocidadAire += GRAVEDAD;
		} else {
			enAire = false;
			cajaColision.y = GetPosYEntidadEnTechoOPiso(cajaColision, velocidadAire);
			celdaY = (int) (cajaColision.y / Juego.TILES_SIZE);
		}
	}

	// Mueve al enemigo en la dirección actual, o cambia de dirección si no puede continuar
	protected void move(int[][] lvlData) {
		float xSpeed = (dirCaminar == IZQUIERDA) ? -velocidadDeCaminar : velocidadDeCaminar;

		if (PuedeMoverAqui(cajaColision.x + xSpeed, cajaColision.y, cajaColision.width, cajaColision.height, lvlData))
			if (EsPiso(cajaColision, xSpeed, lvlData)) {
				cajaColision.x += xSpeed;
				return;
			}

		changeWalkDir();
	}

	// Hace que el enemigo mire hacia el jugador
	protected void turnTowardsPlayer(Jugador jugador, Jugador jugador2) {

	    float distancia1 = Math.abs(jugador.cajaColision.x - cajaColision.x);
	    float distancia2 = Math.abs(jugador2.cajaColision.x - cajaColision.x);

	    Jugador objetivo = (distancia1 < distancia2) ? jugador : jugador2;

	    if (objetivo.cajaColision.x > cajaColision.x)
	        dirCaminar = DERECHA;
	    else
	        dirCaminar = IZQUIERDA;
		
		//		if (jugador.cajaColision.x > cajaColision.x)
//			dirCaminar = DERECHA;
//		else
//			dirCaminar = IZQUIERDA;
	}

	// Verifica si el enemigo puede ver al jugador
	protected boolean canSeePlayer(int[][] lvlData, Jugador jugador, Jugador jugador2) {
		  // Selecciona el jugador más cercano
	    Jugador objetivo = getJugadorMasCercano(jugador, jugador2);

	    // Calcula la celda vertical del jugador objetivo
	    int playerTileY = (int) (objetivo.obtenerCajaColision().y / Juego.TILES_SIZE);

	    // Verifica si está en la misma fila y si hay línea de visión
	    if (playerTileY == celdaY) {
	        if (isPlayerInRange(objetivo)) {
	            if (IsSightClear(lvlData, cajaColision, objetivo.cajaColision, celdaY)) {
	                return true;
	            }
	        }
	    }
	    return false;
//		int playerTileY = (int) (jugador.obtenerCajaColision().y / Juego.TILES_SIZE);
//		if (playerTileY == celdaY)
//			if (isPlayerInRange(jugador)) {
//				if (IsSightClear(lvlData, cajaColision, jugador.cajaColision, celdaY))
//					return true;
//			}
//		return false;
	}

	// Verifica si el jugador está dentro del rango de visión del enemigo
	protected boolean isPlayerInRange(Jugador jugador) {
		int distancia = (int) Math.abs(jugador.cajaColision.x - cajaColision.x);
		return distancia <= distanciaAtaque * 5;
	}

	// Verifica si el jugador está suficientemente cerca para atacar
	protected boolean isPlayerCloseForAttack(Jugador jugador,Jugador jugador2) {
		
		Jugador objetivo = getJugadorMasCercano(jugador, jugador2);
		int distancia = (int) Math.abs(objetivo.cajaColision.x - cajaColision.x);

		return switch (tipoEnemigo) {
			case CANGREJO -> distancia <= distanciaAtaque;
			case TIBURON -> distancia <= distanciaAtaque * 2;
			default -> false;
		};
//		int distancia = (int) Math.abs(jugador.cajaColision.x - cajaColision.x);
//		return switch (tipoEnemigo) {
//			case CANGREJO -> distancia <= distanciaAtaque;
//			case TIBURON -> distancia <= distanciaAtaque * 2;
//			default -> false;
//		};
	}

	// Aplica daño al enemigo y cambia su estado
	public void hurt(int cantidad) {
		vidaActual -= cantidad;

		if (vidaActual <= 0) {
			if (jugando != null)
				jugando.registrarEnemigoEliminado(this.getClass().getSimpleName());
			cambiarEstado(MUERTO);
		} else {
			cambiarEstado(HIT);
			direccionRetroceso = (dirCaminar == IZQUIERDA) ? DERECHA : IZQUIERDA;
			direccionDesplazamientoRetroceso = ARRIBA;
			desplazamientoRetroceso = 0;
		}
	}
	

	// Verifica si el ataque del enemigo golpeó al jugador
	protected void checkPlayerHit(Rectangle2D.Float cajaAtaque, Jugador jugador, Jugador jugador2) {
		
		  if (!ataqueChecked) {
		        if (cajaAtaque.intersects(jugador.cajaColision)) {
		            jugador.cambiarVida(-GetEnemyDmg(tipoEnemigo), this);
		        } else if (cajaAtaque.intersects(jugador2.cajaColision)) {
		            jugador2.cambiarVida(-GetEnemyDmg(tipoEnemigo), this);
		        }
		        ataqueChecked = true;
		    }
		
//		if (cajaAtaque.intersects(jugador.cajaColision))
//			jugador.cambiarVida(-GetEnemyDmg(tipoEnemigo), this);
//		ataqueChecked = true;
	}

	// Controla la animación del enemigo (avanza frames)
	protected void updateAnimationTick() {
		contadorAnimacion++;
		if (contadorAnimacion >= ANI_VELOCIDAD) {
			contadorAnimacion = 0;
			indiceAnimacion++;
			if (indiceAnimacion >= GetSpriteAmount(tipoEnemigo, estado)) {
				switch (tipoEnemigo) {
					case CANGREJO, TIBURON -> {
						indiceAnimacion = 0;
						if (estado == ATACANDO || estado == HIT)
							estado = IDLE;
						else if (estado == MUERTO)
							activo = false;
					}
					case ESTRELLA -> {
						if (estado == ATACANDO)
							indiceAnimacion = 3;
						else {
							indiceAnimacion = 0;
							if (estado == HIT)
								estado = IDLE;
							else if (estado == MUERTO)
								activo = false;
						}
					}
				}
			}
		}
	}

	// Cambia la dirección de movimiento del enemigo
	protected void changeWalkDir() {
		dirCaminar = (dirCaminar == IZQUIERDA) ? DERECHA : IZQUIERDA;
	}

	// Restablece al enemigo a su estado inicial (por ejemplo, tras morir y reiniciar el nivel)
	public void resetEnemy() {
		cajaColision.x = x;
		cajaColision.y = y;
		primeraActualizacion = true;
		vidaActual = vidaMaxima;
		cambiarEstado(IDLE);
		activo = true;
		velocidadAire = 0;
		desplazamientoRetroceso = 0;
	}

	// Calcula cuánto se debe mover la imagen del enemigo hacia la izquierda si está volteado
	public int flipX() {
		return (dirCaminar == DERECHA) ? ancho : 0;
	}

	// Devuelve la dirección horizontal del sprite: 1 normal, -1 invertido
	public int flipW() {
		return (dirCaminar == DERECHA) ? -1 : 1;
	}

	// Verifica si el enemigo sigue activo (no ha muerto completamente)
	public boolean isActivo() {
		return activo;
	}

	// Devuelve el desplazamiento aplicado por el retroceso
	public float getPushDrawOffset() {
		return desplazamientoRetroceso;
	}
	public void setJugando(Jugando jugando) {
	    this.jugando = jugando;
	}
	private Jugador getJugadorMasCercano(Jugador j1, Jugador j2) {
	    float distancia1 = Math.abs(j1.cajaColision.x - cajaColision.x);
	    float distancia2 = Math.abs(j2.cajaColision.x - cajaColision.x);
	    return (distancia1 < distancia2) ? j1 : j2;
	}
}
