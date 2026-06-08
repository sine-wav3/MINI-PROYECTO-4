package modelo.persistencia;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import modelo.carta.*;
import modelo.juego.Jugador;
import modelo.efecto.BoostAtk;

public class GestorArchivos {

    private static final String ARCHIVO_PARTIDA   = "partida.txt";
    private static final String ARCHIVO_RESULTADOS = "resultados.txt";
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    public void guardarPartida(
            Jugador j1, Jugador j2,
            boolean turnoJ1, int turnoNum,
            boolean haRobado, boolean haJugado,
            boolean haAtacado, boolean primerTurno,
            Set<String> cartasUsadas) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(ARCHIVO_PARTIDA, false))) {

            bw.write("TURNO_J1=" + turnoJ1); bw.newLine();
            bw.write("TURNO_NUM=" + turnoNum); bw.newLine();
            bw.write("HA_ROBADO=" + haRobado); bw.newLine();
            bw.write("HA_JUGADO=" + haJugado); bw.newLine();
            bw.write("HA_ATACADO=" + haAtacado); bw.newLine();
            bw.write("PRIMER_TURNO=" + primerTurno); bw.newLine();
            bw.write("CARTAS_USADAS=" + String.join(",", cartasUsadas)); bw.newLine();
            bw.newLine();

            escribirJugador(bw, "J1", j1);
            bw.newLine();
            escribirJugador(bw, "J2", j2);
        }
    }

    private void escribirJugador(BufferedWriter bw, String id, Jugador j)
            throws IOException {

        bw.write("JUGADOR=" + id); bw.newLine();
        bw.write("NOMBRE=" + j.getNombre()); bw.newLine();
        bw.write("LP=" + j.getLp()); bw.newLine();
        bw.write("MANO=" + serializarLista(j.getMano())); bw.newLine();
        bw.write("CAMPO=" + serializarMonstruos(j.getCampo())); bw.newLine();

        List<Carta> mazoLista = new ArrayList<>(j.getMazo());
        bw.write("MAZO=" + serializarLista(mazoLista)); bw.newLine();
    }

    private String serializarLista(List<Carta> cartas) {
        if (cartas.isEmpty()) return "";
        StringJoiner sj = new StringJoiner(" ; ");
        for (Carta c : cartas) sj.add(serializarCarta(c));
        return sj.toString();
    }

    private String serializarMonstruos(List<Monstruo> monstruos) {
        if (monstruos.isEmpty()) return "";
        StringJoiner sj = new StringJoiner(" ; ");
        for (Monstruo m : monstruos) sj.add(serializarCarta(m));
        return sj.toString();
    }

    private String serializarCarta(Carta c) {
        if (c instanceof Monstruo m) {
            return "M|" + m.getNombre() + "|" + m.getAtk() + "|" +
                   m.getDef() + "|" + m.getNivel() + "|" +
                   (m.isEnAtaque() ? "ATK" : "DEF") + "|" +
                   !m.puedeCambiarModo();
        } else {
            return "S|" + c.getNombre();
        }
    }

    public EstadoCargado cargarPartida() throws IOException {
        File f = new File(ARCHIVO_PARTIDA);
        if (!f.exists()) return null;

        Map<String, String> encabezado = new LinkedHashMap<>();
        Map<String, String> bloqueJ1   = new LinkedHashMap<>();
        Map<String, String> bloqueJ2   = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            Map<String, String> destino = encabezado;
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty()) continue;
                if (linea.equals("JUGADOR=J1")) { destino = bloqueJ1; continue; }
                if (linea.equals("JUGADOR=J2")) { destino = bloqueJ2; continue; }
                int eq = linea.indexOf('=');
                if (eq < 0) continue;
                destino.put(linea.substring(0, eq), linea.substring(eq + 1));
            }
        }

        EstadoCargado e = new EstadoCargado();
        e.turnoJ1      = Boolean.parseBoolean(encabezado.get("TURNO_J1"));
        e.turnoNum     = Integer.parseInt(encabezado.getOrDefault("TURNO_NUM", "0"));
        e.haRobado     = Boolean.parseBoolean(encabezado.get("HA_ROBADO"));
        e.haJugado     = Boolean.parseBoolean(encabezado.get("HA_JUGADO"));
        e.haAtacado    = Boolean.parseBoolean(encabezado.get("HA_ATACADO"));
        e.primerTurno  = Boolean.parseBoolean(encabezado.get("PRIMER_TURNO"));

        String cartasStr = encabezado.getOrDefault("CARTAS_USADAS", "");
        e.cartasUsadas = new HashSet<>();
        if (!cartasStr.isBlank()) {
            e.cartasUsadas.addAll(Arrays.asList(cartasStr.split(",")));
        }

        e.nombreJ1 = bloqueJ1.get("NOMBRE");
        e.lpJ1     = Integer.parseInt(bloqueJ1.getOrDefault("LP", "8000"));
        e.manoJ1   = deserializarLista(bloqueJ1.getOrDefault("MANO", ""));
        e.campoJ1  = deserializarMonstruos(bloqueJ1.getOrDefault("CAMPO", ""));
        e.mazoJ1   = deserializarStack(bloqueJ1.getOrDefault("MAZO", ""));

        e.nombreJ2 = bloqueJ2.get("NOMBRE");
        e.lpJ2     = Integer.parseInt(bloqueJ2.getOrDefault("LP", "8000"));
        e.manoJ2   = deserializarLista(bloqueJ2.getOrDefault("MANO", ""));
        e.campoJ2  = deserializarMonstruos(bloqueJ2.getOrDefault("CAMPO", ""));
        e.mazoJ2   = deserializarStack(bloqueJ2.getOrDefault("MAZO", ""));

        return e;
    }

    private List<Carta> deserializarLista(String texto) {
        List<Carta> lista = new ArrayList<>();
        if (texto.isBlank()) return lista;
        for (String token : texto.split(" ; ")) {
            Carta c = deserializarCarta(token.trim());
            if (c != null) lista.add(c);
        }
        return lista;
    }

    private List<Monstruo> deserializarMonstruos(String texto) {
        List<Monstruo> lista = new ArrayList<>();
        if (texto.isBlank()) return lista;
        for (String token : texto.split(" ; ")) {
            Carta c = deserializarCarta(token.trim());
            if (c instanceof Monstruo m) lista.add(m);
        }
        return lista;
    }

    private Stack<Carta> deserializarStack(String texto) {
        Stack<Carta> stack = new Stack<>();
        if (texto.isBlank()) return stack;
        List<Carta> lista = deserializarLista(texto);
        for (int i = lista.size() - 1; i >= 0; i--) {
            stack.push(lista.get(i));
        }
        return stack;
    }

    private Carta deserializarCarta(String token) {
        if (token.isBlank()) return null;
        String[] partes = token.split("\\|");
        if (partes.length < 2) return null;

        String tipo   = partes[0];
        String nombre = partes[1];

        if (tipo.equals("M") && partes.length >= 7) {
            int atk    = Integer.parseInt(partes[2]);
            int def    = Integer.parseInt(partes[3]);
            int nivel  = Integer.parseInt(partes[4]);
            boolean enAtaque = partes[5].equals("ATK");
            boolean cambio   = Boolean.parseBoolean(partes[6]);

            Monstruo m = new Monstruo(nombre, atk, def, nivel);
            if (!enAtaque) m.cambiarModo();
            if (cambio)    m.marcarCambio();
            return m;
        }

        if (tipo.equals("S")) {
            return crearCartaNoMonstruo(nombre);
        }

        return null;
    }

    private Carta crearCartaNoMonstruo(String nombre) {
        return switch (nombre) {
            case "Pot of Greed"              -> new modelo.carta.PotOfGreed();
            case "Boost ATK +500"            -> new BoostAtk();
            case "Dark hole"                 -> new modelo.carta.DarkHole();
            case "Hinotama"                  -> new modelo.carta.Hinotama();
            case "Change of Heart"           -> new modelo.carta.ChangeOfHeart();
            case "Standar Of Courage"        -> new modelo.carta.StandarOfCourage();
            case "Typhoon Of Magical Space"  -> new modelo.carta.TyphoonOfMagicalSpace();
            case "Raigeki"                   -> new modelo.carta.Raigeki();
            case "Aces Coup"                 -> new modelo.carta.AcesCoup();
            case "Aceleron Miauravilloso"    -> new modelo.carta.AceleronMiauravilloso();
            case "Mirror Force"              -> new modelo.carta.MirrorForce();
            case "Sakuretsu Armor"           -> new modelo.carta.SakuretsuArmor();
            default                          -> null;
        };
    }

    // -------------------------------------------------------
    // HISTORIAL DE RESULTADOS
    // -------------------------------------------------------

    public void registrarResultado(
            String nombreJ1, String nombreJ2,
            String ganador, int turnos,
            int lpJ1, int lpJ2) throws IOException {

        try (BufferedWriter bw = new BufferedWriter(
                new FileWriter(ARCHIVO_RESULTADOS, true))) {

            String linea = LocalDateTime.now().format(FMT) +
                    " | J1=" + nombreJ1 +
                    " | J2=" + nombreJ2 +
                    " | GANADOR=" + ganador +
                    " | TURNOS=" + turnos +
                    " | LP_J1=" + lpJ1 +
                    " | LP_J2=" + lpJ2;

            bw.write(linea);
            bw.newLine();
        }
    }

    // -------------------------------------------------------
    // ESTADÍSTICAS
    // -------------------------------------------------------

    public String calcularEstadisticas() throws IOException {
        File f = new File(ARCHIVO_RESULTADOS);
        if (!f.exists()) return "No hay partidas registradas aún.";

        Map<String, Integer> victorias = new LinkedHashMap<>();
        Map<String, Integer> partidas  = new LinkedHashMap<>();
        int totalPartidas = 0;
        int turnosMax = 0;
        String partidaMasLarga = "";

        try (BufferedReader br = new BufferedReader(new FileReader(f))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isBlank()) continue;

                Map<String, String> campos = parsearLinea(linea);
                if (campos == null) continue;

                String j1      = campos.get("J1");
                String j2      = campos.get("J2");
                String ganador = campos.get("GANADOR");
                int turnos     = Integer.parseInt(campos.getOrDefault("TURNOS", "0"));
                String fecha   = campos.getOrDefault("FECHA", "");

                partidas.merge(j1, 1, Integer::sum);
                partidas.merge(j2, 1, Integer::sum);
                victorias.merge(ganador, 1, Integer::sum);

                if (turnos > turnosMax) {
                    turnosMax = turnos;
                    partidaMasLarga = fecha + " — " + j1 + " vs " + j2 +
                                      " (" + turnos + " turnos)";
                }
                totalPartidas++;
            }
        }

        StringBuilder sb = new StringBuilder();
        sb.append("=== ESTADÍSTICAS HISTÓRICAS ===\n");
        sb.append("Total de duelos registrados: ").append(totalPartidas).append("\n\n");
        sb.append("Victorias por duelista:\n");

        List<Map.Entry<String, Integer>> ranking = new ArrayList<>(victorias.entrySet());
        ranking.sort((a, b) -> b.getValue() - a.getValue());
        for (Map.Entry<String, Integer> e : ranking) {
            int p = partidas.getOrDefault(e.getKey(), 1);
            double pct = (e.getValue() * 100.0) / p;
            sb.append(String.format("  %-20s %2d victorias  (%.0f%% de sus duelos)\n",
                    e.getKey(), e.getValue(), pct));
        }

        sb.append("\nPartida más larga:\n  ").append(
                partidaMasLarga.isEmpty() ? "N/A" : partidaMasLarga).append("\n");

        return sb.toString();
    }

    private Map<String, String> parsearLinea(String linea) {
        Map<String, String> m = new LinkedHashMap<>();
        String[] partes = linea.split("\\|");
        if (partes.length < 7) return null;

        m.put("FECHA", partes[0].trim());
        for (int i = 1; i < partes.length; i++) {
            String parte = partes[i].trim();
            int eq = parte.indexOf('=');
            if (eq < 0) continue;
            m.put(parte.substring(0, eq).trim(), parte.substring(eq + 1).trim());
        }
        return m;
    }

    // -------------------------------------------------------
    // UTILIDAD
    // -------------------------------------------------------

    public boolean existePartidaGuardada() {
        return new File(ARCHIVO_PARTIDA).exists();
    }
}
