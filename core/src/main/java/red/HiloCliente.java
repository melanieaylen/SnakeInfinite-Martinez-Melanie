package red;

import interfaces.ControladorJuegoMultijugador;
import elementos.Direcciones;

import java.io.IOException;
import java.net.*;

/**
 * HiloCliente mejorado con modo de testeo local
 */
public class HiloCliente extends Thread {

    private DatagramSocket conexion;
    private int puertoServidor = 9998;
    private String ipServidorStr;
    private InetAddress ipServidor;
    private boolean fin = false;
    private ControladorJuegoMultijugador controlador;
    private boolean conectado = false;
    
    // MODO DE TESTEO: true para testear en la misma computadora
    private static final boolean MODO_TESTEO_LOCAL = true;

    public HiloCliente(ControladorJuegoMultijugador controlador) {
        this.controlador = controlador;
        
        // Seleccionar IP según el modo
        if (MODO_TESTEO_LOCAL) {
            ipServidorStr = "127.0.0.1"; // Localhost para testeo
            System.out.println("🔧 MODO TESTEO LOCAL - Conectando a localhost");
        } else {
            ipServidorStr = "255.255.255.255"; // Broadcast para red local
            System.out.println("🌐 MODO RED LOCAL - Usando broadcast");
        }
        
        try {
            ipServidor = InetAddress.getByName(ipServidorStr);
            conexion = new DatagramSocket();
            conexion.setSoTimeout(100);
            
            if (!MODO_TESTEO_LOCAL) {
                conexion.setBroadcast(true); // Solo activar broadcast si no es local
            }
            
            System.out.println("✅ Cliente inicializado en " + ipServidorStr);
        } catch (SocketException | UnknownHostException e) {
            System.err.println("❌ Error al inicializar cliente");
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        System.out.println("👂 Cliente escuchando mensajes...");
        do {
            DatagramPacket paquete = new DatagramPacket(new byte[1024], 1024);
            try {
                conexion.receive(paquete);
                procesarMensaje(paquete);
            } catch (SocketTimeoutException e) {
                // Timeout normal, continuar
            } catch (IOException e) {
                if (!fin) {
                    System.err.println("⚠️ Error al recibir paquete");
                }
            }
        } while (!fin);
        System.out.println("✅ Cliente detenido");
    }

    private void procesarMensaje(DatagramPacket paquete) {
        String mensaje = (new String(paquete.getData())).trim();
        
        // ⚠️ NO hacer split aquí - cada caso maneja su propio formato
        if (mensaje.startsWith("Conectado:")) {
            manejarConectado(paquete, mensaje);
        } else if (mensaje.equals("YaConectado")) {
            System.out.println("⚠️ Ya estás conectado al servidor");
        } else if (mensaje.equals("Lleno")) {
            System.out.println("⚠️ Servidor lleno (2/2 jugadores)");
            controlador.volverAlMenu();
        } else if (mensaje.equals("NoConectado")) {
            System.out.println("⚠️ No estás conectado al servidor");
        } else if (mensaje.equals("Iniciar")) {
            System.out.println("🎮 Iniciando juego...");
            controlador.iniciarJuego();
        } else if (mensaje.startsWith("ActualizarSerpiente:")) {
            manejarActualizacionSerpiente(mensaje);
        } else if (mensaje.startsWith("ActualizarFrutas:")) {
            String datos = mensaje.substring("ActualizarFrutas:".length());
            controlador.actualizarFrutas(datos);
        } else if (mensaje.startsWith("JugadorComio:")) {
            String[] partes = mensaje.split(":");
            if (partes.length >= 3) {
                int numeroJugador = Integer.parseInt(partes[1]);
                int puntos = Integer.parseInt(partes[2]);
                controlador.jugadorComio(numeroJugador, puntos);
            }
        } else if (mensaje.startsWith("JugadorMurio:")) {
            String[] partes = mensaje.split(":");
            if (partes.length >= 3) {
                int numeroJugador = Integer.parseInt(partes[1]);
                int vidas = Integer.parseInt(partes[2]);
                controlador.jugadorMurio(numeroJugador, vidas);
            }
        } else if (mensaje.startsWith("ActualizarPuntuacion:")) {
            String datos = mensaje.substring("ActualizarPuntuacion:".length());
            controlador.actualizarPuntuacion(datos);
        } else if (mensaje.startsWith("JuegoTerminado:")) {
            String[] partes = mensaje.split(":");
            if (partes.length >= 2) {
                int ganador = Integer.parseInt(partes[1]);
                controlador.finDelJuego(ganador);
            }
        } else if (mensaje.startsWith("JugadorDesconectado:")) {
            String[] partes = mensaje.split(":");
            if (partes.length >= 2) {
                int numeroJugador = Integer.parseInt(partes[1]);
                controlador.jugadorDesconectado(numeroJugador);
            }
        } else if (mensaje.equals("Desconectar")) {
            System.out.println("🔌 Servidor desconectado");
            controlador.volverAlMenu();
        } else {
            System.out.println("⚠️ Mensaje desconocido: " + mensaje);
        }
    }

    private void manejarConectado(DatagramPacket paquete, String mensaje) {
        // Formato: "Conectado:NUMERO"
        String[] partes = mensaje.split(":");
        if (partes.length >= 2) {
            int numeroJugador = Integer.parseInt(partes[1]);
            
            // En modo local, no necesitamos cambiar la IP
            if (!MODO_TESTEO_LOCAL) {
                this.ipServidor = paquete.getAddress();
            }
            this.conectado = true;
            
            System.out.println("✅ Conectado como Jugador " + numeroJugador);
            controlador.conectado(numeroJugador);
        }
    }

    /**
     * ⚠️ CORREGIDO: Procesa actualización completa de serpiente
     * Formato esperado: "ActualizarSerpiente:NUMERO:x1:y1,x2:y2,x3:y3,..."
     */
    private void manejarActualizacionSerpiente(String mensaje) {
        try {
            System.out.println("🔵 manejarActualizacionSerpiente:");
            System.out.println("   - Mensaje completo: " + mensaje);
            
            // Remover el prefijo "ActualizarSerpiente:"
            String datos = mensaje.substring("ActualizarSerpiente:".length());
            System.out.println("   - Datos sin prefijo: " + datos);
            
            // Separar número de jugador y segmentos
            int primerDosPuntos = datos.indexOf(':');
            if (primerDosPuntos == -1) {
                System.err.println("⚠️ Formato incorrecto - no se encontró ':' en: " + datos);
                return;
            }
            
            String numeroStr = datos.substring(0, primerDosPuntos);
            String segmentos = datos.substring(primerDosPuntos + 1);
            
            System.out.println("   - Número jugador: " + numeroStr);
            System.out.println("   - Segmentos: " + segmentos);
            
            int numeroJugador = Integer.parseInt(numeroStr);
            
            // Llamar al método correcto en PantallaJuegoMultijugador
            if (controlador instanceof pantallas.PantallaJuegoMultijugador) {
                System.out.println("   - Llamando a actualizarSerpienteCompleta...");
                ((pantallas.PantallaJuegoMultijugador) controlador)
                    .actualizarSerpienteCompleta(numeroJugador, segmentos);
                System.out.println("   ✅ Actualización completada");
            } else {
                System.err.println("⚠️ Controlador no es instancia de PantallaJuegoMultijugador");
            }
        } catch (Exception e) {
            System.err.println("⚠️ Error al parsear actualización de serpiente: " + mensaje);
            e.printStackTrace();
        }
    }

    public void enviarMensaje(String mensaje) {
        if (conexion == null || conexion.isClosed()) {
            System.err.println("⚠️ Socket cerrado, no se puede enviar mensaje");
            return;
        }

        byte[] datosMensaje = mensaje.getBytes();
        DatagramPacket paquete = new DatagramPacket(datosMensaje, datosMensaje.length, ipServidor, puertoServidor);
        try {
            conexion.send(paquete);
            System.out.println("📤 Mensaje enviado: " + mensaje);
        } catch (IOException e) {
            System.err.println("❌ Error al enviar mensaje: " + mensaje);
            e.printStackTrace();
        }
    }

    public void conectar() {
        enviarMensaje("Conectar");
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
        if (conexion != null && !conexion.isClosed()) {
            conexion.close();
        }
        this.interrupt();
        System.out.println("✅ Cliente terminado");
    }

    public boolean estaConectado() {
        return conectado;
    }
    
    /**
     * ⚠️ NUEVO: Cambia el controlador (cuando pasamos de PantallaSala a PantallaJuegoMultijugador)
     */
    public void cambiarControlador(ControladorJuegoMultijugador nuevoControlador) {
        this.controlador = nuevoControlador;
        System.out.println("🔄 Controlador cambiado a: " + nuevoControlador.getClass().getSimpleName());
    }
}