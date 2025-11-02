package mi.juego.snake;

import elementos.Imagen;
import utiles.Recursos;

public class Serpiente {
	private Imagen serpiente; 
	
	public Serpiente(float x, float y, float ancho, float alto) {
		serpiente = new Imagen(Recursos.ICONO);
		serpiente.setParametros(x, y, ancho, alto);
	}
	
	public void dibujar() {
		serpiente.dibujar();
	}
}
