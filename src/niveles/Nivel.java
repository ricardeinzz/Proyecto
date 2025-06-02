package niveles;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entidades.Cangrejo;
import entidades.Estrella;
import entidades.Tiburon;
import main.Juego;
import objetos.ArbolesDeFondo;
import objetos.Canon;
import objetos.ContenedorJuego;
import objetos.Pasto;
import objetos.Posiones;
import objetos.Puas;

import static utilz.Constantes.ConstanteEnemigos.*;
import static utilz.Constantes.ObjectosConstantes.*;

public class Nivel {

	private BufferedImage img;
	private int[][] lvlData;

	private ArrayList<Cangrejo> crabs = new ArrayList<>();
	private ArrayList<Estrella> estrellas = new ArrayList<>();
	private ArrayList<Tiburon> tiburons = new ArrayList<>();
	private ArrayList<Posiones> posiones = new ArrayList<>();
	private ArrayList<Puas> puas = new ArrayList<>();
	private ArrayList<ContenedorJuego> containers = new ArrayList<>();
	private ArrayList<Canon> canons = new ArrayList<>();
	private ArrayList<ArbolesDeFondo> trees = new ArrayList<>();
	private ArrayList<Pasto> pasto = new ArrayList<>();

	private int lvlTilesWide;
	private int maxTilesOffset;
	private int maxLvlOffsetX;
	private Point playerSpawn;

	public Nivel(BufferedImage img) {
		this.img = img;
		lvlData = new int[img.getHeight()][img.getWidth()];
		loadLevel();
		calcLvlOffsets();
	}

	private void loadLevel() {

		

		for (int y = 0; y < img.getHeight(); y++)
			for (int x = 0; x < img.getWidth(); x++) {
				Color c = new Color(img.getRGB(x, y));
				int red = c.getRed();
				int green = c.getGreen();
				int blue = c.getBlue();

				loadLevelData(red, x, y);
				loadEntities(green, x, y);
				loadObjects(blue, x, y);
			}
	}

	private void loadLevelData(int redValue, int x, int y) {
		if (redValue >= 50)
			lvlData[y][x] = 0;
		else
			lvlData[y][x] = redValue;
		switch (redValue) {
		case 0, 1, 2, 3, 30, 31, 33, 34, 35, 36, 37, 38, 39 -> 
		pasto.add(new Pasto((int) (x * Juego.TILES_SIZE), (int) (y * Juego.TILES_SIZE) - Juego.TILES_SIZE, getRndGrassType(x)));
		}
	}

	private int getRndGrassType(int xPos) {
		return xPos % 2;
	}

	private void loadEntities(int greenValue, int x, int y) {
		switch (greenValue) {
		case CANGREJO -> crabs.add(new Cangrejo(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE));
		case ESTRELLA -> estrellas.add(new Estrella(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE));
		case TIBURON -> tiburons.add(new Tiburon(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE));
		case 100 -> playerSpawn = new Point(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE);
		}
	}

	private void loadObjects(int blueValue, int x, int y) {
		switch (blueValue) {
		case POSION_ROJA, POSION_AZUL -> posiones.add(new Posiones(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE, blueValue));
		case CAJA, BARRIL -> containers.add(new ContenedorJuego(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE, blueValue));
		case PUAS -> puas.add(new Puas(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE, PUAS));
		case CANON_IZQUIERDA, CANON_DERECHA -> canons.add(new Canon(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE, blueValue));
		case ARBOL_UNO, ARBOL_DOS, ARBOL_TRES -> trees.add(new ArbolesDeFondo(x * Juego.TILES_SIZE, y * Juego.TILES_SIZE, blueValue));
		}
	}

	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		maxTilesOffset = lvlTilesWide - Juego.TILES_IN_WIDTH;
		maxLvlOffsetX = Juego.TILES_SIZE * maxTilesOffset;
	}

	public int getSpriteIndex(int x, int y) {
		return lvlData[y][x];
	}

	public int[][] getLevelData() {
		return lvlData;
	}

	public int getLvlOffset() {
		return maxLvlOffsetX;
	}

	public Point getPlayerSpawn() {
		return playerSpawn;
	}

	public ArrayList<Cangrejo> getCrabs() {
		return crabs;
	}

	public ArrayList<Tiburon> getSharks() {
		return tiburons;
	}

	public ArrayList<Posiones> getPotions() {
		return posiones;
	}

	public ArrayList<ContenedorJuego> getContainers() {
		return containers;
	}

	public ArrayList<Puas> getSpikes() {
		return puas;
	}

	public ArrayList<Canon> getCannons() {
		return canons;
	}

	public ArrayList<Estrella> getPinkstars() {
		return estrellas;
	}

	public ArrayList<ArbolesDeFondo> getTrees() {
		return trees;
	}

	public ArrayList<Pasto> getGrass() {
		return pasto;
	}

}
