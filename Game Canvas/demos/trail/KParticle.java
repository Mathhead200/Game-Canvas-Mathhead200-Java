package demos.trail;

import java.awt.Color;

import com.mathhead200.games3d.Behavior;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Vector;


public class KParticle extends Particle implements Behavior
{
	private double k;


	public KParticle(double k) {
		super(Color.WHITE, 5);
		this.k = k;
	}


	public void behave(GameState info) {
		Vector c = centerOfMass();
		Vector d = new Vector( info.mouseLocation.x + info.game.getOrigin().x,
				info.mouseLocation.y + info.game.getOrigin().y ).subtract(c);
		if( info.heldButtons.contains(1) )
			d = d.rotate(Math.PI / 4);
		if( info.heldButtons.contains(3) )
			d = d.rotate(-Math.PI / 4);
		applyImpulse( d.multiply(k * info.deltaTime) );

		if( d.norm() > 600 && d.angleBetween(getVelocity()) > Math.PI / 2 )
			setVelocity( Vector.ZERO );
	}
}
