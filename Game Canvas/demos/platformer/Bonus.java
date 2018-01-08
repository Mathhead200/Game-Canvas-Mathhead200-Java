package demos.platformer;

import java.awt.Image;

import com.mathhead200.games3d.Behavior;
import com.mathhead200.games3d.DenseSprite;
import com.mathhead200.games3d.GameState;


public abstract class Bonus extends DenseSprite implements Behavior
{
	private Protagonist target;
	private double lifespan;
	private int pointValue;


	public Bonus(Iterable<Image> animation, Protagonist target, double lifespan, int pointValue) {
		super(animation);
		this.target = target;
		this.lifespan = lifespan;
		this.pointValue = pointValue;
	}


	public abstract void doBonus(Protagonist target);

	public void behave(GameState info) {
		if( isIntersecting(target) ) {
			doBonus(target);
			target.givePoints(pointValue);
			setHitBox(null);
			lifespan = 0.0;
		}
		if( lifespan <= 0.0 )
			info.game.remove(this);
		else
			lifespan -= info.deltaTime;
	}
}
