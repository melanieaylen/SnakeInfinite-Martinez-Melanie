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
import utiles.ConfigJuego;
import utiles.Recursos;
import utiles.Render;

public class PantallaJuego implements Screen, ControladorJuego {

    private final int TAMANIO_ELEMENTOS = 30;
    private final float VELOCIDAD_SERPIENTE = 0.12f;

    private Jugador jugador;
    private GestorFrutas gestorFrutas;
    private Grilla grilla;

    private OrthographicCamera camaraJuego;
    private Viewport viewportJuego;
    private OrthographicCamera camaraUI;
    private Viewport viewportUI;

    private Texto textoNombre;
    private Texto textoPuntuacion;
    private Texto textoVidas;
    private Texto textoInvulnerable;

    private Entradas entrada;
    private Sound sonidoComer;

    private float posElementosX;
    private float posElementosY;
    private float tiempo;
    private EstadoJuego estadoGuardado;

    public PantallaJuego() {
        this.estadoGuardado = null;
    }

    public PantallaJuego(EstadoJuego estado) {
        this.estadoGuardado = estado;
    }

    @Override
    public void show() {
        textoNombre = new Texto(Recursos.TEXTO, 33, Color.YELLOW, Color.BLACK, -4, 4, true);
        textoPuntuacion = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
        textoVidas = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
        textoInvulnerable = new Texto(Recursos.TEXTO, 33, Color.YELLOW, Color.BLACK, -4, 4, true);

        grilla = new Grilla(TAMANIO_ELEMENTOS);
        sonidoComer = Gdx.audio.newSound(Gdx.files.internal(Recursos.SONIDO_COMER));

        entrada = new Entradas();
        Gdx.input.setInputProcessor(entrada);

        if (estadoGuardado != null) {
            restaurarEstado();
        } else {
            inicializarNuevoJuego();
        }

        configurarCamaras();
    }

    private void inicializarNuevoJuego() {
        posElementosX = 0;
        posElementosY = 0;
        tiempo = 0;

        Serpiente serpiente = new Serpiente(posElementosX, posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
        String nombreJugador = ConfigJuego.getInstancia().getNombreJugador();
        jugador = new Jugador(1, nombreJugador, serpiente);

        gestorFrutas = new GestorFrutas(TAMANIO_ELEMENTOS);
        gestorFrutas.inicializarFrutas(serpiente);
    }

    private void restaurarEstado() {
        jugador = estadoGuardado.getJugador();
        gestorFrutas = estadoGuardado.getGestorFrutas();
        posElementosX = estadoGuardado.getPosElementosX();
        posElementosY = estadoGuardado.getPosElementosY();
        tiempo = estadoGuardado.getTiempo();
    }

    private void configurarCamaras() {
        camaraJuego = new OrthographicCamera();
        camaraJuego.setToOrtho(false, Config.ANCHO, Config.ALTO);
        viewportJuego = new FitViewport(Config.ANCHO, Config.ALTO, camaraJuego);
        camaraJuego.position.set(posElementosX, posElementosY, 0);
        camaraJuego.update();

        camaraUI = new OrthographicCamera();
        viewportUI = new FitViewport(Config.ANCHO, Config.ALTO, camaraUI);
        camaraUI.position.set(Config.ANCHO / 2, Config.ALTO / 2, 0);
        camaraUI.update();
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0, 0, 0);

        actualizar(delta);
        renderizarMundoJuego();
        renderizarUI();
    }

    private void actualizar(float delta) {
        procesarEntradas(delta);
        jugador.actualizarInvulnerabilidad(delta);

        Fruta frutaColisionada = gestorFrutas.verificarColisiones(jugador.getSerpiente());
        if (frutaColisionada != null) {
            frutaComida(frutaColisionada.getPuntos());
            gestorFrutas.reubicarFruta(frutaColisionada, jugador.getSerpiente());
        }

        if (!jugador.isInvulnerable() && jugador.getSerpiente().colisionSerpiente()) {
            perderVida();
        }

        actualizarCamara();
    }

    private void renderizarMundoJuego() {
        viewportJuego.apply();
        Render.batch.setProjectionMatrix(camaraJuego.combined);
        Render.shaper.setProjectionMatrix(camaraJuego.combined);

        Render.shaper.begin(ShapeType.Filled);
        grilla.dibujarGrilla(camaraJuego);

        if (!jugador.isInvulnerable() || (int) (jugador.getTiempoInvulnerabilidad() * 10) % 2 == 0) {
            jugador.getSerpiente().dibujar();
        }

        Render.shaper.end();

        gestorFrutas.dibujarTodas();
    }

    private void renderizarUI() {
        viewportUI.apply();
        Render.batch.setProjectionMatrix(camaraUI.combined);

        Render.batch.begin();

        textoNombre.dibujarTexto(jugador.getNombre(), 20, Config.ALTO - 10);
        textoPuntuacion.dibujarTexto("Puntos: " + jugador.getPuntuacion(), 20, Config.ALTO - 45);
        textoVidas.dibujarTexto("Vidas: " + jugador.getVidas(), 20, Config.ALTO - 80);

        if (jugador.isInvulnerable()) {
            textoInvulnerable.dibujarTexto("INVULNERABLE!", Config.ANCHO / 2 - 150, Config.ALTO / 2);
        }

        Render.batch.end();
    }

    private void procesarEntradas(float delta) {
        if (entrada.isArriba()) {
            moverJugador(Direcciones.ARRIBA);
        } else if (entrada.isAbajo()) {
            moverJugador(Direcciones.ABAJO);
        } else if (entrada.isDerecha()) {
            moverJugador(Direcciones.DERECHA);
        } else if (entrada.isIzquierda()) {
            moverJugador(Direcciones.IZQUIERDA);
        }

        if (entrada.isPausa()) {
            pausarJuego();
            return;
        }

        moverSerpiente(delta);
    }

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

    private void actualizarCamara() {
        float targetX = jugador.getSerpiente().getPosX() + TAMANIO_ELEMENTOS / 2;
        float targetY = jugador.getSerpiente().getPosY() + TAMANIO_ELEMENTOS / 2;

        camaraJuego.position.set(targetX, targetY, 0);
        camaraJuego.update();
    }

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
            posElementosX = 0;
            posElementosY = 0;
            jugador.resetearPosicion(posElementosX, posElementosY, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS);
        }
    }

    @Override
    public void finDelJuego() {
        // ✅ CAMBIO: Pasar la puntuación final
        Render.app.setScreen(new PantallaFinJuego(jugador.getPuntuacion()));
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

    @Override
    public void resize(int width, int height) {
        viewportJuego.update(width, height, true);
        viewportUI.update(width, height, true);
    }

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}

    @Override
    public void dispose() {
        if (estadoGuardado == null && gestorFrutas != null) {
            gestorFrutas.dispose();
        }
        if (sonidoComer != null) sonidoComer.dispose();
        if (textoNombre != null) textoNombre.dispose();
        if (textoPuntuacion != null) textoPuntuacion.dispose();
        if (textoVidas != null) textoVidas.dispose();
        if (textoInvulnerable != null) textoInvulnerable.dispose();
    }
}