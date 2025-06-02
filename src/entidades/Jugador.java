package entidades;

import static utilz.Constantes.PlayerConstants.*;
import static utilz.MetodosDeAyuda.*;
import static utilz.Constantes.*;
import static utilz.Constantes.Direciones.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.AudioJugador;
import gamestates.Jugando;
import main.Juego;
import utilz.LoadSave;

public class Jugador extends Entidad {
	/**
	 * Atributos del jugador:
	 * - Controlan animación, movimiento, físicas (salto, gravedad), barras de estado y colisiones.
	 * - Incluyen referencias a recursos gráficos, estado del juego, y propiedades de combate.
	 */
    private BufferedImage[][] animacion;
    private boolean moviendo = false, atacando = false;
    private boolean izquierda, derecha, salto;
    private int[][] lvlData;
//    private float xDrawOffset = 21 * Juego.SCALE;
//    private float yDrawOffset = 4 * Juego.SCALE;
    private float velocidadSalto = -2.25f * Juego.SCALE;
    private float velocidadCaidaDespuesDeVelocidad = 0.5f * Juego.SCALE;
    private BufferedImage imgBarraDeEstado;
    private int anchoBarraDeEstado = (int) (192 * Juego.SCALE);
    private int altoBarraDeEstado = (int) (58 * Juego.SCALE);
    private int barraDeEstadoX = (int) (10 * Juego.SCALE);
    private int barraDeEstadoY = (int) (10 * Juego.SCALE);
    private int anchoBarraDeVida = (int) (150 * Juego.SCALE);
    private int altoBarraDeVida = (int) (4 * Juego.SCALE);
    private int barraDeVidaXInicio = (int) (34 * Juego.SCALE);
    private int barraDeVidaYInicio = (int) (14 * Juego.SCALE);
    private int anchoVida = anchoBarraDeVida;
    private int anchoBarraDePoder = (int) (104 * Juego.SCALE);
    private int altoBarraDePoder = (int) (2 * Juego.SCALE);
    private int barraDePoderXInicio = (int) (44 * Juego.SCALE);
    private int barraDePoderYInicio = (int) (34 * Juego.SCALE);
    private int anchoPoder = anchoBarraDePoder;
    private int poderMaxValor = 200;
    private int poderValor = poderMaxValor;
    private int giroX = 0;
    private int giroW = 1;
    private boolean ataqueChecked;
    private Jugando jugando;
    private int celdaY = 0;
    private boolean atqueDePoderActivo;
    private int ataquePoderTick;
    private int velocidadDeCrecimentoPoder = 15;
    private int crecimientoPoderTick;
    private final PersonajeJugable personajeJugable;
    /**
     * Constructor del jugador.
     * Inicializa dimensiones del sprite, estado inicial, recursos gráficos, animaciones y cajas de colisión.
     * 
     * @param personajeJugable Objeto con datos del personaje actual (sprite, hitbox, animaciones).
     * @param jugando Referencia al estado actual del juego.
     */
    public Jugador(PersonajeJugable personajeJugable, Jugando jugando) {
        super(0, 0, (int) (personajeJugable.spriteW * Juego.SCALE), (int) (personajeJugable.spriteH * Juego.SCALE));
        this.personajeJugable = personajeJugable;
        this.jugando = jugando;
        this.estado = IDLE;
        this.vidaMaxima = 100;
        this.vidaActual = vidaMaxima;
        this.velocidadDeCaminar = Juego.SCALE * 1.0f;
        animacion = LoadSave.cargarAnimacion(personajeJugable);
        imgBarraDeEstado = LoadSave.GetSpriteAtlas(LoadSave.BARRA_ESTADO);
        inicializarCajaColision(personajeJugable.hitboxW, personajeJugable.hitboxH);
        initAttackBox();
    }
    /**
     * Establece la posición inicial (spawn) del jugador en el mapa.
     * 
     * @param spawn Coordenadas X e Y donde el jugador aparece.
     */
    public void setSpawn(Point spawn) {
        this.x = spawn.x;
        this.y = spawn.y;
        cajaColision.x = x;
        cajaColision.y = y;
    }
    /**
     * Crea y posiciona la caja de colisión del ataque del jugador.
     * La caja se reposiciona dinámicamente en cada frame según la dirección.
     */
    private void initAttackBox() {
        cajaAtaque = new Rectangle2D.Float(x, y, (int) (35 * Juego.SCALE), (int) (20 * Juego.SCALE));
        resetAttackBox();
    }
    /**
     * Método principal de actualización ejecutado en cada frame del juego.
     * - Controla animaciones, físicas, ataques, vida, interacción con el entorno y efectos de estado.
     * - Cambia al estado MUERTO cuando la vida llega a cero y ejecuta las animaciones finales.
     */
    public void actualizar() {
        actualizarBarraDeVida();
        actualizarBarraDePoder();

        if (vidaActual <= 0) {
            if (estado != MUERTO) {
                estado = MUERTO;
                contadorAnimacion = 0;
                indiceAnimacion = 0;
                //jugando.setPlayerDying(true);
                jugando.getGame().getAudioPlayer().reproducirEfecto(AudioJugador.MUERTO);

              
                if (!EstaEntidadEnPiso(cajaColision, lvlData)) {
                    enAire = true;
                    velocidadAire = 0;
                }
            } else if (indiceAnimacion == personajeJugable.getSpriteAmount(MUERTO) - 1 && contadorAnimacion >= ANI_VELOCIDAD - 1) {
//                jugando.setGameOver(true);
               // jugando.getGame().getAudioPlayer().detenerCancion();
                jugando.getGame().getAudioPlayer().reproducirEfecto(AudioJugador.GAMEOVER);
            } else {
                actualizarAnimacionTick();

              
                if (enAire)
                    if (PuedeMoverAqui(cajaColision.x, cajaColision.y + velocidadAire, cajaColision.width, cajaColision.height, lvlData)) {
                        cajaColision.y += velocidadAire;
                        velocidadAire += GRAVEDAD;
                    } else
                        enAire = false;

            }

            return;
        }

        actualizarAttackBox();

        if (estado == HIT) {
            if (indiceAnimacion <= personajeJugable.getSpriteAmount(estado) - 3)
                retroceder(direccionRetroceso, lvlData, 1.25f);
            actualizarDesplazamientoRetroceso();
        } else
            actualizarPos();

        if (moviendo) {
            checkPosionTocado();
            checkPuasTocando();
            checkdentroDelAgua();
            celdaY = (int) (cajaColision.y / Juego.TILES_SIZE);
            if (atqueDePoderActivo) {
                ataquePoderTick++;
                if (ataquePoderTick >= 35) {
                    ataquePoderTick = 0;
                    atqueDePoderActivo = false;
                }
            }
        }

        if (atacando || atqueDePoderActivo)
            checkAtaque();

        actualizarAnimacionTick();
        setAnimation();
    }
    /**
     * Verifica si el jugador está dentro de una celda de agua.
     * Si está en el agua, se considera una muerte instantánea.
     */

    private void checkdentroDelAgua() {
        if (EntidadEstaEnAgua(cajaColision, jugando.getLevelManager().getCurrentLevel().getLevelData()))
            vidaActual = 0;
    }
    /**
     * Verifica si el jugador ha tocado una trampa puas.
     * Aplica efectos definidos por el nivel actual.
     */
    private void checkPuasTocando() {
        jugando.checkSpikesTouched(this);
    }
    /**
     * Verifica si el jugador ha tocado un objeto recogible como pociones.
     * Notifica al controlador de nivel que se recolectó el objeto.
     */
    private void checkPosionTocado() {
        jugando.checkPotionTouched(cajaColision);
    }
    /**
     * Verifica si el jugador ha ejecutado un ataque y detecta colisiones con enemigos u objetos rompibles.
     * Reproduce el sonido del ataque y aplica daño si corresponde.
     */
    private void checkAtaque() {
        if (ataqueChecked || indiceAnimacion != 1)
            return;
        ataqueChecked = true;

        if (atqueDePoderActivo)
            ataqueChecked = false;

        jugando.checkEnemyHit(cajaAtaque);
        jugando.checkObjectHit(cajaAtaque);
        jugando.getGame().getAudioPlayer().reproducirSonidoDeAtaque();
    }

    private void setAttackBoxOnRightSide() {
        cajaAtaque.x = cajaColision.x + cajaColision.width - (int) (Juego.SCALE * 5);
    }

    private void setAttackBoxOnLeftSide() {
        cajaAtaque.x = cajaColision.x - cajaColision.width - (int) (Juego.SCALE * 10);
    }
    /**
     * Actualiza la posición de la caja de ataque en función de la dirección del jugador.
     * Si el jugador se mueve o activa un ataque de poder, la caja se ajusta automáticamente.
     */
    private void actualizarAttackBox() {
        if (derecha && izquierda) {
            if (giroW == 1) {
                setAttackBoxOnRightSide();
            } else {
                setAttackBoxOnLeftSide();
            }

        } else if (derecha || (atqueDePoderActivo && giroW == 1))
            setAttackBoxOnRightSide();
        else if (izquierda || (atqueDePoderActivo && giroW == -1))
            setAttackBoxOnLeftSide();

        cajaAtaque.y = cajaColision.y + (Juego.SCALE * 10);
    }
    /**
     * Recalcula el ancho visual de la barra de vida basado en la vida actual.
     * Esto se utiliza para renderizar la barra de forma proporcional.
     */
    private void actualizarBarraDeVida() {
        anchoVida = (int) ((vidaActual / (float) vidaMaxima) * anchoBarraDeVida);
    }
    /**
     * Recalcula el ancho visual de la barra de poder.
     * El poder se regenera automáticamente con el tiempo, incrementando su valor cada cierto número de frames.
     */

    private void actualizarBarraDePoder() {
        anchoPoder = (int) ((poderValor / (float) poderMaxValor) * anchoBarraDePoder);
        crecimientoPoderTick++;
        if (crecimientoPoderTick >= velocidadDeCrecimentoPoder) {
            crecimientoPoderTick = 0;
            changePower(1);
        }
    }
    /**
     * Renderiza al jugador en pantalla con su animación correspondiente.
     * También dibuja su interfaz de usuario (barras de vida y poder).
     * 
     * @param g Objeto gráfico de Java 2D.
     * @param lvlOffset Offset horizontal del nivel (para scroll).
     */
    public void render(Graphics g, int lvlOffset) {
        g.drawImage(animacion[personajeJugable.getRowIndex(estado)][indiceAnimacion], (int) (cajaColision.x - personajeJugable.xDrawOffset) - lvlOffset + giroX, (int) (cajaColision.y - personajeJugable.yDrawOffset + (int) (desplazamientoRetroceso)), ancho * giroW, alto, null);
        dibujarCajaColision(g, lvlOffset);
//		dibujarCajaAtaque(g, lvlOffset);
        dibujarUI(g);
    }
    /**
     * Dibuja la barra de vida (roja) y la barra de poder (amarilla) sobre la interfaz del jugador.
     */

    private void dibujarUI(Graphics g) {
    	// Si es jugador2, dibuja más abajo
    	boolean esJugador2 = this == jugando.getPlayer2();
    	int offsetY = esJugador2 ? (int)(70 * Juego.SCALE) : 0;

    	// Background UI
    	g.drawImage(imgBarraDeEstado, barraDeEstadoX, barraDeEstadoY + offsetY, anchoBarraDeEstado, altoBarraDeEstado, null);

    	// Health bar
    	g.setColor(Color.red);
    	g.fillRect(barraDeVidaXInicio + barraDeEstadoX, barraDeVidaYInicio + barraDeEstadoY + offsetY, anchoVida, altoBarraDeVida);

    	// Power bar
    	g.setColor(Color.yellow);
    	g.fillRect(barraDePoderXInicio + barraDeEstadoX, barraDePoderYInicio + barraDeEstadoY + offsetY, anchoPoder, altoBarraDePoder);
    }
    /**
     * Controla el avance de animaciones del jugador.
     * Cambia de frame en función de un contador y reinicia la animación si es necesario.
     * También reinicia estado de ataque y golpe si se ha completado la animación correspondiente.
     */
    private void actualizarAnimacionTick() {
        contadorAnimacion++;
        if (contadorAnimacion >= ANI_VELOCIDAD) {
            contadorAnimacion = 0;
            indiceAnimacion++;
            if (indiceAnimacion >= personajeJugable.getSpriteAmount(estado)) {
                indiceAnimacion = 0;
                atacando = false;
                ataqueChecked = false;
                if (estado == HIT) {
                    cambiarEstado(IDLE);
                    velocidadAire = 0f;
                    if (!EsPiso(cajaColision, 0, lvlData))
                        enAire = true;
                }
            }
        }
    }
    /**
     * Establece el estado de animación del jugador (idle, corriendo, saltando, cayendo, atacando).
     * Basado en la entrada del jugador y su estado físico (en el aire, atacando, etc).
     */
    private void setAnimation() {
        int startAni = estado;

        if (estado == HIT)
            return;

        if (moviendo)
            estado = CORRIENDO;
        else
            estado = IDLE;

        if (enAire) {
            if (velocidadAire < 0)
                estado = SALTANDO;
            else
                estado = CAYENDO;
        }

        if (atqueDePoderActivo) {
            estado = ATACANDO;
            indiceAnimacion = 1;
            contadorAnimacion = 0;
            return;
        }

        if (atacando) {
            estado = ATACANDO;
            if (startAni != ATACANDO) {
                indiceAnimacion = 1;
                contadorAnimacion = 0;
                return;
            }
        }
        if (startAni != estado)
            resetAniTick();
    }
    /**
     * Reinicia el contador y el índice de animación a cero.
     * Usado al cambiar de estado visual.
     */

    private void resetAniTick() {
        contadorAnimacion = 0;
        indiceAnimacion = 0;
    }
    /**
     * Calcula el desplazamiento horizontal y vertical del jugador.
     * Incluye físicas de salto y caída, verificación de colisiones con el entorno y actualización de posición.
     */
    private void actualizarPos() {
        moviendo = false;

        if (salto)
            saltar();

        if (!enAire)
            if (!atqueDePoderActivo)
                if ((!izquierda && !derecha) || (derecha && izquierda))
                    return;

        float xSpeed = 0;

        if (izquierda && !derecha) {
            xSpeed -= velocidadDeCaminar;
            giroX = ancho;
            giroW = -1;
        }
        if (derecha && !izquierda) {
            xSpeed += velocidadDeCaminar;
            giroX = 0;
            giroW = 1;
        }

        if (atqueDePoderActivo) {
            if ((!izquierda && !derecha) || (izquierda && derecha)) {
                if (giroW == -1)
                    xSpeed = -velocidadDeCaminar;
                else
                    xSpeed = velocidadDeCaminar;
            }

            xSpeed *= 3;
        }

        if (!enAire)
            if (!EstaEntidadEnPiso(cajaColision, lvlData))
                enAire = true;

        if (enAire && !atqueDePoderActivo) {
            if (PuedeMoverAqui(cajaColision.x, cajaColision.y + velocidadAire, cajaColision.width, cajaColision.height, lvlData)) {
                cajaColision.y += velocidadAire;
                velocidadAire += GRAVEDAD;
                actualizarXPos(xSpeed);
            } else {
                cajaColision.y = GetPosYEntidadEnTechoOPiso(cajaColision, velocidadAire);
                if (velocidadAire > 0)
                    resetInAir();
                else
                    velocidadAire = velocidadCaidaDespuesDeVelocidad;
                actualizarXPos(xSpeed);
            }

        } else
            actualizarXPos(xSpeed);
        moviendo = true;
    }
    /**
     * Inicia el salto del jugador si no está en el aire.
     * Establece velocidad vertical negativa y activa el estado enAire.
     */
    private void saltar() {
        if (enAire)
            return;
        jugando.getGame().getAudioPlayer().reproducirEfecto(AudioJugador.SALTO);
        enAire = true;
        velocidadAire = velocidadSalto;
    }
    /**
     * Detiene el estado de caída y reinicia la velocidad vertical.
     */
    private void resetInAir() {
        enAire = false;
        velocidadAire = 0;
    }
    /**
     * Aplica el movimiento horizontal al jugador.
     * Si detecta una colisión, ajusta la posición y cancela el ataque de poder si estaba activo.
     */
    private void actualizarXPos(float xVelocidad) {
        if (PuedeMoverAqui(cajaColision.x + xVelocidad, cajaColision.y, cajaColision.width, cajaColision.height, lvlData))
            cajaColision.x += xVelocidad;
        else {
            cajaColision.x = GetEntidadJuntoAMuroX(cajaColision, xVelocidad);
            if (atqueDePoderActivo) {
                atqueDePoderActivo = false;
                ataquePoderTick = 0;
            }
        }
    }
    /**
     * Modifica la vida del jugador.
     * Si el valor es negativo y el jugador no está ya en estado de golpe (HIT), cambia a dicho estado.
     * La vida se mantiene entre 0 y el máximo permitido.
     */
    public void cambiarVida(int value) {
        if (value < 0) {
            if (estado == HIT)
                return;
            else
                cambiarEstado(HIT);
        }

        vidaActual += value;
        vidaActual = Math.max(Math.min(vidaActual, vidaMaxima), 0);
    }
    /**
     * Variante de cambiarVida que también calcula la dirección del retroceso en función de la posición del enemigo.
     */
    public void cambiarVida(int value, Enemigos e) {
        if (estado == HIT)
            return;
        cambiarVida(value);
        direccionDesplazamientoRetroceso = ARRIBA;
        desplazamientoRetroceso = 0;

        if (e.obtenerCajaColision().x < cajaColision.x)
            direccionRetroceso = DERECHA;
        else
            direccionRetroceso = IZQUIERDA;
    }
    /**
     * Mata al jugador inmediatamente (vida actual = 0).
     */
    public void kill() {
        vidaActual = 0;
    }
    /**
     * Aumenta o reduce el poder del jugador.
     * El valor se limita entre 0 y poderMaxValor.
     */
    public void changePower(int value) {
        poderValor += value;
        poderValor = Math.max(Math.min(poderValor, poderMaxValor), 0);
    }

    /**
     * Carga la matriz de datos del nivel, usada para verificar colisiones con el entorno.
     * Si el jugador no está tocando el piso, entra en estado de caída.
     */
    public void loadLvlData(int[][] lvlData) {
        this.lvlData = lvlData;
        if (!EstaEntidadEnPiso(cajaColision, lvlData))
            enAire = true;
    }
    /**
     * Reinicia las banderas de dirección izquierda y derecha
     */
    public void resetDirBooleans() {
        izquierda = false;
        derecha = false;
    }
    /**
     * Activa o desactiva el estado de ataque del jugador.
     */
    public void setAtacando(boolean attacking) {
        this.atacando = attacking;
    }

    public boolean isIzquierda() {
        return izquierda;
    }

    public void setIzquierda(boolean left) {
        this.izquierda = left;
    }

    public boolean isDerecha() {
        return derecha;
    }

    public void setDerecha(boolean right) {
        this.derecha = right;
    }

    public void setSalto(boolean jump) {
        this.salto = jump;
    }
    /**
     * Reinicia por completo el estado del jugador: vida, poder, dirección, físicas, cajas de colisión.
     * Se usa al reaparecer o al reiniciar el juego.
     */
    public void resetAll() {
        resetDirBooleans();
        enAire = false;
        atacando = false;
        moviendo = false;
        velocidadAire = 0f;
        estado = IDLE;
        vidaActual = vidaMaxima;
        atqueDePoderActivo = false;
        ataquePoderTick = 0;
        poderValor = poderMaxValor;

        cajaColision.x = x;
        cajaColision.y = y;
        resetAttackBox();

        if (!EstaEntidadEnPiso(cajaColision, lvlData))
            enAire = true;
    }

    private void resetAttackBox() {
        if (giroW == 1)
            setAttackBoxOnRightSide();
        else
            setAttackBoxOnLeftSide();
    }

    public int getCeldaY() {
        return celdaY;
    }
    /**
     * Inicia un ataque de poder si se tiene suficiente energía (mínimo 60 unidades).
     * Reduce el poder actual y activa el estado de ataque especial.
     */
    public void powerAttack() {
        if (atqueDePoderActivo)
            return;
        if (poderValor >= 60) {
            atqueDePoderActivo = true;
            changePower(-60);
        }

    }
    public boolean estaMuerto() {
        return vidaActual <= 0 && estado == MUERTO;
    }
}