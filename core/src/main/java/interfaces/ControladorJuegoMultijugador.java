package interfaces;

import elementos.Direcciones;

/**
 * Interfaz para el controlador del juego multijugador (lado cliente)
 * Similar a ControladorJuego del Pong pero adaptado para Snake
 */
public interface ControladorJuegoMultijugador {
    
    /**
     * Llamado cuando el cliente se conecta exitosamente
     * @param numeroJugador Número del jugador (1 o 2)
     */
    void conectado(int numeroJugador);
    
    /**
     * Llamado cuando el servidor inicia el juego
     */
    void iniciarJuego();
    
    /**
     * Actualiza la posición de una serpiente
     * @param numeroJugador Número del jugador
     * @param x Posición X de la cabeza
     * @param y Posición Y de la cabeza
     */
    void actualizarPosicionSerpiente(int numeroJugador, float x, float y);
    
    /**
     * Actualiza las posiciones de las frutas
     * @param datosFrutas Datos de frutas en formato "TIPO:X:Y|TIPO:X:Y|..."
     */
    void actualizarFrutas(String datosFrutas);
    
    /**
     * Notifica que un jugador comió una fruta
     * @param numeroJugador Número del jugador
     * @param puntos Puntos obtenidos
     */
    void jugadorComio(int numeroJugador, int puntos);
    
    /**
     * Notifica que un jugador perdió una vida
     * @param numeroJugador Número del jugador
     * @param vidasRestantes Vidas que le quedan
     */
    void jugadorMurio(int numeroJugador, int vidasRestantes);
    
    /**
     * Actualiza la puntuación de los jugadores
     * @param datosPuntuacion Datos de puntuación en formato "PUNTAJE1:PUNTAJE2"
     */
    void actualizarPuntuacion(String datosPuntuacion);
    
    /**
     * Notifica el fin del juego
     * @param ganador Número del jugador ganador (0 = empate)
     */
    void finDelJuego(int ganador);
    
    /**
     * Notifica que un jugador se desconectó
     * @param numeroJugador Número del jugador desconectado
     */
    void jugadorDesconectado(int numeroJugador);
    
    /**
     * Volver al menú principal
     */
    void volverAlMenu();
}