package inputs;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import gamestates.Gamestate;
import main.PanelDeJuego;

public class EntradaPorMouse implements MouseListener, MouseMotionListener {

    private PanelDeJuego panelDeJuego;

    public EntradaPorMouse(PanelDeJuego panelDeJuego) {
        this.panelDeJuego = panelDeJuego;
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseDragged(MouseEvent e) {
        switch (Gamestate.estado) {
            case JUGANDO -> panelDeJuego.getGame().getPlaying().mouseDragged(e);
            case OPCIONES -> panelDeJuego.getGame().getGameOptions().mouseDragged(e);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseMoved(MouseEvent e) {
        switch (Gamestate.estado) {
            case MENU -> panelDeJuego.getGame().getMenu().mouseMoved(e);
            case SELECIONAR_JUGADOR_ -> panelDeJuego.getGame().getPlayerSelection().mouseMoved(e);
            case JUGANDO -> panelDeJuego.getGame().getPlaying().mouseMoved(e);
            case OPCIONES -> panelDeJuego.getGame().getGameOptions().mouseMoved(e);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseClicked(MouseEvent e) {
        switch (Gamestate.estado) {
            case JUGANDO -> panelDeJuego.getGame().getPlaying().mouseClicked(e);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mousePressed(MouseEvent e) {
        switch (Gamestate.estado) {
            case MENU -> panelDeJuego.getGame().getMenu().mousePressed(e);
            case SELECIONAR_JUGADOR_ -> panelDeJuego.getGame().getPlayerSelection().mousePressed(e);
            case JUGANDO -> panelDeJuego.getGame().getPlaying().mousePressed(e);
            case OPCIONES -> panelDeJuego.getGame().getGameOptions().mousePressed(e);
        }
    }

    @SuppressWarnings("incomplete-switch")
    @Override
    public void mouseReleased(MouseEvent e) {
        switch (Gamestate.estado) {
            case MENU -> panelDeJuego.getGame().getMenu().mouseReleased(e);
            case SELECIONAR_JUGADOR_ -> panelDeJuego.getGame().getPlayerSelection().mouseReleased(e);
            case JUGANDO -> panelDeJuego.getGame().getPlaying().mouseReleased(e);
            case OPCIONES -> panelDeJuego.getGame().getGameOptions().mouseReleased(e);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not In use
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not In use
    }

}