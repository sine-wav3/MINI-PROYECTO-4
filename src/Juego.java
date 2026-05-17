import java.util.*;

public class Juego {
   
    public static void mostrarEstado(Jugador j1, Jugador j2){
        System.out.println("\n Estado ");
        System.out.println(j1.getNombre() + " Lp " + j1.getLp());
        System.out.println("Monstruos: " + j1.getCampo().size());

        System.out.println("===============================");

        System.out.println(j2.getNombre() + " Lp " + j2.getLp());
        System.out.println("Monstruos: " + j2.getCampo().size());
        System.out.println("===============================");
    }

    public static void faseAtaque(Jugador atacante, Jugador defensor, Scanner sc){

        if (atacante.getCampo().isEmpty()) {
            System.out.println("No tienes monstruos para atacar");
            return;
        }

        System.out.println("Elige un monstruo para atacar:");
        for (int i = 0; i < atacante.getCampo().size(); i++){
            Monstruo m = atacante.getCampo().get(i);
            System.out.println(i + ". " + m.getNombre() + " ATK: " + m.getAtk());
        }

        int a = sc.nextInt();
        Monstruo atk = atacante.getCampo().get(a);

        if (defensor.getCampo().isEmpty()){
            System.out.println("ataque directo");
            defensor.recibirDano(atk.getAtk());
        } else {
            System.out.println("elige enemigo: ");
            for (int i = 0; i < defensor.getCampo().size(); i++){
                Monstruo m = defensor.getCampo().get(i);
                System.out.println(i + ". " + m.getNombre() + " ATK: " + m.getAtk());
            }

            int d = sc.nextInt();
            Monstruo def = defensor.getCampo().get(d);

            if (atk.getAtk() > def.getDef()){
                int diff = atk.getAtk() - def.getDef();
                defensor.getCampo().remove(d);
                defensor.recibirDano(diff);
                System.out.println("Monstruo eliminado");
            }else {
                System.out.println("fallaste :(");
            }
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean cartaJugadaEsteTurno = false; //se agrega esto para limitar que se juegue una sola carta por turno
    
        System.out.println( "Jugador 1 nombre polfas: ");
        String n1 = sc.nextLine();
        
        System.out.println("Jugador 2 nombre polfas: ");
        String n2 = sc.nextLine();

        List<Carta> pool = new ArrayList<>();
        //////////////////////////////////// se modifica esto para agrgar mas cartas y cositas (lo separe en esto para ver si me marca algun error)
        String[] nombresMonstruos = {
        "Mago Oscuro", "Dragón Blanco", "Exodia", "Blue-Eyes", "Red-Eyes",
        "Curse of Dragon", "Gaia", "Summoned Skull", "Celtic Guardian", "Dark Magician Girl",
        "Jinzo", "Insect Queen", "Morphing Jar", "Sangan", "Witch of the Black Forest",
        "Kuriboh", "Man-Eater Bug", "Cyber Dragon", "Elemental HERO", "Neo-Spacian",
        "Gravekeeper's", "Vampire Lord", "Zombie Master", "Ryko", "Lyla",
        "Judgment Dragon", "Celestia", "Wulf", "Ehren", "Gorz"
        };

        for (int i = 0; i < 30; i++) {
            int nivel = 1 + (i % 12); // niveles 1-12
            int atk = 500 + (i * 100);
            int def = 500 + (i * 80);
            pool.add(new Monstruo(nombresMonstruos[i % nombresMonstruos.length] + " " + (i+1), atk, def, nivel));
        }

            // 10 cartas mágicas (las que ya estan creadas y al menos 2 distintas repetidas)
            pool.add(new PotOfGreed());
            pool.add(new PotOfGreed()); // máx 2
            pool.add(new BoostAtk());
            pool.add(new AcesCoup());
            pool.add(new DarkHole());
            pool.add(new Hinotama());
            pool.add(new ChangeOfHeart());
            pool.add(new Raigeki());
            pool.add(new StandarOfCourage());
            pool.add(new TyphoonOfMagicalSpace());

        // 10 cartas trampa (se crean al menos 10 y 2 repetidas)
        for (int i = 0; i < 5; i++) pool.add(new MirrorForce());
        for (int i = 0; i < 5; i++) pool.add(new SakuretsuArmor());

        Collections.shuffle(pool);

    // Repartir 25 y 25
        Stack<Carta> mazo1 = new Stack<>();
        Stack<Carta> mazo2 = new Stack<>();

        for (int i = 0; i < 25; i++) {
            mazo1.push(pool.remove(0));
            mazo2.push(pool.remove(0));
        }
        //////////////////
        Jugador j1 = new Jugador(n1, mazo1);
        Jugador j2 = new Jugador(n2, mazo2);

        for (int i = 0; i < 5; i++){
            j1.robarCarta();
            j2.robarCarta();
        }

        boolean turno = new Random().nextBoolean();

        while (true) { 
            Jugador actual = turno ? j1 : j2;
            Jugador oponente = turno ? j2 : j1;

            for (Monstruo m : actual.getCampo()) {
                m.resetCambio();
            }

            System.out.println("\nTurno de " + actual.getNombre());
            actual.robarCarta();

            mostrarEstado(j1, j2);

            System.out.println("1. Jugar carta");
            System.out.println("2. Atacar");
            System.out.println("3. Cambiar modo");
            System.out.println("4. Pasar");

            int op = sc.nextInt();

            switch (op){
                case 1 -> {
                    if (cartaJugadaEsteTurno) { //se agrega esto para limitar que se juegue una sola carta por turno
                        System.out.println("Ya jugaste una carta este turno!");
                    break;
                    }
                    actual.mostrarMano();
                    System.out.println("Elije una carta:");
                    int idx = sc.nextInt();
                    actual.jugarCarta(idx, oponente, null);
                    cartaJugadaEsteTurno = true; //esto es parte de la modificacion para limitar a una carta por turno

                }

                case 2 -> faseAtaque(actual, oponente, sc);

                case 3 -> {
                    if (actual.getCampo().isEmpty()) {
                        System.out.println("No tienes monstruos");
                        break;
                    }
                    
                    actual.mostrarCampo();
                    System.out.println("Elige monstruo:");
                    int idx = sc.nextInt();
                    Monstruo m = actual.getCampo().get(idx);
                    m.cambiarModo();
                }
                
                case 4 -> System.out.println("pasaste tu turno");
            }

            if (oponente.getLp() <= 0){
                System.out.println(actual.getNombre() + " gana!");
                break;
            }

            turno = !turno;
        }

    }
}
