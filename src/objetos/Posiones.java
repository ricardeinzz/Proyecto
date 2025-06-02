package objetos;

import main.Juego;

public class Posiones extends ObjetosJuego {

	private float hoverOffset;
	private int maxHoverOffset, hoverDir = 1;

	public Posiones(int x, int y, int objType) {
		super(x, y, objType);
		doAnimation = true;

		inicializarCajaColision(7, 14);

		xDrawOffset = (int) (3 * Juego.SCALE);
		yDrawOffset = (int) (2 * Juego.SCALE);

		maxHoverOffset = (int) (10 * Juego.SCALE);
	}

	public void update() {
		updateAnimationTick();
		updateHover();
	}

	private void updateHover() {
		hoverOffset += (0.075f * Juego.SCALE * hoverDir);

		if (hoverOffset >= maxHoverOffset)
			hoverDir = -1;
		else if (hoverOffset < 0)
			hoverDir = 1;

		cajaColision.y = y + hoverOffset;
	}
}
