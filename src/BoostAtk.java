
public class BoostAtk extends Magica{

    public BoostAtk(){
        super("Boost ATK +500");
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente){
        if(!jugador.getCampo().isEmpty()){
            Monstruo m = jugador.getCampo().get(0);
            System.out.println("ATK de" + m.getNombre() + "aumento de 500");
            m.setAtk(m.getAtk() + 500);
        } else {
            System.out.println("No tienes monstruos para aumentar su ataque");
        }
    }
    
}
