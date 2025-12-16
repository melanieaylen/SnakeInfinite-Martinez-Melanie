package elementos;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GestorFrutas {
    
    private List<Fruta> frutas;
    private int tamanioElementos;
    private Random aleatorio;
    
    public GestorFrutas(int tamanioElementos) {
        this.tamanioElementos = tamanioElementos;
        this.frutas = new ArrayList<>();
        this.aleatorio = new Random();
    }

    public void inicializarFrutas(Serpiente serpiente) {
        for (TipoFruta tipo : TipoFruta.values()) {
            Fruta fruta = new Fruta(0, 0, tamanioElementos, tamanioElementos, tipo);
            moverFrutaAleatoria(fruta, serpiente);
            frutas.add(fruta);
        }
    }

    public Fruta verificarColisiones(Serpiente serpiente) {
        for (Fruta fruta : frutas) {
            if (fruta.colisionaConPosicion(serpiente.getPosX(), serpiente.getPosY())) {
                return fruta;
            }
        }
        return null;
    }

    public void reubicarFruta(Fruta fruta, Serpiente serpiente) {
        moverFrutaAleatoria(fruta, serpiente);
    }

    public void dibujarTodas() {
        for (Fruta fruta : frutas) {
            fruta.dibujar();
        }
    }

    private void moverFrutaAleatoria(Fruta fruta, Serpiente serpiente) {
        int rangoMin = -10;
        int rangoMax = 20;
        float nuevaX, nuevaY;
        
        do {
            int desplazamientoX = aleatorio.nextInt(rangoMax - rangoMin + 1) + rangoMin;
            int desplazamientoY = aleatorio.nextInt(rangoMax - rangoMin + 1) + rangoMin;
            
            nuevaX = serpiente.getPosX() + (desplazamientoX * tamanioElementos);
            nuevaY = serpiente.getPosY() + (desplazamientoY * tamanioElementos);
            
        } while (serpiente.colisionConPosicion(nuevaX, nuevaY));
        
        fruta.reubicar(nuevaX, nuevaY);
    }

    public void dispose() {
        for (Fruta fruta : frutas) {
            fruta.dispose();
        }
        frutas.clear();
    }
    
    public List<Fruta> getFrutas() {
        return frutas;
    }
}