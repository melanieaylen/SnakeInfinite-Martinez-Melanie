//package red;
//
//import java.io.IOException;
//import java.net.DatagramPacket;
//import java.net.DatagramSocket;
//import java.net.InetAddress;
//import java.net.SocketException;
//import java.net.UnknownHostException;
//
//import utiles.Global;
//
//public class HiloCliente extends Thread {
//
//	private DatagramSocket conexion;
//	private InetAddress ipServer;
//	private int puerto = 9998;
//	private boolean fin = false;
//
//	public HiloCliente() {
//		try {
//			ipServer = InetAddress.getByName("255.255.255.255");
//			conexion = new DatagramSocket();
//		} catch (SocketException | UnknownHostException e) {
//			e.printStackTrace();
//		}
//		enviarMensaje("Conexion");
//	}
//
//	public void enviarMensaje(String mensaje) {
//		byte[] data = mensaje.getBytes();
//		DatagramPacket dp = new DatagramPacket(data, data.length, ipServer, puerto);
//		try {
//			conexion.send(dp);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}
//
//	@Override
//	public void run() {
//		do {
//			byte[] data = new byte[1024];
//			DatagramPacket dp = new DatagramPacket(data, data.length);
//			try {
//				conexion.receive(dp);
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//			procesarMensaje(dp);
//		} while (!fin);
//	}
//	
//	private void procesarMensaje(DatagramPacket dp) {
//		String mensaje = dp.getData().toString().trim();
//		if(mensaje.equals("OK")) {
//			ipServer = dp.getAddress();
//		}else if(mensaje.equals("Empieza")) {
//			Global.empieza = true; 
//		}
//	}
//
//}
