package elementos;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import utiles.ConfigJuego;

public class Sonido {
    private static Music musica;
    private static String pathMusicaActual;
    private static boolean bucleMusicaActual;

    // Método principal para reproducir música
    public static void reproducirMusica(String path, boolean bucle) {
        ConfigJuego config = ConfigJuego.getInstancia();
        
        if (!config.isMusicaActivada()) return;

        // Detener música anterior si existe
        if (musica != null) {
            musica.stop();
            musica.dispose();
        }

        // Guardar configuración actual
        pathMusicaActual = path;
        bucleMusicaActual = bucle;

        // Crear y reproducir nueva música
        musica = Gdx.audio.newMusic(Gdx.files.internal(path));
        musica.setLooping(bucle);
        musica.setVolume(config.getVolumenMusica());
        musica.play();
    }

    // Reactivar música después de pausar el juego o cambiar configuración
    public static void reactivarMusica() {
        ConfigJuego config = ConfigJuego.getInstancia();
        
        if (config.isMusicaActivada() && 
            (musica == null || !musica.isPlaying()) && 
            pathMusicaActual != null) {
            reproducirMusica(pathMusicaActual, bucleMusicaActual);
        }
    }

    // Detener y liberar música
    public static void detenerMusica() {
        if (musica != null) {
            musica.stop();
            musica.dispose();
            musica = null;
        }
    }

    // Reproducir sonido
    public static void reproducirSonido(String ruta) {
        ConfigJuego config = ConfigJuego.getInstancia();
        
        if (!config.isSonidosActivados()) return;
        
        Sound sonido = Gdx.audio.newSound(Gdx.files.internal(ruta));
        sonido.play(config.getVolumenSonidos());
    }

    // Activar/desactivar música desde configuración
    public static void setMusicaActivada(boolean activada) {
        if (!activada && musica != null) {
            musica.stop();
        } else if (activada && musica != null && !musica.isPlaying()) {
            musica.play();
        }
    }

    // Actualizar volumen de música en tiempo real
    public static void actualizarVolumenMusica(float volumen) {
        if (musica != null) {
            musica.setVolume(volumen);
        }
    }

    // Verificar si está reproduciendo
    public static boolean estaReproduciendo() {
        return musica != null && musica.isPlaying();
    }
}