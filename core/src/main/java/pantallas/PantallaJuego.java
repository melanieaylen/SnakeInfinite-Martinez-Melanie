package pantallas;

import com.badlogic.gdx.Screen;

import elementos.Imagen;
import utiles.Recursos;
import utiles.Render;

public class PantallaJuego implements Screen{

	private Imagen fondo;
	@Override
	public void show() {
		fondo = new Imagen(Recursos.FONDO_JUEGO);
	}

	@Override
	public void render(float delta) {
		Render.batch.begin();
		fondo.dibujar();
		Render.batch.end();
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
	
	}

}
