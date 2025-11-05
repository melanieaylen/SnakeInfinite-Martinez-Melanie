package entradas.salidas.teclado;

import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;

import pantallas.PantallaMenu;

public class Entradas implements InputProcessor {

	public static boolean abajo = false, arriba = false, enter = false;
	PantallaMenu app; 
	
	public Entradas(PantallaMenu app) {
		this.app = app; 
	}
	
	public static boolean isEnter() {
		return enter;
	}

	public static void setEnter(boolean enter) {
		Entradas.enter = enter;
	}

	@Override
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
	public boolean keyUp(int keycode) {
		if(keycode == Keys.DOWN) {
			abajo = false; 
		}
		
		if(keycode == Keys.UP) {
			arriba = false; 
		}
		return false;
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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(float amountX, float amountY) {
		// TODO Auto-generated method stub
		return false;
	}

}
