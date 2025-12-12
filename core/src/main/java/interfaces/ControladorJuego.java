package interfaces;

/**
 * Interfaz que define los mÃ©todos principales del controlador del juego
 * Similar al GameController del Pong, pero adaptado para Snake
 */
public interface ControladorJuego {
    
    /**
     * Cuando la serpiente come una fruta
     */
    void frutaComida(int puntos);
    
    /**
     * Cuando el jugador pierde una vida
     */
    void perderVida();
    
    /**
     * Cuando el juego termina (Game Over)
     */
    void finDelJuego();
    
    /**
     * Mover al jugador en una direcciÃ³n
     * @param direccion La direcciÃ³n a moverse
     */
    void moverJugador(elementos.Direcciones direccion);
    
    /**
     * Pausar el juego
     */
    void pausarJuego();
}