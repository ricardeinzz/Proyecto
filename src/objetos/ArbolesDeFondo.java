package objetos;

import java.util.Random;

public class ArbolesDeFondo {

	private int x, y, type, indiceAnimacion, contadorAnimacion;

	public ArbolesDeFondo(int x, int y, int type) {
		this.x = x;
		this.y = y;
		this.type = type;

		// Sets the indiceAnimacion to a random value, to get some variations for the trees so
		// they all don't move in synch.
		Random r = new Random();
		indiceAnimacion = r.nextInt(4);

	}

	public void update() {
		contadorAnimacion++;
		if (contadorAnimacion >= 35) {
			contadorAnimacion = 0;
			indiceAnimacion++;
			if (indiceAnimacion >= 4)
				indiceAnimacion = 0;
		}
	}

	public int obtenerIndiceAnimacion() {
		return indiceAnimacion;
	}

	public void setAniIndex(int indiceAnimacion) {
		this.indiceAnimacion = indiceAnimacion;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
}
