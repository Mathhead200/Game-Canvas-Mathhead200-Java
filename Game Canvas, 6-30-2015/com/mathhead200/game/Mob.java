package com.mathhead200.game;

import java.awt.Image;


public abstract class Mob extends DenseSprite implements Behavior
{
	public Mob(Iterable<Image> animation) {
		super(animation);
	}

	public Mob() {
		super();
	}
}
