package elementos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * GestorFrutas refactorizado
 * Maneja todas las frutas del juego de forma mÃ¡s limpia
 */
public class GestorFrutas {
    
    private List<Fruta> frutas;
    private int tamanioElementos;
    private Random random;
    
    public GestorFrutas(int tamanioElementos) {
        this.tamanioElementos = tamanioElementos;
        this.frutas = new ArrayList<>();
        this.random = new Random();
    }
    
    /**
     * Inicializa todas las frutas del juego
     */
    public void inicializarFrutas(Serpiente serpiente) {
        for (TipoFruta tipo : TipoFruta.values()) {
            Fruta fruta = new Fruta(0, 0, tamanioElementos, tamanioElementos, tipo);
            moverFrutaAleatoria(fruta, serpiente);
            frutas.add(fruta);
        }
    }
    
    /**
     * Verifica si la serpiente colisionÃ³ con alguna fruta
     * @return La fruta colisionada o null
     */
    public Fruta verificarColisiones(Serpiente serpiente) {
        for (Fruta fruta : frutas) {
            if (fruta.colisionaConPosicion(serpiente.getPosX(), serpiente.getPosY())) {
                return fruta;
            }
        }
        return null;
    }
    
    /**
     * Reubica una fruta despuÃ©s de ser comida
     */
    public void reubicarFruta(Fruta fruta, Serpiente serpiente) {
        moverFrutaAleatoria(fruta, serpiente);
    }
    
    /**
     * Dibuja todas las frutas
     */
    public void dibujarTodas() {
        for (Fruta fruta : frutas) {
            fruta.dibujar();
        }
    }
    
    /**
     * Mueve una fruta a una posiciÃ³n aleatoria vÃ¡lida
     */
    private void moverFrutaAleatoria(Fruta fruta, Serpiente serpiente) {
        int rangoMin = -10;
        int rangoMax = 20;
        float nuevaX, nuevaY;
        
        do {
            int offsetX = random.nextInt(rangoMax - rangoMin + 1) + rangoMin;
            int offsetY = random.nextInt(rangoMax - rangoMin + 1) + rangoMin;
            
            nuevaX = serpiente.getPosX() + (offsetX * tamanioElementos);
            nuevaY = serpiente.getPosY() + (offsetY * tamanioElementos);
            
        } while (serpiente.colisionConPosicion(nuevaX, nuevaY));
        
        fruta.reubicar(nuevaX, nuevaY);
    }
    
    /**
     * Libera todos los recursos
     */
    public void dispose() {
        for (Fruta fruta : frutas) {
            fruta.dispose();
        }
        frutas.clear();
    }
    
    // Getter
    public List<Fruta> getFrutas() {
        return frutas;
    }
}