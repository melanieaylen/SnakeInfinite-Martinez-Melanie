package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import elementos.Serpiente;
import utiles.Config;
import utiles.Render;

public class PantallaJuego implements Screen {

	private Serpiente serpiente;
	private final int ANCHO = 20, ALTO = 20;

	@Override
	public void show() {
		serpiente = new Serpiente();
		serpiente.setAncho(ANCHO);
		serpiente.setAlto(ALTO);
		serpiente.setPosX((Config.ANCHO / 2) - (ANCHO / 2));
		serpiente.setPosY((Config.ALTO / 2) - (ALTO / 2));
		System.out.println("Config ANCHO: " + Config.ANCHO + ", ALTO: " + Config.ALTO);
		System.out.println("Real ANCHO: " + Gdx.graphics.getWidth() + ", ALTO: " + Gdx.graphics.getHeight());
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(1, 1, 1);
		Render.begin();
		Render.end();
		Render.shaper.begin(ShapeType.Filled);
		serpiente.dibujar();
		Render.shaper.end();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}
