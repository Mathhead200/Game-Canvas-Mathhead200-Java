package com.mathhead200.battleground;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.mathhead200.battleground.Tower.Bolt;
import com.mathhead200.game.GameState;
import com.mathhead200.game.Mob;
import com.mathhead200.game.Vector;


public class Mage extends Mob implements Target
{	
	private static final double DEFAULT_MANA_RATE = 0.5 * 1e-3;
	
	private Map<Integer, Runnable> pressedKeyBindings = new HashMap<>();
	private Map<Integer, Runnable> releasedKeyBindings = new HashMap<>();
	
	private int hp = 20;
	private double mana = 3.0;
	private double manaMax = 10.0;
	private double manaRate = DEFAULT_MANA_RATE; //in points per ms
	private boolean shielded = false;
	private double speed = 300 * 1e-3; //in pixels per ms
	private Mage enemy = null;
	
	
	public Mage() {
		pressedKeyBindings.put(KeyEvent.VK_W, () -> {
			applyImpulse( Vector.J.multiply(-speed) );
		});
		pressedKeyBindings.put(KeyEvent.VK_A, () -> {
			applyImpulse( Vector.I.multiply(-speed) );
		});
		pressedKeyBindings.put(KeyEvent.VK_S, () -> {
			applyImpulse( Vector.J.multiply(speed) );
		});
		pressedKeyBindings.put(KeyEvent.VK_D, () -> {
			applyImpulse( Vector.I.multiply(speed) );
		});
		pressedKeyBindings.put(KeyEvent.VK_UP, pressedKeyBindings.get(KeyEvent.VK_W));
		pressedKeyBindings.put(KeyEvent.VK_LEFT, pressedKeyBindings.get(KeyEvent.VK_A));
		pressedKeyBindings.put(KeyEvent.VK_DOWN, pressedKeyBindings.get(KeyEvent.VK_S));
		pressedKeyBindings.put(KeyEvent.VK_RIGHT, pressedKeyBindings.get(KeyEvent.VK_D));
		pressedKeyBindings.put(KeyEvent.VK_SHIFT, () -> {
			shielded = true;
		});
		
		releasedKeyBindings.put(KeyEvent.VK_W, pressedKeyBindings.get(KeyEvent.VK_S));
		releasedKeyBindings.put(KeyEvent.VK_A, pressedKeyBindings.get(KeyEvent.VK_D));
		releasedKeyBindings.put(KeyEvent.VK_S, pressedKeyBindings.get(KeyEvent.VK_W));
		releasedKeyBindings.put(KeyEvent.VK_D, pressedKeyBindings.get(KeyEvent.VK_A));
		releasedKeyBindings.put(KeyEvent.VK_UP, releasedKeyBindings.get(KeyEvent.VK_W));
		releasedKeyBindings.put(KeyEvent.VK_LEFT, releasedKeyBindings.get(KeyEvent.VK_A));
		releasedKeyBindings.put(KeyEvent.VK_DOWN, releasedKeyBindings.get(KeyEvent.VK_S));
		releasedKeyBindings.put(KeyEvent.VK_RIGHT, releasedKeyBindings.get(KeyEvent.VK_D));
		releasedKeyBindings.put(KeyEvent.VK_SHIFT, () -> {
			shielded = false;
		});
	}
	
	
	public int getHP() {
		return hp;
	}
	
	public double getMana() {
		return mana;
	}
	
	public boolean isShielded() {
		return shielded;
	}
	
	public Mage getEnemy() {
		return enemy;
	}
	
	
	public boolean hit(Bolt bolt) {
		int damage = bolt.getPower();
		if( shielded )
			damage /= 2;
		return (hp -= damage) <= 0;
	}
	
	public void behave(GameState info) {
		
		if( (mana += manaRate * info.deltaTime) > manaMax )
			mana = manaMax;
		
		for( int k : info.pressedKeys ) {
			Runnable x = pressedKeyBindings.get(k);
			if( x != null )
				x.run();
		}
		for( int k : info.releasedKeys ) {
			Runnable x = releasedKeyBindings.get(k);
			if( x != null )
				x.run();
		}
		
	}
}
