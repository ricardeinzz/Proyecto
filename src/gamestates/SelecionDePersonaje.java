package gamestates;

import main.Juego;
import ui.BotonesDeMenu;
import utilz.LoadSave;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

import entidades.PersonajeJugable;

import static utilz.Constantes.ANI_VELOCIDAD;
import static utilz.Constantes.PlayerConstants.IDLE;

public class SelecionDePersonaje extends Estados implements MetodosDeEstado {

    private BufferedImage backgroundImg, backgroundImgPink;
    private int menuX, menuY, menuWidth, menuHeight;
    private BotonesDeMenu playButton;
    private int playerIndex = 0;

    private CharacterAnimation[] characterAnimations;


    public SelecionDePersonaje(Juego juego) {
        super(juego);

        loadButtons();
        loadBackground();
        backgroundImgPink = LoadSave.GetSpriteAtlas(LoadSave.MENU_FONDO_IMG);

        loadCharAnimations();
    }

    private void loadCharAnimations() {
        characterAnimations = new CharacterAnimation[3];
        int i = 0;
        characterAnimations[i++] = new CharacterAnimation(PersonajeJugable.PIRATA);
        characterAnimations[i++] = new CharacterAnimation(PersonajeJugable.ORCO);
        characterAnimations[i++] = new CharacterAnimation(PersonajeJugable.SOLDADO);

    }

    private void loadBackground() {
        backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.FONDO_MENU);
        menuWidth = (int) (backgroundImg.getWidth() * Juego.SCALE);
        menuHeight = (int) (backgroundImg.getHeight() * Juego.SCALE);
        menuX = Juego.GAME_WIDTH / 2 - menuWidth / 2;
        menuY = (int) (25 * Juego.SCALE);
    }

    private void loadButtons() {

        playButton = new BotonesDeMenu(Juego.GAME_WIDTH / 2, (int) (340 * Juego.SCALE), 0, Gamestate.JUGANDO);

    }

    @Override
    public void update() {
        playButton.update();
        for (CharacterAnimation ca : characterAnimations)
            ca.update();
    }

    @Override
    public void draw(Graphics g) {
        g.drawImage(backgroundImgPink, 0, 0, Juego.GAME_WIDTH, Juego.GAME_HEIGHT, null);
        g.drawImage(backgroundImg, menuX, menuY, menuWidth, menuHeight, null);

        playButton.draw(g);


        
        drawChar(g, playerIndex, menuX + menuWidth / 2, menuY + menuHeight / 2);

        
        drawChar(g, playerIndex - 1, menuX, menuY + menuHeight / 2);

      
        drawChar(g, playerIndex + 1, menuX + menuWidth, menuY + menuHeight / 2);

    }

    private void drawChar(Graphics g, int playerIndex, int x, int y) {
        if (playerIndex < 0)
            playerIndex = characterAnimations.length - 1;
        else if (playerIndex >= characterAnimations.length)
            playerIndex = 0;
        characterAnimations[playerIndex].draw(g, x, y);
    }

    @Override
    public void mousePressed(MouseEvent e) {

        if (isIn(e, playButton))
            playButton.setMousePressed(true);


    }

    @Override
    public void mouseReleased(MouseEvent e) {

        if (isIn(e, playButton)) {
            if (playButton.isMousePressed()) {

                juego.getPlaying().setPlayerCharacter(characterAnimations[playerIndex].getPc());
                juego.getAudioPlayer().setLevelSong(juego.getPlaying().getLevelManager().getLevelIndex());

                playButton.applyGamestate();
            }

        }

        resetButtons();
    }

    private void resetButtons() {
        playButton.resetBools();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        playButton.setMouseOver(false);


        if (isIn(e, playButton))
            playButton.setMouseOver(true);


    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_LEFT)
            deltaIndex(1);
        else if(e.getKeyCode() == KeyEvent.VK_RIGHT)
            deltaIndex(-1);
    }

    private void deltaIndex(int i) {
        playerIndex += i;
        if (playerIndex < 0)
            playerIndex = characterAnimations.length - 1;
        else if (playerIndex >= characterAnimations.length)
            playerIndex = 0;
    }


    @Override
    public void mouseClicked(MouseEvent e) {
        // TODO Auto-generated method stub

    }

    @Override
    public void keyReleased(KeyEvent e) {
        // TODO Auto-generated method stub

    }

    public class CharacterAnimation {
        private final PersonajeJugable pc;
        private int contadorAnimacion, indiceAnimacion;
        private final BufferedImage[][] animations;
        private int scale;

        public CharacterAnimation(PersonajeJugable pc) {
            this.pc = pc;
            this.scale = (int) (Juego.SCALE + 6);
            animations = LoadSave.cargarAnimacion(pc);
        }

        public void draw(Graphics g, int drawX, int drawY) {
            g.drawImage(animations[pc.getRowIndex(IDLE)][indiceAnimacion],
                    drawX - pc.spriteW * scale / 2,
                    drawY - pc.spriteH * scale / 2,
                    pc.spriteW * scale,
                    pc.spriteH * scale,
                    null);
        }

        public void update() {
            contadorAnimacion++;
            if (contadorAnimacion >= ANI_VELOCIDAD) {
                contadorAnimacion = 0;
                indiceAnimacion++;
                if (indiceAnimacion >= pc.getSpriteAmount(IDLE)) {
                    indiceAnimacion = 0;

                }
            }
        }

        public PersonajeJugable getPc() {
            return pc;
        }
    }
}
