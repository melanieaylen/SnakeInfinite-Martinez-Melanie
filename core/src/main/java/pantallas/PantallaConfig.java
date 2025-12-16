package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import elementos.Imagen;
import elementos.Texto;
import entradas.salidas.teclado.Entradas;
import utiles.ConfigJuego;
import utiles.Recursos;
import utiles.Render;

public class PantallaConfig implements Screen {
	
	private final int TAMANIO_TEXTO = 120;
	private final int TAMANIO_SUB = 60;
	
	private Imagen menu;
	private Imagen serpiente;
	private Music musica;
	private Sound sonidoBoton;
	
	private Texto titulo;
	private Texto subtitulo1;
	private Texto subtitulo2;
	private Texto subtitulo3;
	private Texto opcionElegida;
	private Texto textoNombre;
	
	private OrthographicCamera camara;
	private Viewport viewport;
	private Entradas entrada;
	
	private ConfigJuego config;
	private int indiceColorActual;
	
	private int opcionSeleccionada = 1;
	private float tiempoEntrada = 0;
	private float transparencia = 0;
	
	// Para editar el nombre
	private boolean editandoNombre = false;
	private String nombreTemporal = "";
	
	// ✅ NUEVO: Control de tecla Enter
	private boolean enterPresionadoAntes = false;
	
	// Para vista previa de la serpiente
	private final int TAMANIO_PREVIEW = 40;
	private final int SEGMENTOS_PREVIEW = 5;
	
	public PantallaConfig() {
		config = ConfigJuego.getInstancia();
		indiceColorActual = config.getIndiceColorSeleccionado();
		nombreTemporal = config.getNombreJugador();
	}

	@Override
	public void show() {
		menu = new Imagen(Recursos.FONDO);
		serpiente = new Imagen(Recursos.ICONO);
		serpiente.setParametros(720, 140, 130, 130);
		entrada = new Entradas();
		Gdx.input.setInputProcessor(entrada);
		
		camara = new OrthographicCamera();
		camara.setToOrtho(false, 1440, 900);
		viewport = new FitViewport(1440, 900, camara);
		
		sonidoBoton = Gdx.audio.newSound(Gdx.files.internal("sonidos/boton.ogg"));
		musica = Gdx.audio.newMusic(Gdx.files.internal("sonidos/musica.mp3"));
		musica.setLooping(true);
		musica.play();
		
		titulo = new Texto(Recursos.FUENTE, TAMANIO_TEXTO, Color.WHITE, Color.TEAL, -3, 3, false);
		subtitulo1 = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
		subtitulo2 = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
		subtitulo3 = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
		opcionElegida = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.SKY, Color.BLACK, -4, 4, true);
		textoNombre = new Texto(Recursos.FUENTE, TAMANIO_SUB-20, Color.WHITE, Color.BLACK, -4, 4, true);
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0, 0, 0);
		
		procesarTransparencia();
		procesarEntradas(delta);
		actualizarInterfaz();
		
		viewport.apply();
		Render.batch.setProjectionMatrix(camara.combined);
		Render.shaper.setProjectionMatrix(camara.combined);
		
		// Dibujar fondo
		Render.batch.begin();
		menu.dibujar();
		Render.batch.end();
		
		// Vista previa de la serpiente
		dibujarPreviewSerpiente();
		
		// Textos
		Render.batch.begin();
		
		titulo.dibujarTexto("Configuración", 300, 800);
		
		// Opción 1: Nombre
		subtitulo1.dibujarTexto("Nombre: " + nombreTemporal + 
								(editandoNombre ? "_" : ""), 450, 650);
		
		// Opción 2: Color
		subtitulo2.dibujarTexto("<  " + ConfigJuego.NOMBRES_COLORES[indiceColorActual] + " >", 
								550, 480);
		
		// Opción 3: Volver
		subtitulo3.dibujarTexto("Volver al Menú", 530, 270);

		// Indicador de opción seleccionada
		if (opcionSeleccionada == 1 && !editandoNombre) {
			opcionElegida.dibujarTexto("> ", 350, 650);
		} else if (opcionSeleccionada == 2) {
			opcionElegida.dibujarTexto("> ", 450, 480);
		} else if (opcionSeleccionada == 3) {
			opcionElegida.dibujarTexto("> ", 400, 270);
		}
		
		// Instrucción cuando se edita nombre
		if (editandoNombre) {
			textoNombre.dibujarTexto("Escribe tu nombre (Enter para confirmar)", 320, 580);
		}
		
		Render.batch.end();
	}
	
	private void dibujarPreviewSerpiente() {
		Render.shaper.begin(ShapeType.Filled);
		
		Color[] coloresActuales = ConfigJuego.PALETA_COLORES[indiceColorActual];
		float centroX = 720 - (SEGMENTOS_PREVIEW * TAMANIO_PREVIEW) / 2;
		float centroY = 360;
		
		for (int i = 0; i < SEGMENTOS_PREVIEW; i++) {
			if (i == 0) {
				Render.shaper.setColor(coloresActuales[0]);
			} else {
				Render.shaper.setColor(coloresActuales[1]);
			}
			
			float x = centroX + (i * TAMANIO_PREVIEW);
			Render.shaper.rect(x, centroY, TAMANIO_PREVIEW, TAMANIO_PREVIEW);
		}
		
		Render.shaper.end();
	}
	
	private void procesarTransparencia() {
		transparencia += 0.012f;
		if (transparencia > 1) {
			transparencia = 1;
		}
		menu.setTransparencia(transparencia);
	}
	
	private void procesarEntradas(float delta) {
		tiempoEntrada += delta;
		
		// Si estamos editando el nombre
		if (editandoNombre) {
			procesarEdicionNombre();
			return;
		}
		
		// Navegación normal
		if (entrada.isAbajo() && tiempoEntrada > 0.2f) {
			tiempoEntrada = 0;
			opcionSeleccionada++;
			if (opcionSeleccionada > 3) {
				opcionSeleccionada = 1;
			}
		}
		
		if (entrada.isArriba() && tiempoEntrada > 0.2f) {
			tiempoEntrada = 0;
			opcionSeleccionada--;
			if (opcionSeleccionada < 1) {
				opcionSeleccionada = 3;
			}
		}
		
		// Cambiar color con flechas
		if (opcionSeleccionada == 2) {
			if (entrada.isDerecha() && tiempoEntrada > 0.2f) {
				tiempoEntrada = 0;
				indiceColorActual++;
				if (indiceColorActual >= ConfigJuego.PALETA_COLORES.length) {
					indiceColorActual = 0;
				}
			}
			
			if (entrada.isIzquierda() && tiempoEntrada > 0.2f) {
				tiempoEntrada = 0;
				indiceColorActual--;
				if (indiceColorActual < 0) {
					indiceColorActual = ConfigJuego.PALETA_COLORES.length - 1;
				}
			}
		}
		
		// ✅ CORREGIDO: Control de Enter con detección de flanco
		boolean enterAhora = entrada.isEnter();
		
		if (enterAhora && !enterPresionadoAntes) {
			sonidoBoton.play();
			
			if (opcionSeleccionada == 1) {
				// Editar nombre
				editandoNombre = true;
				nombreTemporal = config.getNombreJugador();
			} else if (opcionSeleccionada == 2) {
				// Guardar color
				config.setColorPorIndice(indiceColorActual);
			} else if (opcionSeleccionada == 3) {
				// Volver al menú
				guardarConfiguracion();
				Render.app.setScreen(new PantallaMenu());
			}
		}
		
		enterPresionadoAntes = enterAhora;
	}
	
	private void procesarEdicionNombre() {
		// Capturar teclas para el nombre
		for (int i = Input.Keys.A; i <= Input.Keys.Z; i++) {
			if (Gdx.input.isKeyJustPressed(i)) {
				if (nombreTemporal.length() < 15) {
					nombreTemporal += (char) ('A' + (i - Input.Keys.A));
				}
			}
		}
		
		// Tecla espacio
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
			if (nombreTemporal.length() < 15 && nombreTemporal.length() > 0) {
				nombreTemporal += " ";
			}
		}
		
		// Borrar
		if (Gdx.input.isKeyJustPressed(Input.Keys.BACKSPACE)) {
			if (nombreTemporal.length() > 0) {
				nombreTemporal = nombreTemporal.substring(0, nombreTemporal.length() - 1);
			}
		}
		
		// ✅ CORREGIDO: Usar isKeyJustPressed en lugar de entrada.isEnter()
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			if (nombreTemporal.trim().length() > 0) {
				config.setNombreJugador(nombreTemporal.trim());
			}
			editandoNombre = false;
		}
		
		// Cancelar
		if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			nombreTemporal = config.getNombreJugador();
			editandoNombre = false;
		}
	}
	
	private void guardarConfiguracion() {
		config.setColorPorIndice(indiceColorActual);
		config.setNombreJugador(nombreTemporal.trim());
	}
	
	private void actualizarInterfaz() {
		if (editandoNombre) {
			subtitulo1.setColor(Color.YELLOW);
			subtitulo2.setColor(Color.WHITE);
			subtitulo3.setColor(Color.WHITE);
		} else if (opcionSeleccionada == 1) {
			subtitulo1.setColor(Color.SKY);
			subtitulo2.setColor(Color.WHITE);
			subtitulo3.setColor(Color.WHITE);
		} else if (opcionSeleccionada == 2) {
			subtitulo1.setColor(Color.WHITE);
			subtitulo2.setColor(Color.SKY);
			subtitulo3.setColor(Color.WHITE);
		} else if (opcionSeleccionada == 3) {
			subtitulo1.setColor(Color.WHITE);
			subtitulo2.setColor(Color.WHITE);
			subtitulo3.setColor(Color.SKY);
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height);
	}

	@Override
	public void pause() {}

	@Override
	public void resume() {}

	@Override
	public void hide() {
		if (musica != null && musica.isPlaying()) {
			musica.stop();
		}
	}

	@Override
	public void dispose() {
		if (musica != null) musica.dispose();
		if (sonidoBoton != null) sonidoBoton.dispose();
		if (titulo != null) titulo.dispose();
		if (subtitulo1 != null) subtitulo1.dispose();
		if (subtitulo2 != null) subtitulo2.dispose();
		if (subtitulo3 != null) subtitulo3.dispose();
		if (opcionElegida != null) opcionElegida.dispose();
		if (textoNombre != null) textoNombre.dispose();
	}
}