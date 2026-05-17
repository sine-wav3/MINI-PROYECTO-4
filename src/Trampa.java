
public abstract class Trampa extends Carta implements Activable {

    private boolean activable = true;

    public Trampa(String nombre) {
        super(nombre);
    }

    public boolean isActivable() {
        return activable;
    }
    
    public void setActivable(boolean activable) {
        this.activable = activable;
    }
    
    // las trampas tienen una condicion para activarse, si se cumple, se activa, si no pos no
    public abstract boolean condicion(Jugador jugador, Jugador oponente);
    
}
