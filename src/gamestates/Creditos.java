package gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import main.Juego;
import utilz.LoadSave;

/**
 * Clase que representa el estado de créditos del juego.
 * Muestra una imagen de créditos que se desliza verticalmente y
 * personajes animados que se mueven en la pantalla.
 */
public class Creditos extends Estados implements MetodosDeEstado {

    private BufferedImage imgDeFondo, imgCreditos; // Imágenes de fondo y créditos.
    private int bgX, bgY, bgW, bgH; // Posición y tamaño de la imagen de créditos.
    private float bgYFloat; // Posición vertical animada de los créditos.

    private ArrayList<ShowEntity> listaDeEntidades; // Lista de personajes animados (idle) en la pantalla.

    // Constructor: inicializa imágenes y entidades visuales.
    public Creditos(Juego juego) {
        super(juego);
        imgDeFondo = LoadSave.GetSpriteAtlas(LoadSave.MENU_FONDO_IMG); // Imagen de fondo.
        imgCreditos = LoadSave.GetSpriteAtlas(LoadSave.CREDITOS); // Imagen con el texto de créditos.

        // Tamaño de la imagen de créditos escalado al tamaño del juego.
        bgW = (int) (imgCreditos.getWidth() * Juego.SCALE);
        bgH = (int) (imgCreditos.getHeight() * Juego.SCALE);
        bgX = Juego.GAME_WIDTH / 2 - bgW / 2;
        bgY = Juego.GAME_HEIGHT;

        cargarEntidades(); // Carga los personajes que se mostrarán animados.
    }

    // Carga los personajes animados (idle) a mostrar durante los créditos.
    private void cargarEntidades() {
        listaDeEntidades = new ArrayList<>();
        listaDeEntidades.add(new ShowEntity(getIdleAni(LoadSave.GetSpriteAtlas(LoadSave.PLAYER_PIRATA), 5, 64, 40), (int) (Juego.GAME_WIDTH * 0.05), (int) (Juego.GAME_HEIGHT * 0.8)));
        listaDeEntidades.add(new ShowEntity(getIdleAni(LoadSave.GetSpriteAtlas(LoadSave.CANGREJO_SPRITE), 9, 72, 32), (int) (Juego.GAME_WIDTH * 0.15), (int) (Juego.GAME_HEIGHT * 0.75)));
        listaDeEntidades.add(new ShowEntity(getIdleAni(LoadSave.GetSpriteAtlas(LoadSave.ESTRELLA_ATLAS), 8, 34, 30), (int) (Juego.GAME_WIDTH * 0.7), (int) (Juego.GAME_HEIGHT * 0.75)));
        listaDeEntidades.add(new ShowEntity(getIdleAni(LoadSave.GetSpriteAtlas(LoadSave.TIBURON_ATLAS), 8, 34, 30), (int) (Juego.GAME_WIDTH * 0.8), (int) (Juego.GAME_HEIGHT * 0.8)));
    }

    // Extrae un arreglo de sprites en idle desde el atlas de sprites.
    private BufferedImage[] getIdleAni(BufferedImage atlas, int spritesAmount, int width, int height) {
        BufferedImage[] arr = new BufferedImage[spritesAmount];
        for (int i = 0; i < spritesAmount; i++)
            arr[i] = atlas.getSubimage(width * i, 0, width, height);
        return arr;
    }

    // Actualiza el estado de créditos y las animaciones de los personajes.
    @Override
    public void update() {
        bgYFloat -= 0.2f; // Hace que los créditos se desplacen lentamente hacia arriba.
        for (ShowEntity se : listaDeEntidades)
            se.update(); // Actualiza animación de cada entidad.
    }

    // Dibuja el fondo, los créditos y los personajes en pantalla.
    @Override
    public void draw(Graphics g) {
        g.drawImage(imgDeFondo, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null); // Dibuja fondo completo.
        g.drawImage(imgCreditos, bgX, (int) (bgY + bgYFloat), bgW, bgH, null); // Dibuja créditos con desplazamiento vertical.

        for (ShowEntity se : listaDeEntidades)
            se.draw(g); // Dibuja cada entidad animada.
    }

    // Permite salir del estado de créditos presionando ESCAPE.
    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            bgYFloat = 0; // Reinicia posición vertical.
            setGamestate(Gamestate.MENU); // Cambia el estado al menú principal.
        }
    }

    // Métodos de entrada de mouse (no usados en este estado).
    @Override
    public void mouseMoved(MouseEvent e) { }

    @Override
    public void mousePressed(MouseEvent e) { }

    @Override
    public void mouseReleased(MouseEvent e) { }

    @Override
    public void mouseClicked(MouseEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) { }

    /**
     * Clase interna que representa a un personaje animado en los créditos.
     * Se usa para mostrar personajes como el pirata, el cangrejo, etc. en animación idle.
     */
    private class ShowEntity {
        private BufferedImage[] idleAnimation; // Arreglo de frames de animación idle.
        private int x, y; // Posición en pantalla.
        private int indiceAnimacion, contadorAnimacion; // Control de la animación.

        // Constructor: recibe la animación y la posición.
        public ShowEntity(BufferedImage[] idleAnimation, int x, int y) {
            this.idleAnimation = idleAnimation;
            this.x = x;
            this.y = y;
        }

        // Dibuja la entidad en pantalla con escalado.
        public void draw(Graphics g) {
            g.drawImage(
                idleAnimation[indiceAnimacion],
                x,
                y,
                (int) (idleAnimation[indiceAnimacion].getWidth() * 4),
                (int) (idleAnimation[indiceAnimacion].getHeight() * 4),
                null
            );
        }

        // Actualiza la animación de la entidad.
        public void update() {
            contadorAnimacion++;
            if (contadorAnimacion >= 25) { // Cambia de frame cada 25 actualizaciones.
                contadorAnimacion = 0;
                indiceAnimacion++;
                if (indiceAnimacion >= idleAnimation.length)
                    indiceAnimacion = 0; // Reinicia al primer frame.
            }
        }
    }
}

