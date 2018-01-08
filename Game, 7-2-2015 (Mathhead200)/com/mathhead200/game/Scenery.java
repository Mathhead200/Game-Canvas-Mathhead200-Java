package com.mathhead200.game;

import java.awt.Image;


public class Scenery implements Drawable
{
	private final Image image;
	private final Vector pos;
	private final boolean isHUD;


	public Scenery(Image image, Vector pos, boolean isHUD) {
		this.image = image;
		this.pos = pos;
		this.isHUD = isHUD;
	}

	public Scenery(Image image, Vector pos) {
		this(image, pos, false);
	}

	public Scenery(Image image, double x, double y, boolean isHUD) {
		this( image, new Vector(x, y), isHUD );
	}

	public Scenery(Image image, double x, double y, double z) {
		this( image, new Vector(x, y, z) );
	}

	public Scenery(Image image, double x, double y) {
		this( image, new Vector(x, y) );
	}


	public Image getImage() {
		return image;
	}

	public Vector getPosition() {
		return pos;
	}

	public boolean isHUD() {
		return isHUD;
	}

	public double getX() {
		return pos.x;
	}

	public double getY() {
		return pos.y;
	}
}
