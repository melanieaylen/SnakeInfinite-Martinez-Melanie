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
	private int margen;
	private Imagen fondo; 
	private int anchoJuego = 0, altoJuego = 0; 
	private int cantCeldasX = 0, cantCeldasY = 0; 
	
	public Grilla(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
		margen = tamanioCelda * 2;
		
		fondo = new Imagen(Recursos.FONDO_GRILLA);
		
		//AREA REAL DEL JUEGO 
		anchoJuego = Config.ANCHO - (margen * 2);
		altoJuego = Config.ALTO - (margen * 2);
		
		//CANTIDAD DE CELDAS DE LA GRILLA 
		cantCeldasX = anchoJuego / tamanioCelda; 
		cantCeldasY = altoJuego / tamanioCelda; 
		
		fondo.setParametros(margen, margen, anchoJuego, altoJuego);
	}
	
	public void dibujarFondoGrilla() {
		fondo.dibujar();
	}
	
	public void dibujarGrilla() {
		Render.shaper.setColor(Color.DARK_GRAY);
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

	public int getTamanioCelda() {
		return tamanioCelda;
	}
	
	public void setTamanioCelda(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
	}
	
	public int getAnchoJuego() {
		return anchoJuego; 
	}
	
	public int getAltoJuego() {
		return altoJuego; 
	}
	
	public int getCeldasX() {
		return cantCeldasX; 
	}
	
	public int getCeldasY() {
		return cantCeldasY; 
	}
	
	public void dispose() {
		fondo.dispose();
	}
}