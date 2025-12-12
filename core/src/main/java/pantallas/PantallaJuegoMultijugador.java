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
import interfaces.ControladorJuegoMultijugador;
import red.HiloCliente;
import utiles.Config;
import utiles.ConfigJuego;
import utiles.Recursos;
import utiles.Render;

public class PantallaJuegoMultijugador implements Screen, ControladorJuegoMultijugador {

    private final int TAMANIO_ELEMENTOS = 30;

    // RED
    private HiloCliente hiloCliente;
    private int miNumeroJugador;

    // SERPIENTES
    private Map<Integer, SerpienteRemota> serpientes;

    // FRUTAS
    private Map<String, FrutaRemota> frutas;

    // CÁMARAS
    private OrthographicCamera camaraJuego;
    private Viewport viewportJuego;
    private OrthographicCamera camaraUI;
    private Viewport viewportUI;

    // UI
    private Texto textoPuntuacion;
    private Texto textoVidas;
    private Texto textoGanador;
    private Texto textoDebug; // ⚠️ NUEVO: Para debugging
    private Grilla grilla;

    // ENTRADA
    private Entradas entrada;

    // AUDIO
    private Sound sonidoComer;

    // ESTADO
    private boolean juegoTerminado = false;
    private int[] puntuaciones = {0, 0};
    private int[] vidas = {3, 3};
    private Direcciones ultimaDireccion = Direcciones.NINGUNA;

    public PantallaJuegoMultijugador(HiloCliente hiloCliente, int miNumeroJugador) {
        this.hiloCliente = hiloCliente;
        this.miNumeroJugador = miNumeroJugador;
        this.serpientes = new HashMap<>();
        this.frutas = new HashMap<>();
    }

    @Override
    public void show() {
        // Inicializar UI
        textoPuntuacion = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
        textoVidas = new Texto(Recursos.TEXTO, 33, Color.WHITE, Color.BLACK, -4, 4, true);
        textoGanador = new Texto(Recursos.FUENTE, 60, Color.YELLOW, Color.BLACK, -4, 4, true);
        textoDebug = new Texto(Recursos.TEXTO, 25, Color.CYAN, Color.BLACK, -3, 3, true);

        // Grilla
        grilla = new Grilla(TAMANIO_ELEMENTOS);

        // Audio
        sonidoComer = Gdx.audio.newSound(Gdx.files.internal(Recursos.SONIDO_COMER));

        // Entrada
        entrada = new Entradas();
        Gdx.input.setInputProcessor(entrada);

        // Configurar cámaras
        configurarCamaras();

        System.out.println("✅ Pantalla multijugador inicializada");
        System.out.println("🎮 Eres el Jugador " + miNumeroJugador);
    }

    private void configurarCamaras() {
        // Cámara del mundo del juego
        camaraJuego = new OrthographicCamera();
        camaraJuego.setToOrtho(false, Config.ANCHO, Config.ALTO);
        viewportJuego = new FitViewport(Config.ANCHO, Config.ALTO, camaraJuego);
        camaraJuego.position.set(0, 0, 0);
        camaraJuego.update();

        // Cámara fija de la UI
        camaraUI = new OrthographicCamera();
        viewportUI = new FitViewport(Config.ANCHO, Config.ALTO, camaraUI);
        camaraUI.position.set(Config.ANCHO / 2, Config.ALTO / 2, 0);
        camaraUI.update();
    }

    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0, 0, 0);

        // Procesar entradas
        procesarEntradas();

        // Actualizar cámara
        actualizarCamara();

        // Renderizar mundo del juego
        renderizarMundoJuego();

        // Renderizar UI
        renderizarUI();
    }

    private void procesarEntradas() {
        if (juegoTerminado) {
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

        // Enviar movimiento al servidor solo si cambió
        if (nuevaDireccion != null && nuevaDireccion != ultimaDireccion) {
            hiloCliente.mover(nuevaDireccion);
            ultimaDireccion = nuevaDireccion;
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

        // Dibujar grilla
        Render.shaper.begin(ShapeType.Filled);
        grilla.dibujarGrilla(camaraJuego);
        Render.shaper.end();

        // Dibujar frutas
        for (FrutaRemota fruta : frutas.values()) {
            fruta.dibujar();
        }

        // Dibujar serpientes
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

        // Puntuación
        textoPuntuacion.dibujarTexto("J1: " + puntuaciones[0] + "  J2: " + puntuaciones[1], 
                                     20, Config.ALTO - 35);

        // Vidas
        textoVidas.dibujarTexto("Vidas J1: " + vidas[0] + "  Vidas J2: " + vidas[1], 
                               20, Config.ALTO - 70);

        // ⚠️ NUEVO: Info de debug
        textoDebug.dibujarTexto("Serpientes: " + serpientes.size() + " | Frutas: " + frutas.size(), 
                               20, Config.ALTO - 105);

        // Mensaje de ganador
        if (juegoTerminado) {
            textoGanador.dibujarTexto("¡Jugador " + obtenerGanador() + " GANÓ!", 
                                     Config.ANCHO / 2 - 300, Config.ALTO / 2);
        }

        Render.batch.end();
    }

    private int obtenerGanador() {
        return (vidas[0] > vidas[1]) ? 1 : 2;
    }

    // ===== IMPLEMENTACIÓN DE ControladorJuegoMultijugador =====

    @Override
    public void conectado(int numeroJugador) {
        // Ya estamos conectados cuando llegamos aquí
    }

    @Override
    public void iniciarJuego() {
        // El juego ya inició cuando llegamos a esta pantalla
    }

    @Override
    public void actualizarPosicionSerpiente(int numeroJugador, float x, float y) {
        // Este método ya no se usa - actualizarSerpienteCompleta lo reemplaza
        System.out.println("⚠️ Método actualizarPosicionSerpiente obsoleto llamado");
    }
    
    /**
     * ⚠️ MÉTODO PRINCIPAL: Actualiza una serpiente con todos sus segmentos
     * @param numeroJugador Número del jugador (1 o 2)
     * @param datosSegmentos String en formato "x1:y1,x2:y2,x3:y3,..."
     */
    public void actualizarSerpienteCompleta(final int numeroJugador, final String datosSegmentos) {
        // ⚠️ CRÍTICO: Ejecutar en el hilo principal de LibGDX
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                SerpienteRemota serpiente = serpientes.get(numeroJugador);
                
                if (serpiente == null) {
                    // Crear nueva serpiente si no existe
                    Color colorCabeza, colorCuerpo;
                    
                    if (numeroJugador == 1) {
                        colorCabeza = ConfigJuego.getInstancia().getColorCabeza();
                        colorCuerpo = ConfigJuego.getInstancia().getColorCuerpo();
                    } else {
                        // Jugador 2 usa colores diferentes
                        colorCabeza = Color.GREEN;
                        colorCuerpo = Color.LIME;
                    }
                    
                    serpiente = new SerpienteRemota(0, 0, TAMANIO_ELEMENTOS, TAMANIO_ELEMENTOS, 
                                                   colorCabeza, colorCuerpo);
                    serpientes.put(numeroJugador, serpiente);
                    System.out.println("🐍 Serpiente J" + numeroJugador + " creada");
                }
                
                // Actualizar segmentos
                serpiente.actualizarSegmentos(datosSegmentos);
            }
        });
    }

    @Override
    public void actualizarFrutas(final String datosFrutas) {
        // ⚠️ CRÍTICO: Ejecutar en el hilo principal de LibGDX
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                // Limpiar frutas actuales
                for (FrutaRemota fruta : frutas.values()) {
                    fruta.dispose();
                }
                frutas.clear();

                if (datosFrutas == null || datosFrutas.isEmpty()) {
                    System.err.println("⚠️ datosFrutas vacío o null");
                    return;
                }

                // Parsear y crear nuevas frutas
                String[] arregloFrutas = datosFrutas.split("\\|");
                
                for (String frutaStr : arregloFrutas) {
                    String[] partes = frutaStr.split(":");
                    if (partes.length >= 3) {
                        try {
                            TipoFruta tipo = TipoFruta.valueOf(partes[0]);
                            float x = Float.parseFloat(partes[1]);
                            float y = Float.parseFloat(partes[2]);
                            
                            FrutaRemota fruta = new FrutaRemota(x, y, TAMANIO_ELEMENTOS, 
                                                                TAMANIO_ELEMENTOS, tipo);
                            frutas.put(frutaStr, fruta);
                        } catch (Exception e) {
                            System.err.println("⚠️ Error al parsear fruta: " + frutaStr);
                            e.printStackTrace();
                        }
                    }
                }
                
                if (frutas.size() > 0) {
                    System.out.println("🍎 " + frutas.size() + " frutas actualizadas");
                }
            }
        });
    }

    @Override
    public void jugadorComio(final int numeroJugador, final int puntos) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                sonidoComer.play();
                System.out.println("🎁 Jugador " + numeroJugador + " comió una fruta (+" + puntos + " puntos)");
            }
        });
    }

    @Override
    public void jugadorMurio(final int numeroJugador, final int vidasRestantes) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                vidas[numeroJugador - 1] = vidasRestantes;
                System.out.println("💀 Jugador " + numeroJugador + " perdió una vida (quedan " + vidasRestantes + ")");
            }
        });
    }

    @Override
    public void actualizarPuntuacion(final String datosPuntuacion) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                String[] puntajes = datosPuntuacion.split(":");
                if (puntajes.length >= 2) {
                    try {
                        puntuaciones[0] = Integer.parseInt(puntajes[0]);
                        puntuaciones[1] = Integer.parseInt(puntajes[1]);
                    } catch (NumberFormatException e) {
                        System.err.println("⚠️ Error al parsear puntuación: " + datosPuntuacion);
                    }
                }
            }
        });
    }

    @Override
    public void finDelJuego(final int ganador) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                juegoTerminado = true;
                System.out.println("🏆 Fin del juego. Ganador: Jugador " + ganador);
                
                // Volver al menú después de 5 segundos
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(5000);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    volverAlMenu();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
    }

    @Override
    public void jugadorDesconectado(final int numeroJugador) {
        Gdx.app.postRunnable(new Runnable() {
            @Override
            public void run() {
                System.out.println("⚠️ Jugador " + numeroJugador + " se desconectó");
                juegoTerminado = true;
                
                // Volver al menú después de 3 segundos
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Thread.sleep(3000);
                            Gdx.app.postRunnable(new Runnable() {
                                @Override
                                public void run() {
                                    volverAlMenu();
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }
        });
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
        // Limpiar frutas
        for (FrutaRemota fruta : frutas.values()) {
            fruta.dispose();
        }
        frutas.clear();

        // Limpiar recursos
        if (sonidoComer != null) {
            sonidoComer.dispose();
        }
        if (textoPuntuacion != null) {
            textoPuntuacion.dispose();
        }
        if (textoVidas != null) {
            textoVidas.dispose();
        }
        if (textoGanador != null) {
            textoGanador.dispose();
        }
        if (textoDebug != null) {
            textoDebug.dispose();
        }
    }
}