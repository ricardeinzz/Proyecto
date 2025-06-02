package objetos;

import static utilz.Constantes.ANI_VELOCIDAD;
import static utilz.Constantes.ObjectosConstantes.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.geom.Rectangle2D;

import main.Juego;

public class ObjetosJuego {

	protected int x, y, objType;
	protected Rectangle2D.Float cajaColision;
	protected boolean doAnimation, active = true;
	protected int contadorAnimacion, indiceAnimacion;
	protected int xDrawOffset, yDrawOffset;

	public ObjetosJuego(int x, int y, int objType) {
		this.x = x;
		this.y = y;
		this.objType = objType;
	}

	protected void updateAnimationTick() {
		contadorAnimacion++;
		if (contadorAnimacion >= ANI_VELOCIDAD) {
			contadorAnimacion = 0;
			indiceAnimacion++;
			if (indiceAnimacion >= GetSpriteAmount(objType)) {
				indiceAnimacion = 0;
				if (objType == BARRIL || objType == CAJA) {
					doAnimation = false;
					active = false;
				} else if (objType == CANON_IZQUIERDA || objType == CANON_DERECHA)
					doAnimation = false;
			}
		}
	}

	public void reset() {
		indiceAnimacion = 0;
		contadorAnimacion = 0;
		active = true;

		if (objType == BARRIL || objType == CAJA || objType == CANON_IZQUIERDA || objType == CANON_DERECHA)
			doAnimation = false;
		else
			doAnimation = true;
	}

	protected void inicializarCajaColision(int width, int height) {
		cajaColision = new Rectangle2D.Float(x, y, (int) (width * Juego.SCALE), (int) (height * Juego.SCALE));
	}

	public void dibujarCajaColision(Graphics g, int xLvlOffset) {
		g.setColor(Color.PINK);
		g.drawRect((int) cajaColision.x - xLvlOffset, (int) cajaColision.y, (int) cajaColision.width, (int) cajaColision.height);
	}

	public int getObjType() {
		return objType;
	}

	public Rectangle2D.Float obtenerCajaColision() {
		return cajaColision;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public void setAnimation(boolean doAnimation) {
		this.doAnimation = doAnimation;
	}

	public int getxDrawOffset() {
		return xDrawOffset;
	}

	public int getyDrawOffset() {
		return yDrawOffset;
	}

	public int obtenerIndiceAnimacion() {
		return indiceAnimacion;
	}

	public int getAniTick() {
		return contadorAnimacion;
	}

}
