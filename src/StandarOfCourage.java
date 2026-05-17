
public class StandarOfCourage extends Magica {
    
    public StandarOfCourage(){
        super("Standar Of Courage");
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente){
        if(!jugador.getCampo().isEmpty()){
            for (Monstruo m : jugador.getCampo()) {
            m.setAtk(m.getAtk() + 200);
        }
        } else {
            System.out.println("No tienes monstruos para aumentar su ataque");
        }
    }
    
    
    
}
