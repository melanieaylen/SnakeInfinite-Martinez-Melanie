package elementos;

import com.badlogic.gdx.math.Rectangle;

public abstract class Elemento {
    
    protected float posX;
    protected float posY;
    protected int ancho;
    protected int alto;
    protected Rectangle limites;
    
    public Elemento(float posX, float posY, int ancho, int alto) {
        this.posX = posX;
        this.posY = posY;
        this.ancho = ancho;
        this.alto = alto;
        this.limites = new Rectangle(posX, posY, ancho, alto);
    }
 
    public void dibujar(){}
    
    public void setPosicion(float x, float y) {
        this.posX = x;
        this.posY = y;
        actualizarLimites();
    }
    

    protected void actualizarLimites() {
        limites.set(posX, posY, ancho, alto);
    }
    

    public boolean colisionaCon(Elemento otro) {
        return limites.overlaps(otro.getLimites());
    }
    
    public boolean colisionaConPosicion(float x, float y) {
        return posX == x && posY == y;
    }
    
    // Getters
    public float getPosX() {
        return posX;
    }
    
    public float getPosY() {
        return posY;
    }
    
    public int getAncho() {
        return ancho;
    }
    
    public int getAlto() {
        return alto;
    }
    
    public Rectangle getLimites() {
        return limites;
    }
    
    // Setters
    public void setPosX(float posX) {
        this.posX = posX;
        actualizarLimites();
    }
    
    public void setPosY(float posY) {
        this.posY = posY;
        actualizarLimites();
    }
}