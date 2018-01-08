package com.mathhead200.battleground;

import com.mathhead200.game.DenseSprite;
import com.mathhead200.game.GameState;
import com.mathhead200.game.Mob;


public class Tower extends Mob implements Target
{
	public class Bolt extends Mob
	{
		private Target target;
		private int power;
		private double speed; //in pixels per ms
		
		
		public Bolt(Target target, int power) {
			if( !(target instanceof DenseSprite) )
				throw new IllegalArgumentException("target must also be a DenceSprite");
			this.target = target;
			this.power = power;
			this.speed = (600.0 * 1e-3) / Math.sqrt(power);
		}
		
		
		public Target getTarget() {
			return target;
		}
		
		public int getPower() {
			return power;
		}
		
		
		public void behave(GameState info) {
			DenseSprite sprite = (DenseSprite) target;
			if( isIntersecting(sprite) ) {
				info.game.remove(this);
				if( target.hit(this) )
					info.game.remove(sprite);
			} else
				setVelocity( sprite.centerOfMass().subtract( this.centerOfMass() ).normalize().multiply(speed) );
		}
	}
	
	
	private Mage owner;
	private int level;
	private Bolt bolt = null;
	
	
	public Tower(Mage owner, int level) {
		this.owner = owner;
		this.level = level;
	}
	
	
	public int getLevel() {
		return level;
	}
	
	
	public boolean hit(Bolt bolt) {
		return (level -= bolt.getPower()) <= 0;
	}
	
	public void behave(GameState info) {
		if( bolt == null ) {
			bolt = new Bolt(owner.getEnemy(), level);
			bolt.setPosition( this.getPosition() );
		}
	}
}
