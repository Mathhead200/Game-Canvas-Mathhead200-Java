package com.mathhead200.game;

import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Set;


public final class GameState
{
	public Game game;
	public long loopID;
	public double deltaTime;
	public double elapsedTime;
	public Set<Integer> pressedKeys;
	public Set<Integer> heldKeys;
	public Set<Integer> releasedKeys;
	public Set<Integer> pressedButtons;
	public Set<Integer> heldButtons;
	public Set<Integer> releasedButtons;
	public MouseEvent mouseEntered;
	public MouseEvent mouseExited;
	public Point mouseLocation;
}
