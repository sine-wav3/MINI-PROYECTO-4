import java.util.Random;

public class AcesCoup extends Magica {
    public AcesCoup() {
        super("Aces Coup");
    }
    //se acomoda la logica para que funcione mejor
    @Override
    public void activar(Jugador jugador, Jugador oponente) {
    Random rand = new Random();
    int moneda = rand.nextInt(2);
    System.out.println("Lanzaste la moneda...");
    if(moneda == 1){
        System.out.println("Cara! Robas 2 cartas");
        jugador.robarCarta();
        jugador.robarCarta();
    } else {
        System.out.println("Cruz! Tu oponente roba 2 cartas");
        oponente.robarCarta();
        oponente.robarCarta();
    }
    }
}
