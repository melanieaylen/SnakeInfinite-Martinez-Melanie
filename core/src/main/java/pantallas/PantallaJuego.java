package pantallas;

import java.util.Random;

import com.badlogic.gdx.Screen;

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

	private int puntuacion; 
	
	@Override
	public void show() {
		posX = (Config.ANCHO / 2f) - (tamanio / 2f);
		posY = (Config.ALTO / 2f) - (tamanio / 2f);

		serpiente = new Serpiente(posX, posY, tamanio, tamanio);
		grilla = new Grilla(tamanio);
		random = new Random();

		manzana = new Manzana(posX + 40, posY + 40, tamanio, tamanio);
		
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(1, 1, 1);
		Render.batch.begin();
		Render.batch.end();
		
		procesarEntradas(delta);

		if(colisionConManzana()) {
			serpiente.crecer();
			moverManzanaAleatoria();
			puntuacion += 10; 
		}
	
		System.out.println("Puntuacion" + puntuacion);
		manzana.dibujar();
		serpiente.dibujar();
		grilla.dibujar();
	}

	private void procesarEntradas(float delta) {
		tiempo += delta;

		if (tiempo > 0.1f) {
			tiempo = 0;
			if (entrada.isArriba()) {
				posY += serpiente.getAlto();
			} else if (entrada.isAbajo()) {
				posY -= serpiente.getAlto();
			} else if (entrada.isDerecha()) {
				posX += serpiente.getAncho();
			} else if (entrada.isIzquierda()) {
				posX -= serpiente.getAncho();
			}
			// MOVER SOLO CUANDO PASA EL TIEMPO
			serpiente.mover(posX, posY);
		}
	}

	private boolean colisionConManzana() {
		return (manzana.getPosX() == serpiente.getPosX()) && 
		       (manzana.getPosY() == serpiente.getPosY());
	}

	private void moverManzanaAleatoria() {
	    // Obtener el margen de la grilla
	    int margen = grilla.getMargen();
	    
	    // Calcular el área jugable (sin los márgenes)
	    int areaJugableAncho = Config.ANCHO - (2 * margen);
	    int areaJugableAlto = Config.ALTO - (2 * margen);
	    
	    // Calcular cuántas celdas caben en el área jugable
	    int celdasX = areaJugableAncho / tamanio;
	    int celdasY = areaJugableAlto / tamanio;
	    
	    // Generar posición aleatoria dentro de la grilla
	    int celdaX = random.nextInt(celdasX);
	    int celdaY = random.nextInt(celdasY);
	    
	    // Convertir a coordenadas de píxeles (sumando el margen)
	    float nuevaX = margen + (celdaX * tamanio);
	    float nuevaY = margen + (celdaY * tamanio);
	    
	    manzana.setPosX(nuevaX);
	    manzana.setPosY(nuevaY);
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