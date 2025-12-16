package elementos;

import utiles.Render;

//Hereda de elemento
public class Fruta extends Elemento {
    
    private TipoFruta tipo;
    private Imagen imagen;
    
    public Fruta(float posX, float posY, int ancho, int alto, TipoFruta tipo) {
        super(posX, posY, ancho, alto);
        this.tipo = tipo;
        this.imagen = new Imagen(tipo.getRutaImagen());
    }
    
    @Override
    public void dibujar() {
        imagen.setParametros(posX, posY, ancho - 1, alto - 1);
        Render.batch.begin();
        imagen.dibujar();
        Render.batch.end();
    }
    
    public void reubicar(float nuevaX, float nuevaY) {
        setPosicion(nuevaX, nuevaY);
    }
    
    // Getters
    public TipoFruta getTipo() {
        return tipo;
    }
    
    public int getPuntos() {
        return tipo.getPuntos();
    }
    
    public void dispose() {
        if (imagen != null) {
            imagen.dispose();
        }
    }
}