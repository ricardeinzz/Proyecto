package niveles;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Juego;
import utilz.LoadSave;

public class LevelManager {

	private Juego juego;
	private BufferedImage[] levelSprite;
	private BufferedImage[] waterSprite;
	private ArrayList<Nivel> nivels;
	private int lvlIndex = 0, contadorAnimacion, indiceAnimacion;

	public LevelManager(Juego juego) {
		this.juego = juego;
		importOutsideSprites();
		createWater();
		nivels = new ArrayList<>();
		buildAllLevels();
	}

	private void createWater() {
		waterSprite = new BufferedImage[5];
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.AGUA_ARRIBA);
		for (int i = 0; i < 4; i++)
			waterSprite[i] = img.getSubimage(i * 32, 0, 32, 32);
		waterSprite[4] = LoadSave.GetSpriteAtlas(LoadSave.AGUA_ABAJO);
	}

	public void loadNextLevel() {
		Nivel newLevel = nivels.get(lvlIndex);
		juego.getPlaying().getEnemyManager().cargarEnemigos(newLevel);
		juego.getPlaying().getPlayer().loadLvlData(newLevel.getLevelData());
		juego.getPlaying().setMaxLvlOffset(newLevel.getLvlOffset());
		juego.getPlaying().getObjectManager().loadObjects(newLevel);
	}

	private void buildAllLevels() {
		BufferedImage[] allLevels = LoadSave.GetAllLevels();
		for (BufferedImage img : allLevels)
			nivels.add(new Nivel(img));
	}

	private void importOutsideSprites() {
		BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.LEVEL_ATLAS);
		levelSprite = new BufferedImage[48];
		for (int j = 0; j < 4; j++)
			for (int i = 0; i < 12; i++) {
				int index = j * 12 + i;
				levelSprite[index] = img.getSubimage(i * 32, j * 32, 32, 32);
			}
	}

	public void draw(Graphics g, int lvlOffset) {
		for (int j = 0; j < Juego.TILES_IN_HEIGHT; j++)
			for (int i = 0; i < nivels.get(lvlIndex).getLevelData()[0].length; i++) {
				int index = nivels.get(lvlIndex).getSpriteIndex(i, j);
				int x = Juego.TILES_SIZE * i - lvlOffset;
				int y = Juego.TILES_SIZE * j;
				if (index == 48)
					g.drawImage(waterSprite[indiceAnimacion], x, y, Juego.TILES_SIZE, Juego.TILES_SIZE, null);
				else if (index == 49)
					g.drawImage(waterSprite[4], x, y, Juego.TILES_SIZE, Juego.TILES_SIZE, null);
				else
					g.drawImage(levelSprite[index], x, y, Juego.TILES_SIZE, Juego.TILES_SIZE, null);
			}
	}

	public void update() {
		updateWaterAnimation();
	}

	private void updateWaterAnimation() {
		contadorAnimacion++;
		if (contadorAnimacion >= 40) {
			contadorAnimacion = 0;
			indiceAnimacion++;

			if (indiceAnimacion >= 4)
				indiceAnimacion = 0;
		}
	}

	public Nivel getCurrentLevel() {
		return nivels.get(lvlIndex);
	}

	public int getAmountOfLevels() {
		return nivels.size();
	}

	public int getLevelIndex() {
		return lvlIndex;
	}

	public void setLevelIndex(int lvlIndex) {
		this.lvlIndex = lvlIndex;
	}
}
