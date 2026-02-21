package ui;

import gamestates.Jugando;
import main.Juego;
import utilz.LoadSave;
import utilz.Platillo;
import utilz.RecetarioCosteno;
import java.util.List;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.*;

public class PantallaDeResumenNivel {
//variables para la pantalla de resumen del nivel
	private final Jugando jugando;
	private final BufferedImage fondo;
	private final Map<String, BufferedImage[]> animacionesIdle = new HashMap<>();
	private final Map<String, Integer> frameIndex = new HashMap<>();
	private boolean resultadoMostrado = false;
	private boolean ganoMoneda = false;
	private int fondoX, fondoY, fondoW, fondoH;
	private int tick = 0;
//variables animación de moneda
	private BufferedImage[] framesMoneda;
	private int frameMoneda = 0;
	private int tickAnimacion = 0;
	private boolean animandoMoneda = false;
	private boolean mostrarResultadoMoneda = false;
	private boolean resultadoMoneda = false;
// Variables para las imágenes de cara y cruz de la moneda
	private BufferedImage imgCara;                
	private BufferedImage imgCruz;                

// Constructor de la pantalla de resumen del nivel
	public PantallaDeResumenNivel(Jugando jugando) {
		this.jugando = jugando;
		this.fondo = LoadSave.GetSpriteAtlas(LoadSave.OLLA_IMG); 

		this.fondoW = (int) (500 * Juego.SCALE);
		this.fondoH = (int) (300 * Juego.SCALE);
		this.fondoX = (Juego.GAME_WIDTH - fondoW) / 2;
		this.fondoY = (Juego.GAME_HEIGHT - fondoH) / 2;
		
		cargarAnimaciones();
		cargarAnimacionMoneda(); 
	}
// Método para mostrar el resultado del nivel
	private void cargarAnimaciones() {
		cargarIdle("Cangrejo", LoadSave.GetSpriteAtlas(LoadSave.CANGREJO_SPRITE), 9, 72, 32);
		cargarIdle("Tiburon", LoadSave.GetSpriteAtlas(LoadSave.TIBURON_ATLAS), 8, 34, 30);
		cargarIdle("Estrella", LoadSave.GetSpriteAtlas(LoadSave.ESTRELLA_ATLAS), 8, 34, 30);

	}
	private void cargarAnimacionMoneda() {
	    BufferedImage sheet = LoadSave.GetSpriteAtlas(LoadSave.MONEDA_ATLAS); 
	    framesMoneda = new BufferedImage[6];
	    int ancho = sheet.getWidth() / 6;
	    int alto = sheet.getHeight();
	    for (int i = 0; i < 6; i++) {
	        framesMoneda[i] = sheet.getSubimage(i * ancho, 0, ancho, alto);
	    }
	    imgCara = LoadSave.GetSpriteAtlas(LoadSave.MONEDA_CARA);   
	    imgCruz = LoadSave.GetSpriteAtlas(LoadSave.MONEDA_CRUZ);
	    
	}
// Método para cargar las animaciones de idle de los enemigos
	private void cargarIdle(String tipo, BufferedImage atlas, int frames, int w, int h) {
		BufferedImage[] arr = new BufferedImage[frames];
		for (int i = 0; i < frames; i++) {
			arr[i] = atlas.getSubimage(i * w, 0, w, h);
		}
		animacionesIdle.put(tipo, arr);
		frameIndex.put(tipo, 0);
	}
// Método para mostrar el resultado del nivel
public void draw(Graphics g) {
   // Fondo de la pantalla
	g.drawImage(fondo, fondoX, fondoY, fondoW, fondoH, null);
    
	g.setColor(Color.WHITE);
    
	g.setFont(new Font("Arial", Font.BOLD, 28));
    
	FontMetrics fm = g.getFontMetrics();
   
	g.setFont(new Font("Arial", Font.PLAIN, 18));
    
	fm = g.getFontMetrics();

    int yTexto = fondoY + 180;

    for (int jugadorID = 1; jugadorID <= 2; jugadorID++) {
        g.setColor(Color.WHITE);
        String puntosTexto = "Jugador " + jugadorID + " - Puntos: " + jugando.getPuntajeJugador(jugadorID);
        int xPuntos = fondoX + (fondoW - fm.stringWidth(puntosTexto)) / 2;
        g.drawString(puntosTexto, xPuntos, yTexto);
        yTexto += 25;

        Stack<String> log = (jugadorID == 1) ? jugando.getLogJugador1() : jugando.getLogJugador2();
        List<String> ultimos = new ArrayList<>();

        int size = log.size();
        if (size > 0) {
            int desde = Math.max(0, size - 3);
            for (int i = desde; i < size; i++) {
                ultimos.add(log.get(i));
            }

            String enemigosTitulo = "Últimos " + ultimos.size() + " enemigo" + (ultimos.size() > 1 ? "s" : "") + ":";
            int xUltimos = fondoX + (fondoW - fm.stringWidth(enemigosTitulo)) / 2;
            g.drawString(enemigosTitulo, xUltimos, yTexto);
            yTexto += 10;

            int totalWidth = 0;
            List<BufferedImage> sprites = new ArrayList<>();
            for (String tipo : ultimos) {
                if (!animacionesIdle.containsKey(tipo)) continue;
                BufferedImage frame = animacionesIdle.get(tipo)[frameIndex.get(tipo)];
                sprites.add(frame);
                totalWidth += frame.getWidth();
            }

            int spacing = 10;
            totalWidth += spacing * (sprites.size() - 1);
            int xSprite = fondoX + (fondoW - totalWidth) / 2;
            int spriteHeightMax = 0;

            for (BufferedImage frame : sprites) {
                g.drawImage(frame, xSprite, yTexto, null);
                spriteHeightMax = Math.max(spriteHeightMax, frame.getHeight());
                xSprite += frame.getWidth() + spacing;
            }

            yTexto += spriteHeightMax + 10;

            if (ultimos.size() == 3) {
                Platillo platillo = RecetarioCosteno.buscarPlatillo(ultimos);
                if (platillo != null) {
                    g.setColor(new Color(255, 215, 0));
                    String platilloTexto = "Platillo: " + platillo.getNombre() + " (+" + platillo.getPuntos() + ")";
                    int xPlatillo = fondoX + (fondoW - fm.stringWidth(platilloTexto)) / 2;
                    g.drawString(platilloTexto, xPlatillo, yTexto);
                    yTexto += 25;
                }
            }
        } else {
            yTexto += 40;
        }

        yTexto += 30;
    }

    // Resultado texto de la moneda
    if (mostrarResultadoMoneda) {
        String mensaje = resultadoMoneda ? "¡Cara! Se reemplazó el ingrediente." : "Cruz... se mantiene la receta.";
        g.setColor(Color.YELLOW);
        g.setFont(new Font("Arial", Font.BOLD, 18));
        g.drawString(mensaje, fondoX + 40, fondoY + fondoH + 30);
    }

    //Animación o resultado visual de la moneda
    int monedaX = fondoX + fondoW - 60;
    int monedaY = fondoY + fondoH - 40;

    if (animandoMoneda) {
        g.drawImage(framesMoneda[Math.min(frameMoneda, framesMoneda.length - 1)], monedaX, monedaY, 32, 32, null);
    } else if (mostrarResultadoMoneda) {
        BufferedImage imgResultado = resultadoMoneda ? imgCara : imgCruz;
        g.drawImage(imgResultado, monedaX, monedaY, 32, 32, null);
    }

    //Instrucciones al pie
    int cuadroX = fondoX;
    int cuadroY = fondoY + fondoH + 40;
    int cuadroW = fondoW;
    int cuadroH = 50;

    g.setColor(new Color(0, 0, 0, 200)); // fondo negro transparente
    g.fillRect(cuadroX, cuadroY, cuadroW, cuadroH);

    g.setColor(Color.WHITE);
    g.setFont(new Font("Arial", Font.BOLD, 16));
    String texto = "[ENTER] para continuar   |   [U] lanzar moneda (si hay suficientes enemigos)";
    FontMetrics instruccionesFM = g.getFontMetrics();
    int textoX = cuadroX + (cuadroW - instruccionesFM.stringWidth(texto)) / 2;
    int textoY = cuadroY + ((cuadroH - instruccionesFM.getHeight()) / 2) + instruccionesFM.getAscent();
    g.drawString(texto, textoX, textoY);
}



	// Método para manejar el evento de tecla presionada
public void keyPressed(KeyEvent e) {
    if (e.getKeyCode() == KeyEvent.VK_U && !animandoMoneda && !mostrarResultadoMoneda) {
        animandoMoneda = true;
        tickAnimacion = 0;
        frameMoneda = 0;
        resultadoMostrado = false;
    }

    if (e.getKeyCode() == KeyEvent.VK_ENTER && !animandoMoneda) {
        resultadoMostrado = false;
        mostrarResultadoMoneda = false;
        jugando.cerrarResumen();   
    }
}


// Método para actualizar la pantalla de resumen del nivel
	public void update() {
    tick++;
    if (tick % 25 == 0) {
        for (String tipo : animacionesIdle.keySet()) {
            int current = frameIndex.getOrDefault(tipo, 0);
            int total = animacionesIdle.get(tipo).length;
            frameIndex.put(tipo, (current + 1) % total);
        }
    }

    // Animación de moneda
    if (animandoMoneda) {
        tickAnimacion++;
        if (tickAnimacion % 10 == 0) {
            frameMoneda++;
            if (frameMoneda >= framesMoneda.length) {
                animandoMoneda = false;
                mostrarResultadoMoneda = true;
                resultadoMoneda = jugando.lanzarMonedaYReevaluar(1);
                resultadoMostrado = true;
                animacionesIdle.clear();
                cargarAnimaciones();
            }
        }
    }
}


}
