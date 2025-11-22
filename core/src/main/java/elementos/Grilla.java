//package elementos;
//
//import com.badlogic.gdx.graphics.Color;
package elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import utiles.Config;
import utiles.Recursos;
import utiles.Render;

public class Grilla {
	private int tamanioCelda;
	private Color color;
	private int margen;
	private Imagen fondo; 
	
	public Grilla(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
		this.color = Color.DARK_GRAY;
		this.margen = tamanioCelda * 3; // Margen de 2 celdas
		
		fondo = new Imagen(Recursos.FONDO_GRILLA);
		
		int anchoJuego = Config.ANCHO - (margen * 2);
		int altoJuego = Config.ALTO - (margen * 2);
		
		fondo.setParametros(margen, margen, anchoJuego, altoJuego);
	}
	
	public void dibujarFondoGrilla() {
		fondo.dibujar();
	}
	
	public void dibujarGrilla() {
		Render.shaper.setColor(color);
		// VERTICAL 
		for (int x = margen; x <= Config.ANCHO - margen; x += tamanioCelda) {
			Render.shaper.line(x, margen, x, Config.ALTO - margen);
		}
		
		// HORIZONTAL
		for (int y = margen; y <= Config.ALTO - margen; y += tamanioCelda) {
			Render.shaper.line(margen, y, Config.ANCHO - margen, y);
		}
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
	
	public void dispose() {
		fondo.dispose();
	}
}