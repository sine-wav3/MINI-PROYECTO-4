import java.util.Scanner;

public class ChangeOfHeart extends Magica {
    public ChangeOfHeart(){ 
        super("Change of Heart"); 
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente) {
        if (oponente.getCampo().isEmpty()) return;

        Scanner sc = new Scanner(System.in);

        for (int i = 0; i < oponente.getCampo().size(); i++) {
            System.out.println(i + ". " + oponente.getCampo().get(i).getNombre());
        }

        int idx = sc.nextInt();
        Monstruo m = oponente.getCampo().remove(idx);
        jugador.getCampo().add(m);

        System.out.println("Robaste un monstruo enemigo");
    }
}