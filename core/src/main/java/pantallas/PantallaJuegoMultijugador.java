package pantallas;

import java.util.HashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import elementos.Direcciones;
import elementos.FrutaRemota;
import elementos.Grilla;
import elementos.SerpienteRemota;
import elementos.Texto;
import elementos.TipoFruta;
import entradas.salidas.teclado.Entradas;
import red.HiloCliente;
import utiles.Config;
import utiles.ConfigJuego;
import utiles.Recursos;
import utiles.Render;

public class PantallaJuegoMultijugador implements Screen {

	private final int TAMANIO_ELEMENTOS = 30;
	private final int MAX_JUGADORES = 2; // ✅ CAMBIO: De 3 a 2 jugadores

	private HiloCliente hiloCliente;
	private int miNumeroJugador;

	private Map<Integer, SerpienteRemota> serpientes;
	private Map<String, FrutaRemota> frutas;

	private OrthographicCamera camaraJuego;
	private Viewport viewportJuego;
	private OrthographicCamera camaraUI;
	private Viewport viewportUI;

	private Texto textoPuntuacion;
	private Texto textoVidas;
	private Texto textoGanador;
	private Texto textoDesconectado;
	private Grilla grilla;

	private Entradas entrada;
	private Sound sonidoComer;

	private boolean juegoTerminado = false;
	private boolean alguienSeDesconecto = false;
	private int[] puntuaciones = { 0, 0 }; // ✅ CAMBIO: Array de 2
	private int[] vidas = { 3, 3 }; // ✅ CAMBIO: Array de 2
	private Direcciones ultimaDireccion = Direcciones.NINGUNA;

	private long ultimoCambioDireccion = 0;
	private static final long COOLDOWN_DIRECCION = 50;

	private int actualizacionesRecibidas = 0;

	public PantallaJuegoMultijugador(HiloCliente hiloCliente, int miNumeroJugador) {
		this.hiloCliente = hiloCliente;
		this.miNumeroJugador = miNumeroJugador;
		this.serpientes = new HashMap<>();
		this.frutas = new HashMap<>();

		System.out.println("[Cliente] Pantalla de juego creada para J" + miNumeroJugador);
	}

	@Override
	public void show() {
		textoPuntuacion = new Texto(Recursos.TEXTO, 28, Color.WHITE, Color.BLACK, -4, 4, true);
		textoVidas = new Texto(Recursos.TEXTO, 28, Color.WHITE, Color.BLACK, -4, 4, true);
		textoGanador = new Texto(Recursos.FUENTE, 60, Color.YELLOW, Color.BLACK, -4, 4, true);
		textoDesconectado = new Texto(Recursos.FUENTE, 50, Color.RED, Color.BLACK, -4, 4, true);

		grilla = new Grilla(TAMANIO_ELEMENTOS);
		sonidoComer = Gdx.audio.newSound(Gdx.files.internal(Recursos.SONIDO_COMER));

		entrada = new Entradas();
		Gdx.input.setInputProcessor(entrada);

		configurarCamaras();

		System.out.println("[Cliente J" + miNumeroJugador + "] Pantalla de juego lista");
	}

	private void configurarCamaras() {
		camaraJuego = new OrthographicCamera();
		camaraJuego.setToOrtho(false, Config.ANCHO, Config.ALTO);
		viewportJuego = new FitViewport(Config.ANCHO, Config.ALTO, camaraJuego);
		camaraJuego.position.set(0, 0, 0);
		camaraJuego.update();

		camaraUI = new OrthographicCamera();
		viewportUI = new FitViewport(Config.ANCHO, Config.ALTO, camaraUI);
		camaraUI.position.set(Config.ANCHO / 2, Config.ALTO / 2, 0);
		camaraUI.update();
	}

	@Override
	public void render(float delta) {
		Render.limpiarPantalla(0, 0, 0);

		procesarEntradas();
		actualizarCamara();
		renderizarMundoJuego();
		renderizarUI();
	}

	private void procesarEntradas() {
		if (juegoTerminado || alguienSeDesconecto)
			return;

		long ahora = System.currentTimeMillis();
		if (ahora - ultimoCambioDireccion < COOLDOWN_DIRECCION) {
			return;
		}

		Direcciones nuevaDireccion = null;

		if (entrada.isArriba() && ultimaDireccion != Direcciones.ABAJO) {
			nuevaDireccion = Direcciones.ARRIBA;
		} else if (entrada.isAbajo() && ultimaDireccion != Direcciones.ARRIBA) {
			nuevaDireccion = Direcciones.ABAJO;
		} else if (entrada.isDerecha() && ultimaDireccion != Direcciones.IZQUIERDA) {
			nuevaDireccion = Direcciones.DERECHA;
		} else if (entrada.isIzquierda() && ultimaDireccion != Direcciones.DERECHA) {
			nuevaDireccion = Direcciones.IZQUIERDA;
		}

		if (nuevaDireccion != null && nuevaDireccion != ultimaDireccion) {
			hiloCliente.mover(nuevaDireccion);
			ultimaDireccion = nuevaDireccion;
			ultimoCambioDireccion = ahora;
		}
	}

	private void actualizarCamara() {
		SerpienteRemota miSerpiente = serpientes.get(miNumeroJugador);
		if (miSerpiente != null) {
			float targetX = miSerpiente.obtenerPosX() + TAMANIO_ELEMENTOS / 2;
			float targetY = miSerpiente.obtenerPosY() + TAMANIO_ELEMENTOS / 2;
			camaraJuego.position.set(targetX, targetY, 0);
			camaraJuego.update();
		}
	}

	private void renderizarMundoJuego() {
		viewportJuego.apply();
		Render.batch.setProjectionMatrix(camaraJuego.combined);
		Render.shaper.setProjectionMatrix(camaraJuego.combined);

		Render.shaper.begin(ShapeType.Filled);
		grilla.dibujarGrilla(camaraJuego);
		Render.shaper.end();

		for (FrutaRemota fruta : frutas.values()) {
			fruta.dibujar();
		}

		Render.shaper.begin(ShapeType.Filled);
		for (SerpienteRemota serpiente : serpientes.values()) {
			serpiente.dibujar();
		}
		Render.shaper.end();
	}

	private void renderizarUI() {
		viewportUI.apply();
		Render.batch.setProjectionMatrix(camaraUI.combined);

		Render.batch.begin();

		StringBuilder sbPuntos = new StringBuilder();
		StringBuilder sbVidas = new StringBuilder();

		// ✅ CAMBIO: Iterar solo 2 jugadores
		for (int i = 0; i < MAX_JUGADORES; i++) {
			if (serpientes.containsKey(i + 1)) {
				if (sbPuntos.length() > 0) {
					sbPuntos.append("  |  ");
					sbVidas.append("  |  ");
				}
				sbPuntos.append("J").append(i + 1).append(": ").append(puntuaciones[i]);
				sbVidas.append("J").append(i + 1).append(": ").append(vidas[i]);
			}
		}

		textoPuntuacion.dibujarTexto(sbPuntos.toString(), 20, Config.ALTO - 30);
		textoVidas.dibujarTexto(sbVidas.toString(), 20, Config.ALTO - 65);

		if (alguienSeDesconecto) {
			textoDesconectado.dibujarTexto("Un jugador se desconecto", Config.ANCHO / 2 - 280, Config.ALTO / 2 + 50);
			textoDesconectado.dibujarTexto("Volviendo al menu...", Config.ANCHO / 2 - 200, Config.ALTO / 2 - 20);
		} else if (juegoTerminado) {
			textoGanador.dibujarTexto("Jugador " + obtenerGanador() + " GANO!", Config.ANCHO / 2 - 250,
					Config.ALTO / 2);
		}

		Render.batch.end();
	}

	private int obtenerGanador() {
		int maxVidas = -1;
		int ganador = 1;

		// ✅ CAMBIO: Verificar solo 2 jugadores
		for (int i = 0; i < MAX_JUGADORES; i++) {
			if (vidas[i] > maxVidas) {
				maxVidas = vidas[i];
				ganador = i + 1;
			}
		}

		return ganador;
	}

	public void cuandoActualizanSerpiente(int numeroJugador, String datosSegmentos) {
		actualizacionesRecibidas++;

		if (actualizacionesRecibidas % 100 == 0) {
			System.out.println("[Cliente J" + miNumeroJugador + "] Actualizacion #" + actualizacionesRecibidas);
			System.out.println("   Serpientes activas: " + serpientes.size());
			for (Integer num : serpientes.keySet()) {
				System.out.println("      - J" + num + " con " + serpientes.get(num).obtenerTamanio() + " segmentos");
			}
		}

		SerpienteRemota serpiente = serpientes.get(numeroJugador);

		if (serpiente == null) {
			Color colorCabeza, colorCuerpo;

			if (numeroJugador == miNumeroJugador) {
				colorCabeza = ConfigJuego.getInstancia().getColorCabeza();
				colorCuerpo = ConfigJuego.getInstancia().getColorCuerpo();
			} else {
				int indicePaleta = obtenerIndiceColorParaJugador(numeroJugador);
				colorCabeza = ConfigJuego.PALETA_COLORES[indicePaleta][0];
				colorCuerpo = ConfigJuego.PALETA_COLORES[indicePaleta][1];
			}

			serpiente = new SerpienteRemota(0, 0, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, colorCabeza, colorCuerpo);
			serpientes.put(numeroJugador, serpiente);

			System.out.println("[Cliente J" + miNumeroJugador + "] Creada serpiente para J" + numeroJugador);
		}

		serpiente.actualizarSegmentos(datosSegmentos);
	}

	private int obtenerIndiceColorParaJugador(int numeroJugador) {
		int miIndice = ConfigJuego.getInstancia().getIndiceColorSeleccionado();
		int totalColores = ConfigJuego.PALETA_COLORES.length;

		int offset = (numeroJugador - 1);
		int indiceCalculado = (miIndice + offset) % totalColores;
		if (indiceCalculado == miIndice) {
			indiceCalculado = (indiceCalculado + 1) % totalColores;
		}

		return indiceCalculado;
	}

	public void cuandoActualizanFrutas(String datosFrutas) {
		System.out.println("[Cliente J" + miNumeroJugador + "] Recibiendo frutas: "
				+ (datosFrutas != null ? datosFrutas.substring(0, Math.min(100, datosFrutas.length())) : "null"));

		for (FrutaRemota fruta : frutas.values()) {
			fruta.dispose();
		}
		frutas.clear();

		if (datosFrutas == null || datosFrutas.isEmpty()) {
			System.out.println("Sin datos de frutas");
			return;
		}

		String[] arregloFrutas = datosFrutas.split("\\|");
		System.out.println("Procesando " + arregloFrutas.length + " frutas");

		for (String frutaStr : arregloFrutas) {
			String[] partes = frutaStr.split(":");
			if (partes.length >= 3) {
				try {
					TipoFruta tipo = TipoFruta.valueOf(partes[0]);
					float x = Float.parseFloat(partes[1]);
					float y = Float.parseFloat(partes[2]);

					FrutaRemota fruta = new FrutaRemota(x, y, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, tipo);
					frutas.put(frutaStr, fruta);
				} catch (Exception e) {
					System.err.println("Error parseando fruta: " + frutaStr);
				}
			}
		}

		System.out.println("Total frutas cargadas: " + frutas.size());
	}

	public void cuandoJugadorCome(int numeroJugador, int puntos) {
		System.out.println("[Cliente J" + miNumeroJugador + "] J" + numeroJugador + " comio (+" + puntos + " pts)");
		sonidoComer.play();
	}

	public void cuandoJugadorMuere(int numeroJugador, int vidasRestantes) {
		int indice = numeroJugador - 1;
		if (indice >= 0 && indice < MAX_JUGADORES) {
			vidas[indice] = vidasRestantes;
			System.out.println("[Cliente J" + miNumeroJugador + "] J" + numeroJugador + " murio (quedan "
					+ vidasRestantes + " vidas)");
		}
	}

	public void cuandoActualizanPuntaje(String datosPuntuacion) {
		String[] puntajes = datosPuntuacion.split(":");
		for (int i = 0; i < Math.min(puntajes.length, MAX_JUGADORES); i++) {
			try {
				puntuaciones[i] = Integer.parseInt(puntajes[i]);
			} catch (NumberFormatException e) {
				// Ignorar
			}
		}
	}

	public void cuandoTerminaJuego(int ganador) {
		juegoTerminado = true;
		System.out.println("[Cliente J" + miNumeroJugador + "] Juego terminado, gano J" + ganador);

		new Thread(() -> {
			try {
				Thread.sleep(5000);
				Gdx.app.postRunnable(this::volverAlMenu);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void cuandoRivalSeDesconecta(int numeroJugador) {
		alguienSeDesconecto = true;
		System.out.println("[Cliente J" + miNumeroJugador + "] J" + numeroJugador + " se desconecto");

		new Thread(() -> {
			try {
				Thread.sleep(3000);
				Gdx.app.postRunnable(this::volverAlMenu);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}).start();
	}

	public void volverAlMenu() {
		if (hiloCliente != null) {
			hiloCliente.terminar();
		}
		Render.app.setScreen(new PantallaMenu());
	}

	@Override
	public void resize(int width, int height) {
		viewportJuego.update(width, height, true);
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
		for (FrutaRemota fruta : frutas.values()) {
			fruta.dispose();
		}
		frutas.clear();

		if (sonidoComer != null)
			sonidoComer.dispose();
		if (textoPuntuacion != null)
			textoPuntuacion.dispose();
		if (textoVidas != null)
			textoVidas.dispose();
		if (textoGanador != null)
			textoGanador.dispose();
		if (textoDesconectado != null)
			textoDesconectado.dispose();
	}
}