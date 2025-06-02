package utilz;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;

import javax.imageio.ImageIO;

import entidades.PersonajeJugable;

/**
 * Clase utilitaria encargada de cargar recursos gráficos (imágenes, atlas, sprites) del juego.
 * Todos los archivos se ubican dentro de la carpeta `/resources` del proyecto.
 */
public class LoadSave {

    // ---------- NOMBRES DE ARCHIVOS DE RECURSOS (se buscan en /resources) ----------

    // Sprites de jugadores
    public static final String PLAYER_PIRATA = "player_sprites.png";
    public static final String PLAYER_ORCO = "player_orc.png";
    public static final String PLAYER_SOLDADO = "player_soldier.png";

    // Atlas de juego
    public static final String LEVEL_ATLAS = "outside_sprites.png";
    public static final String BOTONES_MENU = "button_atlas.png";
    public static final String FONDO_MENU = "menu_background.png";
    public static final String FONDO_PAUSA = "pause_menu.png";
    public static final String BOTONES_SONIDO = "sound_button.png";
    public static final String BOTON_URM = "urm_buttons.png";
    public static final String BOTON_VOLUMEN = "volume_buttons.png";
    public static final String MENU_FONDO_IMG = "background_menu.png";
    public static final String PLAYING_BG_IMG = "playing_bg_img.png";

    // Fondo animado
    public static final String NUBES_GRANDES = "big_clouds.png";
    public static final String NUBES_PEQUE = "small_clouds.png";

    // Enemigos
    public static final String CANGREJO_SPRITE = "crabby_sprite.png";
    public static final String ESTRELLA_ATLAS = "pinkstar_atlas.png";
    public static final String TIBURON_ATLAS = "shark_atlas.png";

    // HUD y pantallas
    public static final String BARRA_ESTADO = "health_power_bar.png";
    public static final String COMPLETA_IMG = "completed_sprite.png";
    public static final String PANTALLA_MUERTE = "death_screen.png";
    public static final String OPCIONES_MENU = "options_background.png";
    public static final String CREDITOS = "credits_list.png";
    public static final String JUEGO_COMPLETO = "game_completed.png";

    // Objetos y entorno
    public static final String POSION_ATLAS = "potions_sprites.png";
    public static final String CONTENEDOR_ATLAS = "objects_sprites.png";
    public static final String TRAPAS_ATLAS = "trap_atlas.png";
    public static final String CANON_ATLAS = "cannon_atlas.png";
    public static final String BALA_CANON = "ball.png";
    public static final String PARTICULAS_LLUVIA = "rain_particle.png";
    public static final String AGUA_ARRIBA = "water_atlas_animation.png";
    public static final String AGUA_ABAJO = "water.png";
    public static final String BARCO = "ship.png";

    public static final String PREGUNTA_ATLAS = "question_atlas.png";
    public static final String EXCLAMACION_ATLAS = "exclamation_atlas.png";
    public static final String PASTO_ATLAS = "grass_atlas.png";
    public static final String ARBOL_UNO_ATLAS = "tree_one_atlas.png";
    public static final String ARBOL_DOS_ATLAS = "tree_two_atlas.png";

    // ---------- MÉTODOS PARA CARGAR RECURSOS ----------

    /**
     * Carga la animación de un personaje jugable (pirata, orco, soldado).
     * Divide el sprite sheet en un arreglo 2D de imágenes.
     * @param pc El personaje jugable que contiene el sprite a cargar
     * @return Matriz de sprites [fila][columna]
     */
    public static BufferedImage[][] cargarAnimacion(PersonajeJugable pc) {
        BufferedImage img = LoadSave.GetSpriteAtlas(pc.playerAtlas);
        BufferedImage[][] animaciones = new BufferedImage[pc.rowA][pc.colA];

        for (int j = 0; j < animaciones.length; j++)
            for (int i = 0; i < animaciones[j].length; i++)
                animaciones[j][i] = img.getSubimage(
                    i * pc.spriteW, j * pc.spriteH, pc.spriteW, pc.spriteH
                );

        return animaciones;
    }

    /**
     * Carga una imagen (atlas o sprite) desde el directorio /resources usando su nombre de archivo.
     * @param nombreArchivo Nombre del archivo (ej. "player_sprites.png")
     * @return Imagen cargada como BufferedImage
     */
    public static BufferedImage GetSpriteAtlas(String nombreArchivo) {
        BufferedImage img = null;
        InputStream is = LoadSave.class.getResourceAsStream("/" + nombreArchivo);
        try {
            img = ImageIO.read(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (is != null) is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return img;
    }

    /**
     * Carga todos los niveles ubicados en la carpeta `/resources/lvls`.
     * Los niveles deben tener nombres secuenciales como `1.png`, `2.png`, etc.
     * @return Arreglo de imágenes, una por nivel, ordenadas por nombre
     */
    public static BufferedImage[] GetAllLevels() {
        URL url = LoadSave.class.getResource("/lvls");
        File file = null;

        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        File[] files = file.listFiles(); // Lista de todos los archivos en /lvls
        File[] filesSorted = new File[files.length];

        // Ordena los archivos por nombre: "1.png", "2.png", ..., "N.png"
        for (int i = 0; i < filesSorted.length; i++)
            for (int j = 0; j < files.length; j++) {
                if (files[j].getName().equals((i + 1) + ".png"))
                    filesSorted[i] = files[j];
            }

        BufferedImage[] imgs = new BufferedImage[filesSorted.length];

        for (int i = 0; i < imgs.length; i++)
            try {
                imgs[i] = ImageIO.read(filesSorted[i]);
            } catch (IOException e) {
                e.printStackTrace();
            }

        return imgs;
    }
}
