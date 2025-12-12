// ========== FrutaRemota.java ==========
package elementos;

import utiles.Render;

/**
 * FrutaRemota - Versión de Fruta para renderizado en cliente multijugador
 */
public class FrutaRemota {
    
    private float posX;
    private float posY;
    private int ancho;
    private int alto;
    private TipoFruta tipo;
    private Imagen imagen;
    
    public FrutaRemota(float posX, float posY, int ancho, int alto, TipoFruta tipo) {
        this.posX = posX;
        this.posY = posY;
        this.ancho = ancho;
        this.alto = alto;
        this.tipo = tipo;
        this.imagen = new Imagen(tipo.getRutaImagen());
    }
    
    /**
     * Dibuja la fruta
     */
    public void dibujar() {
        imagen.setParametros(posX, posY, ancho - 1, alto - 1);
        Render.batch.begin();
        imagen.dibujar();
        Render.batch.end();
    }
    
    /**
     * Libera recursos
     */
    public void dispose() {
        if (imagen != null) {
            imagen.dispose();
        }
    }
    
    public float obtenerPosX() {
        return posX;
    }
    
    public float obtenerPosY() {
        return posY;
    }
    
    public TipoFruta obtenerTipo() {
        return tipo;
    }
}