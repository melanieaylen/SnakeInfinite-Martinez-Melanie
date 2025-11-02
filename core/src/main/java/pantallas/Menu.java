package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

import elementos.Imagen;
import elementos.Texto;
import mi.juego.snake.Serpiente;
import utiles.Recursos;
import utiles.Render;

public class Menu implements Screen {

	private Imagen menu;
	private Music musica;
	private Serpiente serpiente;
	private boolean aparicion = false;
	private float a = 0;
	private float contTiempo = 0, tiempoEspera = 5;
	
	private Texto titulo;
	private Texto subtitulo;
	private Color color; 
	
	@Override
	public void show() {
		menu = new Imagen(Recursos.FONDO_MENU);
		serpiente = new Serpiente(600, 150, 130, 130);

		musica = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica.mp3"));
		musica.play();
		
		titulo = new Texto(120, color.WHITE, color.TEAL, -3, 3, false);
		subtitulo = new Texto(50, color.SKY, color.BLACK, -4, 4, true);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0, 0, 0);
		procesarTransparencia();
		Render.batch.begin();
		menu.dibujar();
		serpiente.dibujar();
		titulo.dibujarTexto("Snake Infinite", 245, 780);
		subtitulo.dibujarTexto("> Un Jugador", 468, 520);
		subtitulo.dibujarTexto("> Multijugador", 448, 410);
		Render.batch.end();
	}

	private void procesarTransparencia() {
		if (!aparicion) {
			a += 0.01f;
			if (a > 1) {
				a = 1;
				aparicion = true;
			}
		} else {
			contTiempo += 0.1f;
			if (contTiempo > tiempoEspera) {
				a -= 0.01f;
				if (a < 0) {
					a = 0;
					Render.app.setScreen(new PantallaJuego());
				}
			}
		}
		menu.setTransparencia(a);
	}
	
	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		musica.dispose();
	}
}
