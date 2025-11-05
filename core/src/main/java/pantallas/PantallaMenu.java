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

	private Imagen menu;
	private Imagen serpiente;

	private Music musica;
	private boolean aparicion = false;
	private float a = 0;
	private float contTiempo = 0, tiempoEspera = 10;

	private Texto titulo;
	private Texto subtitulo1;
	private Texto subtitulo2;
	private Texto opcionElegida; 
	
	Entradas entrada = new Entradas(this);
	int opc = 1; 
	public float tiempo = 0; 
	
	@Override
	public void show() {
		menu = new Imagen(Recursos.FONDO_MENU);
		serpiente = new Imagen(Recursos.ICONO);
		serpiente.setParametros(600, 150, 130, 130);

		Gdx.input.setInputProcessor(entrada);
		
		musica = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica.mp3"));
		musica.play();

		titulo = new Texto(Recursos.FUENTE, 120, Color.WHITE, Color.TEAL, -3, 3, false);
		subtitulo1 = new Texto(Recursos.FUENTE, 50, Color.WHITE, Color.BLACK, -4, 4, true);
		subtitulo2 = new Texto(Recursos.FUENTE, 50, Color.WHITE, Color.BLACK, -4, 4, true);
		opcionElegida = new Texto(Recursos.FUENTE, 50, Color.SKY, Color.BLACK, -4, 4, true);
		
//		anchoTexto = (Config.ANCHO/2) - (titulo.getAncho()/2);
//		altoTexto = (Config.ALTO/2) - (titulo.getAlto()/2);	
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0, 0, 0);
		//procesarTransparencia();
		Render.begin();
		menu.dibujar();
		serpiente.dibujar();
		titulo.dibujarTexto("Snake Infinite", 245, 780);
		subtitulo1.dibujarTexto("   Un Jugador", 468, 520);
		subtitulo2.dibujarTexto("   Multijugador", 448, 410);
		tiempo+= delta;
		if(entrada.isAbajo()) {
			if(tiempo > 0.3f) {
				tiempo = 0; 
			opc++; 
			if(opc>2) {
				opc = 1; 
			}
			}
		}
		
		if(entrada.isArriba()) {
		    if(tiempo > 0.3f) {
		        tiempo = 0; 
		        opc--; 
		        if(opc < 1) {
		            opc = 2; 
		        }
		    }
		}
		
		if(opc == 1) {
			subtitulo1.setColor(Color.SKY);
			subtitulo2.setColor(Color.WHITE);
			opcionElegida.dibujarTexto("> ", 458, 520);
			if(entrada.isEnter()) {
				Render.app.setScreen(new PantallaJuego());
			}
		}
		else if(opc == 2) {
			subtitulo1.setColor(Color.WHITE);
			subtitulo2.setColor(Color.SKY);
			opcionElegida.dibujarTexto("> ", 440, 410);
			if(entrada.isEnter()) {
				Render.app.setScreen(new PantallaEjemplo());
			}
		}
		System.out.println(entrada.isAbajo());
		
		Render.end();
	}

	private void procesarTransparencia() {
		if (!aparicion) {
			a += 0.01f;
			if (a > 1) {
				a = 1;
				aparicion = true;
			}
		} else {
			contTiempo += 0.1f;
			if (contTiempo > tiempoEspera) {
				a -= 0.01f;
				if (a < 0) {
					a = 0;
					Render.app.setScreen(new PantallaJuego());
				}
			}
		}
		menu.setTransparencia(a);
	}

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		musica.dispose();
		titulo.dispose();
		subtitulo1.dispose();
		subtitulo2.dispose();
	}
}
