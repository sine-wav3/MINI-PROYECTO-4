package modelo.persistencia;

import java.util.List;
import java.util.Set;
import java.util.Stack;

import modelo.carta.Carta;
import modelo.carta.Monstruo;

public class EstadoCargado {

    // package-private para que GestorArchivos (mismo paquete) los asigne directamente
    boolean turnoJ1;
    int turnoNum;
    boolean haRobado;
    boolean haJugado;
    boolean haAtacado;
    boolean primerTurno;
    Set<String> cartasUsadas;

    String nombreJ1;
    int lpJ1;
    List<Carta> manoJ1;
    List<Monstruo> campoJ1;
    Stack<Carta> mazoJ1;

    String nombreJ2;
    int lpJ2;
    List<Carta> manoJ2;
    List<Monstruo> campoJ2;
    Stack<Carta> mazoJ2;

    EstadoCargado() {}

    public int getLpJ1() { return lpJ1; }
    public int getLpJ2() { return lpJ2; }
    public boolean isTurnoJ1() { return turnoJ1; }
    public List<Carta> getManoJ1() { return manoJ1; }
    public List<Carta> getManoJ2() { return manoJ2; }
    public List<Monstruo> getCampoJ1() { return campoJ1; }
    public List<Monstruo> getCampoJ2() { return campoJ2; }
    public int getTurnosJugados() { return turnoNum; }

    public boolean isHaRobado() { return haRobado; }
    public boolean isHaJugado() { return haJugado; }
    public boolean isHaAtacado() { return haAtacado; }
    public boolean isPrimerTurno() { return primerTurno; }
    public Set<String> getCartasUsadas() { return cartasUsadas; }

    public String getNombreJ1() { return nombreJ1; }
    public String getNombreJ2() { return nombreJ2; }
    public Stack<Carta> getMazoJ1() { return mazoJ1; }
    public Stack<Carta> getMazoJ2() { return mazoJ2; }
}
