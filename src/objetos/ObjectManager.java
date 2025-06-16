package objetos;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entidades.Enemigos;
import entidades.Jugador;
import gamestates.Jugando;
import main.Juego;
import niveles.Nivel;
import utilz.LoadSave;
import static utilz.Constantes.ObjectosConstantes.*;
import static utilz.MetodosDeAyuda.EsJugadorVisibleParaCanon;
import static utilz.MetodosDeAyuda.HaColisionadoProyectil;
import static utilz.Constantes.Proyectiles.*;

public class ObjectManager {

	private Jugando jugando;
	private BufferedImage[][] potionImgs, containerImgs;
	private BufferedImage[] cannonImgs, grassImgs;
	private BufferedImage[][] treeImgs;
	private BufferedImage spikeImg, cannonBallImg;
	private ArrayList<Posiones> posiones;
	private ArrayList<ContenedorJuego> containers;
	private ArrayList<Proyectiles> proyectiles = new ArrayList<>();

	private Nivel currentLevel;

	public ObjectManager(Jugando jugando) {
		this.jugando = jugando;
		currentLevel = jugando.getLevelManager().getCurrentLevel();
		loadImgs();
	}

	public void applyEffectToPlayer(Posiones p) {
	    Jugador j1 = jugando.getPlayer();
	    Jugador j2 = jugando.getPlayer2();

	    if (p.getObjType() == POSION_ROJA) {
	        if (j1.obtenerCajaColision().intersects(p.obtenerCajaColision())) {
	            j1.cambiarVida(POSION_ROJA_VALOR);
	            System.out.println("CURADO JUGADOR 1 - Vida: " + j1.getVidaActual());
	            p.setActive(false);
	        } else if (j2.obtenerCajaColision().intersects(p.obtenerCajaColision())) {
	            j2.cambiarVida(POSION_ROJA_VALOR);
	            System.out.println("CURADO JUGADOR 2 - Vida: " + j2.getVidaActual());
	            p.setActive(false);
	        }
	        }
	    }

	public void checkObjectHit(Rectangle2D.Float attackbox) {
		for (ContenedorJuego gc : containers)
			if (gc.isActive() && !gc.doAnimation) {
				if (gc.obtenerCajaColision().intersects(attackbox)) {
					gc.setAnimation(true);
					int type = 0;
					if (gc.getObjType() == BARRIL)
						type = 1;
					posiones.add(new Posiones((int) (gc.obtenerCajaColision().x + gc.obtenerCajaColision().width / 2), (int) (gc.obtenerCajaColision().y - gc.obtenerCajaColision().height / 2), type));
					return;
				}
			}
	}

	public void checkObjectTouched(Rectangle2D.Float cajaColision) {
		for (Posiones p : posiones)
			if (p.isActive()) {
				if (cajaColision.intersects(p.obtenerCajaColision())) {
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
	}

	public void checkSpikesTouched(Enemigos e) {
		for (Puas s : currentLevel.getSpikes())
			if (s.obtenerCajaColision().intersects(e.obtenerCajaColision()))
				e.hurt(200);
	}


	public void checkSpikesTouched(Jugador p) {
		for (Puas s : currentLevel.getSpikes())
			if (s.obtenerCajaColision().intersects(p.obtenerCajaColision()))
				p.kill();
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawPotions(g, xLvlOffset);
		drawContainers(g, xLvlOffset);
		drawTraps(g, xLvlOffset);
		drawCannons(g, xLvlOffset);
		drawProjectiles(g, xLvlOffset);
		drawGrass(g, xLvlOffset);
	}

	public void drawBackgroundTrees(Graphics g, int xLvlOffset) {
		for (ArbolesDeFondo bt : currentLevel.getTrees()) {

			int type = bt.getType();
			if (type == 9)
				type = 8;
			g.drawImage(treeImgs[type - 7][bt.obtenerIndiceAnimacion()], bt.getX() - xLvlOffset + GetTreeOffsetX(bt.getType()), (int) (bt.getY() + GetTreeOffsetY(bt.getType())), GetTreeWidth(bt.getType()),
					GetTreeHeight(bt.getType()), null);
		}
	}

	private void drawCannons(Graphics g, int xLvlOffset) {
		for (Canon c : currentLevel.getCannons()) {
			int x = (int) (c.obtenerCajaColision().x - xLvlOffset);
			int width = CANON_ANCHO;

			if (c.getObjType() == CANON_DERECHA) {
				x += width;
				width *= -1;
			}
			g.drawImage(cannonImgs[c.obtenerIndiceAnimacion()], x, (int) (c.obtenerCajaColision().y), width, CANON_ALTO, null);
		}
	}

	private void drawContainers(Graphics g, int xLvlOffset) {
		for (ContenedorJuego gc : containers)
			if (gc.isActive()) {
				int type = 0;
				if (gc.getObjType() == BARRIL)
					type = 1;
				g.drawImage(containerImgs[type][gc.obtenerIndiceAnimacion()], (int) (gc.obtenerCajaColision().x - gc.getxDrawOffset() - xLvlOffset), (int) (gc.obtenerCajaColision().y - gc.getyDrawOffset()), CONTENEDOR_ANCHO,
						CONTENEDOR_ALTO, null);
			}
	}

	private void drawGrass(Graphics g, int xLvlOffset) {
		for (Pasto pasto : currentLevel.getGrass())
			g.drawImage(grassImgs[pasto.getType()], pasto.getX() - xLvlOffset, pasto.getY(), (int) (32 * Juego.SCALE), (int) (32 * Juego.SCALE), null);
	}

	private void drawPotions(Graphics g, int xLvlOffset) {
		for (Posiones p : posiones)
			if (p.isActive()) {
				int type = 0;
				if (p.getObjType() == POSION_ROJA)
					type = 1;
				g.drawImage(potionImgs[type][p.obtenerIndiceAnimacion()], (int) (p.obtenerCajaColision().x - p.getxDrawOffset() - xLvlOffset), (int) (p.obtenerCajaColision().y - p.getyDrawOffset()), POSION_ANCHO, POSION_ALTO,
						null);
			}
	}

	private void drawProjectiles(Graphics g, int xLvlOffset) {
		for (Proyectiles p : proyectiles)
			if (p.isActive())
				g.drawImage(cannonBallImg, (int) (p.obtenerCajaColision().x - xLvlOffset), (int) (p.obtenerCajaColision().y), BALA_CANON_ANCHO, BALA_CANON_ALTO, null);
	}

	private void drawTraps(Graphics g, int xLvlOffset) {
		for (Puas s : currentLevel.getSpikes())
			g.drawImage(spikeImg, (int) (s.obtenerCajaColision().x - xLvlOffset), (int) (s.obtenerCajaColision().y - s.getyDrawOffset()), PUAS_ANCHO, PUAS_ALTO, null);

	}

	private boolean isPlayerInfrontOfCannon(Canon c, Jugador jugador) {
		if (c.getObjType() == CANON_IZQUIERDA) {
			if (c.obtenerCajaColision().x > jugador.obtenerCajaColision().x)
				return true;

		} else if (c.obtenerCajaColision().x < jugador.obtenerCajaColision().x)
			return true;
		return false;
	}

	private boolean isPlayerInRange(Canon c, Jugador jugador) {
		int absValue = (int) Math.abs(jugador.obtenerCajaColision().x - c.obtenerCajaColision().x);
		return absValue <= Juego.TILES_SIZE * 5;
	}

	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POSION_ATLAS);
		potionImgs = new BufferedImage[2][7];

		for (int j = 0; j < potionImgs.length; j++)
			for (int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);

		BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTENEDOR_ATLAS);
		containerImgs = new BufferedImage[2][8];

		for (int j = 0; j < containerImgs.length; j++)
			for (int i = 0; i < containerImgs[j].length; i++)
				containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);

		spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAPAS_ATLAS);

		cannonImgs = new BufferedImage[7];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.CANON_ATLAS);

		for (int i = 0; i < cannonImgs.length; i++)
			cannonImgs[i] = temp.getSubimage(i * 40, 0, 40, 26);

		cannonBallImg = LoadSave.GetSpriteAtlas(LoadSave.BALA_CANON);
		treeImgs = new BufferedImage[2][4];
		BufferedImage treeOneImg = LoadSave.GetSpriteAtlas(LoadSave.ARBOL_UNO_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[0][i] = treeOneImg.getSubimage(i * 39, 0, 39, 92);

		BufferedImage treeTwoImg = LoadSave.GetSpriteAtlas(LoadSave.ARBOL_DOS_ATLAS);
		for (int i = 0; i < 4; i++)
			treeImgs[1][i] = treeTwoImg.getSubimage(i * 62, 0, 62, 54);

		BufferedImage grassTemp = LoadSave.GetSpriteAtlas(LoadSave.PASTO_ATLAS);
		grassImgs = new BufferedImage[2];
		for (int i = 0; i < grassImgs.length; i++)
			grassImgs[i] = grassTemp.getSubimage(32 * i, 0, 32, 32);
	}

	public void loadObjects(Nivel newLevel) {
		currentLevel = newLevel;
		posiones = new ArrayList<>(newLevel.getPotions());
		containers = new ArrayList<>(newLevel.getContainers());
		proyectiles.clear();
	}

	public void resetAllObjects() {
		loadObjects(jugando.getLevelManager().getCurrentLevel());
		for (Posiones p : posiones)
			p.reset();
		for (ContenedorJuego gc : containers)
			gc.reset();
		for (Canon c : currentLevel.getCannons())
			c.reset();
	}

	private void shootCannon(Canon c) {
		int dir = 1;
		if (c.getObjType() == CANON_IZQUIERDA)
			dir = -1;

		proyectiles.add(new Proyectiles((int) c.obtenerCajaColision().x, (int) c.obtenerCajaColision().y, dir));
	}

	public void update(int[][] lvlData, Jugador jugador1, Jugador jugador2) {
		updateBackgroundTrees();
		for (Posiones p : posiones) {
		    if (p.isActive()) {
		        p.update();
		        applyEffectToPlayer(p); 
		    }
		}
		for (ContenedorJuego gc : containers)
			if (gc.isActive())
				gc.update();

		updateCannons(lvlData, jugador1, jugador2);
		updateProjectiles(lvlData, jugador1, jugador2);
		
		
	}

	private void updateBackgroundTrees() {
		for (ArbolesDeFondo bt : currentLevel.getTrees())
			bt.update();
	}

	private void updateCannons(int[][] lvlData, Jugador jugador1, Jugador jugador2) {
	    for (Canon c : currentLevel.getCannons()) {
	        if (!c.doAnimation) {
	            // Solo necesita que uno de los dos active el cañón
	            if ((c.getTileY() == jugador1.getCeldaY() && isPlayerInRange(c, jugador1)
	                    && isPlayerInfrontOfCannon(c, jugador1)
	                    && EsJugadorVisibleParaCanon(lvlData, jugador1.obtenerCajaColision(), c.obtenerCajaColision(), c.getTileY()))
	                || 
	                (c.getTileY() == jugador2.getCeldaY() && isPlayerInRange(c, jugador2)
	                    && isPlayerInfrontOfCannon(c, jugador2)
	                    && EsJugadorVisibleParaCanon(lvlData, jugador2.obtenerCajaColision(), c.obtenerCajaColision(), c.getTileY()))) {
	                c.setAnimation(true);
	            }
	        }

	        c.update();

	        if (c.obtenerIndiceAnimacion() == 4 && c.getAniTick() == 0) {
	            shootCannon(c);
	        }
	    }
	}


	private void updateProjectiles(int[][] lvlData, Jugador jugador1, Jugador jugador2) {
	    for (Proyectiles p : proyectiles) {
	        if (!p.isActive())
	            continue;

	        p.updatePos();

	        // Comprobamos colisión con jugador1
	        if (p.obtenerCajaColision().intersects(jugador1.obtenerCajaColision())) {
	            jugador1.cambiarVida(-25);
	            p.setActive(false);
	            continue; // Ya impactó, no sigue
	        }

	        // Comprobamos colisión con jugador2
	        if (p.obtenerCajaColision().intersects(jugador2.obtenerCajaColision())) {
	            jugador2.cambiarVida(-25);
	            p.setActive(false);
	            continue;
	        }

	        // Verificamos colisión con el entorno
	        if (HaColisionadoProyectil(p, lvlData)) {
	            p.setActive(false);
	        }
	    }
	}

}
