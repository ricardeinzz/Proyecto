package utilz;

import java.util.LinkedList;
import java.util.Map;
import java.util.HashMap;

public class TablaHashKills {
	private final LinkedList<KillRecord>[] tabla;
	private final int capacidad;

	public TablaHashKills(int capacidad) {
		this.capacidad = capacidad;
		this.tabla = new LinkedList[capacidad];
		for (int i = 0; i < capacidad; i++)
			tabla[i] = new LinkedList<>();
	}

	private int calcularHash(String tipoEnemigo, int nivel, long tiempo) {
		long segundos = tiempo / 1000;
		int asciiSuma = 0;
		for (char c : tipoEnemigo.toCharArray()) {
			asciiSuma += c;
		}
		int hash = (asciiSuma + nivel * 31 + (int) (segundos * 13)) % capacidad;
		return Math.abs(hash);
	}

	public int registrarKill(String tipoEnemigo, int nivel, long tiempoDeMuerte, int puntaje, int jugadorID) {
		int hash = calcularHash(tipoEnemigo, nivel, tiempoDeMuerte);
		LinkedList<KillRecord> lista = tabla[hash];

		for (KillRecord record : lista) {
			if (record.coincideCon(tipoEnemigo, nivel, tiempoDeMuerte, jugadorID)) {
				int multiplicador = record.getCantidad() + 1;
				int puntosMultiplicados = puntaje * multiplicador;
				record.agregarKill(puntosMultiplicados);

				System.out.println("Combo x" + multiplicador + " → +" + puntosMultiplicados);
				return puntosMultiplicados;
			}
		}

		lista.add(new KillRecord(tipoEnemigo, nivel, tiempoDeMuerte, puntaje, jugadorID));
		System.out.println("Kill inicial → +" + puntaje);
		return puntaje;
	}

	public void imprimirTodo() {
		for (LinkedList<KillRecord> lista : tabla)
			for (KillRecord registro : lista)
				System.out.println(registro);
	}

	public void limpiarTodo() {
		for (int i = 0; i < tabla.length; i++) {
			tabla[i].clear();
		}
	}

	public Map<String, Integer> resumenKillsPorJugador(int jugadorID) {
		Map<String, Integer> resumen = new HashMap<>();
		for (LinkedList<KillRecord> lista : tabla) {
			for (KillRecord k : lista) {
				if (k.getJugadorID() == jugadorID) {
					resumen.merge(k.getTipoEnemigo(), k.getCantidad(), Integer::sum);
				}
			}
		}
		return resumen;
	}
}
