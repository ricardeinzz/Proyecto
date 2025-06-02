package entidades;

import main.Juego;
import utilz.LoadSave;

import static utilz.Constantes.PlayerConstants.*;

/**
 * Enum que define los personajes jugables disponibles en el juego,
 * con sus configuraciones visuales, animaciones, hitboxes y desplazamientos.
 */
public enum PersonajeJugable {

    // Definición de personajes con sus parámetros de animación, sprites, tamaños, y offsets:

    // Personaje PIRATA: define cantidad de sprites por acción, filas en el atlas, ruta de imagen, etc.
    PIRATA(5, 6, 3, 1, 3, 4, 8, // sprites por acción
            0, 1, 2, 3, 4, 5, 6, // filas en el atlas por acción
            LoadSave.PLAYER_PIRATA, 7, 8, 64, 40, // atlas y dimensiones de sprite
            20, 27, 21, 4), // hitbox y offset de dibujo

    // Personaje ORCO
    ORCO(6, 8, 8, 8, 6, 4, 4,
            0, 1, 1, 1, 2, 4, 5,
            LoadSave.PLAYER_ORCO, 6, 8, 100, 100,
            13, 15, 44, 42),

    // Personaje SOLDADO
    SOLDADO(6, 8, 8, 8, 6, 4, 4,
            0, 1, 1, 1, 2, 5, 6,
            LoadSave.PLAYER_SOLDADO, 7, 8, 100, 100,
            12, 18, 44, 39);

    // Cantidad de sprites por tipo de animación
    public int spriteA_IDLE, spriteA_RUNNING, spriteA_JUMP, spriteA_FALLING, spriteA_ATTACK, spriteA_HIT, spriteA_DEAD;

    // Fila en el atlas de sprites correspondiente a cada acción
    public int rowIDLE, rowRUNNING, rowJUMP, rowFALLING, rowATTACK, rowHIT, rowDEAD;

    // Ruta al atlas del personaje (imagen que contiene todos los sprites)
    public String playerAtlas;

    // Cantidad de filas y columnas que tiene el atlas
    public int rowA, colA;

    // Tamaño de cada sprite en el atlas
    public int spriteW, spriteH;

    // Dimensiones del hitbox (área de colisión) del personaje
    public int hitboxW, hitboxH;

    // Offset en X y Y para alinear correctamente el dibujo en pantalla
    public int xDrawOffset, yDrawOffset;

    // Constructor del enum para cada personaje jugable
    PersonajeJugable(int spriteA_IDLE, int spriteA_RUNNING, int spriteA_JUMP, int spriteA_FALLING, int spriteA_ATTACK, int spriteA_HIT, int spriteA_DEAD,
                     int rowIDLE, int rowRUNNING, int rowJUMP, int rowFALLING, int rowATTACK, int rowHIT, int rowDEAD,
                     String playerAtlas, int rowA, int colA, int spriteW, int spriteH,
                     int hitboxW, int hitboxH,
                     int xDrawOffset, int yDrawOffset) {

        this.spriteA_IDLE = spriteA_IDLE;
        this.spriteA_RUNNING = spriteA_RUNNING;
        this.spriteA_JUMP = spriteA_JUMP;
        this.spriteA_FALLING = spriteA_FALLING;
        this.spriteA_ATTACK = spriteA_ATTACK;
        this.spriteA_HIT = spriteA_HIT;
        this.spriteA_DEAD = spriteA_DEAD;

        this.rowIDLE = rowIDLE;
        this.rowRUNNING = rowRUNNING;
        this.rowJUMP = rowJUMP;
        this.rowFALLING = rowFALLING;
        this.rowATTACK = rowATTACK;
        this.rowHIT = rowHIT;
        this.rowDEAD = rowDEAD;

        this.playerAtlas = playerAtlas;
        this.rowA = rowA;
        this.colA = colA;
        this.spriteW = spriteW;
        this.spriteH = spriteH;

        this.hitboxW = hitboxW;
        this.hitboxH = hitboxH;

        // Escala el offset de dibujo de acuerdo al factor de escala del juego.
        this.xDrawOffset = (int) (xDrawOffset * Juego.SCALE);
        this.yDrawOffset = (int) (yDrawOffset * Juego.SCALE);
    }

    /**
     * Devuelve la cantidad de sprites que tiene el personaje para una acción específica.
     * @param player_action constante de la acción (IDLE, CORRIENDO, etc.)
     * @return cantidad de sprites para la acción
     */
    public int getSpriteAmount(int player_action) {
        return switch (player_action) {
            case IDLE -> spriteA_IDLE;
            case CORRIENDO -> spriteA_RUNNING;
            case SALTANDO -> spriteA_JUMP;
            case CAYENDO -> spriteA_FALLING;
            case ATACANDO -> spriteA_ATTACK;
            case HIT -> spriteA_HIT;
            case MUERTO -> spriteA_DEAD;
            default -> 1;
        };
    }

    /**
     * Devuelve el índice de la fila en el atlas para una acción específica.
     * @param player_action constante de la acción
     * @return índice de la fila
     */
    public int getRowIndex(int player_action) {
        return switch (player_action) {
            case IDLE -> rowIDLE;
            case CORRIENDO -> rowRUNNING;
            case SALTANDO -> rowJUMP;
            case CAYENDO -> rowFALLING;
            case ATACANDO -> rowATTACK;
            case HIT -> rowHIT;
            case MUERTO -> rowDEAD;
            default -> 1;
        };
    }

}
