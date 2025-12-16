package red;

import com.badlogic.gdx.Gdx;
import elementos.Direcciones;
import pantallas.PantallaJuegoMultijugador;
import pantallas.PantallaSala;
import utiles.ConfigJuego;

import java.io.IOException;
import java.net.*;

public class HiloCliente extends Thread {

    private DatagramSocket conexion;
    private int puertoServidor = 9998;
    private InetAddress ipServidor;
    private boolean fin = false;
    private boolean conectado = false;
    
    private PantallaSala pantallaSala;
    private PantallaJuegoMultijugador pantallaJuego;
    private boolean estamosEnSala = true;
    
    private static final boolean MODO_TESTEO_LOCAL = true;
    
    private long ultimoHeartbeat = 0;
    private static final long INTERVALO_HEARTBEAT = 3000;
    
    private int mensajesRecibidos = 0;
    private int miNumeroJugador = 0;
    
    private int[] actualizacionesPorJugador = new int[2];

    public HiloCliente(PantallaSala pantallaSala) {
        this.pantallaSala = pantallaSala;
        this.estamosEnSala = true;
        inicializarSocket();
    }

    public void cambiarAPantallaJuego(PantallaJuegoMultijugador pantallaJuego) {
        this.pantallaJuego = pantallaJuego;
        this.estamosEnSala = false;
        System.out.println("[Cliente J" + miNumeroJugador + "] Cambiado a PantallaJuegoMultijugador");
    }

    private void inicializarSocket() {
        String ipServidorStr = MODO_TESTEO_LOCAL ? "127.0.0.1" : "255.255.255.255";
        
        try {
            ipServidor = InetAddress.getByName(ipServidorStr);
            conexion = new DatagramSocket();
            conexion.setReuseAddress(true);
            conexion.setSoTimeout(100);
            
            if (!MODO_TESTEO_LOCAL) {
                conexion.setBroadcast(true);
            }
            
            System.out.println("Cliente inicializado");
            System.out.println("IP servidor: " + ipServidorStr);
            System.out.println("Mi puerto: " + conexion.getLocalPort());
            
        } catch (Exception e) {
            System.err.println("Error al inicializar cliente");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("Cliente escuchando mensajes...");
        
        while (!fin) {
            enviarHeartbeatSiNecesario();
            
            DatagramPacket paquete = new DatagramPacket(new byte[1024], 1024);
            try {
                conexion.receive(paquete);
                procesarMensaje(paquete);
            } catch (SocketTimeoutException e) {
            } catch (IOException e) {
                if (!fin) {
                    System.err.println("Error al recibir paquete");
                }
            }
        }
        
        System.out.println("Cliente detenido");
    }
    
    private void enviarHeartbeatSiNecesario() {
        if (!conectado) return;
        
        long ahora = System.currentTimeMillis();
        if (ahora - ultimoHeartbeat > INTERVALO_HEARTBEAT) {
            enviarMensaje("Heartbeat");
            ultimoHeartbeat = ahora;
        }
    }

    private void procesarMensaje(DatagramPacket paquete) {
        String mensaje = (new String(paquete.getData())).trim();
        mensajesRecibidos++;
        
        if (mensajesRecibidos % 100 == 0) {
            System.out.println("[Cliente J" + miNumeroJugador + "] Estadisticas:");
            System.out.println("   Total mensajes recibidos: " + mensajesRecibidos);
            System.out.println("   Actualizaciones de serpientes:");
            for (int i = 0; i < 2; i++) {
                if (actualizacionesPorJugador[i] > 0) {
                    System.out.println("      J" + (i+1) + ": " + actualizacionesPorJugador[i] + " actualizaciones");
                }
            }
        }
        
        if (!mensaje.startsWith("ActualizarSerpiente") && !mensaje.equals("Heartbeat")) {
            System.out.println("[Cliente J" + miNumeroJugador + " #" + mensajesRecibidos + "] " + 
                             mensaje.substring(0, Math.min(80, mensaje.length())));
        }
        
        if (mensaje.equals("Iniciar")) {
            System.out.println("[Cliente J" + miNumeroJugador + "] Recibido mensaje INICIAR!");
            
            if (estamosEnSala && pantallaSala != null) {
                System.out.println("Procesando inicio desde sala...");
                Gdx.app.postRunnable(() -> {
                    pantallaSala.cuandoIniciaJuego();
                });
            } else {
                System.out.println("Recibido 'Iniciar' pero no estamos en sala");
            }
            return;
        }
        
        if (mensaje.startsWith("Conectado:")) {
            String[] partes = mensaje.split(":");
            if (partes.length >= 2) {
                miNumeroJugador = Integer.parseInt(partes[1]);
                String nombre = partes.length >= 3 ? partes[2] : ("Jugador " + miNumeroJugador);
                
                if (!MODO_TESTEO_LOCAL) {
                    this.ipServidor = paquete.getAddress();
                }
                this.conectado = true;
                this.ultimoHeartbeat = System.currentTimeMillis();
                
                System.out.println("Conectado como " + nombre + " (J" + miNumeroJugador + ")");
                
                if (estamosEnSala && pantallaSala != null) {
                    Gdx.app.postRunnable(() -> pantallaSala.cuandoSeConecta(miNumeroJugador));
                }
            }
            
        } else if (mensaje.startsWith("ActualizarSerpiente:")) {
            if (!estamosEnSala && pantallaJuego != null) {
                procesarActualizacionSerpiente(mensaje);
            } else {
                System.out.println("[Cliente J" + miNumeroJugador + "] Recibida actualizacion de serpiente pero no estamos en juego");
            }
            
        } else if (mensaje.startsWith("ActualizarFrutas:")) {
            if (!estamosEnSala && pantallaJuego != null) {
                String datos = mensaje.substring("ActualizarFrutas:".length());
                Gdx.app.postRunnable(() -> pantallaJuego.cuandoActualizanFrutas(datos));
            }
            
        } else if (mensaje.startsWith("ActualizarNombres:")) {
            String datos = mensaje.substring("ActualizarNombres:".length());
            
            if (estamosEnSala && pantallaSala != null) {
                Gdx.app.postRunnable(() -> pantallaSala.cuandoActualizanNombres(datos));
            } 
            
        } else if (mensaje.startsWith("JugadorComio:")) {
            if (!estamosEnSala && pantallaJuego != null) {
                String[] partes = mensaje.split(":");
                if (partes.length >= 3) {
                    int numeroJugador = Integer.parseInt(partes[1]);
                    int puntos = Integer.parseInt(partes[2]);
                    System.out.println("[Cliente J" + miNumeroJugador + "] J" + numeroJugador + " comio (+" + puntos + " pts)");
                    Gdx.app.postRunnable(() -> pantallaJuego.cuandoJugadorCome(numeroJugador, puntos));
                }
            }
            
        } else if (mensaje.startsWith("JugadorMurio:")) {
            if (!estamosEnSala && pantallaJuego != null) {
                String[] partes = mensaje.split(":");
                if (partes.length >= 3) {
                    int numeroJugador = Integer.parseInt(partes[1]);
                    int vidas = Integer.parseInt(partes[2]);
                    Gdx.app.postRunnable(() -> pantallaJuego.cuandoJugadorMuere(numeroJugador, vidas));
                }
            }
            
        } else if (mensaje.startsWith("ActualizarPuntuacion:")) {
            if (!estamosEnSala && pantallaJuego != null) {
                String datos = mensaje.substring("ActualizarPuntuacion:".length());
                Gdx.app.postRunnable(() -> pantallaJuego.cuandoActualizanPuntaje(datos));
            }
            
        } else if (mensaje.startsWith("JuegoTerminado:")) {
            if (!estamosEnSala && pantallaJuego != null) {
                String[] partes = mensaje.split(":");
                if (partes.length >= 2) {
                    int ganador = Integer.parseInt(partes[1]);
                    Gdx.app.postRunnable(() -> pantallaJuego.cuandoTerminaJuego(ganador));
                }
            }
            
        } else if (mensaje.startsWith("JugadorDesconectado:")) {
            String[] partes = mensaje.split(":");
            if (partes.length >= 2) {
                int numeroJugador = Integer.parseInt(partes[1]);
                
                if (estamosEnSala && pantallaSala != null) {
                    Gdx.app.postRunnable(() -> pantallaSala.cuandoRivalSeDesconecta(numeroJugador));
                } else if (!estamosEnSala && pantallaJuego != null) {
                    Gdx.app.postRunnable(() -> pantallaJuego.cuandoRivalSeDesconecta(numeroJugador));
                }
            }
            
        } else if (mensaje.equals("Desconectar")) {
            System.out.println("Servidor cerrado");
            if (estamosEnSala && pantallaSala != null) {
                Gdx.app.postRunnable(() -> pantallaSala.volverAlMenu());
            } else if (!estamosEnSala && pantallaJuego != null) {
                Gdx.app.postRunnable(() -> pantallaJuego.volverAlMenu());
            }
            
        } else if (mensaje.equals("Lleno")) {
            System.out.println("Servidor lleno");
            if (estamosEnSala && pantallaSala != null) {
                Gdx.app.postRunnable(() -> pantallaSala.servidorLleno());
            }
        }
    }

    private void procesarActualizacionSerpiente(String mensaje) {
        try {
            String datos = mensaje.substring("ActualizarSerpiente:".length());
            int primerDosPuntos = datos.indexOf(':');
            
            if (primerDosPuntos == -1) {
                System.err.println("[Cliente J" + miNumeroJugador + "] Formato invalido: " + mensaje);
                return;
            }
            
            int numeroJugador = Integer.parseInt(datos.substring(0, primerDosPuntos));
            String segmentos = datos.substring(primerDosPuntos + 1);
            
            if (numeroJugador >= 1 && numeroJugador <= 2) {
                actualizacionesPorJugador[numeroJugador - 1]++;
                
                if (numeroJugador == miNumeroJugador && actualizacionesPorJugador[numeroJugador - 1] % 50 == 0) {
                    System.out.println("[Cliente J" + miNumeroJugador + "] Recibida actualizacion #" + 
                                     actualizacionesPorJugador[numeroJugador - 1] + " de MI serpiente");
                }
            }
            
            Gdx.app.postRunnable(() -> pantallaJuego.cuandoActualizanSerpiente(numeroJugador, segmentos));
            
        } catch (Exception e) {
            System.err.println("[Cliente J" + miNumeroJugador + "] Error al parsear serpiente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String mensaje) {
        if (conexion == null || conexion.isClosed()) {
            return;
        }

        byte[] datos = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(datos, datos.length, ipServidor, puertoServidor);
        
        try {
            conexion.send(paquete);
        } catch (IOException e) {
            System.err.println("Error al enviar: " + mensaje);
        }
    }

    public void conectar() {
        String nombreJugador = ConfigJuego.getInstancia().getNombreJugador();
        System.out.println("Conectando como: " + nombreJugador);
        enviarMensaje("Conectar:" + nombreJugador);
        ultimoHeartbeat = System.currentTimeMillis();
    }

    public void mover(Direcciones direccion) {
        if (conectado) {
            enviarMensaje("Mover:" + direccion.name());
        }
    }

    public void desconectar() {
        if (conectado) {
            enviarMensaje("Desconectar");
            conectado = false;
        }
    }

    public void terminar() {
        this.fin = true;
        desconectar();
        
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
        this.interrupt();
    }

    public boolean estaConectado() {
        return conectado;
    }
}