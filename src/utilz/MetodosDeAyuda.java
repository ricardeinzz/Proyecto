package utilz;

import java.awt.geom.Rectangle2D;

import main.Juego;
import objetos.Proyectiles;

/**
 * Clase que contiene métodos utilitarios para validaciones y colisiones
 * dentro del entorno del juego (como movimiento, contacto con piso/agua/obstáculos, visibilidad, etc).
 */
public class MetodosDeAyuda {

    /**
     * Verifica si una entidad puede moverse a una posición dada.
     * Se validan las cuatro esquinas del área de colisión.
     */
    public static boolean PuedeMoverAqui(float x, float y, float ancho, float alto, int[][] lvlData) {
        if (!EsSolido(x, y, lvlData))
            if (!EsSolido(x + ancho, y + alto, lvlData))
                if (!EsSolido(x + ancho, y, lvlData))
                    if (!EsSolido(x, y + alto, lvlData))
                        return true;
        return false;
    }

    /**
     * Determina si una coordenada específica es sólida (no caminable o bloqueada).
     */
    private static boolean EsSolido(float x, float y, int[][] lvlData) {
        int maxAncho = lvlData[0].length * Juego.TILES_SIZE;
        if (x < 0 || x >= maxAncho)
            return true;
        if (y < 0 || y >= Juego.GAME_HEIGHT)
            return true;

        float xIndex = x / Juego.TILES_SIZE;
        float yIndex = y / Juego.TILES_SIZE;

        return EsCeldaSolida((int) xIndex, (int) yIndex, lvlData);
    }

    /**
     * Verifica si un proyectil ha colisionado con una celda sólida.
     */
    public static boolean HaColisionadoProyectil(Proyectiles p, int[][] lvlData) {
        return EsSolido(
            p.obtenerCajaColision().x + p.obtenerCajaColision().width / 2,
            p.obtenerCajaColision().y + p.obtenerCajaColision().height / 2,
            lvlData
        );
    }

    /**
     * Determina si una entidad está en contacto con agua por la parte inferior.
     */
    public static boolean EntidadEstaEnAgua(Rectangle2D.Float cajaColision, int[][] lvlData) {
        if (GetValorCelda(cajaColision.x, cajaColision.y + cajaColision.height, lvlData) != 48)
            if (GetValorCelda(cajaColision.x + cajaColision.width, cajaColision.y + cajaColision.height, lvlData) != 48)
                return false;
        return true;
    }

    /**
     * Devuelve el valor de la celda en una posición específica del mapa.
     */
    private static int GetValorCelda(float xPos, float yPos, int[][] lvlData) {
        int xCord = (int) (xPos / Juego.TILES_SIZE);
        int yCord = (int) (yPos / Juego.TILES_SIZE);
        return lvlData[yCord][xCord];
    }

    /**
     * Determina si una celda es sólida, en función de su valor.
     * Las celdas 11, 48 y 49 se consideran "no sólidas" (transitables).
     */
    public static boolean EsCeldaSolida(int xTile, int yTile, int[][] lvlData) {
        int value = lvlData[yTile][xTile];
        return switch (value) {
            case 11, 48, 49 -> false;
            default -> true;
        };
    }

    /**
     * Calcula la posición X junto a la pared más cercana.
     */
    public static float GetEntidadJuntoAMuroX(Rectangle2D.Float cajaColision, float xVelocidad) {
        int celdaActual = (int) (cajaColision.x / Juego.TILES_SIZE);
        if (xVelocidad > 0) {
            // Si va a la derecha
            int posXCelda = celdaActual * Juego.TILES_SIZE;
            int xOffset = (int) (Juego.TILES_SIZE - cajaColision.width);
            return posXCelda + xOffset - 1;
        } else {
            // Si va a la izquierda
            return celdaActual * Juego.TILES_SIZE;
        }
    }

    /**
     * Calcula la posición Y correcta al tocar piso o techo.
     */
    public static float GetPosYEntidadEnTechoOPiso(Rectangle2D.Float cajaColision, float velocidadAire) {
        int celdaActual = (int) (cajaColision.y / Juego.TILES_SIZE);
        if (velocidadAire > 0) {
            // Está cayendo - tocando piso
            int posYCelda = celdaActual * Juego.TILES_SIZE;
            int yOffset = (int) (Juego.TILES_SIZE - cajaColision.height);
            return posYCelda + yOffset - 1;
        } else {
            // Está saltando - tocando techo
            return celdaActual * Juego.TILES_SIZE;
        }
    }

    /**
     * Verifica si hay suelo directamente debajo de la entidad.
     */
    public static boolean EstaEntidadEnPiso(Rectangle2D.Float cajaColision, int[][] lvlData) {
        return EsSolido(cajaColision.x, cajaColision.y + cajaColision.height + 1, lvlData) ||
               EsSolido(cajaColision.x + cajaColision.width, cajaColision.y + cajaColision.height + 1, lvlData);
    }

    /**
     * Verifica si hay una celda sólida en dirección del movimiento.
     */
    public static boolean EsPiso(Rectangle2D.Float cajaColision, float xSpeed, int[][] lvlData) {
        if (xSpeed > 0)
            return EsSolido(cajaColision.x + cajaColision.width + xSpeed, cajaColision.y + cajaColision.height + 1, lvlData);
        else
            return EsSolido(cajaColision.x + xSpeed, cajaColision.y + cajaColision.height + 1, lvlData);
    }

    /**
     * Verifica si hay piso bajo ambos extremos de la entidad.
     */
    public static boolean EsPiso(Rectangle2D.Float cajaColision, int[][] lvlData) {
        return EsSolido(cajaColision.x + cajaColision.width, cajaColision.y + cajaColision.height + 1, lvlData) ||
               EsSolido(cajaColision.x, cajaColision.y + cajaColision.height + 1, lvlData);
    }

    /**
     * Determina si el jugador es visible desde un cañón (línea recta sin obstáculos).
     */
    public static boolean EsJugadorVisibleParaCanon(int[][] lvlData, Rectangle2D.Float primeraHitbox, Rectangle2D.Float segundaHitbox, int yTile) {
        int primeraCeldaX = (int) (primeraHitbox.x / Juego.TILES_SIZE);
        int segundaCeldaX = (int) (segundaHitbox.x / Juego.TILES_SIZE);

        if (primeraCeldaX > segundaCeldaX)
            return EstanTodasLasCeldasLimpias(segundaCeldaX, primeraCeldaX, yTile, lvlData);
        else
            return EstanTodasLasCeldasLimpias(primeraCeldaX, segundaCeldaX, yTile, lvlData);
    }

    /**
     * Verifica si todas las celdas entre dos puntos están libres (sin obstáculos).
     */
    public static boolean EstanTodasLasCeldasLimpias(int xStart, int xEnd, int y, int[][] lvlData) {
        for (int i = 0; i < xEnd - xStart; i++)
            if (EsCeldaSolida(xStart + i, y, lvlData))
                return false;
        return true;
    }

    /**
     * Verifica si todas las celdas entre dos puntos son caminables (libres y con piso debajo).
     */
    public static boolean SonTodasLasCeldasCaminables(int xStart, int xEnd, int y, int[][] lvlData) {
        if (EstanTodasLasCeldasLimpias(xStart, xEnd, y, lvlData))
            for (int i = 0; i < xEnd - xStart; i++)
                if (!EsCeldaSolida(xStart + i, y + 1, lvlData))
                    return false;
        return true;
    }

    /**
     * Verifica si hay línea de visión limpia entre un enemigo y el jugador, considerando
     * que el jugador puede estar en el borde de una plataforma.
     */
    public static boolean IsSightClear(int[][] lvlData, Rectangle2D.Float enemyBox, Rectangle2D.Float playerBox, int yTile) {
        int firstXTile = (int) (enemyBox.x / Juego.TILES_SIZE);

        int secondXTile;
        if (EsSolido(playerBox.x, playerBox.y + playerBox.height + 1, lvlData))
            secondXTile = (int) (playerBox.x / Juego.TILES_SIZE);
        else
            secondXTile = (int) ((playerBox.x + playerBox.width) / Juego.TILES_SIZE);

        if (firstXTile > secondXTile)
            return SonTodasLasCeldasCaminables(secondXTile, firstXTile, yTile, lvlData);
        else
            return SonTodasLasCeldasCaminables(firstXTile, secondXTile, yTile, lvlData);
    }

    /**
     * Versión antigua de IsSightClear (puede ser menos precisa).
     */
    public static boolean IsSightClear_OLD(int[][] lvlData, Rectangle2D.Float firstHitbox, Rectangle2D.Float secondHitbox, int yTile) {
        int firstXTile = (int) (firstHitbox.x / Juego.TILES_SIZE);
        int secondXTile = (int) (secondHitbox.x / Juego.TILES_SIZE);

        if (firstXTile > secondXTile)
            return SonTodasLasCeldasCaminables(secondXTile, firstXTile, yTile, lvlData);
        else
            return SonTodasLasCeldasCaminables(firstXTile, secondXTile, yTile, lvlData);
    }
}
