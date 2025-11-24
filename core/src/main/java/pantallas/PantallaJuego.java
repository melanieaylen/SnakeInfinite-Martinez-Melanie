package pantallas;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
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
	private final float VELOCIDAD_ENTRADAS = 0.12f;

	// ELEMENTOS
	private Serpiente serpiente;
	private Manzana manzana;
	private Grilla grilla;

	// DISEÑO
	private Imagen manzanaImagen;
	private Texto textoPuntuacion;	

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
		manzanaImagen.setParametros(100, 855, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
		textoPuntuacion = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
		grilla = new Grilla(TAMANIO_ELEMENTOS);

		posicionInicial();
		serpiente = new Serpiente(posElementosX, posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
		manzana = new Manzana(posElementosX + (TAMANIO_ELEMENTOS * 4), posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
		random = new Random();
		
		Gdx.input.setInputProcessor(entrada);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0.90f, 0.80f, 1.0f);
		
		Render.viewport.apply();
		// LOGICA
		procesarEntradas(delta);
		if (colisionConManzana()) {
			serpiente.crecer();
			moverManzanaAleatoria();
			puntuacion++;
		}

		// SHAPER LINE (GRILLA)
		Render.shaper.begin(ShapeType.Line);
		grilla.dibujarGrilla();
		Render.shaper.end();
		
		// RENDER (IMAGENES)
		Render.batch.begin();
		manzanaImagen.dibujar();
		Render.batch.end();

		// SHAPER FILLED (SERPIENTE)
		Render.shaper.begin(ShapeType.Filled);
		if (serpiente.colisionSerpiente() || colisionBordes()) {
			Render.app.setScreen(new GameOver());
		} else {
			serpiente.dibujar();
		}
		Render.shaper.end();

		// RENDER
		System.out.println(String.valueOf(puntuacion));
		Render.batch.begin();
		manzana.dibujar();
		textoPuntuacion.dibujarTexto(String.valueOf(puntuacion), 160, 882);
		Render.batch.end();
	}

	private void posicionInicial() {
		int celdasCentroX = (Config.ANCHO / TAMANIO_ELEMENTOS) / 2;
		int celdasCentroY = (Config.ALTO / TAMANIO_ELEMENTOS) / 2;

		// Posición inicial en píxeles (centro del área jugable)
		posElementosX = celdasCentroX * TAMANIO_ELEMENTOS;
		posElementosY = celdasCentroY * TAMANIO_ELEMENTOS;
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
		if (tiempo > VELOCIDAD_ENTRADAS) {
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
			serpiente.mover(posElementosX, posElementosY);
		}
	}

	private boolean colisionConManzana() {
		return (manzana.getPosX() == serpiente.getPosX()) && (manzana.getPosY() == serpiente.getPosY());
	}

	private void moverManzanaAleatoria() {
		int celdaX = random.nextInt(grilla.getCeldasX());
		int celdaY = random.nextInt(grilla.getCeldasY());

		float nuevaX = celdaX * TAMANIO_ELEMENTOS;
		float nuevaY = celdaY * TAMANIO_ELEMENTOS;

		manzana.setPosX(nuevaX);
		manzana.setPosY(nuevaY);
	}

	private boolean colisionBordes() {
		float serpienteX = serpiente.getPosX(), serpienteY = serpiente.getPosY();
		boolean colision = false;

		if (serpienteX < 0 || serpienteX >= Config.ANCHO || serpienteY < 0 || serpienteY >= Config.ALTO) {
			colision = true;
		}

		return colision;
	}

	@Override
	public void resize(int width, int height) {
		Render.viewport.update(width, height);
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