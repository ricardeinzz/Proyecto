package audio;

import java.io.IOException;
import java.net.URL;
import java.util.Random;

import javax.sound.sampled.*;

/**
 * Clase que gestiona toda la lógica de audio del jugador,
 * incluyendo música de fondo y efectos de sonido como saltos, ataques y eventos.
 */
public class AudioJugador {

	// Identificadores de canciones de fondo
	public static int MENU_1 = 0;
	public static int NIVEL_1 = 1;
	public static int NIVEL_2 = 2;

	// Identificadores de efectos de sonido
	public static int MUERTO = 0;
	public static int SALTO = 1;
	public static int GAMEOVER = 2;
	public static int LVL_COMPLETADO = 3;
	public static int ATAQUE_UNO = 4;
	public static int ATAQUE_DOS = 5;
	public static int ATAQUE_TRES = 6;

	private Clip[] cancion, efectos; // Arreglos de clips de audio para música y efectos
	private int cancionActualId; // ID de la canción actual en reproducción
	private float volumen = 0.5f; // Volumen general del audio (0.0 a 1.0)
	private boolean cancionMute, efectoMute; // Banderas para silenciar música o efectos
	private Random rand = new Random(); // Para seleccionar aleatoriamente efectos de ataque

	// Constructor: carga música, efectos y comienza reproduciendo la música del menú
	public AudioJugador() {
		cargarCancion();
		cargarEfecto();
		reproducirCancion(MENU_1);
	}

	// Carga los clips de música desde archivos .wav
	private void cargarCancion() {
		String[] names = { "menu", "level1", "level2" };
		cancion = new Clip[names.length];
		for (int i = 0; i < cancion.length; i++)
			cancion[i] = getClip(names[i]);
	}

	// Carga los clips de efectos de sonido
	private void cargarEfecto() {
		String[] effectNames = { "die", "jump", "gameover", "lvlcompleted", "attack1", "attack2", "attack3" };
		efectos = new Clip[effectNames.length];
		for (int i = 0; i < efectos.length; i++)
			efectos[i] = getClip(effectNames[i]);

		updateEffectsVolume(); // Ajusta el volumen inicial
	}

	// Carga un archivo .wav como Clip
	private Clip getClip(String name) {
		URL url = getClass().getResource("/audio/" + name + ".wav"); // Ruta del archivo de sonido
		try {
			AudioInputStream audio = AudioSystem.getAudioInputStream(url);
			Clip c = AudioSystem.getClip();
			c.open(audio); // Abre el clip con el sonido cargado
			return c;
		} catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
			e.printStackTrace();
		}
		return null;
	}

	// Establece el volumen general
	public void setVolumen(float volume) {
		this.volumen = volume;
		updateSongVolume();
		updateEffectsVolume();
	}

	// Detiene la canción actual si está activa
	public void detenerCancion() {
		if (cancion[cancionActualId].isActive())
			cancion[cancionActualId].stop();
	}

	// Cambia la canción según el índice del nivel (par/impar)
	public void setLevelSong(int lvlIndex) {
		if (lvlIndex % 2 == 0)
			reproducirCancion(NIVEL_1);
		else
			reproducirCancion(NIVEL_2);
	}

	// Detiene la música y reproduce el sonido de "nivel completado"
	public void lvlCompleto() {
		detenerCancion();
		reproducirEfecto(LVL_COMPLETADO);
	}

	// Reproduce aleatoriamente uno de los tres sonidos de ataque
	public void reproducirSonidoDeAtaque() {
		int start = 4;
		start += rand.nextInt(3); // Escoge entre 4, 5 o 6
		reproducirEfecto(start);
	}

	// Reproduce un efecto específico, reiniciando su posición si ya estaba sonando
	public void reproducirEfecto(int effect) {
		if (efectos[effect].getMicrosecondPosition() > 0)
			efectos[effect].setMicrosecondPosition(0);
		efectos[effect].start();
	}

	// Reproduce una canción de fondo en bucle
	public void reproducirCancion(int song) {
		detenerCancion(); // Detiene cualquier canción anterior

		cancionActualId = song;
		updateSongVolume();
		cancion[cancionActualId].setMicrosecondPosition(0);
		cancion[cancionActualId].loop(Clip.LOOP_CONTINUOUSLY);
	}

	// Alterna el estado de silencio de la música
	public void toggleSongMute() {
		this.cancionMute = !cancionMute;
		for (Clip c : cancion) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(cancionMute);
		}
	}

	// Alterna el estado de silencio de los efectos de sonido
	public void toggleEffectMute() {
		this.efectoMute = !efectoMute;
		for (Clip c : efectos) {
			BooleanControl booleanControl = (BooleanControl) c.getControl(BooleanControl.Type.MUTE);
			booleanControl.setValue(efectoMute);
		}
		if (!efectoMute)
			reproducirEfecto(SALTO); // Reproduce sonido de salto como prueba si se reactivan los efectos
	}

	// Ajusta el volumen actual de la canción activa
	private void updateSongVolume() {
		FloatControl gainControl = (FloatControl) cancion[cancionActualId].getControl(FloatControl.Type.MASTER_GAIN);
		float range = gainControl.getMaximum() - gainControl.getMinimum();
		float gain = (range * volumen) + gainControl.getMinimum();
		gainControl.setValue(gain);
	}

	// Ajusta el volumen de todos los efectos de sonido
	private void updateEffectsVolume() {
		for (Clip c : efectos) {
			FloatControl gainControl = (FloatControl) c.getControl(FloatControl.Type.MASTER_GAIN);
			float range = gainControl.getMaximum() - gainControl.getMinimum();
			float gain = (range * volumen) + gainControl.getMinimum();
			gainControl.setValue(gain);
		}
	}
}

