public class SakuretsuArmor extends Trampa {
    
    public SakuretsuArmor() {
        super("Sakuretsu Armor");
    }
    
    @Override
    public boolean condicion(Jugador jugador, Jugador oponente) {
        return true; // Se activa cuando un monstruo enemigo ataca
    }
    
    @Override
    public void activar(Jugador jugador, Jugador oponente) {
        System.out.println("Sakuretsu Armor! Destruyes al monstruo atacante");
        if (!oponente.getCampo().isEmpty()) {
            oponente.getCampo().remove(0);
        }
    }
}