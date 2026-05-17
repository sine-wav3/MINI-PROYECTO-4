
public class PotOfGreed extends Magica {

    public PotOfGreed() {
        super("Pot of Greed");
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente) {
        System.out.println("se activo Pot of greed, roba 2 cartas");
        jugador.robarCarta();
        jugador.robarCarta();
    }
    
}
