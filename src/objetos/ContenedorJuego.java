package objetos;

import static utilz.Constantes.ObjectosConstantes.*;

import main.Juego;

public class ContenedorJuego extends ObjetosJuego {

	public ContenedorJuego(int x, int y, int objType) {
		super(x, y, objType);
		createHitbox();
	}

	private void createHitbox() {
		if (objType == CAJA) {
			inicializarCajaColision(25, 18);

			xDrawOffset = (int) (7 * Juego.SCALE);
			yDrawOffset = (int) (12 * Juego.SCALE);

		} else {
			inicializarCajaColision(23, 25);
			xDrawOffset = (int) (8 * Juego.SCALE);
			yDrawOffset = (int) (5 * Juego.SCALE);
		}

		cajaColision.y += yDrawOffset + (int) (Juego.SCALE * 2);
		cajaColision.x += xDrawOffset / 2;
	}

	public void update() {
		if (doAnimation)
			updateAnimationTick();
	}
}
