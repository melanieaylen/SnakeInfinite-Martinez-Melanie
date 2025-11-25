package pantallas;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import elementos.Grilla;
import elementos.Imagen;
import elementos.Manzana;
import elementos.Serpiente;
import elementos.Texto;
import entradas.salidas.teclado.Entradas;
import utiles.Config;
import utiles.Recursos;
import utiles.Render;

public class PantallaJuego implements Screen {

	// CONSTANTES
	private final int TAMANIO_ELEMENTOS = 30;
	private final float VELOCIDAD_SERPIENTE = 0.12f; // Tiempo entre movimientos

	// ELEMENTOS
	private Serpiente serpiente;
	private Manzana manzana;
	private Grilla grilla;

	// DISEÑO
	private Imagen manzanaImagen;
	private Texto textoPuntuacion;
	private OrthographicCamera camara;

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
		  grilla.setColores(
			        new Color(0.96f, 0.86f, 0.90f, 1f),  // Rosa claro
			        new Color(0.92f, 0.80f, 0.86f, 1f)   // Rosa sutilmente más oscuro
			    );
			    
			    // Líneas muy suaves para no romper el efecto
			    grilla.setColorLineas(new Color(0.85f, 0.70f, 0.80f, 0.2f));
		// Posición inicial
		posElementosX = 0;
		posElementosY = 0;

		serpiente = new Serpiente(posElementosX, posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
		manzana = new Manzana(posElementosX + (TAMANIO_ELEMENTOS * 4), posElementosY, TAMANIO_ELEMENTOS,
				TAMANIO_ELEMENTOS);
		random = new Random();

		// Configurar cámara
		camara = new OrthographicCamera();
		camara.setToOrtho(false, Config.ANCHO, Config.ALTO);
		camara.position.set(posElementosX, posElementosY, 0);
		camara.update();

		Gdx.input.setInputProcessor(entrada);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0.90f, 0.80f, 1.0f);

		// LOGICA
		procesarEntradas(delta);
		if (colisionConManzana()) {
			serpiente.crecer();
			moverManzanaAleatoria();
			puntuacion++;
		}

		// ACTUALIZAR CAMARA PARA SEGUIR A LA SERPIENTE
		actualizarCamara();

		// Aplicar la cámara a los renderers
		Render.batch.setProjectionMatrix(camara.combined);
		Render.shaper.setProjectionMatrix(camara.combined);

		// RENDER GRILLA (con patrón de ajedrez)
		grilla.dibujarGrillaInfinita(camara);

		// RENDER SERPIENTE
		Render.shaper.begin(ShapeType.Filled);
		if (serpiente.colisionSerpiente()) {
			Render.app.setScreen(new GameOver());
		} else {
			serpiente.dibujar();
		}
		Render.shaper.end();

		// RENDER MANZANA
		Render.batch.begin();
		manzana.dibujar();
		Render.batch.end();

		// UI FIJA (sin afectar por la cámara)
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
		// Guardar la matriz de proyección actual
		camara.setToOrtho(false, Config.ANCHO, Config.ALTO);
		camara.position.set(Config.ANCHO / 2, Config.ALTO / 2, 0);
		camara.update();
		Render.batch.setProjectionMatrix(camara.combined);

		// Dibujar UI fija
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

	private boolean colisionConManzana() {
		return (manzana.getPosX() == serpiente.getPosX()) && (manzana.getPosY() == serpiente.getPosY());
	}

	private void moverManzanaAleatoria() {
		// Generar manzana cerca de la serpiente (dentro de un rango visible)
		int rangoMin = -10;
		int rangoMax = 20;

		int offsetX = random.nextInt(rangoMax - rangoMin + 1) + rangoMin;
		int offsetY = random.nextInt(rangoMax - rangoMin + 1) + rangoMin;

		float nuevaX = serpiente.getPosX() + (offsetX * TAMANIO_ELEMENTOS);
		float nuevaY = serpiente.getPosY() + (offsetY * TAMANIO_ELEMENTOS);

		manzana.setPosX(nuevaX);
		manzana.setPosY(nuevaY);
	}

	@Override
	public void resize(int width, int height) {
		camara.setToOrtho(false, width, height);
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