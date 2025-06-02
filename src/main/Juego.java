package main;

import java.awt.Graphics;

import audio.AudioJugador;
import gamestates.*;
import ui.OpcionesDeAudio;

public class Juego implements Runnable {

    private PanelDeJuego panelDeJuego;
    private Thread gameThread;
    private final int FPS_SET = 120;
    private final int UPS_SET = 200;

    private Jugando jugando;
    private Menu menu;
    private Creditos creditos;
    private SelecionDePersonaje selecionDePersonaje;
    private OpcionesDeJuego opcionesDeJuego;
    private OpcionesDeAudio opcionesDeAudio;
    private AudioJugador audioJugador;

    public final static int TILES_DEFAULT_SIZE = 32;
    public final static float SCALE = 1.5f;
    public final static int TILES_IN_WIDTH = 26;
    public final static int TILES_IN_HEIGHT = 14;
    public final static int TILES_SIZE = (int) (TILES_DEFAULT_SIZE * SCALE);
    public final static int GAME_WIDTH = TILES_SIZE * TILES_IN_WIDTH;
    public final static int GAME_HEIGHT = TILES_SIZE * TILES_IN_HEIGHT;

    private final boolean SHOW_FPS_UPS = true;

    public Juego() {
        System.out.println("size: " + GAME_WIDTH + " : " + GAME_HEIGHT);
        initClasses();
        panelDeJuego = new PanelDeJuego(this);
        new VentanaDeJuego(panelDeJuego);
        panelDeJuego.requestFocusInWindow();
        startGameLoop();
    }

    private void initClasses() {
        opcionesDeAudio = new OpcionesDeAudio(this);
        audioJugador = new AudioJugador();
        menu = new Menu(this);
        jugando = new Jugando(this);
        selecionDePersonaje = new SelecionDePersonaje(this);
        creditos = new Creditos(this);
        opcionesDeJuego = new OpcionesDeJuego(this);
    }

    private void startGameLoop() {
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void update() {
        switch (Gamestate.estado) {
            case MENU -> menu.update();
            case SELECIONAR_JUGADOR_ -> selecionDePersonaje.update();
            case JUGANDO -> jugando.update();
            case OPCIONES -> opcionesDeJuego.update();
            case CREDITOS -> creditos.update();
            case SALIR -> System.exit(0);
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void render(Graphics g) {
        switch (Gamestate.estado) {
            case MENU -> menu.draw(g);
            case SELECIONAR_JUGADOR_ -> selecionDePersonaje.draw(g);
            case JUGANDO -> jugando.draw(g);
            case OPCIONES -> opcionesDeJuego.draw(g);
            case CREDITOS -> creditos.draw(g);
        }
    }

    @Override
    public void run() {
        double timePerFrame = 1000000000.0 / FPS_SET;
        double timePerUpdate = 1000000000.0 / UPS_SET;

        long previousTime = System.nanoTime();

        int frames = 0;
        int updates = 0;
        long lastCheck = System.currentTimeMillis();

        double deltaU = 0;
        double deltaF = 0;

        while (true) {

            long currentTime = System.nanoTime();

            deltaU += (currentTime - previousTime) / timePerUpdate;
            deltaF += (currentTime - previousTime) / timePerFrame;
            previousTime = currentTime;

            if (deltaU >= 1) {

                update();
                updates++;
                deltaU--;

            }

            if (deltaF >= 1) {

                panelDeJuego.repaint();
                frames++;
                deltaF--;

            }

            if (SHOW_FPS_UPS)
                if (System.currentTimeMillis() - lastCheck >= 1000) {

                    lastCheck = System.currentTimeMillis();
                    System.out.println("FPS: " + frames + " | UPS: " + updates);
                    frames = 0;
                    updates = 0;

                }

        }
    }

    public void windowFocusLost() {
        if (Gamestate.estado == Gamestate.JUGANDO)
            jugando.getPlayer().resetDirBooleans();
    }

    public Menu getMenu() {
        return menu;
    }

    public Jugando getPlaying() {
        return jugando;
    }

    public Creditos getCredits() {
        return creditos;
    }

    public SelecionDePersonaje getPlayerSelection() {
        return selecionDePersonaje;
    }

    public OpcionesDeJuego getGameOptions() {
        return opcionesDeJuego;
    }

    public OpcionesDeAudio getAudioOptions() {
        return opcionesDeAudio;
    }

    public AudioJugador getAudioPlayer() {
        return audioJugador;
    }
}