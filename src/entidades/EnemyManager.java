package entidades;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import gamestates.Jugando;
import niveles.Nivel;
import utilz.LoadSave;
import static utilz.Constantes.ConstanteEnemigos.*;

/**
 * Clase encargada de gestionar todos los enemigos del nivel actual.
 * Controla la carga, actualización, dibujo y colisiones de enemigos como Cangrejo, Estrella y Tiburón.
 */
public class EnemyManager {

	private Jugando jugando; // Referencia al estado actual del juego.
	private BufferedImage[][] cangrejoArr, estrellaArr, tiburonArr; // Arreglos con las animaciones de los enemigos.
	private Nivel nivelActual; // Nivel actual donde se encuentran los enemigos.

	// Constructor: se guarda el estado Jugando y se cargan las imágenes de los enemigos.
	public EnemyManager(Jugando jugando) {
		this.jugando = jugando;
		cargarImgEnemigos();
	}

	// Asigna los enemigos del nivel actual al EnemyManager.
	public void cargarEnemigos(Nivel nivel) {
		this.nivelActual = nivel;
	}

	// Actualiza todos los enemigos activos del nivel.
	public void actualizar(int[][] lvlData) {
		boolean isAnyActive = false;

		// Actualiza cada cangrejo si está activo.
		for (Cangrejo c : nivelActual.getCrabs())
			if (c.isActivo()) {
				c.actualizar(lvlData, jugando);
				isAnyActive = true;
			}

		// Actualiza cada estrella si está activa.
		for (Estrella p : nivelActual.getPinkstars())
			if (p.isActivo()) {
				p.actualizar(lvlData, jugando);
				isAnyActive = true;
			}

		// Actualiza cada tiburón si está activo.
		for (Tiburon s : nivelActual.getSharks())
			if (s.isActivo()) {
				s.actualizar(lvlData, jugando);
				isAnyActive = true;
			}

		// Si no quedan enemigos activos, se marca el nivel como completado.
		if (!isAnyActive)
			jugando.setLevelCompleted(true);
	}

	// Dibuja todos los enemigos activos en pantalla.
	public void dibujar(Graphics g, int xLvlOffset) {
		dibujarCangrejo(g, xLvlOffset);
		dibujarEstrella(g, xLvlOffset);
		dibujarTiburon(g, xLvlOffset);
	}

	// Dibuja todos los tiburones activos.
	private void dibujarTiburon(Graphics g, int xLvlOffset) {
		for (Tiburon s : nivelActual.getSharks())
			if (s.isActivo()) {
				g.drawImage(
					tiburonArr[s.getState()][s.obtenerIndiceAnimacion()],
					(int) s.obtenerCajaColision().x - xLvlOffset - TIBURON_DIBUJOOFFSET_X + s.flipX(),
					(int) s.obtenerCajaColision().y - TIBURON_DIBUJOOFFSET_Y + (int) s.getPushDrawOffset(),
					TIBURON_ANCHO * s.flipW(),
					TIBURON_ALTO,
					null
				);
				// s.dibujarCajaColision(g, xLvlOffset);
				// s.dibujarCajaAtaque(g, xLvlOffset);
			}
	}

	// Dibuja todas las estrellas activas.
	private void dibujarEstrella(Graphics g, int xLvlOffset) {
		for (Estrella p : nivelActual.getPinkstars())
			if (p.isActivo()) {
				g.drawImage(
					estrellaArr[p.getState()][p.obtenerIndiceAnimacion()],
					(int) p.obtenerCajaColision().x - xLvlOffset - ESTRELLA_DIBUJOOFFSET_X + p.flipX(),
					(int) p.obtenerCajaColision().y - ESTRELLA_DIBUJOOFFSET_Y + (int) p.getPushDrawOffset(),
					ESTRELLA_ANCHO * p.flipW(),
					ESTRELLA_ALTO,
					null
				);
				// p.dibujarCajaColision(g, xLvlOffset);
			}
	}

	// Dibuja todos los cangrejos activos.
	private void dibujarCangrejo(Graphics g, int xLvlOffset) {
		for (Cangrejo c : nivelActual.getCrabs())
			if (c.isActivo()) {
				g.drawImage(
					cangrejoArr[c.getState()][c.obtenerIndiceAnimacion()],
					(int) c.obtenerCajaColision().x - xLvlOffset - CANGREJO_DIBUJOOFFSET_X + c.flipX(),
					(int) c.obtenerCajaColision().y - CANGREJO_DIBUJOOFFSET_Y + (int) c.getPushDrawOffset(),
					CANGREJO_ANCHO * c.flipW(),
					CANGREJO_ALTO,
					null
				);
				// c.dibujarCajaColision(g, xLvlOffset);
				// c.dibujarCajaAtaque(g, xLvlOffset);
			}
	}

	// Verifica si el jugador golpeó a algún enemigo mediante su caja de ataque.
	public void checkHitEnemigo(Rectangle2D.Float cajaAtaque) {

		// Verifica golpe a cangrejos.
		for (Cangrejo c : nivelActual.getCrabs())
			if (c.isActivo())
				if (c.getState() != MUERTO && c.getState() != HIT)
					if (cajaAtaque.intersects(c.obtenerCajaColision())) {
						c.hurt(20); // Aplica daño de 20.
						return;
					}

		// Verifica golpe a estrellas.
		for (Estrella p : nivelActual.getPinkstars())
			if (p.isActivo()) {
				if (p.getState() == ATACANDO && p.obtenerIndiceAnimacion() >= 3)
					return; // No se puede golpear si está en animación avanzada de ataque.
				else {
					if (p.getState() != MUERTO && p.getState() != HIT)
						if (cajaAtaque.intersects(p.obtenerCajaColision())) {
							p.hurt(20);
							return;
						}
				}
			}

		// Verifica golpe a tiburones.
		for (Tiburon s : nivelActual.getSharks())
			if (s.isActivo()) {
				if (s.getState() != MUERTO && s.getState() != HIT)
					if (cajaAtaque.intersects(s.obtenerCajaColision())) {
						s.hurt(20);
						return;
					}
			}
	}

	// Carga las animaciones de cada enemigo desde sus respectivos atlas de sprites.
	private void cargarImgEnemigos() {
		cangrejoArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.CANGREJO_SPRITE), 9, 5, CANGREJO_ANCHO_DEFAULT, CANGREJO_ALTO_DEFAULT);
		estrellaArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.ESTRELLA_ATLAS), 8, 5, ESTRELLA_ANCHO_DEFAULT, ESTRELLA_ALTO_DEFAULT);
		tiburonArr = getImgArr(LoadSave.GetSpriteAtlas(LoadSave.TIBURON_ATLAS), 8, 5, TIBURON_ANCHO_DEFAULT, TIBURON_ALTO_DEFAULT);
	}

	// Convierte una hoja de sprites en una matriz de imágenes individuales por animación.
	private BufferedImage[][] getImgArr(BufferedImage atlas, int xSize, int ySize, int spriteW, int spriteH) {
		BufferedImage[][] tempArr = new BufferedImage[ySize][xSize];
		for (int j = 0; j < tempArr.length; j++)
			for (int i = 0; i < tempArr[j].length; i++)
				tempArr[j][i] = atlas.getSubimage(i * spriteW, j * spriteH, spriteW, spriteH);
		return tempArr;
	}

	// Reinicia el estado de todos los enemigos (como si el nivel comenzara de nuevo).
	public void reiniciarEnemigos() {
		for (Cangrejo c : nivelActual.getCrabs())
			c.resetEnemy();
		for (Estrella p : nivelActual.getPinkstars())
			p.resetEnemy();
		for (Tiburon s : nivelActual.getSharks())
			s.resetEnemy();
	}
}

