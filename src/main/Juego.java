
package main;

import java.awt.Graphics;

import audio.AudioJugador;
import gamestates.*;
import ui.OpcionesDeAudio;

// Clase principal del juego que implementa la interfaz Runnable para manejar el bucle del juego.
public class Juego implements Runnable {

	// Tamaño predeterminado de los tiles (cuadrados gráficos del juego).
	public final static int TILES_DEFAULT_SIZE = 32;
	// Escala para ajustar el tamaño de los tiles.
	public final static float SCALE = 1.5f;
	// Cantidad de tiles en el ancho de la pantalla.
	public final static int TILES_IN_WIDTH = 26;
	// Cantidad de tiles en el alto de la pantalla.
	public final static int TILES_IN_HEIGHT = 14;

	// Tamaño final de los tiles después de aplicar la escala.
	public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
	// Ancho total del juego calculado en base al tamaño de los tiles y su cantidad.
	public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
	// Alto total del juego calculado en base al tamaño de los tiles y su cantidad.
	public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

	// Panel donde se renderiza el juego.
	private PanelDeJuego panelDeJuego;
	// Hilo principal del juego.
	private Thread gameThread;
	// Fotogramas por segundo (FPS) configurados.
	private final int FPS_SET = 120;
	// Actualizaciones por segundo (UPS) configuradas.
	private final int UPS_SET = 200;

	// Estados y componentes del juego.
	private Jugando jugando;
	private Menu menu;
	private Creditos creditos;
	private SelecionDePersonaje selecionDePersonaje;
	private OpcionesDeJuego opcionesDeJuego;
	private OpcionesDeAudio opcionesDeAudio;
	private AudioJugador audioJugador;

	// Bandera para mostrar los FPS y UPS en la consola.
	private final boolean SHOW_FPS_UPS = true;

	// Constructor de la clase Juego.
	public Juego() {
		// Imprime el tamaño del juego en la consola.
		System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);
		// Inicializa las clases necesarias para el juego.
		initClasses();
		// Crea el panel de juego y la ventana.
		panelDeJuego = new PanelDeJuego(this);
		new VentanaDeJuego(panelDeJuego);
		// Solicita el foco en la ventana para el panel de juego.
		panelDeJuego.requestFocusInWindow();
		// Inicia el bucle del juego.
		startGameLoop();
	}

	// Métodos para obtener las opciones de audio, el reproductor de audio y otros
	// estados del juego.
	public OpcionesDeAudio getAudioOptions() {
		return opcionesDeAudio;
	}

	public AudioJugador getAudioPlayer() {
		return audioJugador;
	}

	public Creditos getCredits() {
		return creditos;
	}

	public OpcionesDeJuego getGameOptions() {
		return opcionesDeJuego;
	}

	public Menu getMenu() {
		return menu;
	}

	public SelecionDePersonaje getPlayerSelection() {
		return selecionDePersonaje;
	}

	public Jugando getPlaying() {
		return jugando;
	}

	// Método para inicializar las clases necesarias para los diferentes estados del
	// juego.
	private void initClasses() {
		opcionesDeAudio = new OpcionesDeAudio(this);
		audioJugador = new AudioJugador();
		menu = new Menu(this);
		jugando = new Jugando(this);
		selecionDePersonaje = new SelecionDePersonaje(this);
		creditos = new Creditos(this);
		opcionesDeJuego = new OpcionesDeJuego(this);
	}

	// Método para renderizar los gráficos según el estado actual del juego.
	@SuppressWarnings("incomplete-switch")
	public void render(Graphics g) {
		switch (Gamestate.estado) {
		case MENU -> menu.draw(g); // Renderiza el menú.
		case SELECIONAR_JUGADOR_ -> selecionDePersonaje.draw(g); // Renderiza la selección de personaje.
		case JUGANDO -> jugando.draw(g); // Renderiza el estado de juego.
		case OPCIONES -> opcionesDeJuego.draw(g); // Renderiza las opciones del juego.
		case CREDITOS -> creditos.draw(g); // Renderiza los créditos.
		}
	}

	// Método principal del hilo del juego que ejecuta el bucle de actualización y
	// renderizado.
	@Override
	public void run() {
		// Tiempo por fotograma en nanosegundos.
		double timePerFrame = 1000000000.0 / FPS_SET;
		// Tiempo por actualización en nanosegundos.
		double timePerUpdate = 1000000000.0 / UPS_SET;

		long previousTime = System.nanoTime();

		int frames = 0; // Contador de fotogramas.
		int updates = 0; // Contador de actualizaciones.
		long lastCheck = System.currentTimeMillis();

		double deltaU = 0; // Delta para actualizaciones.
		double deltaF = 0; // Delta para fotogramas.

		while (true) {
			long currentTime = System.nanoTime();

			// Calcula el tiempo transcurrido para actualizaciones y fotogramas.
			deltaU += (currentTime - previousTime) / timePerUpdate;
			deltaF += (currentTime - previousTime) / timePerFrame;
			previousTime = currentTime;

			// Si se alcanza el tiempo para una actualización, se ejecuta.
			if (deltaU >= 1) {
				update();
				updates++;
				deltaU--;
			}

			// Si se alcanza el tiempo para un fotograma, se renderiza.
			if (deltaF >= 1) {
				panelDeJuego.repaint();
				frames++;
				deltaF--;
			}

			// Muestra los FPS y UPS en la consola cada segundo si está habilitado.
			if (SHOW_FPS_UPS)
				if (System.currentTimeMillis() - lastCheck >= 1000) {
					lastCheck = System.currentTimeMillis();
					System.out.println("FPS: " + frames + " | UPS: " + updates);
					frames = 0;
					updates = 0;
				}
		}
	}

	// Método para iniciar el hilo del juego.
	private void startGameLoop() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	// Método para actualizar el estado del juego según el estado actual.
	public void update() {
		switch (Gamestate.estado) {
		case MENU -> menu.update(); // Actualiza el menú.
		case SELECIONAR_JUGADOR_ -> selecionDePersonaje.update(); // Actualiza la selección de personaje.
		case JUGANDO -> jugando.update(); // Actualiza el estado de juego.
		case OPCIONES -> opcionesDeJuego.update(); // Actualiza las opciones del juego.
		case CREDITOS -> creditos.update(); // Actualiza los créditos.
		case SALIR -> System.exit(0); // Sale del juego.
		}
	}

	// Este método se llama cuando la ventana pierde el foco.
	public void windowFocusLost() {
		// Si el estado actual del juego es "JUGANDO", se reinician las direcciones del
		// jugador.
		if (Gamestate.estado == Gamestate.JUGANDO)
			jugando.getPlayer().resetDirBooleans(); // Resetea las variables booleanas que controlan las direcciones del
													// jugador.
	}

}

