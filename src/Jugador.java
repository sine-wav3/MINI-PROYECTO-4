import java.util.*;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Jugador {
    private String nombre;
    private int lp = 8000;
    private List<Carta> mano = new ArrayList<>();
    private List<Monstruo> campo = new ArrayList<>();
    private Stack<Carta> mazo;

    public Jugador(String nombre, Stack<Carta> mazo) {
        this.nombre = nombre;
        this.mazo = mazo;
    }

    public void robarCarta() {
        if (mazo.isEmpty()) {
            System.out.println(nombre + " pierde por quedarse sin cartas en el mazo");
            return;
        }
        Carta carta = mazo.pop();
        mano.add(carta);
    }
    //aqui ya vienen varios cambios para el sistema de sacrificio, se agregan ventanas emergentes para elegir los monstruos a sacrificar
    public void jugarCarta(int index, Jugador oponente, JFrame parent) {
        if (index < 0 || index >= mano.size()) {
            return;
        }

        Carta carta = mano.remove(index);

        if (carta instanceof Monstruo) {
            Monstruo m = (Monstruo) carta;
            
            // Sistema de sacrificio de nivel >= 4 como requiere el documento del profe
            if (m.getNivel() >= 4) {
                int necesarios = m.getNivel() >= 7 ? 2 : 1;
                if (campo.size() < necesarios) {
                    JOptionPane.showMessageDialog(parent, 
                        "Necesitas " + necesarios + " sacrificio(s) para invocar " + m.getNombre(),
                        "Sacrificio requerido",
                        JOptionPane.WARNING_MESSAGE);
                    mano.add(index, carta);
                    return;
                }
                
                // se eligen que mostruos se sacrifican y asi
                StringBuilder msg = new StringBuilder("Elige " + necesarios + " monstruo(s) para sacrificar:\n");
                for (int i = 0; i < campo.size(); i++) {
                    Monstruo mon = campo.get(i);
                    msg.append(i).append(". ").append(mon.getNombre())
                       .append(" [").append(mon.isEnAtaque() ? "ataque" : "defensa").append("]\n");
                }
                
                String input = JOptionPane.showInputDialog(parent, msg.toString(), "Sacrificio", JOptionPane.QUESTION_MESSAGE);
                if (input != null) {
                    try {
                        String[] indices = input.split(",");
                        List<Monstruo> sacrificados = new ArrayList<>();
                        for (String idxStr : indices) {
                            int idx = Integer.parseInt(idxStr.trim());
                            if (idx >= 0 && idx < campo.size()) {
                                sacrificados.add(campo.remove(idx));
                            }
                        }
                        if (sacrificados.size() == necesarios) {
                            StringBuilder sacNames = new StringBuilder();
                            for (Monstruo sac : sacrificados) {
                                sacNames.append(sac.getNombre()).append(" ");
                            }
                            JOptionPane.showMessageDialog(parent, "Sacrificaste: " + sacNames.toString(), "Sacrificio", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(parent, "Sacrificio incorrecto, la carta vuelve a la mano", "Error", JOptionPane.ERROR_MESSAGE);
                            mano.add(index, carta);
                            return;
                        }
                    } catch (Exception e) {
                        mano.add(index, carta);
                        return;
                    }
                } else {
                    mano.add(index, carta);
                    return;
                }
            }
            
            campo.add(m);
            JOptionPane.showMessageDialog(parent, nombre + " invoca " + carta.getNombre(), "Invocación", JOptionPane.INFORMATION_MESSAGE);
            
        } else if (carta instanceof Activable) {
            ((Activable) carta).activar(this, oponente);
            JOptionPane.showMessageDialog(parent, nombre + " activa " + carta.getNombre(), "Carta Activada", JOptionPane.INFORMATION_MESSAGE);
        }
    } //perdonen que quede tan largo fue la manera en la que se me ocurrio D:

    public void recibirDano(int dano) {
        lp -= dano;
        if (lp < 0) lp = 0;
    }

    public String getNombre() { return nombre; }
    public int getLp() { return lp; }
    public List<Monstruo> getCampo() { return campo; }
    public List<Carta> getMano() { return mano; }
    public Stack<Carta> getMazo() { return mazo; }
    public boolean isMazoVacio() { return mazo.isEmpty(); }
    
    public void mostrarMano() {
        System.out.println("Mano de " + nombre + ":");
        for (int i = 0; i < mano.size(); i++) {
            System.out.println(i + ". " + mano.get(i).getNombre());
        }
    }
    
    public void mostrarCampo() {
        for (int i = 0; i < campo.size(); i++) {
            Monstruo m = campo.get(i);
            System.out.println(i + ". " + m.getNombre() + " [" + (m.isEnAtaque() ? "ataque" : "defensa") + "]");
        }
    }
}