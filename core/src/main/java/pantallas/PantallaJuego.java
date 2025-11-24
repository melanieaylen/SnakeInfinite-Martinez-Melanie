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

	// ELEMENTOS
	private Serpiente serpiente;
	private Grilla grilla;
	private Manzana manzana;

	// ENTRADAS
	private Entradas entrada = new Entradas();
	private float tiempo = 0;
	private Direcciones direccionActual = Direcciones.NINGUNA;

	// LOGICA
	private Random random;
	private int puntuacion = 0; 

	// DISEÑO Y CONFIGURACION
	private Imagen fondo;
	private Texto textoPuntuacion; 
	private int tamanioElementos = 30;
	private float posElementosX = 0, posElementosY = 0;

	private float a = 0; 
	@Override
	public void show() {
		// AREA JUGABLE

		// INICIALIZACION
		fondo = new Imagen(Recursos.FONDO_JUEGO);
		textoPuntuacion = new Texto(Recursos.FUENTE, 30, Color.WHITE, Color.BLACK, -4, 4, true);
		grilla = new Grilla(tamanioElementos);

		int celdasCentroX = (grilla.getAnchoJuego() / tamanioElementos) / 2;
		int celdasCentroY = (grilla.getAltoJuego() / tamanioElementos) / 2;

		// Posición inicial en píxeles (centro del área jugable)
		posElementosX = grilla.getMargen() + (celdasCentroX * tamanioElementos);
		posElementosY = grilla.getMargen() + (celdasCentroY * tamanioElementos);

		serpiente = new Serpiente(posElementosX, posElementosY, tamanioElementos, tamanioElementos);
		manzana = new Manzana(posElementosX + (tamanioElementos * 4), posElementosY, tamanioElementos,tamanioElementos);
		random = new Random();

		Gdx.input.setInputProcessor(entrada);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(1, 1, 1);
		procesarTransparencia();
		Render.batch.begin();
		fondo.dibujar();
		grilla.dibujarFondoGrilla();
		Render.batch.end();

		Render.shaper.begin(ShapeType.Line);
		grilla.dibujarGrilla();
		Render.shaper.end();

		procesarEntradas(delta);
		if (colisionConManzana()) {
			serpiente.crecer();
			moverManzanaAleatoria();
			puntuacion++; 
		}

		Render.shaper.begin(ShapeType.Filled);
		if (serpiente.colisionSerpiente() || colisionBordes()) {
			Render.app.setScreen(new GameOver());
		} else {
			serpiente.dibujar();
		}
		manzana.dibujar();
		Render.shaper.end();
		
		Render.batch.begin();
		textoPuntuacion.dibujarTexto(String.valueOf(puntuacion), 500, 500);
		Render.batch.end();
	}
	
	private void procesarTransparencia() {
		a+= 0.004f;
		if(a > 1) {		
			a = 1; 
		}
		fondo.setTransparencia(a);
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
		if (tiempo > 0.12f) {
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

		float nuevaX = grilla.getMargen() + (celdaX * tamanioElementos);
		float nuevaY = grilla.getMargen() + (celdaY * tamanioElementos);

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