package gamestates;

import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import main.Juego;
import ui.BotonesDeMenu;
import utilz.LoadSave;

public class Menu extends Estados implements MetodosDeEstado {

    private BotonesDeMenu[] botones = new BotonesDeMenu[4];
    private BufferedImage backgroundImg, backgroundImgPink;
    private int menuX, menuY, menuWidth, menuHeight;

    public Menu(Juego juego) {
        super(juego);
        loadButtons();
        loadBackground();
        backgroundImgPink = LoadSave.GetSpriteAtlas(LoadSave.MENU_FONDO_IMG);

    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MENU);
        menuWidth = (int) (backgroundImg.getWidth() * Juego.SCALE);
        menuHeight = (int) (backgroundImg.getHeight() * Juego.SCALE);
        menuX = Juego.GAME_WIDTH / 2 - menuWidth / 2;
        menuY = (int) (25 * Juego.SCALE);
    }

    private void loadButtons() {
        botones[0] = new BotonesDeMenu(Juego.GAME_WIDTH / 2, (int) (130 * Juego.SCALE), 0, Gamestate.SELECIONAR_JUGADOR_);
        botones[1] = new BotonesDeMenu(Juego.GAME_WIDTH / 2, (int) (200 * Juego.SCALE), 1, Gamestate.OPCIONES);
        botones[2] = new BotonesDeMenu(Juego.GAME_WIDTH / 2, (int) (270 * Juego.SCALE), 3, Gamestate.CREDITOS);
        botones[3] = new BotonesDeMenu(Juego.GAME_WIDTH / 2, (int) (340 * Juego.SCALE), 2, Gamestate.SALIR);
    }

    @Override
    public void update() {
        for (BotonesDeMenu mb : botones)
            mb.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImgPink, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);

        for (BotonesDeMenu mb : botones)
            mb.draw(g);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        for (BotonesDeMenu mb : botones) {
            if (isIn(e, mb)) {
                mb.setMousePressed(true);
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        for (BotonesDeMenu mb : botones) {
            if (isIn(e, mb)) {
                if (mb.isMousePressed())
                    mb.applyGamestate();
                if (mb.getState() == Gamestate.JUGANDO)
                    juego.getAudioPlayer().setLevelSong(juego.getPlaying().getLevelManager().getLevelIndex());
                break;
            }
        }
        resetButtons();
    }

    private void resetButtons() {
        for (BotonesDeMenu mb : botones)
            mb.resetBools();

    }

    @Override
    public void mouseMoved(MouseEvent e) {
        for (BotonesDeMenu mb : botones)
            mb.setMouseOver(false);

        for (BotonesDeMenu mb : botones)
            if (isIn(e, mb)) {
                mb.setMouseOver(true);
                break;
            }

    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

}