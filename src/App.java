import javax.swing.SwingUtilities;

import vista.VentanaYuGiOh;

public class App {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeLater(() -> {
            new VentanaYuGiOh().setVisible(true);
        });
    }
}
