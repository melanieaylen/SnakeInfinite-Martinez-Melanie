package pantallas;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;

import elementos.Imagen;
import elementos.Texto;
import entradas.salidas.teclado.Entradas;
import utiles.ConfigJuego;
import utiles.Recursos;
import utiles.Render;

/**
 * ✅ MEJORADA: Con botones de reiniciar/menú y sistema de récords
 */
public class PantallaFinJuego implements Screen {
    
    private float transparencia = 0;
    private Imagen fondo;
    private Music musica;
    private Sound sonidoBoton;
    
    private Texto tituloGameOver;
    private Texto textoPuntuacion;
    private Texto textoRecord;
    private Texto textoNuevoRecord;
    private Texto opcionReiniciar;
    private Texto opcionMenu;
    private Texto indicador;
    
    private Entradas entrada;
    private int opcionSeleccionada = 1; // 1 = Reiniciar, 2 = Menú
    private float tiempoEntrada = 0;
    
    private int puntuacionFinal;
    private int recordActual;
    private boolean esNuevoRecord = false;
    
    public PantallaFinJuego(int puntuacion) {
        this.puntuacionFinal = puntuacion;
        cargarYCompararRecord();
    }
    
    /**
     * ✅ NUEVO: Sistema de récords persistente
     */
    private void cargarYCompararRecord() {
        Preferences prefs = Gdx.app.getPreferences("SnakeInfiniteRecords");
        recordActual = prefs.getInteger("recordPuntuacion", 0);
        
        if (puntuacionFinal > recordActual) {
            esNuevoRecord = true;
            recordActual = puntuacionFinal;
            prefs.putInteger("recordPuntuacion", recordActual);
            prefs.flush();
            System.out.println("🏆 ¡NUEVO RÉCORD! " + recordActual + " puntos");
        }
    }
    
    @Override
    public void show() {
        fondo = new Imagen(Recursos.GAME_OVER);
        musica = Gdx.audio.newMusic(Gdx.files.internal(Recursos.MUSICA_GAME_OVER));
        sonidoBoton = Gdx.audio.newSound(Gdx.files.internal(Recursos.SONIDO_BOTON));
        musica.play();
        
        entrada = new Entradas();
        Gdx.input.setInputProcessor(entrada);
        
        tituloGameOver = new Texto(Recursos.FUENTE, 120, Color.RED, Color.BLACK, -4, 4, true);
        textoPuntuacion = new Texto(Recursos.FUENTE, 60, Color.WHITE, Color.BLACK, -4, 4, true);
        textoRecord = new Texto(Recursos.FUENTE, 50, Color.GOLD, Color.BLACK, -4, 4, true);
        textoNuevoRecord = new Texto(Recursos.FUENTE, 70, Color.YELLOW, Color.BLACK, -4, 4, true);
        opcionReiniciar = new Texto(Recursos.FUENTE, 50, Color.WHITE, Color.BLACK, -4, 4, true);
        opcionMenu = new Texto(Recursos.FUENTE, 50, Color.WHITE, Color.BLACK, -4, 4, true);
        indicador = new Texto(Recursos.FUENTE, 50, Color.SKY, Color.BLACK, -4, 4, true);
    }
    
    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0, 0, 0);
        
        actualizarTransparencia();
        procesarEntradas(delta);
        actualizarInterfaz();
        
        Render.batch.begin();
        
        // Fondo
        fondo.setTransparencia(transparencia);
        fondo.dibujar();
        
        // Título
        tituloGameOver.dibujarTexto("GAME OVER", 380, 750);
        
        // Puntuación
        textoPuntuacion.dibujarTexto("Puntuación: " + puntuacionFinal, 480, 600);
        
        // Récord
        textoRecord.dibujarTexto("Récord: " + recordActual, 520, 540);
        
        // Mensaje de nuevo récord
        if (esNuevoRecord) {
            textoNuevoRecord.dibujarTexto("¡NUEVO RÉCORD!", 440, 480);
        }
        
        // Opciones
        opcionReiniciar.dibujarTexto("   Jugar de nuevo", 480, 380);
        opcionMenu.dibujarTexto("   Volver al menú", 480, 300);
        
        // Indicador
        if (opcionSeleccionada == 1) {
            indicador.dibujarTexto("> ", 440, 380);
        } else {
            indicador.dibujarTexto("> ", 440, 300);
        }
        
        Render.batch.end();
    }
    
    private void actualizarTransparencia() {
        transparencia += 0.004f;
        if (transparencia > 1) {
            transparencia = 1;
        }
    }
    
    private void procesarEntradas(float delta) {
        tiempoEntrada += delta;
        
        if (entrada.isArriba() && tiempoEntrada > 0.2f) {
            tiempoEntrada = 0;
            opcionSeleccionada = 1;
        }
        
        if (entrada.isAbajo() && tiempoEntrada > 0.2f) {
            tiempoEntrada = 0;
            opcionSeleccionada = 2;
        }
        
        if (entrada.isEnter()) {
            sonidoBoton.play();
            
            if (opcionSeleccionada == 1) {
                // Reiniciar juego
                Render.app.setScreen(new PantallaJuego());
            } else {
                // Volver al menú
                Render.app.setScreen(new PantallaMenu());
            }
        }
    }
    
    private void actualizarInterfaz() {
        if (opcionSeleccionada == 1) {
            opcionReiniciar.setColor(Color.SKY);
            opcionMenu.setColor(Color.WHITE);
        } else {
            opcionReiniciar.setColor(Color.WHITE);
            opcionMenu.setColor(Color.SKY);
        }
    }

    @Override
    public void resize(int width, int height) {}

    @Override
    public void pause() {
        if (musica != null && musica.isPlaying()) {
            musica.pause();
        }
    }

    @Override
    public void resume() {
        if (musica != null && !musica.isPlaying()) {
            musica.play();
        }
    }

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
        if (tituloGameOver != null) tituloGameOver.dispose();
        if (textoPuntuacion != null) textoPuntuacion.dispose();
        if (textoRecord != null) textoRecord.dispose();
        if (textoNuevoRecord != null) textoNuevoRecord.dispose();
        if (opcionReiniciar != null) opcionReiniciar.dispose();
        if (opcionMenu != null) opcionMenu.dispose();
        if (indicador != null) indicador.dispose();
    }
}