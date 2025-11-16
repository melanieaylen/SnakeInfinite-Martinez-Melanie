//package elementos;
//
//import com.badlogic.gdx.graphics.Color;
package elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import utiles.Config;
import utiles.Render;

public class Grilla {
	private int tamanioCelda;
	private Color color;
	private int margen;
	
	public Grilla(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
		this.color = Color.DARK_GRAY;
		this.margen = tamanioCelda * 2; // Margen de 2 celdas
	}
	
	public void dibujar() {
		Render.shaper.begin(ShapeType.Line);
		Render.shaper.setColor(color);
		
		// VERTICAL 
		for (int x = margen; x <= Config.ANCHO - margen; x += tamanioCelda) {
			Render.shaper.line(x, margen, x, Config.ALTO - margen);
		}
		
		// HORIZONTAL
		for (int y = margen; y <= Config.ALTO - margen; y += tamanioCelda) {
			Render.shaper.line(margen, y, Config.ANCHO - margen, y);
		}
		
		Render.shaper.end();
	}
	
	public int getMargen() {
		return margen;
	}

	public void setMargen(int margen) {
		this.margen = margen;
	}

	public int getTamanioCelda() {
		return tamanioCelda;
	}
	
	public void setTamanioCelda(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
	}
	
}