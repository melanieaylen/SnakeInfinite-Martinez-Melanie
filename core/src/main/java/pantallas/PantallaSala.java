package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import elementos.Imagen;
import elementos.Texto;
import entradas.salidas.teclado.Entradas;
import interfaces.ControladorJuegoMultijugador;
import red.HiloCliente;
import utiles.Config;
import utiles.Recursos;
import utiles.Render;

public class PantallaSala implements Screen, ControladorJuegoMultijugador {

    private final int TAMANIO_TEXTO = 80;
    private final int TAMANIO_SUB = 50;
    private final int TAMANIO_PEQUENO = 35;

    // UI
    private Imagen fondo;
    private Texto titulo;
    private Texto textoEstado;
    private Texto textoJugador;
    private Texto textoEspera;
    private Texto textoVolver;
    private Texto textoInfo;
    private Music musica;

    private OrthographicCamera camara;
    private Viewport viewport;
    private Entradas entrada;

    // RED
    private HiloCliente hiloCliente;
    private int miNumeroJugador = 0;
    private boolean conectado = false;
    private boolean juegoIniciado = false;

    // ANIMACIÓN
    private float tiempo = 0;
    private float transparencia = 0;
    private int puntosAnimacion = 0;

    @Override
    public void show() {
        // Inicializar UI
        fondo = new Imagen(Recursos.FONDO_MENU);
        titulo = new Texto(Recursos.FUENTE, TAMANIO_TEXTO, Color.WHITE, Color.TEAL, -3, 3, false);
        textoEstado = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.YELLOW, Color.BLACK, -4, 4, true);
        textoJugador = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.SKY, Color.BLACK, -4, 4, true);
        textoEspera = new Texto(Recursos.FUENTE, TAMANIO_SUB, Color.WHITE, Color.BLACK, -4, 4, true);
        textoVolver = new Texto(Recursos.FUENTE, 40, Color.GRAY, Color.BLACK, -4, 4, true);
        textoInfo = new Texto(Recursos.FUENTE, TAMANIO_PEQUENO, Color.CYAN, Color.BLACK, -3, 3, true);

        // Configurar cámara
        camara = new OrthographicCamera();
        camara.setToOrtho(false, Config.ANCHO, Config.ALTO);
        viewport = new FitViewport(Config.ANCHO, Config.ALTO, camara);

        // Configurar entrada
        entrada = new Entradas();
        Gdx.input.setInputProcessor(entrada);

        // Música
        musica = Gdx.audio.newMusic(Gdx.files.internal(Recursos.MUSICA_MENU));
        musica.setLooping(true);
        musica.play();

        // Iniciar cliente y conectar
        hiloCliente = new HiloCliente(this);
        hiloCliente.start();
        hiloCliente.conectar();

        System.out.println("🔍 Buscando servidor...");
        System.out.println("📋 INSTRUCCIONES DE TESTEO:");
        System.out.println("   1. Ejecuta primero el SERVIDOR");
        System.out.println("   2. Ejecuta DOS instancias del CLIENTE");
        System.out.println("   3. Cuando ambos clientes estén conectados, el juego iniciará");
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0, 0, 0);

        actualizarAnimaciones(delta);
        procesarEntradas();

        if (juegoIniciado) {
            // ⚠️ CRÍTICO: Crear la pantalla de juego y pasar el HiloCliente
            PantallaJuegoMultijugador pantallaJuego = new PantallaJuegoMultijugador(hiloCliente, miNumeroJugador);
            
            // ⚠️ NUEVO: Cambiar el controlador del HiloCliente a la nueva pantalla
            hiloCliente.cambiarControlador(pantallaJuego);
            
            System.out.println("✅ Controlador cambiado a PantallaJuegoMultijugador");
            
            Render.app.setScreen(pantallaJuego);
            return;
        }

        viewport.apply();
        Render.batch.setProjectionMatrix(camara.combined);

        Render.batch.begin();
        fondo.setTransparencia(transparencia);
        fondo.dibujar();

        // Título
        titulo.dibujarTexto("Multijugador", 350, 800);

        // Estado de conexión
        if (!conectado) {
            textoEstado.dibujarTexto("Buscando servidor" + obtenerPuntos(), 400, 500);
            textoInfo.dibujarTexto("Asegúrate de que el servidor esté ejecutándose", 300, 400);
        } else {
            textoJugador.dibujarTexto("Conectado como Jugador " + miNumeroJugador, 350, 550);
            textoEspera.dibujarTexto("Esperando rival" + obtenerPuntos(), 450, 450);
            
            // Info útil para testeo
            if (miNumeroJugador == 1) {
                textoInfo.dibujarTexto("Se necesita 1 jugador más para iniciar", 350, 350);
            }
        }

        // Opción volver
        textoVolver.dibujarTexto("Presiona ESC para volver al menú", 350, 150);

        Render.batch.end();
    }

    private void actualizarAnimaciones(float delta) {
        // Transparencia
        transparencia += 0.012f;
        if (transparencia > 1) {
            transparencia = 1;
        }
        fondo.setTransparencia(transparencia);

        // Animación de puntos suspensivos
        tiempo += delta;
        if (tiempo > 0.5f) {
            tiempo = 0;
            puntosAnimacion = (puntosAnimacion + 1) % 4;
        }
    }

    private String obtenerPuntos() {
        switch (puntosAnimacion) {
            case 0: return "";
            case 1: return ".";
            case 2: return "..";
            case 3: return "...";
            default: return "";
        }
    }

    private void procesarEntradas() {
        if (entrada.isPausa() || Gdx.input.isKeyPressed(com.badlogic.gdx.Input.Keys.ESCAPE)) {
            volverAlMenu();
        }
    }

    // ===== IMPLEMENTACIÓN DE ControladorJuegoMultijugador =====

    @Override
    public void conectado(int numeroJugador) {
        this.miNumeroJugador = numeroJugador;
        this.conectado = true;
        System.out.println("✅ Conectado como Jugador " + numeroJugador);
        System.out.println("⏳ Esperando otro jugador...");
    }

    @Override
    public void iniciarJuego() {
        this.juegoIniciado = true;
        System.out.println("🎮 ¡Ambos jugadores conectados! Iniciando partida...");
    }

    @Override
    public void actualizarPosicionSerpiente(int numeroJugador, float x, float y) {
        // No usado en la sala
    }

    @Override
    public void actualizarFrutas(String datosFrutas) {
        // No usado en la sala
    }

    @Override
    public void jugadorComio(int numeroJugador, int puntos) {
        // No usado en la sala
    }

    @Override
    public void jugadorMurio(int numeroJugador, int vidasRestantes) {
        // No usado en la sala
    }

    @Override
    public void actualizarPuntuacion(String datosPuntuacion) {
        // No usado en la sala
    }

    @Override
    public void finDelJuego(int ganador) {
        // No usado en la sala
    }

    @Override
    public void jugadorDesconectado(int numeroJugador) {
        System.out.println("⚠️ Jugador " + numeroJugador + " se desconectó");
    }

    @Override
    public void volverAlMenu() {
        if (hiloCliente != null) {
            hiloCliente.desconectar();
            hiloCliente.terminar();
        }
        Render.app.setScreen(new PantallaMenu());
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
        if (musica != null && musica.isPlaying()) {
            musica.stop();
        }
    }

    @Override
    public void dispose() {
        if (musica != null) {
            musica.dispose();
        }
        if (titulo != null) titulo.dispose();
        if (textoEstado != null) textoEstado.dispose();
        if (textoJugador != null) textoJugador.dispose();
        if (textoEspera != null) textoEspera.dispose();
        if (textoVolver != null) textoVolver.dispose();
        if (textoInfo != null) textoInfo.dispose();
    }
}