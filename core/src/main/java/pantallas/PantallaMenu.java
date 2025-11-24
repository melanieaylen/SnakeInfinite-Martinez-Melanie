package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;

import elementos.Imagen;
import elementos.Texto;
import entradas.salidas.teclado.Entradas;
import utiles.Recursos;
import utiles.Render;

public class PantallaMenu implements Screen {

	//CONSTANTES
	private final int TAMANIO_TEXTO = 120;
	private final int TAMANIO_SUB = 50;
	
	private Imagen menu;
	private Imagen serpiente;

	private Music musica;

	private Texto titulo;
	private Texto subtitulo1;
	private Texto subtitulo2;
	private Texto opcionElegida;

	private Entradas entrada = new Entradas();
	private int opc = 1;
	private float tiempo = 0;

	private float a = 0; 
	@Override
	public void show() {
		menu = new Imagen(Recursos.FONDO_MENU);
		serpiente = new Imagen(Recursos.ICONO);
		serpiente.setParametros(720, 140, 130, 130);

		Gdx.input.setInputProcessor(entrada);

		musica = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica.mp3"));
		musica.play();

		titulo = new Texto(Recursos.FUENTE, TAMANIO_TEXTO, Color.WHITE, Color.TEAL, -3, 3, false);
		subtitulo1 = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
		subtitulo2 = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
		opcionElegida = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.SKY, Color.BLACK, -4, 4, true);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0, 0, 0);
		procesarTransparencia();
		procesarEntradas(delta);
		actualizarInterfaz();

		Render.batch.begin();
		menu.dibujar();
		serpiente.dibujar();
		
		titulo.dibujarTexto("Snake Infinite", 330, 750);
		subtitulo1.dibujarTexto("   Un Jugador", 580, 530);
		subtitulo2.dibujarTexto("   Multijugador", 565, 400);

		if (opc == 1) {
			opcionElegida.dibujarTexto("> ", 540, 530);

		} else if (opc == 2) {
			opcionElegida.dibujarTexto("> ", 540, 400);
		}
		Render.batch.end();
	}

	private void procesarTransparencia() {
		a+= 0.012f;
		if(a > 1) {		
			a = 1; 
		}
		menu.setTransparencia(a);
	}

	private void procesarEntradas(float delta) {
		tiempo += delta;
		if (entrada.isAbajo()) {
			if (tiempo > 0.2f) {
				tiempo = 0;
				opc++;
				if (opc > 2) {
					opc = 1;
				}
			}
		}

		if (entrada.isArriba()) {
			if (tiempo > 0.2f) {
				tiempo = 0;
				opc--;
				if (opc < 1) {
					opc = 2;
				}
			}
		}

		if (entrada.isEnter()) {
			if (opc == 1) {
				Render.app.setScreen(new PantallaJuego());
			} else if (opc == 2) {
				Render.app.setScreen(new PantallaEjemplo());
			}
		}
	}

	private void actualizarInterfaz() {
		if (opc == 1) {
			subtitulo1.setColor(Color.SKY);
			subtitulo2.setColor(Color.WHITE);
		} else if (opc == 2) {
			subtitulo1.setColor(Color.WHITE);
			subtitulo2.setColor(Color.SKY);
		}
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
		musica.dispose();
		titulo.dispose();
		subtitulo1.dispose();
		subtitulo2.dispose();
		opcionElegida.dispose();
	}
}
