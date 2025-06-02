package efectos;

import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import main.Juego;
import utilz.LoadSave;

/**
 * Clase que simula el efecto visual de lluvia en el juego.
 * Genera partículas que caen continuamente y se reinician al llegar al fondo.
 */
public class Lluvia {

	private Point2D.Float[] caida; // Arreglo de gotas de lluvia, cada una con posición (x, y).
	private Random rand; // Para generar posiciones aleatorias.
	private float velocidadDeLluvia = 1.25f; // Velocidad de caída vertical de las gotas.
	private BufferedImage particualasDeLluvia; // Imagen de cada partícula de lluvia (gotita).

	// Constructor: inicializa las gotas y carga el sprite.
	public Lluvia() {
		rand = new Random();
		caida = new Point2D.Float[1000]; // Se crean 1000 gotas de lluvia.
		particualasDeLluvia = LoadSave.GetSpriteAtlas(LoadSave.PARTICULAS_LLUVIA); // Carga la imagen de la partícula.
		initDrops(); // Inicializa posiciones aleatorias de cada gota.
	}

	// Inicializa cada gota con una posición aleatoria en el eje X e Y.
	private void initDrops() {
		for (int i = 0; i < caida.length; i++)
			caida[i] = getRndPos(); // Posición inicial aleatoria.
	}

	// Genera una posición aleatoria en X dentro de un rango, y en Y dentro del alto del juego.
	private Point2D.Float getRndPos() {
		return new Point2D.Float((int) getNewX(0), rand.nextInt(Juego.GAME_HEIGHT));
	}

	// Actualiza la posición de cada gota, haciendo que caiga hacia abajo.
	// Si llega al fondo, la reposiciona arriba con nueva X aleatoria.
	public void update(int xLvlOffset) {
		for (Point2D.Float p : caida) {
			p.y += velocidadDeLluvia; // Movimiento vertical

			if (p.y >= Juego.GAME_HEIGHT) { // Si sale de la pantalla...
				p.y = -20; // Se reinicia arriba
				p.x = getNewX(xLvlOffset); // Con nueva X aleatoria
			}
		}
	}

	// Genera una nueva posición X aleatoria, desplazada según el offset del nivel.
	private float getNewX(int xLvlOffset) {
		float value = (-Juego.GAME_WIDTH) + rand.nextInt((int) (Juego.GAME_WIDTH * 3f)) + xLvlOffset;
		return value;
	}

	// Dibuja todas las gotas en pantalla con su posición actual.
	public void dibujar(Graphics g, int xLvlOffset) {
		for (Point2D.Float p : caida)
			g.drawImage(particualasDeLluvia, (int) p.getX() - xLvlOffset, (int) p.getY(), 3, 12, null);
	}
}
