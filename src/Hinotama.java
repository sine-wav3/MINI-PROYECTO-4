
public class Hinotama extends Magica {
    public Hinotama(){
        super("Hinotama");
    }

    @Override
    public void activar(Jugador jugador, Jugador oponente){
        System.out.println("Hinotama! 500 de daño directo");
        oponente.recibirDano(500);
    }
}
