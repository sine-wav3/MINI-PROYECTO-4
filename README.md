<h3>INTEGRANTES:</h3>
<h3>Daniela Franco Ibarra: 2477154</h3>
<h3>Dayan Stefany Marulanda: 2477427</h3>
<h3>Juan Alejandro Marquez: 2559853 </h3>
<br>


 Yu-Gi-Oh! 
Mini Proyecto 2 – Programación Orientada a Eventos (Java)
-


Descripción del juego
-

Este proyecto implementa una simulación simplificada de un duelo de Yu-Gi-Oh! ejecutado completamente por consola en Java 21.
Dos jugadores se enfrentan utilizando mazos de cartas compuestos por monstruos y cartas mágicas. Cada jugador inicia con 8000 puntos de vida (LP) y el objetivo es reducir los LP del oponente a 0 o hacer que pierda por quedarse sin cartas en el mazo.
El sistema modela entidades del juego como clases, aplicando los principios fundamentales de la Programación Orientada a Objetos (OOP).<br>


 Instrucciones de ejecución
 -

Requisitos:
Java JDK 21 o superior
Consola (CMD, PowerShell, terminal Linux o Mac)<br>

 Pasos para ejecutar
 -
1. Clonar el repositorio: (desde Git Bash)
git clone <https://github.com/sine-wav3/MINI-PROYECTO-1.git>

2. Acceder al proyecto:
cd MINI-PROYECTO-2

3. Compilar los archivos en un Entorno de Desarrollo Integrado (IDE) como Visual Studio.

4. Ejecutar el programa.<br>



Mecánica del juego
Inicio
-
Se ingresan los nombres de los jugadores, se genera un conjunto de cartas (monstruos y mágicas), se reparten 20 cartas a cada jugador, cada jugador roba 5 cartas iniciales y se selecciona aleatoriamente quién comienza.


 Turno de juego
 -
Cada turno sigue este flujo:
El jugador roba 1 carta
Se muestra el estado actual:
LP de ambos jugadores
Número de monstruos en campo
Se elige una acción:
1. Jugar carta
2. Atacar
3. Cambiar modo
4. Pasar turno
<br>


 Acciones disponibles:
 -
Jugar carta<br>

Monstruo: se invoca al campo<br>

Mágica: se activa inmediatamente y ejecuta su efecto<br>


 Atacar
 -
Si el oponente no tiene monstruos → ataque directo<br>

Si tiene:<br>

Se selecciona un objetivo<br>

Si ATK atacante > DEF defensor:<br>

Se destruye el monstruo<br>

Se inflige daño por la diferencia<br>


 Cambiar modo
 -
Permite alternar entre:<br>

Modo ataque<br>

Modo defensa<br>

Solo una vez por turno por monstruo<br>


 Pasar turno
 -
No se realiza ninguna acción

Condiciones de victoria
-
Un jugador gana si:<br>

El oponente llega a 0 LP<br>

El oponente intenta robar carta y su mazo está vacío<br>


Estructura del proyecto
-
 Clase abstracta Carta<br>

Atributo: nombre<br>

Base para todas las cartas<br>


 Clase Monstruo (hereda de Carta)
 -
Atributos:<br>

ATK (ataque)<br>
DEF (defensa)<br>

nivel<br>

modo (ataque/defensa)<br>

Funcionalidades:<br>

Cambio de modo (cambiarModo())<br>

Restricción de cambio por turno<br>

Modificación de estadísticas (buffs)<br>


 Interfaz Activable
 -
Define el comportamiento de cartas con efectos:<br>

void activar(Jugador jugador, Jugador oponente);<br>



 Clase Mágica
 -
Hereda de Carta<br>

Implementa Activable<br>

Representa cartas con efectos inmediatos<br>


 Cartas mágicas implementadas
 -
Carta/Efecto<br>

PotOfGreed: Roba 2 cartas<br>

Hinotama: Inflige 500 de daño directo<br>

DarkHole: Destruye todos los monstruos en el campo<br>

Raigeki: Destruye todos los monstruos del oponente<br>

BoostAtk: Aumenta ATK de un monstruo en 500<br>

StandarOfCourage: Aumenta ATK de todos tus monstruos en 200<br>

AceleronMiauravilloso: Aumenta DEF de un monstruo en 200<br>

AcesCoup: Lanza moneda: roba 2 cartas tú o el oponente<br>

ChangeOfHeart: Roba un monstruo del oponente<br>

TyphoonOfMagicalSpace: Destruye un monstruo enemigo específico<br>



 Clase Jugador
 -
Responsabilidades:<br>

Manejo de:<br>

mano<br>

campo<br>

mazo<br>

puntos de vida (LP)<br>

Métodos clave:<br>

robarCarta()<br>

jugarCarta()<br>

recibirDano()<br>

mostrarMano()<br>

mostrarCampo()<br>


 Clase Juego (main)
Controla:
-
Flujo del juego<br>

Turnos<br>

Acciones del jugador<br>

Sistema de combate<br>


Conceptos de OOP aplicados
-
 Encapsulamiento:<br>

Atributos privados en clases<br>

Acceso mediante getters y setters<br>


 Herencia
 -
Carta → Monstruo, Magica<br>


Polimorfismo
-
Uso de instanceof para distinguir tipos de carta<br>

Ejecución dinámica del método activar()<br>


 Interfaces
 -
Activable define comportamiento común para cartas mágicas<br>








