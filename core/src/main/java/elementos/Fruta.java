package elementos;


import utiles.Recursos;

public class Fruta {
	private float posX, posY;
	private int ancho, alto;
	private TipoFruta tipo; 
	private Imagen imagen; 

	public Fruta(float posX, float posY, int ancho, int alto, TipoFruta tipo) {
		this.posX = posX;
		this.posY = posY;
		this.ancho = ancho;
		this.alto = alto;
		this.tipo = tipo; 
		
		String imagenTipo = obtenerImagen(tipo);
		imagen = new Imagen(imagenTipo);
	}
	
	private String obtenerImagen(TipoFruta tipo) {
		String rutaImagen;  
		switch (tipo) {
         case MANZANA:
        	 rutaImagen = Recursos.MANZANA;
        	 break;
         case BANANA:
        	 rutaImagen = Recursos.BANANA;
        	 break; 
         case CEREZA:
        	 rutaImagen = Recursos.CEREZA;
        	 break;
         case SANDIA:
        	 rutaImagen = Recursos.SANDIA;
        	 break; 
         case UVA:
        	 rutaImagen = Recursos.UVA;
         default:
        	 rutaImagen = Recursos.MANZANA;
        	 break; 
     }
		return rutaImagen;
	}
	
	public void dibujar () {
		imagen.setParametros(posX, posY, ancho-1, alto-1);
		imagen.dibujar();
	}
	
	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		this.posY = posY;
	}

	public int getAncho() {
		return ancho;
	}

	public void setAncho(int ancho) {
		this.ancho = ancho;
	}
	public int getAlto() {
		return alto;
	}

	public void setAlto(int alto) {
		this.alto = alto;
	}

	public void dispose() {
		imagen.dispose();
	}

	public TipoFruta getTipo() {
		return tipo;
	}

	public void setTipo(TipoFruta tipo) {
		this.tipo = tipo;
	}
}
