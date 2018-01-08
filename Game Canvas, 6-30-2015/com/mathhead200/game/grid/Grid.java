package com.mathhead200.game.grid;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

import com.mathhead200.game.Behavior;
import com.mathhead200.game.Drawable;
import com.mathhead200.game.GameState;
import com.mathhead200.game.Vector;


/**
 * TODO: Must be tested!
 * @author Christopher D'Angelo
 */
public class Grid implements Drawable, Behavior
{
	private int rows, columns;
	private BufferedImage image = null;
	private Set<GridDrawable> sprites = new HashSet<>();
	
	
	public Grid(int rows, int columns) {
		this.rows = rows;
		this.columns = columns;
	}
	
	
	private void resizeImage(int width, int height) {
		if( image != null && image.getWidth() == width && image.getHeight() == height )
			return;
		image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	public Image getImage() {
		int width = image.getWidth() / columns;
		int height = image.getHeight() / rows;
		Graphics2D g = image.createGraphics();
		for( GridDrawable sprite : sprites )
			g.drawImage( sprite.drawable.getImage(), sprite.xPos, sprite.yPos, width, height, null );
		g.dispose();
		return image;
	}

	public Vector getPosition() {
		return Vector.ZERO;
	}

	public boolean isHUD() {
		return true;
	}
	
	public void behave(GameState info) {
		resizeImage( info.game.getWidth(), info.game.getHeight() );
	}
}
