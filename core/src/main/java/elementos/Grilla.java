package elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import utiles.Config;
import utiles.Render;

public class Grilla {
	private int tamanioCelda;
	private Color color;

	public Grilla(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
		this.color = Color.GRAY;
	}

	public void dibujar() {
		Render.shaper.begin(ShapeType.Line);
		Render.shaper.setColor(color);
		//VERTICAL 
		for (int x = 0; x <= Config.ANCHO; x += tamanioCelda) {
			Render.shaper.line(x, 0, x, Config.ALTO);
		}

		// HORIZONTAL
		for (int y = 0; y <= Config.ALTO; y += tamanioCelda) {
			Render.shaper.line(0, y, Config.ANCHO, y);
		}

		Render.shaper.end();
	}

	public int getTamanioCelda() {
		return tamanioCelda;
	}

	public void setTamanioCelda(int tamanioCelda) {
		this.tamanioCelda = tamanioCelda;
	}
}
