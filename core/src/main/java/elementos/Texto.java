package elementos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import utiles.Recursos;
import utiles.Render;

public class Texto {
	private BitmapFont fuente; 	
	
	public Texto(int dimension, Color colorLetra, Color colorSombra, int sombraX, int sombraY, boolean borde) {		
	generarTexto(dimension, colorLetra, colorSombra, sombraX, sombraY, borde);
	}
	
	private void generarTexto(int dimension, Color colorLetra, Color colorSombra, int sombraX, int sombraY, boolean borde) {
		FreeTypeFontGenerator generador = new FreeTypeFontGenerator(Gdx.files.internal(Recursos.FUENTE));
		FreeTypeFontParameter parametros = new FreeTypeFontGenerator.FreeTypeFontParameter();
		
		parametros.size = dimension;
		parametros.color = colorLetra;
		parametros.shadowColor = colorSombra;
		parametros.shadowOffsetX = sombraX;
		parametros.shadowOffsetY = sombraY;

		if(borde) {
			parametros.borderWidth = 1;
		}
		
		fuente = generador.generateFont(parametros);
	}	
	
	public void dibujarTexto(String texto, int x, int y) {
		fuente.draw(Render.batch, texto, x, y);
	}
}
