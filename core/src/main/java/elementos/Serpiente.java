package elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import utiles.Config;
import utiles.Render;

public class Serpiente {
	private int ancho, alto;
	private float[][] posiciones;
	private int tamanioMaximo, tamanioActual;
	private boolean debeCrecer = false; 
	
	public Serpiente(float posX, float posY, int ancho, int alto) {
		this.ancho = ancho;
		this.alto = alto; 
		
		int areaTotal = Config.ANCHO * Config.ALTO;
		int areaCelda = ancho * alto;
		this.tamanioMaximo = areaTotal / areaCelda; 
		
		posiciones = new float[tamanioMaximo][2];

		posiciones[0][0] = posX;
		posiciones[0][1] = posY;
		tamanioActual = 1;
	}

	public void dibujar() {
		for (int i = 0; i < tamanioActual; i++) {
			if (i == 0) {
				Render.shaper.setColor(Color.NAVY);
			} else {
				Render.shaper.setColor(Color.BLACK);
			}
			Render.shaper.rect(posiciones[i][0], posiciones[i][1], ancho, alto);
		}
	}

	public void mover(float nuevaX, float nuevaY) {
		float ultimaX = posiciones[tamanioActual - 1][0];
		float ultimaY = posiciones[tamanioActual - 1][1];
		
		// Mover cuerpo
		for (int i = tamanioActual - 1; i > 0; i--) {
			posiciones[i][0] = posiciones[i - 1][0];
			posiciones[i][1] = posiciones[i - 1][1];
		}
		
		// Mover cabeza
		posiciones[0][0] = nuevaX;
		posiciones[0][1] = nuevaY;
		
		// crecer
		if (debeCrecer) {
			posiciones[tamanioActual][0] = ultimaX;
			posiciones[tamanioActual][1] = ultimaY;
			tamanioActual++;
			debeCrecer = false;
		}
		System.out.println("Tamaño actual: " + tamanioActual);
	}
	
	public void crecer() {
		if (tamanioActual < tamanioMaximo) {
			debeCrecer = true;
		}
	}

	public boolean colisionSerpiente() {
		boolean colision = false; 
		for (int i = 1; i < tamanioActual; i++) {
			if (posiciones[0][0] == posiciones[i][0] && posiciones[0][1] == posiciones[i][1]) {
				colision = true;
			}
		}
		
		return colision;
	}

	
	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public float getPosX() {
		return posiciones[0][0];
	}

	public float getPosY() {
		return posiciones[0][1];
	}
}