package com.mathhead200.battleground;

import com.mathhead200.battleground.Tower.Bolt;


public interface Target
{
	/**
	 * Called when a Bolt hits its target, <code>this</code>.
	 * @param bolt
	 * @return <code>true</code> if this target got killed, <code>false</code> otherwise. <br>
	 * 	If the target was killed it will be removed from the game.
	 */
	public boolean hit(Bolt bolt);
}
