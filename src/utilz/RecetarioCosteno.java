package utilz;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;

import static utilz.Constantes.Ingredientes.*;

public class RecetarioCosteno {

    public static final List<Platillo> RECETAS = List.of(
        new Platillo(List.of(TIBURON, POCION_ROJA, POCION_AZUL), "Encebollado de Aleta de Tiburón", 500, "Difícil"),
        new Platillo(List.of(POCION_AZUL, TIBURON, POCION_ROJA), "Ceviche del Guardián de la Corriente", 500, "Difícil"),
        new Platillo(List.of(CANGREJO, CANGREJO, CANGREJO), "Cangrejada del Montuvio", 150, "Fácil"),
        new Platillo(List.of(TIBURON, TIBURON, TIBURON), "Asado Bravo de Tiburón Porteño", 300, "Media"),
        new Platillo(List.of(ESTRELLA, ESTRELLA, ESTRELLA), "Sopa Mística de la Estrella Esmeraldeña", 450, "Difícil"),
        new Platillo(List.of(TIBURON, ESTRELLA, CANGREJO), "Encocado de Tiburón con Toque de Estrella", 300, "Media"),
        new Platillo(List.of(CANGREJO, TIBURON, POCION_ROJA), "Caldo de los Valientes del Río Verde", 350, "Media"),
        new Platillo(List.of(POCION_ROJA, TIBURON, ESTRELLA), "Tonga Montuvia del Pescador Sano", 450, "Difícil"),
        new Platillo(List.of(ESTRELLA, POCION_ROJA, POCION_AZUL), "Caña Manabita", 550, "Difícil"),
        new Platillo(List.of(POCION_AZUL, CANGREJO, ESTRELLA), "Batido Energético del Estero Profundo", 400, "Difícil"),
        new Platillo(List.of(TIBURON, POCION_AZUL, POCION_ROJA), "Colada Refrescante de Puerto López", 500, "Difícil"),
        new Platillo(List.of(POCION_AZUL, POCION_AZUL, ESTRELLA), "Infusión Costera de Espíritu de Estrella", 550, "Difícil"),
        new Platillo(List.of(POCION_ROJA, POCION_ROJA, POCION_AZUL), "Encebollado Quita Chuchaquis", 600, "Difícil"),
        new Platillo(List.of(POCION_ROJA, POCION_AZUL, TIBURON), "Sancocho Revivido de Colmillo y Fuerza", 500, "Difícil"),
        new Platillo(List.of(TIBURON, CANGREJO, ESTRELLA), "Banderazo Orense", 300, "Media")
    );

    public static Platillo buscarPlatillo(List<String> ultimos3Ingredientes) {
        if (ultimos3Ingredientes == null || ultimos3Ingredientes.size() < 3)
            return null;

        List<String> copia = new ArrayList<>(ultimos3Ingredientes);
        Collections.sort(copia);  // Ordenar para que no importe el orden

        for (Platillo p : RECETAS) {
            List<String> receta = new ArrayList<>(p.getIngredientes());
            Collections.sort(receta);
            if (receta.equals(copia)) {
                return p;
            }
        }
        return null;
    }
}
