package utilz;

import java.util.LinkedList;
import java.util.Queue;

public class SecuenciaDeGuion {
// Clase abstracta que define una acción genérica en la secuencia de guion.
	private abstract class Accion {
		public abstract void ejecutar(); // Método para ejecutar la acción.

		public abstract String getTexto(); // Método para obtener el texto asociado a la acción.

		public abstract boolean haTerminado(); // Método para verificar si la acción ha terminado.
	}

// Clase que representa un diálogo en la secuencia de guion.
	private class Dialogo extends Accion {
		private final String mensaje; // Mensaje del diálogo.
		private boolean mostrado = false; // Indica si el mensaje ya fue mostrado.

		public Dialogo(String mensaje) {
			this.mensaje = mensaje; // Inicializa el mensaje del diálogo.
		}

		@Override
		public void ejecutar() {
			mostrado = true; // Marca el diálogo como mostrado.
		}

		@Override
		public String getTexto() {
			return mensaje; // Devuelve el texto del diálogo.
		}

		@Override
		public boolean haTerminado() {
			return mostrado; // Indica si el diálogo ya fue mostrado.
		}
	}

// Clase que representa una acción de espera en la secuencia de guion.
	private class Esperar extends Accion {
		private final long duracion; // Duración de la espera en milisegundos.
		private long inicio = -1; // Marca de tiempo de inicio de la espera.

		public Esperar(long duracionMs) {
			this.duracion = duracionMs; // Inicializa la duración de la espera.
		}

		@Override
		public void ejecutar() {
			if (inicio < 0)
				inicio = System.currentTimeMillis(); // Registra el tiempo de inicio si aún no se ha iniciado.
		}

		@Override
		public String getTexto() {
			return ""; // No hay texto asociado a la acción de espera.
		}

		@Override
		public boolean haTerminado() {
			// Verifica si el tiempo transcurrido desde el inicio supera la duración.
			return inicio > 0 && (System.currentTimeMillis() - inicio >= duracion);
		}
	}

	private final Queue<Accion> acciones = new LinkedList<>(); // Cola de acciones en la secuencia de guion.
	private Accion accionActual; // Acción que se está ejecutando actualmente.
	private boolean esperandoInput = false; // Indica si se está esperando la confirmación del jugador.

// Método para avanzar la lógica del guion.
	public void actualizar() {
		// Si no hay acción actual y la cola no está vacía, toma la siguiente acción.
		if (accionActual == null && !acciones.isEmpty()) {
			accionActual = acciones.poll();
			accionActual.ejecutar(); // Ejecuta la acción.
		}

		// Si la acción actual ha terminado, decide cómo proceder.
		if (accionActual != null && accionActual.haTerminado()) {
			if (accionActual instanceof Dialogo) {
				esperandoInput = true; // Espera confirmación manual si es un diálogo.
			} else {
				accionActual = null; // Avanza automáticamente si es una acción de espera.
			}
		}
	}

// Método para agregar un diálogo a la cola de acciones.
	public void agregarDialogo(String mensaje) {
		acciones.add(new Dialogo(mensaje));
	}

// Método para agregar una acción de espera a la cola de acciones.
	public void agregarEspera(long ms) {
		acciones.add(new Esperar(ms));
	}

// Método para confirmar que el jugador quiere avanzar el diálogo.
	public void confirmarDialogo() {
		if (esperandoInput) {
			esperandoInput = false; // Marca que ya no se está esperando input.
			accionActual = null; // Avanza a la siguiente acción.
		}
	}

// Método para verificar si la secuencia de guion ha terminado.
	public boolean estaTerminada() {
		return acciones.isEmpty() && accionActual == null; // Verifica si no hay acciones pendientes.
	}

// Método para obtener el texto de la acción actual.
	public String getTextoActual() {
		if (accionActual != null)
			return accionActual.getTexto(); // Devuelve el texto de la acción actual.
		return ""; // Devuelve una cadena vacía si no hay acción actual.
	}

}
