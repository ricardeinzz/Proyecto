package entidades; // Paquete que contiene todas las entidades del juego, como enemigos y jugadores.

import static utilz.Constantes.ConstanteEnemigos.*; // Importa constantes específicas del enemigo tipo Cangrejo.
import static utilz.MetodosDeAyuda.EsPiso; // Método para verificar si hay piso debajo del enemigo.
import static utilz.Constantes.Dialogo.*; // Importa símbolos visuales para diálogo (como la exclamación).

import gamestates.Jugando; // Estado del juego que contiene información sobre el jugador, nivel, etc.

/**
 * Clase que representa al enemigo tipo Cangrejo.
 * Hereda de la clase Enemigos y contiene comportamiento personalizado.
 */
public class Cangrejo extends Enemigos {

    /**
     * Constructor del enemigo Cangrejo.
     * @param x posición X inicial.
     * @param y posición Y inicial.
     */
    public Cangrejo(float x, float y) {
        // Inicializa el enemigo con posición, tamaño y tipo definido en las constantes.
        super(x, y, CANGREJO_ANCHO, CANGREJO_ALTO, CANGREJO);

        // Inicializa la caja de colisión del cuerpo (para detectar piso, paredes, etc).
        inicializarCajaColision(22, 19);

        // Inicializa la caja de ataque (área donde puede golpear al jugador).
        initAttackBox(82, 19, 30);
    }

    /**
     * Método que se llama cada frame para actualizar la lógica del cangrejo.
     * @param lvlData datos del nivel (plataformas, obstáculos, etc).
     * @param jugando instancia del estado actual del juego.
     */
    public void actualizar(int[][] lvlData, Jugando jugando) {
        actualizarComportamiento(lvlData, jugando);     // Aplica lógica de movimiento, ataque, detección, etc.
        updateAnimationTick();                // Avanza el fotograma de la animación actual.
        actualizarAttackBox();                // Ajusta la posición de la caja de ataque.
    }
    

    /**
     * Lógica interna del comportamiento del cangrejo según su estado.
     * @param lvlData datos del nivel.
     * @param jugando estado actual del juego.
     */
    private void actualizarComportamiento(int[][] lvlData, Jugando jugando) {
        // Verificación inicial al crearse la entidad (ej. si está en el aire o no).
        Jugador jugador1=jugando.getPlayer();
        Jugador jugador2=jugando.getPlayer2();
    	if (primeraActualizacion)
            firstUpdateCheck(lvlData);

        // Si el enemigo está en el aire (saltando o cayendo).
        if (enAire) {
            inAirChecks(lvlData, jugando); // Aplica gravedad y revisa colisiones con el piso.
        } else {
            // Según el estado actual del enemigo, realiza una acción distinta.
            switch (estado) {
                case IDLE: // Si está quieto.
                    if (EsPiso(cajaColision, lvlData)) // Si está sobre el piso...
                        cambiarEstado(CORRIENDO);     // Comienza a correr.
                    else
                        enAire = true; // Si no hay piso debajo, comienza a caer.
                    break;

                case CORRIENDO: // Si está corriendo.
                    if (canSeePlayer(lvlData, jugador1,jugador2)) { // Si ve al jugador...
                        turnTowardsPlayer(jugador1,jugador2); // Gira hacia él.

                        if (isPlayerCloseForAttack(jugador1,jugador2)) // Si está cerca...
                            cambiarEstado(ATACANDO); // Inicia el ataque.
                    }

                    move(lvlData); // Se mueve en la dirección actual.

                    if (enAire) // Si al moverse cae, muestra una exclamación visual.
                        jugando.addDialogue((int) cajaColision.x, (int) cajaColision.y, EXCLAMACION);
                    break;

                case ATACANDO: // Si está atacando.
                    if (indiceAnimacion == 0)
                        ataqueChecked = false; // Reinicia el chequeo de golpe.

                    if (indiceAnimacion == 3 && !ataqueChecked)
                        checkPlayerHit(cajaAtaque, jugador1,jugador2); // Si coincide el frame y aún no golpeó, revisa si golpea al jugador.
                    break;

                case HIT: // Si recibió daño.
                    if (indiceAnimacion <= GetSpriteAmount(tipoEnemigo, estado) - 2)
                        retroceder(direccionRetroceso, lvlData, 2f); // Retrocede al ser golpeado.

                    actualizarDesplazamientoRetroceso(); // Actualiza la distancia retrocedida.
                    break;
            }
        }
    }
}
