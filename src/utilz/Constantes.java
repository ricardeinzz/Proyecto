package utilz;

import main.Juego;

/**
 * Clase que centraliza todas las constantes utilizadas en el juego.
 * Estas constantes incluyen configuraciones de físicas, enemigos, objetos, UI, diálogos y más.
 * Está organizada en clases internas para mantener una estructura limpia y fácil de navegar.
 */
public class Constantes {

	// Constantes físicas generales
	public static final float GRAVEDAD = 0.04f * Juego.SCALE;
	public static final int ANI_VELOCIDAD = 25; // Velocidad base para animaciones

	/**
	 * Constantes relacionadas con íconos de diálogo que se muestran sobre entidades (exclamación, pregunta).
	 */
	public static class Dialogo {
		public static final int PREGUNTA = 0;
		public static final int EXCLAMACION = 1;

		// Dimensiones del sprite del diálogo, escaladas
		public static final int DIALOGO_ANCHO = (int) (14 * Juego.SCALE);
		public static final int DIALOGO_ALTO = (int) (12 * Juego.SCALE);

		/**
		 * Devuelve la cantidad de frames que tiene la animación de un ícono de diálogo.
		 */
		public static int GetSpriteAmount(int tipo) {
			switch (tipo) {
				case PREGUNTA, EXCLAMACION:
					return 5;
			}
			return 0;
		}
	}

	/**
	 * Constantes relacionadas con los proyectiles (como balas de cañón).
	 */
	public static class Proyectiles {
		public static final int BALA_CANON_DEFAULT_ANCHO = 15;
		public static final int BALA_CANON_DEFAULT_ALTO = 15;

		// Dimensiones escaladas
		public static final int BALA_CANON_ANCHO = (int) (Juego.SCALE * BALA_CANON_DEFAULT_ANCHO);
		public static final int BALA_CANON_ALTO = (int) (Juego.SCALE * BALA_CANON_DEFAULT_ALTO);

		// Velocidad de movimiento del proyectil
		public static final float VELOCIDAD = 0.75f * Juego.SCALE;
	}

	/**
	 * Constantes para los objetos interactivos del juego (pociones, cajas, barriles, trampas).
	 */
	public static class ObjectosConstantes {
		// Tipos de objetos
		public static final int POSION_ROJA = 0;
		public static final int POSION_AZUL = 1;
		public static final int BARRIL = 2;
		public static final int CAJA = 3;
		public static final int PUAS = 4;
		public static final int CANON_IZQUIERDA = 5;
		public static final int CANON_DERECHA = 6;
		public static final int ARBOL_UNO = 7;
		public static final int ARBOL_DOS = 8;
		public static final int ARBOL_TRES = 9;

		// Valores que recuperan las pociones
		public static final int POSION_ROJA_VALOR = 15;
		public static final int POSION_AZUL_VALOR = 10;

		// Tamaños de contenedores (cajas, barriles)
		public static final int CONTENEDOR_ANCHO_DEFAULT = 40;
		public static final int CONTENEDOR_ALTO_DEFAULT = 30;
		public static final int CONTENEDOR_ANCHO = (int) (Juego.SCALE * CONTENEDOR_ANCHO_DEFAULT);
		public static final int CONTENEDOR_ALTO = (int) (Juego.SCALE * CONTENEDOR_ALTO_DEFAULT);

		// Tamaños de pociones
		public static final int POSION_ANCHO_DEFAULT = 12;
		public static final int POSION_ALTURA_DEFAULT = 16;
		public static final int POSION_ANCHO = (int) (Juego.SCALE * POSION_ANCHO_DEFAULT);
		public static final int POSION_ALTO = (int) (Juego.SCALE * POSION_ALTURA_DEFAULT);

		// Tamaños de las púas
		public static final int PUAS_ANCHO_DEFAULT = 32;
		public static final int PUAS_ALTO_DEFAULT = 32;
		public static final int PUAS_ANCHO = (int) (Juego.SCALE * PUAS_ANCHO_DEFAULT);
		public static final int PUAS_ALTO = (int) (Juego.SCALE * PUAS_ALTO_DEFAULT);

		// Tamaños del cañón
		public static final int CANON_ANCHO_DEFAULT = 40;
		public static final int CANON_ALTO_DEFAULT = 26;
		public static final int CANON_ANCHO = (int) (CANON_ANCHO_DEFAULT * Juego.SCALE);
		public static final int CANON_ALTO = (int) (CANON_ALTO_DEFAULT * Juego.SCALE);

		// Determina cuántos sprites tiene cada objeto para su animación
		public static int GetSpriteAmount(int tipo_objeto) {
			switch (tipo_objeto) {
				case POSION_ROJA, POSION_AZUL:
					return 7;
				case BARRIL, CAJA:
					return 8;
				case CANON_IZQUIERDA, CANON_DERECHA:
					return 7;
			}
			return 1;
		}

		// Posicionamiento visual de árboles (offset X e Y)
		public static int GetTreeOffsetX(int tipoArbol) {
			switch (tipoArbol) {
				case ARBOL_UNO:
					return (Juego.TILES_SIZE / 2) - (GetTreeWidth(tipoArbol) / 2);
				case ARBOL_DOS:
					return (int) (Juego.TILES_SIZE / 2.5f);
				case ARBOL_TRES:
					return (int) (Juego.TILES_SIZE / 1.65f);
			}
			return 0;
		}

		public static int GetTreeOffsetY(int tipoArbol) {
			switch (tipoArbol) {
				case ARBOL_UNO:
					return -GetTreeHeight(tipoArbol) + Juego.TILES_SIZE * 2;
				case ARBOL_DOS, ARBOL_TRES:
					return -GetTreeHeight(tipoArbol) + (int) (Juego.TILES_SIZE / 1.25f);
			}
			return 0;
		}

		public static int GetTreeWidth(int tipoArbol) {
			switch (tipoArbol) {
				case ARBOL_UNO:
					return (int) (39 * Juego.SCALE);
				case ARBOL_DOS:
					return (int) (62 * Juego.SCALE);
				case ARBOL_TRES:
					return -(int) (62 * Juego.SCALE); // ¡Cuidado! valor negativo podría ser error
			}
			return 0;
		}

		public static int GetTreeHeight(int tipoArbol) {
			switch (tipoArbol) {
				case ARBOL_UNO:
					return (int) (92 * Juego.SCALE);
				case ARBOL_DOS, ARBOL_TRES:
					return (int) (54 * Juego.SCALE);
			}
			return 0;
		}
	}

	/**
	 * Constantes específicas para enemigos (tipos, estados, daño, salud y sprites).
	 */
	public static class ConstanteEnemigos {
		// Tipos
		public static final int CANGREJO = 0;
		public static final int ESTRELLA = 1;
		public static final int TIBURON = 2;

		// Estados
		public static final int IDLE = 0;
		public static final int CORRIENDO = 1;
		public static final int ATACANDO = 2;
		public static final int HIT = 3;
		public static final int MUERTO = 4;

		// Cangrejo
		public static final int CANGREJO_ANCHO_DEFAULT = 72;
		public static final int CANGREJO_ALTO_DEFAULT = 32;
		public static final int CANGREJO_ANCHO = (int) (CANGREJO_ANCHO_DEFAULT * Juego.SCALE);
		public static final int CANGREJO_ALTO = (int) (CANGREJO_ALTO_DEFAULT * Juego.SCALE);
		public static final int CANGREJO_DIBUJOOFFSET_X = (int) (26 * Juego.SCALE);
		public static final int CANGREJO_DIBUJOOFFSET_Y = (int) (9 * Juego.SCALE);

		// Estrella
		public static final int ESTRELLA_ANCHO_DEFAULT = 34;
		public static final int ESTRELLA_ALTO_DEFAULT = 30;
		public static final int ESTRELLA_ANCHO = (int) (ESTRELLA_ANCHO_DEFAULT * Juego.SCALE);
		public static final int ESTRELLA_ALTO = (int) (ESTRELLA_ALTO_DEFAULT * Juego.SCALE);
		public static final int ESTRELLA_DIBUJOOFFSET_X = (int) (9 * Juego.SCALE);
		public static final int ESTRELLA_DIBUJOOFFSET_Y = (int) (7 * Juego.SCALE);

		// Tiburón
		public static final int TIBURON_ANCHO_DEFAULT = 34;
		public static final int TIBURON_ALTO_DEFAULT = 30;
		public static final int TIBURON_ANCHO = (int) (TIBURON_ANCHO_DEFAULT * Juego.SCALE);
		public static final int TIBURON_ALTO = (int) (TIBURON_ALTO_DEFAULT * Juego.SCALE);
		public static final int TIBURON_DIBUJOOFFSET_X = (int) (8 * Juego.SCALE);
		public static final int TIBURON_DIBUJOOFFSET_Y = (int) (6 * Juego.SCALE);

		// Frames por estado de animación según tipo de enemigo
		public static int GetSpriteAmount(int tipo_enemigo, int estado_enemigo) {
			switch (estado_enemigo) {
				case IDLE:
					if (tipo_enemigo == CANGREJO) return 9;
					else return 8;
				case CORRIENDO: return 6;
				case ATACANDO:
					return (tipo_enemigo == TIBURON) ? 8 : 7;
				case HIT: return 4;
				case MUERTO: return 5;
			}
			return 0;
		}

		// Vida máxima por tipo de enemigo
		public static int GetMaxHealth(int tipo_enemigo) {
			return switch (tipo_enemigo) {
				case CANGREJO -> 50;
				case ESTRELLA, TIBURON -> 25;
				default -> 1;
			};
		}

		// Daño por tipo de enemigo
		public static int GetEnemyDmg(int tipo_enemigo) {
			return switch (tipo_enemigo) {
				case CANGREJO -> 15;
				case ESTRELLA -> 20;
				case TIBURON -> 25;
				default -> 0;
			};
		}
	}

	/**
	 * Constantes para elementos ambientales como nubes.
	 */
	public static class Ambiente {
		public static final int NUBE_GRANDE_ANCHO_DEFAULT = 448;
		public static final int NUBE_GRANDE_ALTO_DEFAULT = 101;
		public static final int NUBE_PEQUE_ANCHO_DEFAULT = 74;
		public static final int NUBE_PEQUE_ALTO_DEFAULT = 24;

		public static final int NUBE_GRANDE_ANCHO = (int) (NUBE_GRANDE_ANCHO_DEFAULT * Juego.SCALE);
		public static final int NUBE_GRANDE_ALTO = (int) (NUBE_GRANDE_ALTO_DEFAULT * Juego.SCALE);
		public static final int NUBE_PEQUE_ANCHO = (int) (NUBE_PEQUE_ANCHO_DEFAULT * Juego.SCALE);
		public static final int NUBE_PEQUE_ALTO = (int) (NUBE_PEQUE_ALTO_DEFAULT * Juego.SCALE);
	}

	/**
	 * Constantes de la interfaz de usuario: botones, volumen, sliders
	 */
	public static class UI {
		public static class Botones {
			public static final int B_ANCHO_DEFAULT = 140;
			public static final int B_ALTO_DEFAULT = 56;
			public static final int B_ANCHO = (int) (B_ANCHO_DEFAULT * Juego.SCALE);
			public static final int B_ALTO = (int) (B_ALTO_DEFAULT * Juego.SCALE);
		}

		public static class BotonPausa {
			public static final int TAMANO_SONIDO_DEFAULT = 42;
			public static final int TAMANO_SONIDO = (int) (TAMANO_SONIDO_DEFAULT * Juego.SCALE);
		}

		public static class BotonUrm {
			public static final int URM_TAMANO_DEFAULT = 56;
			public static final int URM_TAMANO = (int) (URM_TAMANO_DEFAULT * Juego.SCALE);
		}

		public static class BotonVolumen {
			public static final int VOLUMEN_ANCHO_DEFAULT = 28;
			public static final int VOLUMEN_ALTO_HEIGHT = 44;
			public static final int SLIDER_ANCHO_DEFAULT = 215;

			public static final int VOLUMEN_ANCHO = (int) (VOLUMEN_ANCHO_DEFAULT * Juego.SCALE);
			public static final int VOLUMEN_ALTO = (int) (VOLUMEN_ALTO_HEIGHT * Juego.SCALE);
			public static final int SLIDER_ANCHO = (int) (SLIDER_ANCHO_DEFAULT * Juego.SCALE);
		}
	}

	/**
	 * Constantes para representar direcciones básicas.
	 */
	public static class Direciones {
		public static final int IZQUIERDA = 0;
		public static final int ARRIBA = 1;
		public static final int DERECHA = 2;
		public static final int ABAJO = 3;
	}

	/**
	 * Constantes de acciones del jugador .
	 */
	public static class PlayerConstants {
		public static final int IDLE = 0;
		public static final int CORRIENDO = 1;
		public static final int SALTANDO = 2;
		public static final int CAYENDO = 3;
		public static final int ATACANDO = 4;
		public static final int HIT = 5;
		public static final int MUERTO = 6;


	}
}
