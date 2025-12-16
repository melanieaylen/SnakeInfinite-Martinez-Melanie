package jugadores;

import elementos.Direcciones;
import elementos.Serpiente;

public class Jugador {
    
    private int id;
    private String nombre;
    private int puntuacion;
    private int vidas;
    private Serpiente serpiente;
    private Direcciones direccionActual;

    private boolean invulnerable;
    private float tiempoInvulnerabilidad;
    private static final float DURACION_INVULNERABILIDAD = 1.5f;
    
    public Jugador(int id, String nombre, Serpiente serpiente) {
        this.id = id;
        this.nombre = nombre;
        this.serpiente = serpiente;
        this.puntuacion = 0;
        this.vidas = 3;
        this.direccionActual = Direcciones.NINGUNA;
        this.invulnerable = false;
        this.tiempoInvulnerabilidad = 0;
    }
    

    public void agregarPuntos(int puntos) {
        this.puntuacion += puntos;
    }

    public void crecerSerpiente() {
        serpiente.crecer();
    }
    
    public boolean perderVida() {
        vidas--;
        if (vidas > 0) {
            activarInvulnerabilidad();
            return true;
        }
        return false;
    }
    
    public void activarInvulnerabilidad() {
        invulnerable = true;
        tiempoInvulnerabilidad = 0;
    }
    
    public void actualizarInvulnerabilidad(float delta) {
        if (invulnerable) {
            tiempoInvulnerabilidad += delta;
            if (tiempoInvulnerabilidad >= DURACION_INVULNERABILIDAD) {
                invulnerable = false;
                tiempoInvulnerabilidad = 0;
            }
        }
    }
    
    public void resetearPosicion(float posX, float posY, int ancho, int alto) {
        this.serpiente = new Serpiente(posX, posY, ancho, alto);
        this.direccionActual = Direcciones.NINGUNA;
    }

    public void cambiarDireccion(Direcciones nuevaDireccion) {
        // No permitir direcciÃ³n opuesta
        if (nuevaDireccion == Direcciones.ARRIBA && direccionActual != Direcciones.ABAJO) {
            direccionActual = nuevaDireccion;
        } else if (nuevaDireccion == Direcciones.ABAJO && direccionActual != Direcciones.ARRIBA) {
            direccionActual = nuevaDireccion;
        } else if (nuevaDireccion == Direcciones.DERECHA && direccionActual != Direcciones.IZQUIERDA) {
            direccionActual = nuevaDireccion;
        } else if (nuevaDireccion == Direcciones.IZQUIERDA && direccionActual != Direcciones.DERECHA) {
            direccionActual = nuevaDireccion;
        }
    }
    
    // Getters
    public int getId() {
        return id;
    }
    
    public String getNombre() {
        return nombre;
    }
    
    public int getPuntuacion() {
        return puntuacion;
    }
    
    public int getVidas() {
        return vidas;
    }
    
    public Serpiente getSerpiente() {
        return serpiente;
    }
    
    public Direcciones getDireccionActual() {
        return direccionActual;
    }
    
    public boolean isInvulnerable() {
        return invulnerable;
    }
    
    public float getTiempoInvulnerabilidad() {
        return tiempoInvulnerabilidad;
    }
    
    // Setters
    public void setSerpiente(Serpiente serpiente) {
        this.serpiente = serpiente;
    }
    
    public void setDireccionActual(Direcciones direccion) {
        this.direccionActual = direccion;
    }
    
    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
    
    public void setVidas(int vidas) {
        this.vidas = vidas;
    }
}