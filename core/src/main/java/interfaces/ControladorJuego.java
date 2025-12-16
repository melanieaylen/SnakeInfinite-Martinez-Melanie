package interfaces;

public interface ControladorJuego {
    
    //Cuando la serpiente come una fruta
    void frutaComida(int puntos);
    
    //Cuando el jugador pierde una vida
    void perderVida();
    
    //Game over
    void finDelJuego();
    
    //mover jugador
    void moverJugador(elementos.Direcciones direccion);
    
    //pausar juego
    void pausarJuego();
}