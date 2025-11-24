package pantallas;

import com.badlogic.gdx.Screen;

import elementos.Imagen;
import utiles.Recursos;
import utiles.Render;

public class GameOver implements Screen {
    
    private float transparencia = 0; // Empieza invisible
    private Imagen fondo; // o los elementos que tengas
    @Override
    public void show() {
        // Inicializa tus elementos
        fondo = new Imagen(Recursos.GAME_OVER);
    }
    
    @Override
    public void render(float delta) {
        Render.limpiarPantalla(0, 0, 0);
        
        // Incrementar transparencia gradualmente
        procesarTransparencia();
        
        Render.batch.begin();
        fondo.setTransparencia(transparencia);
        fondo.dibujar();
        
        Render.batch.end();
    }
    
    private void procesarTransparencia() {
        transparencia += 0.004f; // Misma velocidad que usas en PantallaJuego
        if(transparencia > 1) {
            transparencia = 1;
        }
    }

	@Override
	public void resize(int width, int height) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
	}
    
    // ... resto de métodos
}