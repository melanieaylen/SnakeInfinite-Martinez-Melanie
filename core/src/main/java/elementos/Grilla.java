package elementos;

import com.badlogic.gdx.graphics.Color;
import utiles.Render;

public class Grilla {
	private int tamanioCelda;
	private int cantCeldasX = 0, cantCeldasY = 0;

	public Grilla(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
		actualizarCeldas();
	}

	public void dibujarGrilla() {
		// Obtener dimensiones actuales del viewport
		float anchoViewport = Render.viewport.getWorldWidth();
		float altoViewport = Render.viewport.getWorldHeight();
		
		Render.shaper.setColor(Color.DARK_GRAY);
		
		// LÍNEAS VERTICALES
		for (float x = 0; x <= anchoViewport; x += tamanioCelda) {
			Render.shaper.line(x, 0, x, altoViewport);
		}

		// LÍNEAS HORIZONTALES
		for (float y = 0; y <= altoViewport; y += tamanioCelda) {
			Render.shaper.line(0, y, anchoViewport, y);
		}
	}
	
	// Método para actualizar la cantidad de celdas según el viewport
	public void actualizarCeldas() {
		cantCeldasX = (int)(Render.viewport.getWorldWidth() / tamanioCelda);
		cantCeldasY = (int)(Render.viewport.getWorldHeight() / tamanioCelda);
	}

	public int getTamanioCelda() {
		return tamanioCelda;
	}

	public void setTamanioCelda(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
	}

	public int getCeldasX() {
		return cantCeldasX;
	}

	public int getCeldasY() {
		return cantCeldasY;
	}
}