package modelo.carta;

import modelo.juego.Jugador;

public class PotOfGreed extends Magica {

    public PotOfGreed() {
        super("Pot of Greed");
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente) {

        jugador.robarCarta();
        jugador.robarCarta();
    }
}
