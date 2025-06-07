package utilz;

import java.util.LinkedList;
import java.util.Queue;

public class SecuenciaDeGuion {

    private abstract class Accion {
        public abstract void ejecutar();
        public abstract boolean haTerminado();
        public abstract String getTexto();
    }

    private class Dialogo extends Accion {
        private final String mensaje;
        private boolean mostrado = false;

        public Dialogo(String mensaje) {
            this.mensaje = mensaje;
        }

        @Override
        public void ejecutar() {
            mostrado = true;
        }

        @Override
        public boolean haTerminado() {
            return mostrado;
        }

        @Override
        public String getTexto() {
            return mensaje;
        }
    }

    private class Esperar extends Accion {
        private final long duracion;
        private long inicio = -1;

        public Esperar(long duracionMs) {
            this.duracion = duracionMs;
        }

        @Override
        public void ejecutar() {
            if (inicio < 0) inicio = System.currentTimeMillis();
        }

        @Override
        public boolean haTerminado() {
            return inicio > 0 && (System.currentTimeMillis() - inicio >= duracion);
        }

        @Override
        public String getTexto() {
            return "";
        }
    }

    private final Queue<Accion> acciones = new LinkedList<>();
    private Accion accionActual;
    private boolean esperandoInput = false;

    // Agregar diálogos y esperas
    public void agregarDialogo(String mensaje) {
        acciones.add(new Dialogo(mensaje));
    }

    public void agregarEspera(long ms) {
        acciones.add(new Esperar(ms));
    }

    // Avanzar lógica del guion
    public void actualizar() {
        if (accionActual == null && !acciones.isEmpty()) {
            accionActual = acciones.poll();
            accionActual.ejecutar();
        }

        if (accionActual != null && accionActual.haTerminado()) {
            // Si es un diálogo, esperar confirmación manual
            if (accionActual instanceof Dialogo) {
                esperandoInput = true;
            } else {
                accionActual = null; // avanzar automáticamente si es "Esperar"
            }
        }
    }

    // Confirmar que el jugador quiere avanzar el diálogo
    public void confirmarDialogo() {
        if (esperandoInput) {
            esperandoInput = false;
            accionActual = null;
        }
    }

    public boolean estaTerminada() {
        return acciones.isEmpty() && accionActual == null;
    }

    public String getTextoActual() {
        if (accionActual != null) return accionActual.getTexto();
        return "";
    }
}
