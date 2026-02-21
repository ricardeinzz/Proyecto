package utilz;


public class KillRecord {
    private final String tipoEnemigo;
    private final int nivel;
    private final long tiempoDeMuerte;
    private int cantidad;
    private int puntajeTotal;
    private int jugadorID;
    public KillRecord(String tipoEnemigo, int nivel, long tiempoDeMuerte, int puntajeInicial, int jugadorID) {
        this.tipoEnemigo = tipoEnemigo;
        this.nivel = nivel;
        this.tiempoDeMuerte = tiempoDeMuerte;
        this.cantidad = 1;
        this.puntajeTotal = puntajeInicial;
        this.jugadorID = jugadorID;
    }
    public boolean coincideCon(String tipo, int nivel, long tiempo, int jugadorID) {
        return this.tipoEnemigo.equals(tipo) &&
               this.nivel == nivel &&
               this.jugadorID == jugadorID &&
               (this.tiempoDeMuerte / 1000) == (tiempo / 1000);
    }

    public void agregarKill(int puntaje) {
        this.cantidad++;
        this.puntajeTotal += puntaje;
    }

    public String getTipoEnemigo() {
        return tipoEnemigo;
    }

    public int getNivel() {
        return nivel;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getPuntajeTotal() {
        return puntajeTotal;
    }

    public long getTiempoDeMuerte() {
        return tiempoDeMuerte;
    }
    public int getJugadorID() {
        return jugadorID;
    }
    @Override
    public String toString() {
        return "Jugador " + jugadorID + ": " + tipoEnemigo + " x" + cantidad +
               " (" + puntajeTotal + " puntos, nivel " + nivel + ")";
    }

}
