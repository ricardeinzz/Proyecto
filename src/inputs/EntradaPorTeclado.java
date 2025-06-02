package inputs;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import gamestates.Gamestate;
import main.PanelDeJuego;

public class EntradaPorTeclado implements KeyListener {

    private PanelDeJuego panelDeJuego;

    public EntradaPorTeclado(PanelDeJuego panelDeJuego) {
        this.panelDeJuego = panelDeJuego;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void keyReleased(KeyEvent e) {
        switch (Gamestate.estado) {
            case MENU -> panelDeJuego.getGame().getMenu().keyReleased(e);
            case JUGANDO -> panelDeJuego.getGame().getPlaying().keyReleased(e);
            case CREDITOS -> panelDeJuego.getGame().getCredits().keyReleased(e);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void keyPressed(KeyEvent e) {
        switch (Gamestate.estado) {
            case MENU -> panelDeJuego.getGame().getMenu().keyPressed(e);
            case SELECIONAR_JUGADOR_ -> panelDeJuego.getGame().getPlayerSelection().keyPressed(e);
            case JUGANDO -> panelDeJuego.getGame().getPlaying().keyPressed(e);
            case OPCIONES -> panelDeJuego.getGame().getGameOptions().keyPressed(e);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not In Use
    }
}