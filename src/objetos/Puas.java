package objetos;

import main.Juego;

public class Puas extends ObjetosJuego{

	public Puas(int x, int y, int objType) {
		super(x, y, objType);
		inicializarCajaColision(32, 16);
		xDrawOffset = 0;
		yDrawOffset = (int)(Juego.SCALE * 16);
		cajaColision.y += yDrawOffset;
	}
}
