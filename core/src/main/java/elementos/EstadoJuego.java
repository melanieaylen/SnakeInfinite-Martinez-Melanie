package elementos;

import jugadores.Jugador;

/**
 * Estado del juego para sistema de pausa
 * Ahora usa la clase Jugador en vez de manejar todo directamente
 */
public class EstadoJuego {
    
    private Jugador jugador;
    private GestorFrutas gestorFrutas;
    private float posElementosX;
    private float posElementosY;
    private float tiempo;
    
    public EstadoJuego(Jugador jugador, GestorFrutas gestorFrutas, 
                       float posElementosX, float posElementosY, float tiempo) {
        this.jugador = jugador;
        this.gestorFrutas = gestorFrutas;
        this.posElementosX = posElementosX;
        this.posElementosY = posElementosY;
        this.tiempo = tiempo;
    }
    
    // Getters
    public Jugador getJugador() {
        return jugador;
    }
    
    public GestorFrutas getGestorFrutas() {
        return gestorFrutas;
    }
    
    public float getPosElementosX() {
        return posElementosX;
    }
    
    public float getPosElementosY() {
        return posElementosY;
    }
    
    public float getTiempo() {
        return tiempo;
    }
}