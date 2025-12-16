package interfaces;

public interface ControladorJuego {

    void frutaComida(int puntos);

    void perderVida();

    void finDelJuego();

    void moverJugador(elementos.Direcciones direccion);

    void pausarJuego();
}