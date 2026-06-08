package modelo.juego;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import modelo.carta.Carta;
import modelo.carta.Monstruo;

public class Jugador {

    private String nombre;
    private int lp = 8000;

    // LinkedList para cumplir estructura requerida
    private LinkedList<Carta> mano = new LinkedList<>();

    private List<Monstruo> campo = new LinkedList<>();

    // Stack para el mazo
    private Stack<Carta> mazo;

    public Jugador(String nombre, Stack<Carta> mazo) {
        this.nombre = nombre;
        this.mazo = mazo;
    }

    public void robarCarta() {
        if (mazo.isEmpty()) return;

        mano.add(mazo.pop());
    }

    public void recibirDano(int dano) {
        lp -= dano;

        if (lp < 0)
            lp = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getLp() {
        return lp;
    }

    public List<Carta> getMano() {
        return mano;
    }

    public List<Monstruo> getCampo() {
        return campo;
    }

    public Stack<Carta> getMazo() {
        return mazo;
    }

    public boolean isMazoVacio() {
        return mazo.isEmpty();
    }
    
    public void setLp(int lp) {
        this.lp = lp;
    }

    public void setMano(List<Carta> mano) {
        this.mano.clear();
        this.mano.addAll(mano);
    }

    public void setCampo(List<Monstruo> campo) {
        this.campo.clear();
        this.campo.addAll(campo);
    }
}