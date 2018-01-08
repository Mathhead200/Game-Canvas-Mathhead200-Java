package com.mathhead200.game.grid;

import com.mathhead200.game.Drawable;


public class GridDrawable
{
	public final Drawable drawable;
	public int xPos = 0;
	public int yPos = 0;
	public int xOffset = 0;
	public int yOffset = 0;
	
	public GridDrawable(Drawable d) {
		if( d == null )
			throw new IllegalArgumentException("null Drawable");
		this.drawable = d;
	}
}
