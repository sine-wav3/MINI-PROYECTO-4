
public class DarkHole extends Magica {
    public DarkHole(){
        super("Dark hole");
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente){
        jugador.getCampo().clear();
        oponente.getCampo().clear();
    }
}
