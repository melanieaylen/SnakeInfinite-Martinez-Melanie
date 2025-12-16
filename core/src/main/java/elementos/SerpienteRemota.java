package elementos;

import com.badlogic.gdx.graphics.Color;
import utiles.Render;

import java.util.ArrayList;
import java.util.List;

public class SerpienteRemota {
    
    private List<float[]> segmentos;
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
        
        segmentos.add(new float[]{posX, posY});
    }

    public void actualizarSegmentos(String datosSegmentos) {
        if (datosSegmentos == null || datosSegmentos.isEmpty()) {
            return;
        }
        
        segmentos.clear();
        
        String[] segmentosStr = datosSegmentos.split(",");
        
        for (String segmentoStr : segmentosStr) {
            String[] coords = segmentoStr.split(":");
            if (coords.length >= 2) {
                try {
                    float x = Float.parseFloat(coords[0]);
                    float y = Float.parseFloat(coords[1]);
                    segmentos.add(new float[]{x, y});
                } catch (NumberFormatException e) {
                }
            }
        }
    }
    
    public void actualizarPosicion(float x, float y) {
        if (segmentos.isEmpty()) {
            segmentos.add(new float[]{x, y});
        } else {
            segmentos.set(0, new float[]{x, y});
        }
    }

    public void dibujar() {
        if (segmentos.isEmpty()) {
            return;
        }
        
        for (int i = 0; i < segmentos.size(); i++) {
            float[] segmento = segmentos.get(i);
            
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