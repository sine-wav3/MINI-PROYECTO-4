public class Monstruo extends Carta {

    private int atk;
    private int def;
    private int nivel;
    private boolean enAtaque = true;
    private boolean cambioEsteTurno = false;

    public Monstruo(String nombre, int atk, int def, int nivel) {
        super(nombre);
        this.atk = atk;
        this.def = def;
        this.nivel = nivel;
    }

    public int getAtk() {
        return atk;
    }

    public int getDef() {
        return def;
    }

    public int getNivel() {
        return nivel;
    }

    public boolean isEnAtaque() {
        return enAtaque;
    }

    public boolean puedeCambiarModo() {
        return !cambioEsteTurno;
    }

    public void setAtk(int atk) {
        this.atk = atk;
    }

    public void setDef(int def){
        this.def = def;
    }

    public void cambiarModo() {
        if (!puedeCambiarModo()) {
            System.out.println("Ya cambiaste el modo este turno");
        return;
    }
    
    enAtaque = !enAtaque;
    marcarCambio();
    System.out.println(getNombre() + " ahora está en " + (enAtaque ? "ataque" : "defensa"));
    }

    public void marcarCambio() {
        cambioEsteTurno = true;
    }
    
    public void resetCambio() {
        cambioEsteTurno = false;
    }
}