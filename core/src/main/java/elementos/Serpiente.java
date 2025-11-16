package elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import utiles.Config;
import utiles.Render;

public class Serpiente {
	private int ancho, alto;
	private float[][] posiciones;
	private int tamanioMaximo, tamanioActual;
	float posX = 0, posY = 0;

	public Serpiente(float posX, float posY, int ancho, int alto) {
		this.ancho = ancho;
		this.alto = alto;
		this.posX = posX;
		this.posY = posY;

		int areaTotal = Config.ANCHO * Config.ALTO;
		int areaCelda = ancho * alto;
		this.tamanioMaximo = areaTotal / areaCelda; // Toda la pantalla
		// se inicializa con 100 posiciones en la cual cada una se almacena x,y
		posiciones = new float[tamanioMaximo][2];

		posiciones[0][0] = posX;
		posiciones[0][1] = posY;
		tamanioActual = 1;
	}

	public void dibujar() {
		Render.shaper.begin(ShapeType.Filled);
		for (int i = 0; i < tamanioActual; i++) {
			if (i == 0) {
				Render.shaper.setColor(Color.GREEN);
			} else {
				Render.shaper.setColor(Color.BLACK);
			}
			Render.shaper.rect(posiciones[i][0], posiciones[i][1], ancho, alto);
		}
		Render.shaper.end();
	}

	public void mover(float nuevaX, float nuevaY) {
		for (int i = tamanioActual - 1; i > 0; i--) {
			posiciones[i][0] = posiciones[i - 1][0];
			posiciones[i][1] = posiciones[i - 1][1];
		}
		posiciones[0][0] = nuevaX;
		posiciones[0][1] = nuevaY;
	}

	public void crecer() {
		if (tamanioActual < tamanioMaximo) {
			int ultimoIndice = tamanioActual - 1;
			posiciones[tamanioActual][0] = posiciones[ultimoIndice][0];
			posiciones[tamanioActual][1] = posiciones[ultimoIndice][1];
			tamanioActual++;
			System.out.println("Tamaño actual: " + tamanioActual);
		}
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