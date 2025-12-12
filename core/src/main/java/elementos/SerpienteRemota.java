package elementos;

import com.badlogic.gdx.graphics.Color;
import utiles.Render;

import java.util.ArrayList;
import java.util.List;

/**
 * SerpienteRemota - Versión de Serpiente para renderizado en cliente multijugador
 * Ahora dibuja todos los segmentos recibidos del servidor
 */
public class SerpienteRemota {
    
    private List<float[]> segmentos; // Lista de [x, y]
    private int ancho;
    private int alto;
    private Color colorCabeza;
    private Color colorCuerpo;
    
    public SerpienteRemota(float posX, float posY, int ancho, int alto, 
                          Color colorCabeza, Color colorCuerpo) {
        this.ancho = ancho;
        this.alto = alto;
        this.colorCabeza = colorCabeza;
        this.colorCuerpo = colorCuerpo;
        this.segmentos = new ArrayList<>();
        
        // Inicialmente solo tiene la cabeza
        segmentos.add(new float[]{posX, posY});
        
        System.out.println("🐍 SerpienteRemota creada en (" + posX + ", " + posY + ")");
    }
    
    /**
     * Actualiza la posición de la serpiente con los datos del servidor
     * @param datosSegmentos String en formato "x1:y1,x2:y2,x3:y3,..."
     */
    public void actualizarSegmentos(String datosSegmentos) {
        if (datosSegmentos == null || datosSegmentos.isEmpty()) {
            System.err.println("⚠️ datosSegmentos vacío o null");
            return;
        }
        
        segmentos.clear();
        
        // Parsear los segmentos
        String[] segmentosStr = datosSegmentos.split(",");
        
        for (String segmentoStr : segmentosStr) {
            String[] coords = segmentoStr.split(":");
            if (coords.length >= 2) {
                try {
                    float x = Float.parseFloat(coords[0]);
                    float y = Float.parseFloat(coords[1]);
                    segmentos.add(new float[]{x, y});
                } catch (NumberFormatException e) {
                    System.err.println("⚠️ Error al parsear segmento: " + segmentoStr);
                    e.printStackTrace();
                }
            }
        }
        
        // Debug: mostrar cuántos segmentos se parsearon
        if (segmentos.size() > 0 && segmentos.size() <= 5) {
            System.out.println("✅ Parseados " + segmentos.size() + " segmentos. Cabeza: (" + 
                             segmentos.get(0)[0] + ", " + segmentos.get(0)[1] + ")");
        }
    }
    
    /**
     * Actualiza solo la posición de la cabeza (compatibilidad)
     */
    public void actualizarPosicion(float x, float y) {
        if (segmentos.isEmpty()) {
            segmentos.add(new float[]{x, y});
        } else {
            segmentos.set(0, new float[]{x, y});
        }
    }
    
    /**
     * Dibuja la serpiente completa
     */
    public void dibujar() {
        if (segmentos.isEmpty()) {
            System.err.println("⚠️ No hay segmentos para dibujar");
            return;
        }
        
        for (int i = 0; i < segmentos.size(); i++) {
            float[] segmento = segmentos.get(i);
            
            // Cabeza en color especial
            if (i == 0) {
                Render.shaper.setColor(colorCabeza);
            } else {
                Render.shaper.setColor(colorCuerpo);
            }
            
            Render.shaper.rect(segmento[0], segmento[1], ancho, alto);
        }
    }
    
    public float obtenerPosX() {
        return segmentos.isEmpty() ? 0 : segmentos.get(0)[0];
    }
    
    public float obtenerPosY() {
        return segmentos.isEmpty() ? 0 : segmentos.get(0)[1];
    }
    
    public int obtenerTamanio() {
        return segmentos.size();
    }
}