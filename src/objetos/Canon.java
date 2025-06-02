package objetos;

import main.Juego;

public class Canon extends ObjetosJuego {

	private int tileY;

	public Canon(int x, int y, int objType) {
		super(x, y, objType);
		tileY = y / Juego.TILES_SIZE;
		inicializarCajaColision(40, 26);
//		cajaColision.x -= (int) (1 * Juego.SCALE);
		cajaColision.y += (int) (6 * Juego.SCALE);
	}

	public void update() {
		if (doAnimation)
			updateAnimationTick();
	}

	public int getTileY() {
		return tileY;
	}

}
