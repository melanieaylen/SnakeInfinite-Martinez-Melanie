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
import red.HiloCliente;
import utiles.Config;
import utiles.ConfigJuego;
import utiles.Recursos;
import utiles.Render;

public class PantallaSala implements Screen {

    private Imagen fondo;
    private Texto titulo;
    private Texto textoEstado;
    private Texto textoJugador;
    private Texto textoNombre;
    private Texto textoJugadoresConectados;
    private Texto textoEspera;
    private Texto textoVolver;
    private Texto textoInfo;
    private Music musica;

    private OrthographicCamera camara;
    private Viewport viewport;
    private Entradas entrada;

    private HiloCliente hiloCliente;
    private int miNumeroJugador = 0;
    private boolean conectado = false;
    private boolean juegoIniciado = false;
    private boolean servidorLleno = false;
    
    private String[] nombresJugadores = new String[2];
    private int cantidadJugadoresConectados = 0;

    private float tiempo = 0;
    private float transparencia = 0;
    private int puntosAnimacion = 0;

    @Override
    public void show() {
        fondo = new Imagen(Recursos.FONDO);
        titulo = new Texto(Recursos.FUENTE, 80, Color.WHITE, Color.TEAL, -3, 3, false);
        textoEstado = new Texto(Recursos.FUENTE, 50, Color.YELLOW, Color.BLACK, -4, 4, true);
        textoJugador = new Texto(Recursos.FUENTE, 50, Color.SKY, Color.BLACK, -4, 4, true);
        textoNombre = new Texto(Recursos.FUENTE, 45, Color.GREEN, Color.BLACK, -4, 4, true);
        textoJugadoresConectados = new Texto(Recursos.FUENTE, 40, Color.WHITE, Color.BLACK, -4, 4, true);
        textoEspera = new Texto(Recursos.FUENTE, 50, Color.WHITE, Color.BLACK, -4, 4, true);
        textoVolver = new Texto(Recursos.FUENTE, 40, Color.GRAY, Color.BLACK, -4, 4, true);
        textoInfo = new Texto(Recursos.FUENTE, 35, Color.CYAN, Color.BLACK, -3, 3, true);

        camara = new OrthographicCamera();
        camara.setToOrtho(false, Config.ANCHO, Config.ALTO);
        viewport = new FitViewport(Config.ANCHO, Config.ALTO, camara);

        entrada = new Entradas();
        Gdx.input.setInputProcessor(entrada);

        musica = Gdx.audio.newMusic(Gdx.files.internal(Recursos.MUSICA_MENU));
        musica.setLooping(true);
        musica.play();

        hiloCliente = new HiloCliente(this);
        hiloCliente.start();
        hiloCliente.conectar();
        
        for (int i = 0; i < 2; i++) {
            nombresJugadores[i] = "";
        }
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0, 0, 0);

        if (juegoIniciado) {
            System.out.println("juegoIniciado = true, creando PantallaJuegoMultijugador...");
            PantallaJuegoMultijugador pantallaJuego = new PantallaJuegoMultijugador(hiloCliente, miNumeroJugador);
            hiloCliente.cambiarAPantallaJuego(pantallaJuego);
            Render.app.setScreen(pantallaJuego);
            return;
        }

        actualizarAnimaciones(delta);
        procesarEntradas();

        viewport.apply();
        Render.batch.setProjectionMatrix(camara.combined);

        Render.batch.begin();
        fondo.setTransparencia(transparencia);
        fondo.dibujar();

        titulo.dibujarTexto("Multijugador", 350, 800);

        if (servidorLleno) {
            textoEstado.dibujarTexto("Servidor lleno (2/2 jugadores)", 320, 500);
            textoInfo.dibujarTexto("Intenta conectarte mas tarde", 420, 400);
        } else if (!conectado) {
            textoEstado.dibujarTexto("Buscando servidor" + obtenerPuntos(), 400, 550);
            textoInfo.dibujarTexto("Asegurate de que el servidor este ejecutandose", 300, 450);
            textoInfo.dibujarTexto("Tu nombre: " + ConfigJuego.getInstancia().getNombreJugador(), 420, 350);
        } else {
            textoJugador.dibujarTexto("Eres: Jugador #" + miNumeroJugador, 500, 600);
            textoNombre.dibujarTexto(ConfigJuego.getInstancia().getNombreJugador(), 530, 545);
            
            textoJugadoresConectados.dibujarTexto("Jugadores conectados:", 480, 470);
            
            int yInicial = 420;
            int espaciado = 40;
            
            for (int i = 0; i < 2; i++) {
                if (nombresJugadores[i] != null && !nombresJugadores[i].isEmpty()) {
                    String linea = (i + 1) + ". " + nombresJugadores[i];
                    int yActual = yInicial - (i * espaciado);
                    
                    if (i + 1 == miNumeroJugador) {
                        linea += " (Tu)";
                        textoNombre.dibujarTexto(linea, 520, yActual);
                    } else {
                        textoJugadoresConectados.dibujarTexto(linea, 520, yActual);
                    }
                }
            }
            
            int yFinal = yInicial - (cantidadJugadoresConectados * espaciado) - 30;
            textoEspera.dibujarTexto("Esperando jugadores" + obtenerPuntos(), 420, yFinal);
            textoInfo.dibujarTexto("Se requieren 2 jugadores", 450, yFinal - 40);
        }

        textoVolver.dibujarTexto("Presiona ESC para volver al menu", 350, 150);

        Render.batch.end();
    }

    private void actualizarAnimaciones(float delta) {
        transparencia += 0.012f;
        if (transparencia > 1) transparencia = 1;
        
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

    public void cuandoSeConecta(int numeroJugador) {
        this.miNumeroJugador = numeroJugador;
        this.conectado = true;
        
        String miNombre = ConfigJuego.getInstancia().getNombreJugador();
        nombresJugadores[numeroJugador - 1] = miNombre;
        cantidadJugadoresConectados = Math.max(cantidadJugadoresConectados, numeroJugador);
        
        System.out.println("Conectado como J" + numeroJugador + ": " + miNombre);
    }

    public void cuandoActualizanNombres(String datosNombres) {
        System.out.println("[Sala] Recibiendo nombres: " + datosNombres);
        
        if (datosNombres == null || datosNombres.isEmpty()) {
            return;
        }
        
        String[] nombres = datosNombres.split("\\|");
        cantidadJugadoresConectados = 0;
        
        for (int i = 0; i < Math.min(nombres.length, 2); i++) {
            if (nombres[i] != null && !nombres[i].trim().isEmpty()) {
                nombresJugadores[i] = nombres[i].trim();
                cantidadJugadoresConectados++;
                System.out.println("   J" + (i + 1) + ": " + nombres[i].trim());
            } else {
                nombresJugadores[i] = "";
            }
        }
    }

    public void cuandoIniciaJuego() {
        System.out.println("MENSAJE 'INICIAR' RECIBIDO!");
        this.juegoIniciado = true;
    }

    public void cuandoRivalSeDesconecta(int numeroJugador) {
        if (numeroJugador > 0 && numeroJugador <= 2) {
            System.out.println("J" + numeroJugador + " (" + nombresJugadores[numeroJugador - 1] + ") se desconecto");
            nombresJugadores[numeroJugador - 1] = "";
            
            cantidadJugadoresConectados = 0;
            for (String nombre : nombresJugadores) {
                if (nombre != null && !nombre.isEmpty()) {
                    cantidadJugadoresConectados++;
                }
            }
        }
    }

    public void servidorLleno() {
        this.servidorLleno = true;
        
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
        if (titulo != null) titulo.dispose();
        if (textoEstado != null) textoEstado.dispose();
        if (textoJugador != null) textoJugador.dispose();
        if (textoNombre != null) textoNombre.dispose();
        if (textoJugadoresConectados != null) textoJugadoresConectados.dispose();
        if (textoEspera != null) textoEspera.dispose();
        if (textoVolver != null) textoVolver.dispose();
        if (textoInfo != null) textoInfo.dispose();
    }
}