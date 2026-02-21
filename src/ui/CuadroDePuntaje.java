package ui;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import gamestates.Jugando;
import main.Juego;

public class CuadroDePuntaje {
    private Jugando jugando;

    public CuadroDePuntaje(Jugando jugando) {
        this.jugando = jugando;
    }

    public void draw(Graphics g) {
        int padding = 10;
        int boxHeight = 40;
        int boxWidth = 300;
        int x = (Juego.GAME_WIDTH - boxWidth) / 2;
        int y = 10; // Parte superior

        // Fondo del rectángulo
        g.setColor(new Color(0, 0, 0, 180));
        g.fillRoundRect(x, y, boxWidth, boxHeight, 15, 15);

        // Texto blanco
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 16));

        // Mostrar ambos puntajes en línea
        String text = "Jugador 1: " + jugando.getPuntajeJugador(1) + "   |   Jugador 2: " + jugando.getPuntajeJugador(2);
        int textWidth = g.getFontMetrics().stringWidth(text);

        g.drawString(text, x + (boxWidth - textWidth) / 2, y + 25);
    }
}
