package pantallas;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import elementos.Fruta;
import elementos.Grilla;
import elementos.Imagen;
import elementos.Serpiente;
import elementos.Texto;
import elementos.TipoFruta;
import entradas.salidas.teclado.Entradas;
import utiles.Config;
import utiles.Recursos;
import utiles.Render;

public class PantallaJuego implements Screen {

	// CONSTANTES
	private final int TAMANIO_ELEMENTOS = 30;
	private final float VELOCIDAD_SERPIENTE = 0.12f; // Tiempo entre movimientos

	// Para el mundo del juego
	private OrthographicCamera camaraUI;
	private Viewport viewportUI;
	
	// ELEMENTOS - FRUTAS
	private Serpiente serpiente;
	private Fruta manzana;
	private Fruta banana;
	private Fruta cereza;
	private Fruta sandia;
	private Fruta uva;
	private Grilla grilla;

	// DISEÑO
	private Imagen manzanaImagen;
	private Texto textoPuntuacion;
	private OrthographicCamera camara;
	private Viewport viewport;

	// ENTRADAS
	private Entradas entrada = new Entradas();
	private Direcciones direccionActual = Direcciones.NINGUNA;

	// LOGICA Y ETC
	private float posElementosX;
	private float posElementosY;
	private int puntuacion = 0;
	private float tiempo;
	private Random random;

	@Override
	public void show() {
		// INICIALIZACION
		manzanaImagen = new Imagen(Recursos.MANZANA);
		textoPuntuacion = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
		grilla = new Grilla(TAMANIO_ELEMENTOS);

		// Posición inicial
		posElementosX = 0;
		posElementosY = 0;

		serpiente = new Serpiente(posElementosX, posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);

		random = new Random();

		// Inicializar todas las frutas en posiciones aleatorias
		manzana = new Fruta(0, 0, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, TipoFruta.MANZANA);
		moverFrutaAleatoria(manzana);

		banana = new Fruta(0, 0, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, TipoFruta.BANANA);
		moverFrutaAleatoria(banana);

		cereza = new Fruta(0, 0, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, TipoFruta.CEREZA);
		moverFrutaAleatoria(cereza);

		sandia = new Fruta(0, 0, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, TipoFruta.SANDIA);
		moverFrutaAleatoria(sandia);

		uva = new Fruta(0, 0, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, TipoFruta.UVA);
		moverFrutaAleatoria(uva);

		// Configurar cámara
		camara = new OrthographicCamera();
		camara.setToOrtho(false, Config.ANCHO, Config.ALTO);
		viewport = new FitViewport(Config.ANCHO, Config.ALTO, camara);
		camara.position.set(posElementosX, posElementosY, 0);
		camara.update();

		// Cámara para UI fija (no se mueve)
		camaraUI = new OrthographicCamera();
		viewportUI = new FitViewport(Config.ANCHO, Config.ALTO, camaraUI);
		camaraUI.position.set(Config.ANCHO / 2, Config.ALTO / 2, 0);
		camaraUI.update();

		Gdx.input.setInputProcessor(entrada);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0.90f, 0.80f, 1.0f);

		// LOGICA
		procesarEntradas(delta);
		
		// Verificar colisión con cada fruta
		if (colisionConFruta(manzana)) {
			serpiente.crecer();
			moverFrutaAleatoria(manzana);
			puntuacion++;
		}
		
		if (colisionConFruta(banana)) {
			serpiente.crecer();
			moverFrutaAleatoria(banana);
			puntuacion++;
		}
		
		if (colisionConFruta(cereza)) {
			serpiente.crecer();
			moverFrutaAleatoria(cereza);
			puntuacion++;
		}
		
		if (colisionConFruta(sandia)) {
			serpiente.crecer();
			moverFrutaAleatoria(sandia);
			puntuacion++;
		}
		
		if (colisionConFruta(uva)) {
			serpiente.crecer();
			moverFrutaAleatoria(uva);
			puntuacion++;
		}

		// ACTUALIZAR CAMARA PARA SEGUIR A LA SERPIENTE
		actualizarCamara();

		// ===== RENDER DEL MUNDO DEL JUEGO =====
		viewport.apply();
		Render.batch.setProjectionMatrix(camara.combined);
		Render.shaper.setProjectionMatrix(camara.combined);

		// RENDER SERPIENTE
		Render.shaper.begin(ShapeType.Filled);
		grilla.dibujarGrilla(camara);
		if (serpiente.colisionSerpiente()) {
			Render.app.setScreen(new GameOver());
		} else {
			serpiente.dibujar();
		}
		Render.shaper.end();

		// RENDER FRUTAS
		Render.batch.begin();
		manzana.dibujar();
		banana.dibujar();
		cereza.dibujar();
		sandia.dibujar();
		uva.dibujar();
		Render.batch.end();

		// ===== RENDER DE LA UI FIJA =====
		dibujarUI();
	}

	private void actualizarCamara() {
		// Centrar la cámara directamente en la serpiente (sin lag)
		float targetX = serpiente.getPosX() + TAMANIO_ELEMENTOS / 2;
		float targetY = serpiente.getPosY() + TAMANIO_ELEMENTOS / 2;

		// Actualización instantánea
		camara.position.set(targetX, targetY, 0);
		camara.update();
	}

	private void dibujarUI() {
		// ✅ Usa la cámara UI dedicada
		viewportUI.apply();
		Render.batch.setProjectionMatrix(camaraUI.combined);

		Render.batch.begin();
		manzanaImagen.setParametros(20, Config.ALTO - 60, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
		manzanaImagen.dibujar();
		textoPuntuacion.dibujarTexto(String.valueOf(puntuacion), 60, Config.ALTO - 35);
		Render.batch.end();
	}

	private void procesarEntradas(float delta) {
		if (entrada.isArriba() && direccionActual != Direcciones.ABAJO) {
			direccionActual = Direcciones.ARRIBA;
		} else if (entrada.isAbajo() && direccionActual != Direcciones.ARRIBA) {
			direccionActual = Direcciones.ABAJO;
		} else if (entrada.isDerecha() && direccionActual != Direcciones.IZQUIERDA) {
			direccionActual = Direcciones.DERECHA;
		} else if (entrada.isIzquierda() && direccionActual != Direcciones.DERECHA) {
			direccionActual = Direcciones.IZQUIERDA;
		}else if(entrada.isPausa()) {
			Render.app.setScreen(new PantallaPausa());
		}

		moverSerpiente(delta);
	}

	private void moverSerpiente(float delta) {
		tiempo += delta;
		if (tiempo > VELOCIDAD_SERPIENTE) {
			tiempo = 0;

			switch (direccionActual) {
			case ARRIBA:
				posElementosY += serpiente.getAlto();
				break;

			case ABAJO:
				posElementosY -= serpiente.getAlto();
				break;

			case DERECHA:
				posElementosX += serpiente.getAncho();
				break;

			case IZQUIERDA:
				posElementosX -= serpiente.getAncho();
				break;

			case NINGUNA:
				break;

			default:
				break;
			}

			// La serpiente puede moverse libremente sin límites
			serpiente.mover(posElementosX, posElementosY);
		}
	}

	private boolean colisionConFruta(Fruta fruta) {
		return fruta.getPosX() == serpiente.getPosX() && fruta.getPosY() == serpiente.getPosY();
	}

	private void moverFrutaAleatoria(Fruta fruta) {
		// Generar fruta cerca de la serpiente (dentro de un rango visible)
		int rangoMin = -10, rangoMax = 20;
		float nuevaX = 0, nuevaY = 0;

		do {
			int offsetX = random.nextInt(rangoMax - rangoMin + 1) + rangoMin;
			int offsetY = random.nextInt(rangoMax - rangoMin + 1) + rangoMin;

			nuevaX = serpiente.getPosX() + (offsetX * TAMANIO_ELEMENTOS);
			nuevaY = serpiente.getPosY() + (offsetY * TAMANIO_ELEMENTOS);

		} while (serpiente.colisionConPosicion(nuevaX, nuevaY)); 

		fruta.setPosX(nuevaX);
		fruta.setPosY(nuevaY);
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewportUI.update(width, height, true);
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