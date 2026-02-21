package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.Stack;

import efectos.EfectosDeDialogo;
import efectos.Lluvia;
import entidades.EnemyManager;
import entidades.Jugador;
import entidades.PersonajeJugable;
import utilz.*;
import java.util.ArrayList;

import main.Juego;
import niveles.LevelManager;
import objetos.ObjectManager;
import ui.PantallaDeJuegoCompleto;
import ui.CuadroDePuntaje;
import ui.PantallaDeGameOver;
import ui.PantallaDeNivelCompleto;
import ui.PantallaDePausa;
import ui.PantallaDeResumenNivel;
import utilz.LoadSave;

import static utilz.Constantes.Ambiente.*;
import static utilz.Constantes.Dialogo.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jugando extends Estados implements MetodosDeEstado {

// Mapa que lleva el conteo de enemigos eliminados por tipo.
	private HashMap<String, Integer> killsJugador1 = new HashMap<>();
	private HashMap<String, Integer> killsJugador2 = new HashMap<>();
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
	private boolean mostrandoResumen = false;
	private PantallaDeResumenNivel pantallaResumenNivel;
	private boolean gameOver;
	private boolean lvlCompleted;
	private boolean gameCompleted;
	private boolean playerDying;
	private boolean drawRain;
	private boolean drawShip = true;
	private int shipAni, shipTick, shipDir = 1;
	private int puntajeJugador1 = 0;
	private int puntajeJugador2 = 0;
	private float shipHeightDelta, shipHeightChange = 0.05f * Juego.SCALE;

	private SecuenciaDeGuion intro;
	private Stack<String> logJugador1 = new Stack<>();
	private Stack<String> logJugador2 = new Stack<>();
	private TablaHashKills tablaKills = new TablaHashKills(50);
	private CuadroDePuntaje cuadroDePuntaje;

	private final HashMap<String, Integer> puntajePorEnemigo = new HashMap<>() {
		{
			put("Tiburon", 100);
			put("Cangrejo", 50);
			put("Estrella", 150);
		}
	};

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
		iniciarEscena();
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

	private void calcLvlOffset() {
		maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
	}

	private SecuenciaDeGuion cargarGuionDesdeArchivo(String nombreArchivo, int nivel) {
		SecuenciaDeGuion secuencia = new SecuenciaDeGuion();
		try (BufferedReader br = new BufferedReader(new FileReader(nombreArchivo))) {
			String linea;
			boolean dentroDelNivel = false;
			while ((linea = br.readLine()) != null) {
				linea = linea.trim();
				if (linea.isEmpty())
					continue;
				if (linea.startsWith("#")) {
					dentroDelNivel = linea.contains("Nivel " + nivel);
					continue;
				}
				if (!dentroDelNivel)
					continue;

				if (linea.startsWith("DIALOGO ")) {
					String texto = linea.substring("DIALOGO ".length());
					secuencia.agregarDialogo(texto);
				} else if (linea.startsWith("ESPERA ")) {
					long ms = Long.parseLong(linea.substring("ESPERA ".length()));
					secuencia.agregarEspera(ms);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return secuencia;
	}

	// Verifica si el jugador se acerca a los bordes visibles para ajustar la
	// cámara.
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
			// Ambos muertos no mover cámara
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

	public void checkEnemyHit(Rectangle2D.Float cajaAtaque, int jugadorID) {
		enemyManager.checkHitEnemigo(cajaAtaque, jugadorID);
	}

	public void checkObjectHit(Rectangle2D.Float cajaAtaque) {
		objectManager.checkObjectHit(cajaAtaque);
	}

	public void checkPotionTouched(Rectangle2D.Float cajaColision) {
		objectManager.checkObjectTouched(cajaColision);
	}

	public void checkSpikesTouched(Jugador p) {
		objectManager.checkSpikesTouched(p);
	}

	@Override
	public void draw(Graphics g) {
	    // Dibuja fondo del nivel
	    g.drawImage(backgroundImg, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);

	    // Dibuja nubes
	    drawClouds(g);

	    // Dibuja efecto de lluvia si está activo
	    if (drawRain)
	        lluvia.dibujar(g, xLvlOffset);

	    // Dibuja barco animado si corresponde
	    if (drawShip)
	        g.drawImage(shipImgs[shipAni], (int) (100 * Juego.SCALE) - xLvlOffset,
	                (int) ((288 * Juego.SCALE) + shipHeightDelta), (int) (78 * Juego.SCALE), (int) (72 * Juego.SCALE),
	                null);

	    // Dibuja todos los elementos del nivel
	    levelManager.draw(g, xLvlOffset);
	    objectManager.draw(g, xLvlOffset);
	    enemyManager.dibujar(g, xLvlOffset);
	    jugador.render(g, xLvlOffset);
	    jugador2.render(g, xLvlOffset);
	    objectManager.drawBackgroundTrees(g, xLvlOffset);
	    drawDialogue(g, xLvlOffset);

	    // Dibuja cuadro de diálogo narrativo si está activo
	    if (intro != null && !intro.estaTerminada()) {
	        String texto = intro.getTextoActual();
	        if (!texto.isEmpty()) {
	            int padding = 20;
	            int boxWidth = Juego.GAME_WIDTH - 2 * padding;
	            int boxHeight = 80;
	            int boxY = Juego.GAME_HEIGHT - boxHeight - 30;

	            g.setColor(new Color(0, 0, 0, 200));
	            g.fillRoundRect(padding, boxY, boxWidth, boxHeight, 15, 15);

	            g.setColor(Color.WHITE);
	            g.setFont(g.getFont().deriveFont(20f));
	            g.drawString(texto, padding + 20, boxY + 50);
	        }
	    }

	    // Dibuja cuadro de puntaje si está presente
	    if (cuadroDePuntaje != null)
	        cuadroDePuntaje.draw(g);

	    // Dibuja pantallas de estado especiales
	    if (paused) {
	        g.setColor(new Color(0, 0, 0, 150));
	        g.fillRect(0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT);
	        pantallaDePausa.draw(g);
	    } else if (gameOver) {
	        pantallaDeGameOver.draw(g);
	    } else if (mostrandoResumen && pantallaResumenNivel != null) {
	        pantallaResumenNivel.draw(g); 
	    } else if (lvlCompleted) {
	        pantallaDeNivelCompleto.draw(g);
	    } else if (gameCompleted) {
	        pantallaDeJuegoCompleto.draw(g);
	    }
	}

	private void drawClouds(Graphics g) {
		for (int i = 0; i < 4; i++)
			g.drawImage(bigCloud, i * NUBE_GRANDE_ANCHO - (int) (xLvlOffset * 0.3), (int) (204 * Juego.SCALE),
					NUBE_GRANDE_ANCHO, NUBE_GRANDE_ALTO, null);

		for (int i = 0; i < smallCloudsPos.length; i++)
			g.drawImage(smallCloud, NUBE_PEQUE_ANCHO * 4 * i - (int) (xLvlOffset * 0.7), smallCloudsPos[i],
					NUBE_PEQUE_ANCHO, NUBE_PEQUE_ALTO, null);
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

	public boolean estaMostrandoDialogo() {
		return intro != null && !intro.estaTerminada();
	}

	public EnemyManager getEnemyManager() {
		return enemyManager;
	}

	public Map<String, Integer> getKillsJugador1() {
		return tablaKills.resumenKillsPorJugador(1);
	}

	public Map<String, Integer> getKillsJugador2() {
		return tablaKills.resumenKillsPorJugador(2);
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public Stack<String> getLogJugador1() {
		return logJugador1;
	}

	public Stack<String> getLogJugador2() {
		return logJugador2;
	}

// Dibuja todos los elementos visuales en pantalla, como jugador, enemigos, fondo y efectos.

	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public Jugador getPlayer() {
		return jugador;
	}

	public Jugador getPlayer2() {
		return jugador2;
	}

	public int getPuntajeJugador(int jugadorID) {
		return jugadorID == 1 ? puntajeJugador1 : puntajeJugador2;
	}

	public String[] getResumenKills(int jugadorID) {
		HashMap<String, Integer> mapa = (jugadorID == 1) ? killsJugador1 : killsJugador2;
		String[] resumen = new String[mapa.size()];
		int i = 0;
		for (var entry : mapa.entrySet()) {
			resumen[i++] = entry.getKey() + ": " + entry.getValue();
		}
		return resumen;
	}

	public void iniciarEscena() {
		int nivelActual = levelManager.getLevelIndex() + 1;
		intro = cargarGuionDesdeArchivo("res/dialogos/dialogos.txt", nivelActual);
	}

	private void initClasses() {
		levelManager = new LevelManager(juego);
		enemyManager = new EnemyManager(this);
		objectManager = new ObjectManager(this);
		cuadroDePuntaje = new CuadroDePuntaje(this);
		pantallaDePausa = new PantallaDePausa(this);
		pantallaDeGameOver = new PantallaDeGameOver(this);
		pantallaDeNivelCompleto = new PantallaDeNivelCompleto(this);
		pantallaDeJuegoCompleto = new PantallaDeJuegoCompleto(this);
		pantallaResumenNivel = new PantallaDeResumenNivel(this);

		lluvia = new Lluvia();
	}

// Controla las acciones del jugador al presionar teclas: movimiento, salto o pausa.

	@Override
public void keyPressed(KeyEvent e) {
    //Redirige teclas a PantallaDeResumenNivel si está activa
    if (mostrandoResumen && pantallaResumenNivel != null) {
        pantallaResumenNivel.keyPressed(e);

        //NO hacer return si es ENTER, para que el juego lo cierre normalmente
        if (e.getKeyCode() != KeyEvent.VK_ENTER)
            return;
    }

    //ENTER cierra la pantalla de resumen si el nivel fue completado
    if (lvlCompleted && mostrandoResumen && e.getKeyCode() == KeyEvent.VK_ENTER) {
        mostrandoResumen = false;
        return;
    }

    //Control de diálogos (intro)
    if (intro != null && !intro.estaTerminada()) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            intro.confirmarDialogo();
        }
        return;
    }

    //Controles normales si no está pausado ni terminado
    if (!gameOver && !gameCompleted && !lvlCompleted) {
        switch (e.getKeyCode()) {
            // Jugador 1
            case KeyEvent.VK_A -> jugador.setIzquierda(true);
            case KeyEvent.VK_D -> jugador.setDerecha(true);
            case KeyEvent.VK_SPACE -> jugador.setSalto(true);

            // Jugador 2
            case KeyEvent.VK_LEFT -> jugador2.setIzquierda(true);
            case KeyEvent.VK_RIGHT -> jugador2.setDerecha(true);
            case KeyEvent.VK_UP -> jugador2.setSalto(true);
            case KeyEvent.VK_M -> jugador2.setAtacando(true);
            case KeyEvent.VK_N -> jugador2.powerAttack();

            // Pausa
            case KeyEvent.VK_ESCAPE -> paused = !paused;
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

	// Lanza una moneda para determinar si el jugador gana o pierde.
	public boolean lanzarMonedaYReevaluar(int jugadorID) {
    Stack<String> log = (jugadorID == 1) ? logJugador1 : logJugador2;

    // Primero, asegurar que hay al menos 4 elementos para permitir moneda
    if (log.size() < 4) {
        System.out.println("⛔ No se puede lanzar la moneda. El stack tiene menos de 4 enemigos.");
        return false;
    }

    boolean gano = rnd.nextBoolean(); // true: cara, false: cruz

    if (gano) {
        log.pop(); // Eliminar el tope
        verificarPlatilloFinal(log, jugadorID);
        System.out.println("Cara: se eliminó el tope del stack y se reevaluó la receta.");
    } else {
        System.out.println("Cruz: el stack se mantiene igual.");
    }

    return gano;
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
		iniciarEscena();
		Point spawn = levelManager.getCurrentLevel().getPlayerSpawn();
		jugador.setSpawn(spawn);

		Point spawnJugador2 = new Point(spawn.x + 50, spawn.y);
		jugador2.setSpawn(spawnJugador2);

		// Esto fuerza a que reconozca si está tocando el piso
		jugador.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		jugador2.loadLvlData(levelManager.getCurrentLevel().getLevelData());

		resetAll();
		drawShip = false;
	}

	private void loadStartLevel() {
		enemyManager.cargarEnemigos(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
	}

	public void mostrarResumenKills() {
	    System.out.println("===== RESUMEN DE PARTIDA =====");

	    System.out.println("\nJugador 1:");
	    killsJugador1.forEach((tipo, cantidad) -> System.out.println(" - " + tipo + ": " + cantidad + " eliminados"));
	    System.out.println("Puntaje Total (sin platillo): " + puntajeJugador1);
	    System.out.println("Últimos enemigos eliminados: " + logJugador1);

	    verificarPlatilloFinal(logJugador1, 1);

	    System.out.println("\nJugador 2:");
	    killsJugador2.forEach((tipo, cantidad) -> System.out.println(" - " + tipo + ": " + cantidad + " eliminados"));
	    System.out.println("Puntaje Total (sin platillo): " + puntajeJugador2);
	    System.out.println("Últimos enemigos eliminados: " + logJugador2);

	    verificarPlatilloFinal(logJugador2, 2);

	    System.out.println("=================================");
	    System.out.println("\nTabla hash de kills (todas las partidas):");
	    tablaKills.imprimirTodo();

	    //  Doble seguridad
	    if (pantallaResumenNivel == null) {
	        pantallaResumenNivel = new PantallaDeResumenNivel(this);
	        mostrandoResumen = true;
	    }
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

	public void mouseDragged(MouseEvent e) {
		if (!gameOver && !gameCompleted && !lvlCompleted)
			if (paused)
				pantallaDePausa.mouseDragged(e);
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

	public void registrarKill(int jugadorID, String tipoEnemigo) {
		if (jugadorID != 1 && jugadorID != 2) {
			System.out.println("Kill ignorada: jugadorID inválido = " + jugadorID);
			return;
		}
		int puntosBase = puntajePorEnemigo.getOrDefault(tipoEnemigo, 0);
		int nivelActual = levelManager.getLevelIndex();
		long tiempoActual = System.currentTimeMillis();

		// Obtener puntos reales ganados (con combo si aplica)
		int puntosGanados = tablaKills.registrarKill(tipoEnemigo, nivelActual, tiempoActual, puntosBase, jugadorID);

		// Actualizar estadísticas y stacks
		if (jugadorID == 1) {
			killsJugador1.put(tipoEnemigo, killsJugador1.getOrDefault(tipoEnemigo, 0) + 1);
			logJugador1.push(tipoEnemigo);
			puntajeJugador1 += puntosGanados;
		} else {
			killsJugador2.put(tipoEnemigo, killsJugador2.getOrDefault(tipoEnemigo, 0) + 1);
			logJugador2.push(tipoEnemigo);
			puntajeJugador2 += puntosGanados;
		}

		System.out.println("Jugador " + jugadorID + " mató un " + tipoEnemigo + " → +" + puntosGanados
				+ " puntos (base: " + puntosBase + ")");
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
		puntajeJugador1 = 0;
		puntajeJugador2 = 0;
		tablaKills.limpiarTodo();
	}

	// Marca el estado del juego como completado (todos los niveles terminados).
	public void resetGameCompleted() {
		gameCompleted = false;
	}

	// Decide aleatoriamente si debe llover en el nivel (20% de probabilidad).
	private void setDrawRainBoolean() {
		// This method makes it lluvia 20% of the time you load a level.
		if (rnd.nextFloat() >= 0.8f)
			drawRain = true;
	}

	// Marca el estado del juego como completado (todos los niveles terminados).
	public void setGameCompleted() {
		gameCompleted = true;
	}

	// Indica si el juego ha terminado.
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	public void cerrarResumen() {
	    mostrandoResumen = false;
	    pantallaResumenNivel = null;
	}
	public void setLevelCompleted(boolean levelCompleted) {

		// Al salir del resumen y continuar
		juego.getAudioPlayer().lvlCompleto();

		if (levelManager.getLevelIndex() + 1 >= levelManager.getAmountOfLevels()) {
			gameCompleted = true;
			levelManager.setLevelIndex(0);
			levelManager.loadNextLevel();
			resetAll();
			Gamestate.estado = Gamestate.JUGANDO;
		}
		if (!mostrandoResumen) {
			mostrandoResumen = true;
			mostrarResumenKills(); // Mostrar resumen cuando se activa por primera vez
			this.lvlCompleted = true; // Marcar que se completó para mostrar pantalla luego
			return;
		}

	}

	public void setMaxLvlOffset(int lvlOffset) {
		this.maxLvlOffsetX = lvlOffset;
	}

	public void setPlayerCharacter(PersonajeJugable pc) {

		jugador = new Jugador(pc, this, 1);
		jugador.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		jugador.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());

		jugador2 = new Jugador(pc, this, 2);
		jugador2.loadLvlData(levelManager.getCurrentLevel().getLevelData());
		jugador2.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
	}

	public void setPlayerDying(boolean playerDying) {
		this.playerDying = playerDying;
	}

	public void unpauseGame() {
		paused = false;
	}
@Override
// Actualiza la lógica del juego dependiendo del estado actual (pausado, victoria, derrota, etc.).
public void update() {
    // Corrección: actualizar pantalla de resumen si está activa
    if (mostrandoResumen && pantallaResumenNivel != null) {
        pantallaResumenNivel.update();
        return; 
    }

    // Si estamos en la pantalla de puntaje del nivel, salir
    if (Gamestate.estado == Gamestate.PUNTAJE_NIVEL) {
        return;
    }

    // Si el juego está pausado, actualizar la pantalla de pausa
    if (paused) {
        pantallaDePausa.update();
    }

    // Si se completó el nivel, actualizar la pantalla correspondiente
    else if (lvlCompleted) {
        pantallaDeNivelCompleto.update();
    }

    // Si se completó el juego, actualizar la pantalla final
    else if (gameCompleted) {
        pantallaDeJuegoCompleto.update();
    }

    // Si el jugador perdió, actualizar la pantalla de Game Over
    else if (gameOver) {
        pantallaDeGameOver.update();
    }

    // Caso general: se sigue jugando activamente
    else {
        updateDialogue(); // Actualiza efectos visuales de diálogo (! ?)

        if (intro != null && !intro.estaTerminada()) {
            intro.actualizar();
        }

        if (drawRain)
            lluvia.update(xLvlOffset);

        levelManager.update();
        objectManager.update(levelManager.getCurrentLevel().getLevelData(), jugador, jugador2);

        jugador.actualizar();
        jugador2.actualizar();

        enemyManager.actualizar(levelManager.getCurrentLevel().getLevelData());

        checkCloseToBorder();

        if (drawShip)
            updateShipAni();

        if (!paused && jugador.estaMuerto() && jugador2.estaMuerto()) {
            juego.getAudioPlayer().detenerCancion();
            setGameOver(true);
        }
    }
}

// Actualiza los efectos de diálogo activos, como los signos de interrogación y exclamación.
	private void updateDialogue() {
		for (EfectosDeDialogo de : dialogEffects)
			if (de.isActive())
				de.update();
	}

	// Actualiza la animación del barco, alternando entre imágenes y ajustando su altura.
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

	// Verifica si el jugador ha combinado los últimos 3 enemigos eliminados para
	// formar un platillo.
	private void verificarPlatilloFinal(Stack<String> log, int jugadorID) {
		if (log.size() < 3)
			return;

		List<String> ultimos = List.of(log.get(log.size() - 1), log.get(log.size() - 2), log.get(log.size() - 3));

		Platillo platillo = RecetarioCosteno.buscarPlatillo(ultimos);

		if (platillo != null) {
			if (jugadorID == 1) {
				puntajeJugador1 += platillo.getPuntos();
			} else if (jugadorID == 2) {
				puntajeJugador2 += platillo.getPuntos();
			}

			// registrar en la tabla hash como kill especial
			tablaKills.registrarKill("PLATILLO: " + platillo.getNombre(), levelManager.getLevelIndex(),
					System.currentTimeMillis(), platillo.getPuntos(), jugadorID);

			System.out.println("Jugador " + jugadorID + " preparó: " + platillo.getNombre() + " ["
					+ platillo.getDificultad() + "] +" + platillo.getPuntos() + " puntos");
		} else {
			System.out.println("Jugador " + jugadorID + " no logró combinar un platillo válido.");
		}
	}

	public void windowFocusLost() {
		jugador.resetDirBooleans();
		jugador2.resetDirBooleans();
	}
}