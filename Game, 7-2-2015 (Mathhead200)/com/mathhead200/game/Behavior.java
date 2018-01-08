package com.mathhead200.game;


/**
 * <p>
 * 	The parent interface for all behavior in a {@link Game}.
 * 	This method is invoked once per frame within
 * 	the game canvas, and therefore should minimize its "temporal footprint".
 * </p> <p>
 * 	A <code>Behavior</code> could be implemented by an NPC or Mob
 *  as an AI; or simply be running physics calculations, and be non-drawable.
 * </p>
 * 
 * @author Christopher D'Angelo
 * @version Feb 19, 2014
 */
public interface Behavior extends Item
{
	/**
	 * Is invoked once per frame, and can be used
	 * to run calculations and/or modify behavior.
	 * 
	 * @param info - Carries information about the current state of the game
	 * 	during this frame.
	 */
	public void behave(GameState info);
}
