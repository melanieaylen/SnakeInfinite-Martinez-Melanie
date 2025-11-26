package elementos;

import pantallas.Direcciones;

public class EstadoJuego {
	private Serpiente serpiente;
	private GestorFrutas gestorFrutas;
	private int puntuacion;
	private float posElementosX;
	private float posElementosY;
	private Direcciones direccionActual;
	private float tiempo;

	public EstadoJuego(Serpiente serpiente, GestorFrutas gestorFrutas, int puntuacion, float posX, float posY,
			Direcciones direccion, float tiempo) {
		this.serpiente = serpiente;
		this.gestorFrutas = gestorFrutas;
		this.puntuacion = puntuacion;
		this.posElementosX = posX;
		this.posElementosY = posY;
		this.direccionActual = direccion;
		this.tiempo = tiempo;
	}

	// Getters
	public Serpiente getSerpiente() {
		return serpiente;
	}

	public GestorFrutas getGestorFrutas() {
		return gestorFrutas;
	}

	public int getPuntuacion() {
		return puntuacion;
	}

	public float getPosElementosX() {
		return posElementosX;
	}

	public float getPosElementosY() {
		return posElementosY;
	}

	public Direcciones getDireccionActual() {
		return direccionActual;
	}

	public float getTiempo() {
		return tiempo;
	}
}
