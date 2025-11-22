package pantallas;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

import elementos.Grilla;
import elementos.Imagen;
import elementos.Manzana;
import elementos.Serpiente;
import entradas.salidas.teclado.Entradas;
import utiles.Config;
import utiles.Recursos;
import utiles.Render;

public class PantallaJuego implements Screen {

	private Serpiente serpiente;
	private Entradas entrada = new Entradas();
	private Grilla grilla;
	private Manzana manzana;
	private Random random;

	private int tamanio = 20;
	private float posX = 0, posY = 0;
	private float tiempo = 0;
	 
	private Direcciones direccionActual = Direcciones.NINGUNA;

	private Imagen fondo;

	@Override
	public void show() {
		posX = (Config.ANCHO / 2f) - (tamanio / 2f);
		posY = (Config.ALTO / 2f) - (tamanio / 2f);

		grilla = new Grilla(tamanio);
		serpiente = new Serpiente(posX, posY, tamanio, tamanio);
		random = new Random();
		manzana = new Manzana(posX + 40, posY + 40, tamanio, tamanio);
		fondo = new Imagen(Recursos.FONDO_JUEGO);
		
		Gdx.input.setInputProcessor(entrada);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(1, 1, 1);
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
		}
		
		Render.shaper.begin(ShapeType.Filled);
		if (serpiente.colisionSerpiente() || colisionBordes()) {
			Render.app.setScreen(new GameOver());
		}else {
			serpiente.dibujar();
		}
		manzana.dibujar();
		Render.shaper.end();
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
		if (tiempo > 0.1f) {
			tiempo = 0;

			switch (direccionActual) {
			case ARRIBA:
				posY += serpiente.getAlto();
				break;

			case ABAJO:
				posY -= serpiente.getAlto();
				break;

			case DERECHA:
				posX += serpiente.getAncho();
				break;

			case IZQUIERDA:
				posX -= serpiente.getAncho();
				break;

			case NINGUNA:
				break;

			default:
				break;

			}
			serpiente.mover(posX, posY);
		}
	}

	private boolean colisionConManzana() {
		return (manzana.getPosX() == serpiente.getPosX()) && (manzana.getPosY() == serpiente.getPosY());
	}

	private void moverManzanaAleatoria() {
		int margen = grilla.getMargen();

		int areaJugableAncho = Config.ANCHO - (2 * margen);
		int areaJugableAlto = Config.ALTO - (2 * margen);

		int celdasX = areaJugableAncho / tamanio;
		int celdasY = areaJugableAlto / tamanio;

		int celdaX = random.nextInt(celdasX);
		int celdaY = random.nextInt(celdasY);

		float nuevaX = margen + (celdaX * tamanio);
		float nuevaY = margen + (celdaY * tamanio);

		manzana.setPosX(nuevaX);
		manzana.setPosY(nuevaY);
	}

	private boolean colisionBordes() {
		int margen = grilla.getMargen();
		float serpienteX = serpiente.getPosX(), serpienteY = serpiente.getPosY();
		boolean colision = false; 
		
		if(serpienteX < margen || serpienteX >= Config.ANCHO - margen || serpienteY < margen || serpienteY >= Config.ALTO - margen) {
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