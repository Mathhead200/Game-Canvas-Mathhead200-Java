package demos.three;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.mathhead200.games3d.Behavior;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Vector;


public class Camera implements Behavior
{
	private static final Map<Integer, Vector> VEL_MAP = new HashMap<Integer, Vector>();
	static {
		VEL_MAP.put( KeyEvent.VK_A, new Vector(-0.25, 0, 0) );
		VEL_MAP.put( KeyEvent.VK_D, new Vector(0.25, 0, 0) );
		VEL_MAP.put( KeyEvent.VK_W, new Vector(0, -0.25, 0) );
		VEL_MAP.put( KeyEvent.VK_S, new Vector(0, 0.25, 0) );
		VEL_MAP.put( KeyEvent.VK_R, new Vector(0, 0, 0.25) );
		VEL_MAP.put( KeyEvent.VK_F, new Vector(0, 0, -0.25) );
	}


	public void behave(GameState info) {
		for( Integer k : info.heldKeys ) {
			Vector vel = VEL_MAP.get(k);
			if( vel != null )
				info.game.setPerspective( info.game.getPerspective().add(vel.multiply(info.deltaTime)) );
		}
	}
}
