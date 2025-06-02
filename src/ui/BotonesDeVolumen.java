package ui;

import java.awt.Graphics;
import java.awt.image.BufferedImage;

import utilz.LoadSave;
import static utilz.Constantes.UI.BotonVolumen.*;

public class BotonesDeVolumen extends BotonDePausa {

	private BufferedImage[] imgs;
	private BufferedImage slider;
	private int index = 0;
	private boolean mouseOver, mousePressed;
	private int buttonX, minX, maxX;
	private float floatValue = 0f;

	public BotonesDeVolumen(int x, int y, int width, int height) {
		super(x + width / 2, y, VOLUMEN_ANCHO, height);
		bounds.x -= VOLUMEN_ANCHO / 2;
		buttonX = x + width / 2;
		this.x = x;
		this.width = width;
		minX = x + VOLUMEN_ANCHO / 2;
		maxX = x + width - VOLUMEN_ANCHO / 2;
		loadImgs();
	}

	private void loadImgs() {
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.BOTON_VOLUMEN);
		imgs = new BufferedImage[3];
		for (int i = 0; i < imgs.length; i++)
			imgs[i] = temp.getSubimage(i * VOLUMEN_ANCHO_DEFAULT, 0, VOLUMEN_ANCHO_DEFAULT, VOLUMEN_ALTO_HEIGHT);

		slider = temp.getSubimage(3 * VOLUMEN_ANCHO_DEFAULT, 0, SLIDER_ANCHO_DEFAULT, VOLUMEN_ALTO_HEIGHT);

	}

	public void update() {
		index = 0;
		if (mouseOver)
			index = 1;
		if (mousePressed)
			index = 2;

	}

	public void draw(Graphics g) {

		g.drawImage(slider, x, y, width, height, null);
		g.drawImage(imgs[index], buttonX - VOLUMEN_ANCHO / 2, y, VOLUMEN_ANCHO, height, null);

	}

	public void changeX(int x) {
		if (x < minX)
			buttonX = minX;
		else if (x > maxX)
			buttonX = maxX;
		else
			buttonX = x;
		updateFloatValue();
		bounds.x = buttonX - VOLUMEN_ANCHO / 2;

	}

	private void updateFloatValue() {
		float range = maxX - minX;
		float value = buttonX - minX;
		floatValue = value / range;
	}

	public void resetBools() {
		mouseOver = false;
		mousePressed = false;
	}

	public boolean isMouseOver() {
		return mouseOver;
	}

	public void setMouseOver(boolean mouseOver) {
		this.mouseOver = mouseOver;
	}

	public boolean isMousePressed() {
		return mousePressed;
	}

	public void setMousePressed(boolean mousePressed) {
		this.mousePressed = mousePressed;
	}

	public float getFloatValue() {
		return floatValue;
	}
}
