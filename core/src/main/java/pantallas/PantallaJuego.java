package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

import elementos.Direcciones;
import elementos.EstadoJuego;
import elementos.Fruta;
import elementos.GestorFrutas;
import elementos.Grilla;
import elementos.Serpiente;
import elementos.Texto;
import entradas.salidas.teclado.Entradas;
import interfaces.ControladorJuego;
import jugadores.Jugador;
import utiles.Config;
import utiles.Recursos;
import utiles.Render;

/**
 * PantallaJuego refactorizada siguiendo el patrÃ³n del Pong
 * Ahora implementa ControladorJuego (como GameScreen implementa GameController)
 */
public class PantallaJuego implements Screen, ControladorJuego {

    // CONSTANTES
    private final int TAMANIO_ELEMENTOS = 30;
    private final float VELOCIDAD_SERPIENTE = 0.12f;

    // JUGADOR Y ELEMENTOS
    private Jugador jugador;
    private GestorFrutas gestorFrutas;
    private Grilla grilla;

    // CÃMARAS Y VIEWPORT
    private OrthographicCamera camaraJuego;
    private Viewport viewportJuego;
    private OrthographicCamera camaraUI;
    private Viewport viewportUI;

    // UI
    private Texto textoPuntuacion;
    private Texto textoVidas;
    private Texto textoInvulnerable;

    // ENTRADA
    private Entradas entrada;

    // AUDIO
    private Sound sonidoComer;

    // LÃ“GICA
    private float posElementosX;
    private float posElementosY;
    private float tiempo;
    private EstadoJuego estadoGuardado;

    /**
     * Constructor para juego nuevo
     */
    public PantallaJuego() {
        this.estadoGuardado = null;
    }

    /**
     * Constructor para restaurar estado (desde pausa)
     */
    public PantallaJuego(EstadoJuego estado) {
        this.estadoGuardado = estado;
    }

    @Override
    public void show() {
        // Inicializar UI
        textoPuntuacion = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
        textoVidas = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
        textoInvulnerable = new Texto(Recursos.TEXTO, 33, Color.YELLOW, Color.BLACK, -4, 4, true);

        // Inicializar elementos bÃ¡sicos
        grilla = new Grilla(TAMANIO_ELEMENTOS);
        sonidoComer = Gdx.audio.newSound(Gdx.files.internal(Recursos.SONIDO_COMER));

        // Configurar entrada
        entrada = new Entradas();
        Gdx.input.setInputProcessor(entrada);

        // Restaurar estado o inicializar nuevo juego
        if (estadoGuardado != null) {
            restaurarEstado();
        } else {
            inicializarNuevoJuego();
        }

        // Configurar cÃ¡maras
        configurarCamaras();
    }

    /**
     * Inicializa un juego nuevo
     */
    private void inicializarNuevoJuego() {
        posElementosX = 0;
        posElementosY = 0;
        tiempo = 0;

        // Crear serpiente
        Serpiente serpiente = new Serpiente(posElementosX, posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);

        // Crear jugador
        jugador = new Jugador(1, "Jugador 1", serpiente);

        // Inicializar frutas
        gestorFrutas = new GestorFrutas(TAMANIO_ELEMENTOS);
        gestorFrutas.inicializarFrutas(serpiente);
    }

    /**
     * Restaura el estado guardado
     */
    private void restaurarEstado() {
        jugador = estadoGuardado.getJugador();
        gestorFrutas = estadoGuardado.getGestorFrutas();
        posElementosX = estadoGuardado.getPosElementosX();
        posElementosY = estadoGuardado.getPosElementosY();
        tiempo = estadoGuardado.getTiempo();
    }

    /**
     * Configura las cÃ¡maras del juego y UI
     */
    private void configurarCamaras() {
        // CÃ¡mara del mundo del juego
        camaraJuego = new OrthographicCamera();
        camaraJuego.setToOrtho(false, Config.ANCHO, Config.ALTO);
        viewportJuego = new FitViewport(Config.ANCHO, Config.ALTO, camaraJuego);
        camaraJuego.position.set(posElementosX, posElementosY, 0);
        camaraJuego.update();

        // CÃ¡mara fija de la UI
        camaraUI = new OrthographicCamera();
        viewportUI = new FitViewport(Config.ANCHO, Config.ALTO, camaraUI);
        camaraUI.position.set(Config.ANCHO / 2, Config.ALTO / 2, 0);
        camaraUI.update();
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0, 0, 0);

        // Actualizar
        actualizar(delta);

        // Renderizar mundo del juego
        renderizarMundoJuego();

        // Renderizar UI
        renderizarUI();
    }

    /**
     * Actualiza la lÃ³gica del juego
     */
    private void actualizar(float delta) {
        // Procesar entradas
        procesarEntradas(delta);

        // Actualizar invulnerabilidad
        jugador.actualizarInvulnerabilidad(delta);

        // Verificar colisiones con frutas
        Fruta frutaColisionada = gestorFrutas.verificarColisiones(jugador.getSerpiente());
        if (frutaColisionada != null) {
            frutaComida(frutaColisionada.getPuntos());
            gestorFrutas.reubicarFruta(frutaColisionada, jugador.getSerpiente());
        }

        // Verificar colisiÃ³n con sÃ­ misma (solo si no es invulnerable)
        if (!jugador.isInvulnerable() && jugador.getSerpiente().colisionSerpiente()) {
            perderVida();
        }

        // Actualizar cÃ¡mara
        actualizarCamara();
    }

    /**
     * Renderiza el mundo del juego (grilla, serpiente, frutas)
     */
    private void renderizarMundoJuego() {
        viewportJuego.apply();
        Render.batch.setProjectionMatrix(camaraJuego.combined);
        Render.shaper.setProjectionMatrix(camaraJuego.combined);

        // Dibujar con ShapeRenderer
        Render.shaper.begin(ShapeType.Filled);
        grilla.dibujarGrilla(camaraJuego);

        // Dibujar serpiente con efecto de parpadeo si es invulnerable
        if (!jugador.isInvulnerable() || (int) (jugador.getTiempoInvulnerabilidad() * 10) % 2 == 0) {
            jugador.getSerpiente().dibujar();
        }

        Render.shaper.end();

        // Dibujar frutas
        gestorFrutas.dibujarTodas();
    }

    /**
     * Renderiza la interfaz de usuario
     */
    private void renderizarUI() {
        viewportUI.apply();
        Render.batch.setProjectionMatrix(camaraUI.combined);

        Render.batch.begin();

        // PuntuaciÃ³n
        textoPuntuacion.dibujarTexto("Puntos: " + jugador.getPuntuacion(), 20, Config.ALTO - 35);

        // Vidas
        textoVidas.dibujarTexto("Vidas: " + jugador.getVidas(), 20, Config.ALTO - 70);

        // Mensaje de invulnerabilidad
        if (jugador.isInvulnerable()) {
            textoInvulnerable.dibujarTexto("INVULNERABLE!", Config.ANCHO / 2 - 150, Config.ALTO / 2);
        }

        Render.batch.end();
    }

    /**
     * Procesa las entradas del teclado
     */
    private void procesarEntradas(float delta) {
        // Cambiar direcciÃ³n
        if (entrada.isArriba()) {
            moverJugador(Direcciones.ARRIBA);
        } else if (entrada.isAbajo()) {
            moverJugador(Direcciones.ABAJO);
        } else if (entrada.isDerecha()) {
            moverJugador(Direcciones.DERECHA);
        } else if (entrada.isIzquierda()) {
            moverJugador(Direcciones.IZQUIERDA);
        }

        // Pausar
        if (entrada.isPausa()) {
            pausarJuego();
            return;
        }

        // Mover serpiente
        moverSerpiente(delta);
    }

    /**
     * Mueve la serpiente segÃºn el tiempo y direcciÃ³n actual
     */
    private void moverSerpiente(float delta) {
        tiempo += delta;

        if (tiempo > VELOCIDAD_SERPIENTE) {
            tiempo = 0;

            Direcciones direccion = jugador.getDireccionActual();

            switch (direccion) {
                case ARRIBA:
                    posElementosY += TAMANIO_ELEMENTOS;
                    break;
                case ABAJO:
                    posElementosY -= TAMANIO_ELEMENTOS;
                    break;
                case DERECHA:
                    posElementosX += TAMANIO_ELEMENTOS;
                    break;
                case IZQUIERDA:
                    posElementosX -= TAMANIO_ELEMENTOS;
                    break;
                case NINGUNA:
                    break;
            }

            jugador.getSerpiente().mover(posElementosX, posElementosY);
        }
    }

    /**
     * Actualiza la cÃ¡mara para seguir a la serpiente
     */
    private void actualizarCamara() {
        float targetX = jugador.getSerpiente().getPosX() + TAMANIO_ELEMENTOS / 2;
        float targetY = jugador.getSerpiente().getPosY() + TAMANIO_ELEMENTOS / 2;

        camaraJuego.position.set(targetX, targetY, 0);
        camaraJuego.update();
    }

    // ===== IMPLEMENTACIÃ“N DE ControladorJuego =====

    @Override
    public void frutaComida(int puntos) {
        sonidoComer.play();
        jugador.agregarPuntos(puntos);
        jugador.crecerSerpiente();
    }

    @Override
    public void perderVida() {
        boolean tienesVidas = jugador.perderVida();

        if (!tienesVidas) {
            finDelJuego();
        } else {
            // Resetear posiciÃ³n
            posElementosX = 0;
            posElementosY = 0;
            jugador.resetearPosicion(posElementosX, posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
        }
    }

    @Override
    public void finDelJuego() {
        Render.app.setScreen(new PantallaGameOver());
    }

    @Override
    public void moverJugador(Direcciones direccion) {
        jugador.cambiarDireccion(direccion);
    }

    @Override
    public void pausarJuego() {
        EstadoJuego estado = new EstadoJuego(jugador, gestorFrutas, posElementosX, posElementosY, tiempo);
        Render.app.setScreen(new PantallaPausa(estado));
    }

    // ===== MÃ‰TODOS DE SCREEN =====

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
        if (estadoGuardado == null && gestorFrutas != null) {
            gestorFrutas.dispose();
        }
        if (sonidoComer != null) {
            sonidoComer.dispose();
        }
        if (textoPuntuacion != null) {
            textoPuntuacion.dispose();
        }
        if (textoVidas != null) {
            textoVidas.dispose();
        }
        if (textoInvulnerable != null) {
            textoInvulnerable.dispose();
        }
    }
}