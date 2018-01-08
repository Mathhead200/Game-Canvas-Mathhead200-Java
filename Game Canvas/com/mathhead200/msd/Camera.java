package com.mathhead200.msd;

import java.awt.event.KeyEvent;
import java.util.HashMap;
import java.util.Map;

import com.mathhead200.games3d.Behavior;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Vector;


public class Camera implements Behavior
{
	private Map<Integer, Vector> o_map = new HashMap<Integer, Vector>(6);
	private Map<Integer, Vector> p_map = new HashMap<Integer, Vector>(6);
	private Integer o_reset, p_reset;

	public Camera() {
		o_map.put( KeyEvent.VK_UP, Vector.J.multiply(-0.3) );
		o_map.put( KeyEvent.VK_DOWN, Vector.J.multiply(0.3) );
		o_map.put( KeyEvent.VK_LEFT, Vector.I.multiply(-0.3) );
		o_map.put( KeyEvent.VK_RIGHT, Vector.I.multiply(0.3) );
		o_map.put( KeyEvent.VK_CONTROL, Vector.K.multiply(-0.1) );
		o_map.put( KeyEvent.VK_SHIFT, Vector.K.multiply(0.1) );
		o_reset = KeyEvent.VK_ENTER;

		p_map.put( KeyEvent.VK_W, Vector.J.multiply(-0.9) );
		p_map.put( KeyEvent.VK_S, Vector.J.multiply(0.9) );
		p_map.put( KeyEvent.VK_A, Vector.I.multiply(-0.9) );
		p_map.put( KeyEvent.VK_D, Vector.I.multiply(0.9) );
		p_map.put( KeyEvent.VK_Q, Vector.K.multiply(-0.3) );
		p_map.put( KeyEvent.VK_E, Vector.K.multiply(0.3) );
		p_reset = KeyEvent.VK_R;
	}

	public void behave(GameState info) {
		for( Integer k : info.heldKeys ) {
			if( o_map.containsKey(k) )
				info.game.setOrigin( info.game.getOrigin().add(
						o_map.get(k).multiply(-info.deltaTime) ) );
			if( o_reset.equals(k) )
				info.game.setOrigin( Vector.ZERO );

			if( p_map.containsKey(k) )
				info.game.setPerspective( info.game.getPerspective().add(
						p_map.get(k).multiply(info.deltaTime) ) );
			if( p_reset.equals(k) )
				info.game.setPerspective( Vector.K.multiply(-1000) );
		}
	}

}
