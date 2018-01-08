package com.mathhead200.msd;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import com.mathhead200.games3d.Sprite;
import com.mathhead200.games3d.Vector;


public class Atom extends Sprite
{
	private BufferedImage image = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
	private Vector spin = Vector.J.negate();
	private boolean spinChanged = true;

	private int c(double mag) {
		return (int)( (mag + 1) / 2 * 255 );
	}


	public Atom() {
		Graphics2D g = image.createGraphics();
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR) );
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		g.dispose();
	}


	public Vector getSpin() {
		return spin;
	}

	public void setSpin(Vector spin) {
		this.spin = spin;
		spinChanged = true;
	}

	public Image getImage() {
		if( spinChanged ) {
			Graphics2D g = image.createGraphics();
			g.setColor( new Color(c(spin.x), c(spin.y), c(spin.z)) );
			g.fillOval( 0, 0, image.getWidth(), image.getHeight() );
			g.dispose();
			spinChanged = false;
		}
		return image;
	}

	public String toString() {
		return "pos=" + getPosition() + ", spin=" + getSpin();
	}
}
