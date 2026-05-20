package vista;

import controlador.JuegoControlador;
import java.util.Scanner;
import modelo.juego.Jugador;

public class VistaConsola implements VistaJuego {

    private final Scanner sc; 
    public VistaConsola(JuegoControlador controlador) {
        this.sc = new Scanner(System.in); // inicialización aquí
    }

    @Override
    public void actualizar(Jugador j1, Jugador j2, boolean turnoJ1) {
        System.out.println("\n===== ESTADO =====");
        System.out.println(j1.getNombre() + " LP:" + j1.getLp() +
            " | Campo:" + j1.getCampo().size() +
            " | Mano:" + j1.getMano().size());
        System.out.println(j2.getNombre() + " LP:" + j2.getLp() +
            " | Campo:" + j2.getCampo().size() +
            " | Mano:" + j2.getMano().size());
        System.out.println("Turno: " + (turnoJ1 ? j1.getNombre() : j2.getNombre()));
    }

    @Override
    public void mostrarMensaje(String mensaje) {
        System.out.println("[LOG] " + mensaje);
    }

    @Override
    public void mostrarGanador(String nombreGanador) {
        System.out.println("\n¡" + nombreGanador.toUpperCase() + " GANA EL DUELO!");
    }

    @Override
    public int pedirEleccion(String titulo, String[] opciones) {
        System.out.println(titulo);
        for (int i = 0; i < opciones.length; i++) {
            System.out.println(i + ". " + opciones[i]);
        }
        return sc.nextInt();
    }
}
