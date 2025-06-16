package utilz;

import java.util.List;
import java.util.Objects;

/**
 * Clase que representa un platillo combinable a partir de 3 ingredientes válidos.
 * Se utiliza en la lógica de recompensa al finalizar un nivel.
 */
public class Platillo {
    private final List<String> ingredientes;
    private final String nombre;
    private final int puntos;
    private final String dificultad;

    public Platillo(List<String> ingredientes, String nombre, int puntos, String dificultad) {
        this.ingredientes = ingredientes;
        this.nombre = nombre;
        this.puntos = puntos;
        this.dificultad = dificultad;
    }

    public List<String> getIngredientes() {
        return ingredientes;
    }

    public String getNombre() {
        return nombre;
    }

    public int getPuntos() {
        return puntos;
    }

    public String getDificultad() {
        return dificultad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Platillo)) return false;
        Platillo platillo = (Platillo) o;
        return puntos == platillo.puntos &&
               Objects.equals(nombre, platillo.nombre) &&
               Objects.equals(dificultad, platillo.dificultad) &&
               Objects.equals(ingredientes, platillo.ingredientes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientes, nombre, puntos, dificultad);
    }

    @Override
    public String toString() {
        return nombre + " (" + dificultad + ") +" + puntos + " pts";
    }
}
