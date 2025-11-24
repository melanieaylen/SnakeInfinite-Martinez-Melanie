
package elementos;

import com.badlogic.gdx.graphics.Color;

import utiles.Config;
import utiles.Recursos;
import utiles.Render;

public class Grilla {
	private int tamanioCelda;
	private int cantCeldasX = 0, cantCeldasY = 0; 
	
	public Grilla(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
		//CANTIDAD DE CELDAS DE LA GRILLA 
		cantCeldasX = Config.ANCHO / tamanioCelda; 
		cantCeldasY = Config.ALTO / tamanioCelda; 
	}
	
	public void dibujarGrilla() {
		Render.shaper.setColor(Color.DARK_GRAY);
		// VERTICAL 
		for (int x = 0; x <= Config.ANCHO; x += tamanioCelda) {
			Render.shaper.line(x, 0, x, Config.ALTO);
		}
		
		// HORIZONTAL
		for (int y = 0; y <= Config.ALTO; y += tamanioCelda) {
			Render.shaper.line(0, y, Config.ANCHO, y);
		}
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