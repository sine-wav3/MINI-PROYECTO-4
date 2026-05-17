

public class MirrorForce extends Trampa {
    
    public MirrorForce() {
        super("Mirror Force");
    }
    
    @Override
    public boolean condicion(Jugador jugador, Jugador oponente) {
        // Se activa cuando el oponente declara un ataque
        return true; // La condición la revisará el juego
    }
    
    @Override
    public void activar(Jugador jugador, Jugador oponente) {
        System.out.println("MIRROR FORCE! Destruyes todos los monstruos atacantes del oponente");
        oponente.getCampo().clear();
    }
}