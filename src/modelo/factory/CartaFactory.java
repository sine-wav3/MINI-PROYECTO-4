package modelo.factory;

import java.util.HashMap;
import java.util.Map;

import modelo.carta.Carta;

public class CartaFactory {

    private Map<String, Class<? extends Carta>> registro =
            new HashMap<>();

    public void registrar(
            String nombre,
            Class<? extends Carta> clase) {

        registro.put(nombre, clase);
    }

    public Carta crear(String nombre)
            throws Exception {

        Class<? extends Carta> clase =
                registro.get(nombre);

        if (clase == null) {
            throw new IllegalArgumentException(
                    "Carta no registrada: " + nombre);
        }

        return clase
                .getDeclaredConstructor()
                .newInstance();
    }

    public boolean contiene(String nombre) {
        return registro.containsKey(nombre);
    }
}
