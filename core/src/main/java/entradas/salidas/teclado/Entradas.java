package entradas.salidas.teclado;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import pantallas.PantallaMenu;

public class Entradas implements InputProcessor {

	public static boolean abajo = false, arriba = false, enter = false, izquierda = false, derecha = false;
	
	//getters y setters
	public static boolean isEnter() {
		return enter;
	}

	public static void setEnter(boolean enter) {
		Entradas.enter = enter;
	}

	@Override
	//SE PRESIONA LA TECLA
	public boolean keyDown(int keycode) {
		if(keycode == Keys.DOWN) {
			abajo = true; 
		}
		else if(keycode == Keys.UP) {
			arriba = true; 
		}
		
		if(keycode == Keys.ENTER) {
			enter = true; 
		}
		
		if(keycode == Keys.LEFT) {
			izquierda = true; 
		}
		
		if (keycode == Keys.RIGHT) {
			derecha = true; 
		}
		return false;
	}

	public static boolean isAbajo() {
		return abajo;
	}

	public void setAbajo(boolean abajo) {
		this.abajo = abajo;
	}

	public boolean isArriba() {
		return arriba;
	}

	public void setArriba(boolean arriba) {
		this.arriba = arriba;
	}

	@Override
	//SE ALZA LA TECLA 
	public boolean keyUp(int keycode) {
		if(keycode == Keys.DOWN) {
			abajo = false; 
		}
		
		if(keycode == Keys.UP) {
			arriba = false; 
		}
		
		if(keycode == Keys.ENTER) {
			enter = false; 
		}
		
		if(keycode == Keys.LEFT) {
			izquierda = false; 
		}
		
		if(keycode == Keys.RIGHT) {
			derecha = false; 
		}
		return false;
	}

	public static boolean isIzquierda() {
		return izquierda;
	}

	public static void setIzquierda(boolean izquierda) {
		Entradas.izquierda = izquierda;
	}

	public static boolean isDerecha() {
		return derecha;
	}

	public static void setDerecha(boolean derecha) {
		Entradas.derecha = derecha;
	}

	@Override
	public boolean keyTyped(char character) {

		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {

		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		return false;
	}

}
