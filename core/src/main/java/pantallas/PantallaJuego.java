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
	private Imagen fondo;
	private Imagen manzanaImagen;
	private Texto textoPuntuacion;	

	// ENTRADAS
	private Entradas entrada = new Entradas();
	private Direcciones direccionActual = Direcciones.NINGUNA;

	// LOGICA Y ETC
	private float posElementosX;
	private float posElementosY;
	private int puntuacion;
	private float tiempo;
	private Random random;

	@Override
	public void show() {
		// INICIALIZACION
		fondo = new Imagen(Recursos.FONDO_JUEGO);
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
		Render.limpiarPantalla(1, 1, 1);

		// LOGICA
		procesarEntradas(delta);
		if (colisionConManzana()) {
			serpiente.crecer();
			moverManzanaAleatoria();
			puntuacion++;
		}

		// RENDER (IMAGENES)
		Render.batch.begin();
		fondo.dibujar();
		grilla.dibujarFondoGrilla();
		manzanaImagen.dibujar();
		Render.batch.end();

		// SHAPER LINE (GRILLA)
		Render.shaper.begin(ShapeType.Line);
		grilla.dibujarGrilla();
		Render.shaper.end();

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
		int celdasCentroX = (grilla.getAnchoJuego() / TAMANIO_ELEMENTOS) / 2;
		int celdasCentroY = (grilla.getAltoJuego() / TAMANIO_ELEMENTOS) / 2;

		// Posición inicial en píxeles (centro del área jugable)
		posElementosX = grilla.getMargen() + (celdasCentroX * TAMANIO_ELEMENTOS);
		posElementosY = grilla.getMargen() + (celdasCentroY * TAMANIO_ELEMENTOS);
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

		float nuevaX = grilla.getMargen() + (celdaX * TAMANIO_ELEMENTOS);
		float nuevaY = grilla.getMargen() + (celdaY * TAMANIO_ELEMENTOS);

		manzana.setPosX(nuevaX);
		manzana.setPosY(nuevaY);
	}

	private boolean colisionBordes() {
		int margen = grilla.getMargen();
		float serpienteX = serpiente.getPosX(), serpienteY = serpiente.getPosY();
		boolean colision = false;

		if (serpienteX < margen || serpienteX >= Config.ANCHO - margen || serpienteY < margen
				|| serpienteY >= Config.ALTO - margen) {
			colision = true;
		}

		return colision;
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
		grilla.dispose();
		fondo.dispose();
	}

}