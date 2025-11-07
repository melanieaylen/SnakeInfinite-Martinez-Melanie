package elementos;

import com.badlogic.gdx.graphics.Color;

import utiles.Render;

public class Serpiente {
	private float posX = 0, posY = 0;
	private int alto = 0, ancho = 0;

	public float getPosY() {
		return posY;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public float getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public float getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}

	public void dibujar() {
		Render.shaper.setColor(Color.BLACK);
		Render.shaper.rect(posX, posY, ancho, alto);
	}
}
