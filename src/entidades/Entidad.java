package entidades;


import static utilz.Constantes.Direciones.ABAJO;
import static utilz.Constantes.Direciones.IZQUIERDA;
import static utilz.Constantes.Direciones.ARRIBA;
import static utilz.MetodosDeAyuda.PuedeMoverAqui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Juego;

// Clase abstracta que sirve como base para todos los personajes o enemigos en el juego
public abstract class Entidad {

	// Posición en el mundo del juego
	protected float x, y;

	// Dimensiones del personaje
	protected int ancho, alto;

	// Caja de colisión para detectar impactos con el entorno
	protected Rectangle2D.Float cajaColision;

	// Control de animación: índice actual y ticks para temporizar
	protected int contadorAnimacion, indiceAnimacion;

	// Estado actual del personaje (ej: quieto, corriendo, atacando)
	protected int estado;

	// Variables para controlar el salto o caídas
	protected float velocidadAire;
	protected boolean enAire = false;

	// Vida del personaje
	protected int vidaMaxima;
	protected int vidaActual;

	// Caja para detectar ataques del personaje
	protected Rectangle2D.Float cajaAtaque;

	// Velocidad a la que se mueve caminando
	protected float velocidadDeCaminar;

	// Control del retroceso cuando el personaje recibe daño
	protected int direccionRetroceso;
	protected float desplazamientoRetroceso;
	protected int direccionDesplazamientoRetroceso = ARRIBA;

	// Constructor: establece posición y tamaño inicial
	public Entidad(float x, float y, int ancho, int alto) {
		this.x = x;
		this.y = y;
		this.ancho = ancho;
		this.alto = alto;
	}

	// Controla la animación del retroceso cuando el personaje es golpeado
	protected void actualizarDesplazamientoRetroceso() {
		float velocidad = 0.95f;
		float limite = -30f;

		if (direccionDesplazamientoRetroceso == ARRIBA) {
			desplazamientoRetroceso -= velocidad;
			if (desplazamientoRetroceso <= limite)
				direccionDesplazamientoRetroceso = ABAJO;
		} else {
			desplazamientoRetroceso += velocidad;
			if (desplazamientoRetroceso >= 0)
				desplazamientoRetroceso = 0;
		}
	}

	// Mueve al personaje hacia atrás (retroceso) si es posible
	protected void retroceder(int direccionRetroceso, int[][] lvlData, float multiplicadorVelocidad) {
		float velocidadX = 0;
		if (direccionRetroceso == IZQUIERDA)
			velocidadX = -velocidadDeCaminar;
		else
			velocidadX = velocidadDeCaminar;

		if (PuedeMoverAqui(cajaColision.x + velocidadX * multiplicadorVelocidad, cajaColision.y, cajaColision.width, cajaColision.height, lvlData))
			cajaColision.x += velocidadX * multiplicadorVelocidad;
	}

	// Dibuja la caja de ataque en pantalla (útil para depuración)
	protected void dibujarCajaAtaque(Graphics g, int desplazamientoX) {
		g.setColor(Color.red);
		g.drawRect((int) (cajaAtaque.x - desplazamientoX), (int) cajaAtaque.y, (int) cajaAtaque.width, (int) cajaAtaque.height);
	}

	// Dibuja la caja de colisión del personaje (útil para depuración)
	protected void dibujarCajaColision(Graphics g, int desplazamientoX) {
		g.setColor(Color.BLUE);
		g.drawRect((int) cajaColision.x - desplazamientoX, (int) cajaColision.y, (int) cajaColision.width, (int) cajaColision.height);
	}

	// Inicializa la caja de colisión con las dimensiones escaladas al tamaño del juego
	protected void inicializarCajaColision(int ancho, int alto) {
		cajaColision = new Rectangle2D.Float(x, y, (int) (ancho * Juego.SCALE), (int) (alto * Juego.SCALE));
	}

	// Devuelve la caja de colisión actual
	public Rectangle2D.Float obtenerCajaColision() {
		return cajaColision;
	}

	// Devuelve el estado actual del personaje (idle, corriendo, golpeado, etc.)
	public int getState() {
		return estado;
	}

	// Devuelve el índice de animación para saber qué frame dibujar
	public int obtenerIndiceAnimacion() {
		return indiceAnimacion;
	}

	// Cambia el estado del personaje y reinicia la animación
	protected void cambiarEstado(int estado) {
		this.estado = estado;
		contadorAnimacion = 0;
		indiceAnimacion = 0;
	}
}
