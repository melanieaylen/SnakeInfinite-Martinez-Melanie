package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import elementos.Imagen;
import elementos.Texto;
import entradas.salidas.teclado.Entradas;
import utiles.Recursos;
import utiles.Render;

public class PantallaFinJuego implements Screen {

	private final int TAMANIO_TEXTO = 120;
	private final int TAMANIO_SUB = 50;

	private Imagen fondo;
	private Imagen serpiente;
	private Music musica;
	private Sound sonidoBoton;

	private Texto tituloGameOver;
	private Texto textoPuntuacion;
	private Texto textoRecord;
	private Texto opcionReiniciar;
	private Texto opcionMenu;
	private Texto indicador;

	private OrthographicCamera camara;
	private Viewport viewport;

	private Entradas entrada;
	private int opcionSeleccionada = 1;
	private float tiempoEntrada = 0;
	private float transparencia = 0;

	private int puntuacionFinal;
	private int recordActual;
	private boolean esNuevoRecord = false;

	public PantallaFinJuego(int puntuacion) {
		this.puntuacionFinal = puntuacion;
		cargarYCompararRecord();
	}

	private void cargarYCompararRecord() {
		Preferences prefs = Gdx.app.getPreferences("SnakeInfiniteRecords");
		recordActual = prefs.getInteger("recordPuntuacion", 0);

		if (puntuacionFinal > recordActual) {
			esNuevoRecord = true;
			recordActual = puntuacionFinal;
			prefs.putInteger("recordPuntuacion", recordActual);
			prefs.flush();
			System.out.println("Nuevo record guardado: " + recordActual + " puntos");
		} else {
			System.out.println("Puntuacion: " + puntuacionFinal + " | Record actual: " + recordActual);
		}
	}

	@Override
	public void show() {
		fondo = new Imagen(Recursos.FONDO);
		serpiente = new Imagen(Recursos.ICONO);
		serpiente.setParametros(680, 140, 130, 130);

		musica = Gdx.audio.newMusic(Gdx.files.internal(Recursos.MUSICA_GAME_OVER));
		sonidoBoton = Gdx.audio.newSound(Gdx.files.internal(Recursos.SONIDO_BOTON));
		musica.setLooping(true);
		musica.play();

		entrada = new Entradas();
		Gdx.input.setInputProcessor(entrada);

		camara = new OrthographicCamera();
		camara.setToOrtho(false, 1440, 900);
		viewport = new FitViewport(1440, 900, camara);

		tituloGameOver = new Texto(Recursos.FUENTE, TAMANIO_TEXTO, Color.RED, Color.BLACK, -4, 4, true);
		textoPuntuacion = new Texto(Recursos.TEXTO, TAMANIO_SUB + 10, Color.WHITE, Color.BLACK, -4, 4, true);
		textoRecord = new Texto(Recursos.TEXTO, TAMANIO_SUB, Color.GOLD, Color.BLACK, -4, 4, true);
		opcionReiniciar = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
		opcionMenu = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
		indicador = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.SKY, Color.BLACK, -4, 4, true);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0, 0, 0);

		actualizarTransparencia();
		procesarEntradas(delta);
		actualizarInterfaz();

		viewport.apply();
		Render.batch.setProjectionMatrix(camara.combined);

		Render.batch.begin();

		fondo.setTransparencia(transparencia);
		fondo.dibujar();
		serpiente.dibujar();

		tituloGameOver.dibujarTexto("GAME OVER", 380, 750);

		textoPuntuacion.dibujarTexto("Puntuacion: " + puntuacionFinal, 480, 600);

		textoRecord.dibujarTexto("Record: " + recordActual, 580, 540);

		opcionReiniciar.dibujarTexto("   Jugar de nuevo", 480, 440);
		opcionMenu.dibujarTexto("   Volver al menu", 485, 360);

		if (opcionSeleccionada == 1) {
			indicador.dibujarTexto("> ", 440, 440);
		} else {
			indicador.dibujarTexto("> ", 440, 360);
		}
		Render.batch.end();
	}

	private void actualizarTransparencia() {
		transparencia += 0.012f;
		if (transparencia > 1) {
			transparencia = 1;
		}
	}

	private void procesarEntradas(float delta) {
		tiempoEntrada += delta;

		if (entrada.isArriba() && tiempoEntrada > 0.2f) {
			tiempoEntrada = 0;
			opcionSeleccionada = 1;
		}

		if (entrada.isAbajo() && tiempoEntrada > 0.2f) {
			tiempoEntrada = 0;
			opcionSeleccionada = 2;
		}

		if (entrada.isEnter()) {
			sonidoBoton.play();

			if (opcionSeleccionada == 1) {
				Render.app.setScreen(new PantallaJuego());
			} else {
				Render.app.setScreen(new PantallaMenu());
			}
		}
	}

	private void actualizarInterfaz() {
		if (opcionSeleccionada == 1) {
			opcionReiniciar.setColor(Color.SKY);
			opcionMenu.setColor(Color.WHITE);
		} else {
			opcionReiniciar.setColor(Color.WHITE);
			opcionMenu.setColor(Color.SKY);
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {
		if (musica != null && musica.isPlaying()) {
			musica.pause();
		}
	}

	@Override
	public void resume() {
		if (musica != null && !musica.isPlaying()) {
			musica.play();
		}
	}

	@Override
	public void hide() {
		if (musica != null && musica.isPlaying()) {
			musica.stop();
		}
	}

	@Override
	public void dispose() {
		if (musica != null)
			musica.dispose();
		if (sonidoBoton != null)
			sonidoBoton.dispose();
		if (tituloGameOver != null)
			tituloGameOver.dispose();
		if (textoPuntuacion != null)
			textoPuntuacion.dispose();
		if (textoRecord != null)
			textoRecord.dispose();
		if (opcionReiniciar != null)
			opcionReiniciar.dispose();
		if (opcionMenu != null)
			opcionMenu.dispose();
		if (indicador != null)
			indicador.dispose();
	}
}