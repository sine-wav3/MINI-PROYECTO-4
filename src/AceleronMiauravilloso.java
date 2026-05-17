
public class AceleronMiauravilloso extends Magica{

    public AceleronMiauravilloso(){
        super("Aceleron Miauravilloso");
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente){
        if(!jugador.getCampo().isEmpty()){
            Monstruo m = jugador.getCampo().get(0);
            System.out.println("DEF de" + m.getNombre() + "aumento de 200");
            m.setDef(m.getDef() + 200);
        } else {
            System.out.println("No tienes monstruos para aumentar su defensa");
        }
    }
    
}