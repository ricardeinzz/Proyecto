package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import efectos.EfectosDeDialogo;
import efectos.Lluvia;
import entidades.EnemyManager;
import entidades.Jugador;
import entidades.PersonajeJugable;

import java.util.ArrayList;

import main.Juego;
import niveles.LevelManager;
import objetos.ObjectManager;
import ui.PantallaDeJuegoCompleto;
import ui.PantallaDeGameOver;
import ui.PantallaDeNivelCompleto;
import ui.PantallaDePausa;
import utilz.LoadSave;

import static utilz.Constantes.Ambiente.*;
import static utilz.Constantes.Dialogo.*;
import java.util.HashMap;

public class Jugando extends Estados implements MetodosDeEstado {

// Mapa que lleva el conteo de enemigos eliminados por tipo.
	private HashMap<String, Integer> enemigosPorTipo = new HashMap<>();
// Contador total de enemigos eliminados en el nivel.
	private int enemigosEliminados = 0;
// Contador de vidas que el jugador ha perdido.
	private int vidasPerdidas = 0;

// Instancia del jugador principal controlado por el usuario.
	private Jugador jugador;
	private Jugador jugador2;
// Gestor de niveles: carga y administra los datos del nivel actual.
	private LevelManager levelManager;
// Gestor de enemigos: controla la lógica de todos los enemigos.
	private EnemyManager enemyManager;
// Gestor de objetos: maneja objetos interactivos del entorno como pociones y trampas.
	private ObjectManager objectManager;
// Pantalla que se muestra cuando el juego está en pausa.
	private PantallaDePausa pantallaDePausa;
// Pantalla que aparece cuando el jugador pierde.
	private PantallaDeGameOver pantallaDeGameOver;
// Pantalla que aparece al completar todos los niveles del juego.
	private PantallaDeJuegoCompleto pantallaDeJuegoCompleto;
// Pantalla que aparece al completar un nivel exitosamente.
	private PantallaDeNivelCompleto pantallaDeNivelCompleto;
// Efecto visual de lluvia que puede activarse aleatoriamente.
	private Lluvia lluvia;

// Bandera para indicar si el juego está pausado.
	private boolean paused = false;

// Desplazamiento horizontal del nivel para simular cámara.
	private int xLvlOffset;
	private int leftBorder = (int) (0.25 * Juego.GAME_WIDTH);
	private int rightBorder = (int) (0.75 * Juego.GAME_WIDTH);
	private int maxLvlOffsetX;

// Imagen de fondo principal del nivel.
	private BufferedImage backgroundImg, bigCloud, smallCloud, shipImgs[];
	private BufferedImage[] questionImgs, exclamationImgs;
	private ArrayList<EfectosDeDialogo> dialogEffects = new ArrayList<>();

	private int[] smallCloudsPos;
	private Random rnd = new Random();

// Indica si el juego ha terminado.
	private boolean gameOver;
	private boolean lvlCompleted;
	private boolean gameCompleted;
	private boolean playerDying;
	private boolean drawRain;
	private boolean drawShip = true;
	private int shipAni, shipTick, shipDir = 1;
	private float shipHeightDelta, shipHeightChange = 0.05f * Juego.SCALE;

	public Jugando(Juego juego) {
		super(juego);
		initClasses();

		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
		bigCloud = LoadSave.GetSpriteAtlas(LoadSave.NUBES_GRANDES);
		smallCloud = LoadSave.GetSpriteAtlas(LoadSave.NUBES_PEQUE);
		smallCloudsPos = new int[8];
		for (int i = 0; i < smallCloudsPos.length; i++)
			smallCloudsPos[i] = (int) (90 * Juego.SCALE) + rnd.nextInt((int) (100 * Juego.SCALE));

		shipImgs = new BufferedImage[4];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.BARCO);
		for (int i = 0; i < shipImgs.length; i++)
			shipImgs[i] = temp.getSubimage(i * 78, 0, 78, 72);

		loadDialogue();
		calcLvlOffset();
		loadStartLevel();
		setDrawRainBoolean();
	}

	private void loadDialogue() {
		loadDialogueImgs();

		for (int i = 0; i < 10; i++)
			dialogEffects.add(new EfectosDeDialogo(0, 0, EXCLAMACION));
		for (int i = 0; i < 10; i++)
			dialogEffects.add(new EfectosDeDialogo(0, 0, PREGUNTA));

		for (EfectosDeDialogo de : dialogEffects)
			de.deactive();
	}

	private void loadDialogueImgs() {
		BufferedImage qtemp = LoadSave.GetSpriteAtlas(LoadSave.PREGUNTA_ATLAS);
		questionImgs = new BufferedImage[5];
		for (int i = 0; i < questionImgs.length; i++)
			questionImgs[i] = qtemp.getSubimage(i * 14, 0, 14, 12);

		BufferedImage etemp = LoadSave.GetSpriteAtlas(LoadSave.EXCLAMACION_ATLAS);
		exclamationImgs = new BufferedImage[5];
		for (int i = 0; i < exclamationImgs.length; i++)
			exclamationImgs[i] = etemp.getSubimage(i * 14, 0, 14, 12);
	}

	public void loadNextLevel() {
		levelManager.setLevelIndex(levelManager.getLevelIndex() + 1);
		levelManager.loadNextLevel();

		Point spawn = levelManager.getCurrentLevel().getPlayerSpawn();
		jugador.setSpawn(spawn);

		Point spawnJugador2 = new Point(spawn.x + 50, spawn.y);
		jugador2.setSpawn(spawnJugador2);

		// Esto fuerza a que reconozca si está tocando el piso
		jugador.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		jugador2.loadLvlData(levelManager.getCurrentLevel().getLevelData());

		resetAll();
		drawShip = false;
//		levelManager.setLevelIndex(levelManager.getLevelIndex() + 1);
//		levelManager.loadNextLevel();
//		jugador.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
//		jugador2.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
//		resetAll();
//		drawShip = false;
	}

	private void loadStartLevel() {
		enemyManager.cargarEnemigos(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
	}

	private void calcLvlOffset() {
		maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
	}

	private void initClasses() {
		levelManager = new LevelManager(juego);
		enemyManager = new EnemyManager(this);
		objectManager = new ObjectManager(this);

		pantallaDePausa = new PantallaDePausa(this);
		pantallaDeGameOver = new PantallaDeGameOver(this);
		pantallaDeNivelCompleto = new PantallaDeNivelCompleto(this);
		pantallaDeJuegoCompleto = new PantallaDeJuegoCompleto(this);

		lluvia = new Lluvia();
	}

	public void registrarEnemigoEliminado(String tipoEnemigo) {
		enemigosEliminados++;
		enemigosPorTipo.put(tipoEnemigo, enemigosPorTipo.getOrDefault(tipoEnemigo, 0) + 1);
	}

	public void setPlayerCharacter(PersonajeJugable pc) {

		jugador = new Jugador(pc, this);
		jugador.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		jugador.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());

		jugador2 = new Jugador(pc, this);
		jugador2.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		jugador2.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
	}

	@Override
// Actualiza la lógica del juego dependiendo del estado actual (pausado, victoria, derrota, etc.).
	public void update() {
		if (paused) {
			pantallaDePausa.update();
		} else if (lvlCompleted) {
			pantallaDeNivelCompleto.update();
		} else if (gameCompleted) {
			pantallaDeJuegoCompleto.update();
		} else if (gameOver) {
			pantallaDeGameOver.update();
		} else {
			updateDialogue();

			if (drawRain)
				lluvia.update(xLvlOffset);

			levelManager.update();

			objectManager.update(levelManager.getCurrentLevel().getLevelData(), jugador);
			objectManager.update(levelManager.getCurrentLevel().getLevelData(), jugador2);

			jugador.actualizar();
			jugador2.actualizar();

			enemyManager.actualizar(levelManager.getCurrentLevel().getLevelData());

			checkCloseToBorder();

			if (drawShip)
				updateShipAni();

			// Verifica si ambos jugadores están muertos
			if (!paused &&jugador.estaMuerto() && jugador2.estaMuerto()) {
				juego.getAudioPlayer().detenerCancion();
				setGameOver(true);
			}
		}
	}

	private void updateShipAni() {
		shipTick++;
		if (shipTick >= 35) {
			shipTick = 0;
			shipAni++;
			if (shipAni >= 4)
				shipAni = 0;
		}

		shipHeightDelta += shipHeightChange * shipDir;
		shipHeightDelta = Math.max(Math.min(10 * Juego.SCALE, shipHeightDelta), 0);

		if (shipHeightDelta == 0)
			shipDir = 1;
		else if (shipHeightDelta == 10 * Juego.SCALE)
			shipDir = -1;

	}

	private void updateDialogue() {
		for (EfectosDeDialogo de : dialogEffects)
			if (de.isActive())
				de.update();
	}

// Desplazamiento horizontal del nivel para simular cámara.
	private void drawDialogue(Graphics g, int xLvlOffset) {
		for (EfectosDeDialogo de : dialogEffects)
			if (de.isActive()) {
				if (de.getType() == PREGUNTA)
					g.drawImage(questionImgs[de.obtenerIndiceAnimacion()], de.getX() - xLvlOffset, de.getY(),
							DIALOGO_ANCHO, DIALOGO_ALTO, null);
				else
					g.drawImage(exclamationImgs[de.obtenerIndiceAnimacion()], de.getX() - xLvlOffset, de.getY(),
							DIALOGO_ANCHO, DIALOGO_ALTO, null);
			}
	}

// Agrega un efecto visual de diálogo (¡! o ?) en la posición del evento.
	public void addDialogue(int x, int y, int type) {

		dialogEffects.add(new EfectosDeDialogo(x, y - (int) (Juego.SCALE * 15), type));
		for (EfectosDeDialogo de : dialogEffects)
			if (!de.isActive())
				if (de.getType() == type) {
					de.reset(x, -(int) (Juego.SCALE * 15));
					return;
				}
	}

// Verifica si el jugador se acerca a los bordes visibles para ajustar la cámara.
	private void checkCloseToBorder() {
		int xCentro;

		// Verifica quién está vivo
		boolean j1Vivo = !jugador.estaMuerto();
		boolean j2Vivo = !jugador2.estaMuerto();

		if (j1Vivo && j2Vivo) {
			// Ambos vivos → cámara sigue el centro entre los dos
			int x1 = (int) jugador.obtenerCajaColision().x;
			int x2 = (int) jugador2.obtenerCajaColision().x;
			xCentro = (x1 + x2) / 2;
		} else if (j1Vivo) {
			// Solo jugador 1 vivo
			xCentro = (int) jugador.obtenerCajaColision().x;
		} else if (j2Vivo) {
			// Solo jugador 2 vivo
			xCentro = (int) jugador2.obtenerCajaColision().x;
		} else {
			// Ambos muertos → no mover cámara
			return;
		}

		int diff = xCentro - xLvlOffset;

		if (diff > rightBorder)
			xLvlOffset += diff - rightBorder;
		else if (diff < leftBorder)
			xLvlOffset += diff - leftBorder;

		// Limitar desplazamiento
		xLvlOffset = Math.max(Math.min(xLvlOffset, maxLvlOffsetX), 0);
	}

	@Override
// Dibuja todos los elementos visuales en pantalla, como jugador, enemigos, fondo y efectos.
	public void draw(Graphics g) {
		g.drawImage(backgroundImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);

		drawClouds(g);
		if (drawRain)
			lluvia.dibujar(g, xLvlOffset);

		if (drawShip)
			g.drawImage(shipImgs[shipAni], (int) (100 * Juego.SCALE) - xLvlOffset,
					(int) ((288 * Juego.SCALE) + shipHeightDelta), (int) (78 * Juego.SCALE), (int) (72 * Juego.SCALE),
					null);

		levelManager.draw(g, xLvlOffset);
		objectManager.draw(g, xLvlOffset);
		enemyManager.dibujar(g, xLvlOffset);
		jugador.render(g, xLvlOffset);
		jugador2.render(g, xLvlOffset);
		objectManager.drawBackgroundTrees(g, xLvlOffset);
		drawDialogue(g, xLvlOffset);

		if (paused) {
			g.setColor(new Color(0, 0, 0, 150));
			g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
			pantallaDePausa.draw(g);
		} else if (gameOver)
			pantallaDeGameOver.draw(g);
		else if (lvlCompleted)
			pantallaDeNivelCompleto.draw(g);
		else if (gameCompleted)
			pantallaDeJuegoCompleto.draw(g);

	}

	private void drawClouds(Graphics g) {
		for (int i = 0; i < 4; i++)
			g.drawImage(bigCloud, i * NUBE_GRANDE_ANCHO - (int) (xLvlOffset * 0.3), (int) (204 * Juego.SCALE),
					NUBE_GRANDE_ANCHO, NUBE_GRANDE_ALTO, null);

		for (int i = 0; i < smallCloudsPos.length; i++)
			g.drawImage(smallCloud, NUBE_PEQUE_ANCHO * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i],
					NUBE_PEQUE_ANCHO, NUBE_PEQUE_ALTO, null);
	}

// Marca el estado del juego como completado (todos los niveles terminados).
	public void setGameCompleted() {
		gameCompleted = true;
	}

// Marca el estado del juego como completado (todos los niveles terminados).
	public void resetGameCompleted() {
		gameCompleted = false;
	}

// Reinicia el estado del juego: enemigos, objetos, clima, jugador.
	public void resetAll() {
		gameOver = false;
		paused = false;
		lvlCompleted = false;
		playerDying = false;
		drawRain = false;

		setDrawRainBoolean();

		jugador.resetAll();
		jugador2.resetAll();
		enemyManager.reiniciarEnemigos();
		objectManager.resetAllObjects();
		dialogEffects.clear();
	}

// Decide aleatoriamente si debe llover en el nivel (20% de probabilidad).
	private void setDrawRainBoolean() {
		// This method makes it lluvia 20% of the time you load a level.
		if (rnd.nextFloat() >= 0.8f)
			drawRain = true;
	}

// Indica si el juego ha terminado.
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}

	public void checkObjectHit(Rectangle2D.Float cajaAtaque) {
		objectManager.checkObjectHit(cajaAtaque);
	}

	public void checkEnemyHit(Rectangle2D.Float cajaAtaque) {
		enemyManager.checkHitEnemigo(cajaAtaque);
	}

	public void checkPotionTouched(Rectangle2D.Float cajaColision) {
		objectManager.checkObjectTouched(cajaColision);
	}

	public void checkSpikesTouched(Jugador p) {
		objectManager.checkSpikesTouched(p);
	}

	@Override
// Detecta clics del mouse y activa ataques del jugador según el botón presionado.
	public void mouseClicked(MouseEvent e) {
		if (!gameOver) {
			if (e.getButton() == MouseEvent.BUTTON1)
				jugador.setAtacando(true);
			else if (e.getButton() == MouseEvent.BUTTON3)
				jugador.powerAttack();
		}
	}

	@Override
// Controla las acciones del jugador al presionar teclas: movimiento, salto o pausa.
	public void keyPressed(KeyEvent e) {
		if (!gameOver && !gameCompleted && !lvlCompleted) {
			switch (e.getKeyCode()) {
				// Jugador 1
				case KeyEvent.VK_A:
					jugador.setIzquierda(true);
					break;
				case KeyEvent.VK_D:
					jugador.setDerecha(true);
					break;
				case KeyEvent.VK_SPACE:
					jugador.setSalto(true);
					break;

				// Jugador 2
				case KeyEvent.VK_LEFT:
					jugador2.setIzquierda(true);
					break;
				case KeyEvent.VK_RIGHT:
					jugador2.setDerecha(true);
					break;
				case KeyEvent.VK_UP:
					jugador2.setSalto(true);
					break;
				case KeyEvent.VK_M:
					jugador2.setAtacando(true);
					break;
				case KeyEvent.VK_N:
					jugador2.powerAttack();
					break;

				// Pausa (una sola vez)
				case KeyEvent.VK_ESCAPE:
					paused = !paused;
					break;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (!gameOver && !gameCompleted && !lvlCompleted) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_A:
				jugador.setIzquierda(false);
				break;
			case KeyEvent.VK_D:
				jugador.setDerecha(false);
				break;
			case KeyEvent.VK_SPACE:
				jugador.setSalto(false);
				break;
			}
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				jugador2.setIzquierda(false);
				break;
			case KeyEvent.VK_RIGHT:

				jugador2.setDerecha(false);
				break;
			case KeyEvent.VK_UP:
				jugador2.setSalto(false);
				break;
			case KeyEvent.VK_M:
				jugador2.setAtacando(false);
				break;

			}
		}
	}

	public void mouseDragged(MouseEvent e) {
		if (!gameOver && !gameCompleted && !lvlCompleted)
			if (paused)
				pantallaDePausa.mouseDragged(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if (gameOver)
			pantallaDeGameOver.mousePressed(e);
		else if (paused)
			pantallaDePausa.mousePressed(e);
		else if (lvlCompleted)
			pantallaDeNivelCompleto.mousePressed(e);
		else if (gameCompleted)
			pantallaDeJuegoCompleto.mousePressed(e);

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (gameOver)
			pantallaDeGameOver.mouseReleased(e);
		else if (paused)
			pantallaDePausa.mouseReleased(e);
		else if (lvlCompleted)
			pantallaDeNivelCompleto.mouseReleased(e);
		else if (gameCompleted)
			pantallaDeJuegoCompleto.mouseReleased(e);
	}

	@Override
// Actualiza elementos de UI dependiendo de la posición del mouse cuando está pausado u otro estado.
	public void mouseMoved(MouseEvent e) {
		if (gameOver)
			pantallaDeGameOver.mouseMoved(e);
		else if (paused)
			pantallaDePausa.mouseMoved(e);
		else if (lvlCompleted)
			pantallaDeNivelCompleto.mouseMoved(e);
		else if (gameCompleted)
			pantallaDeJuegoCompleto.mouseMoved(e);
	}

	public void setLevelCompleted(boolean levelCompleted) {
		juego.getAudioPlayer().lvlCompleto();
		if (levelManager.getLevelIndex() + 1 >= levelManager.getAmountOfLevels()) {
			// No more niveles
			gameCompleted = true;
			levelManager.setLevelIndex(0);
			levelManager.loadNextLevel();
			resetAll();
			return;
		}
		this.lvlCompleted = levelCompleted;
	}

	public void setMaxLvlOffset(int lvlOffset) {
		this.maxLvlOffsetX = lvlOffset;
	}

	public void unpauseGame() {
		paused = false;
	}

	public void windowFocusLost() {
		jugador.resetDirBooleans();
		jugador2.resetDirBooleans();
	}

	public Jugador getPlayer() {
		return jugador;
	}

	public Jugador getPlayer2() {
		return jugador2;
	}

	public EnemyManager getEnemyManager() {
		return enemyManager;
	}

	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public void setPlayerDying(boolean playerDying) {
		this.playerDying = playerDying;
	}

}