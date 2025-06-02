package objetos;

import java.awt.geom.Rectangle2D;

import main.Juego;

import static utilz.Constantes.Proyectiles.*;

public class Proyectiles {
	private Rectangle2D.Float cajaColision;
	private int dir;
	private boolean active = true;

	public Proyectiles(int x, int y, int dir) {
		int xOffset = (int) (-3 * Juego.SCALE);
		int yOffset = (int) (5 * Juego.SCALE);

		if (dir == 1)
			xOffset = (int) (29 * Juego.SCALE);

		cajaColision = new Rectangle2D.Float(x + xOffset, y + yOffset, BALA_CANON_ANCHO, BALA_CANON_ALTO);
		this.dir = dir;
	}

	public void updatePos() {
		cajaColision.x += dir * VELOCIDAD;
	}

	public void setPos(int x, int y) {
		cajaColision.x = x;
		cajaColision.y = y;
	}

	public Rectangle2D.Float obtenerCajaColision() {
		return cajaColision;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}

}
