import controlador.JuegoControlador;
import vista.VistaConsola;
import vista.VentanaYuGiOh;
import javax.swing.SwingUtilities;

public class App {
    public static void main(String[] args) {
        JuegoControlador controlador = new JuegoControlador();

        VistaConsola consola = new VistaConsola(controlador);
        controlador.agregarVista(consola);

        SwingUtilities.invokeLater(() ->
            new VentanaYuGiOh(controlador).setVisible(true)
        );

        // Hilo principal maneja la terminal; no puedo creer que se me pasó ESTA línea 
        consola.iniciar();
    }
}