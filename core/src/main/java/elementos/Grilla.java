package elementos;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import utiles.Render;

public class Grilla {
    private int tamanioCelda;
    private Color color1;
    private Color color2;
    private Color colorLineas;

    public Grilla(int tamanioCelda) {
        this.tamanioCelda = tamanioCelda;
        // Colores por defecto tipo ajedrez
        this.color1 = new Color(0.9f, 0.9f, 0.9f, 1f); // Blanco grisáceo
        this.color2 = new Color(0.3f, 0.3f, 0.3f, 1f); // Gris oscuro
        this.colorLineas = Color.DARK_GRAY;
    }

    public Grilla(int tamanioCelda, Color color1, Color color2) {
        this.tamanioCelda = tamanioCelda;
        this.color1 = color1;
        this.color2 = color2;
        this.colorLineas = Color.DARK_GRAY;
    }

    public void dibujarGrillaInfinita(OrthographicCamera camara) {
        // Calcular el área visible basada en la cámara
        float camX = camara.position.x;
        float camY = camara.position.y;
        float anchoVisible = camara.viewportWidth;
        float altoVisible = camara.viewportHeight;
        
        // Calcular límites de la grilla visible
        int inicioX = (int)((camX - anchoVisible / 2) / tamanioCelda) - 1;
        int finX = (int)((camX + anchoVisible / 2) / tamanioCelda) + 1;
        int inicioY = (int)((camY - altoVisible / 2) / tamanioCelda) - 1;
        int finY = (int)((camY + altoVisible / 2) / tamanioCelda) + 1;
        
        // DIBUJAR CUADRADOS (patrón ajedrez)
        Render.shaper.begin(ShapeType.Filled);
        for (int x = inicioX; x <= finX; x++) {
            for (int y = inicioY; y <= finY; y++) {
                // Alternar colores según posición (patrón ajedrez)
                if ((x + y) % 2 == 0) {
                    Render.shaper.setColor(color1);
                } else {
                    Render.shaper.setColor(color2);
                }
                
                float posX = x * tamanioCelda;
                float posY = y * tamanioCelda;
                Render.shaper.rect(posX, posY, tamanioCelda, tamanioCelda);
            }
        }
        Render.shaper.end();
        
        // DIBUJAR LÍNEAS (opcional, para delimitar las celdas)
        Render.shaper.begin(ShapeType.Line);
        Render.shaper.setColor(colorLineas);
        
        // Líneas verticales
        for (int x = inicioX; x <= finX + 1; x++) {
            float posX = x * tamanioCelda;
            Render.shaper.line(posX, inicioY * tamanioCelda, posX, finY * tamanioCelda);
        }
        
        // Líneas horizontales
        for (int y = inicioY; y <= finY + 1; y++) {
            float posY = y * tamanioCelda;
            Render.shaper.line(inicioX * tamanioCelda, posY, finX * tamanioCelda, posY);
        }
        Render.shaper.end();
    }

    public int getTamanioCelda() {
        return tamanioCelda;
    }

    public void setColores(Color color1, Color color2) {
        this.color1 = color1;
        this.color2 = color2;
    }

    public void setColorLineas(Color colorLineas) {
        this.colorLineas = colorLineas;
    }
}