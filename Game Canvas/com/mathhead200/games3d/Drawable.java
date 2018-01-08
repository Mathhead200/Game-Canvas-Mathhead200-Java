package com.mathhead200.games3d;

import java.awt.Image;


/**
 * The parent interface for all graphics in a {@link Game}.
 * 	Its methods are (usually) invoked once per frame within
 * 	the game canvas, and therefore should minimize their "temporal footprint".
 * @author Christopher D'Angelo
 * @version Feb 19, 2014
 */
public interface Drawable extends Item
{
	/**
	 * @return The image to be drawn onto the game canvas.
	 */
	public Image getImage();

	/**
	 * @return The position of the image on the game canvas.
	 */
	public Vector getPosition();

	/**
	 * @return <code>true</code> if the image should be not be positioned
	 * 	relative to the game's origin or perspective, as in an element of
	 * 	a heads up display; <code>false</code> if the image should be positioned
	 * 	in the game space, having its final drawn position affected by both
	 * 	the game's origin and perspective.
	 */
	public boolean isHUD();
}
