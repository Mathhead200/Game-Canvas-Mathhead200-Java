package demos.three;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Mob;
import com.mathhead200.games3d.Resources;
import com.mathhead200.games3d.Vector;


public class Me extends Mob
{
	private static final Map<Integer, Vector> VEL_MAP = new HashMap<Integer, Vector>();
	static {
		VEL_MAP.put( KeyEvent.VK_LEFT, new Vector(-0.5, 0, 0) );
		VEL_MAP.put( KeyEvent.VK_RIGHT, new Vector(0.5, 0, 0) );
		VEL_MAP.put( KeyEvent.VK_UP, new Vector(0, -0.5, 0) );
		VEL_MAP.put( KeyEvent.VK_DOWN, new Vector(0, 0.5, 0) );
		VEL_MAP.put( KeyEvent.VK_CONTROL, new Vector(0, 0, -0.5) );
		VEL_MAP.put( KeyEvent.VK_SHIFT, new Vector(0, 0, 0.5) );
	}


	public Me() {
		super( Resources.loadStaticImageFromFile("rsc/bandit3-r.dmi") );
	}


	public void behave(GameState info) {
		for( Integer k : info.pressedKeys ) {
			Vector vel = VEL_MAP.get(k);
			if( vel != null )
				applyImpulse(vel);
		}
		for( Integer k : info.releasedKeys ) {
			Vector vel = VEL_MAP.get(k);
			if( vel != null )
				applyImpulse( vel.negate() );
		}
	}
}
