package demos.trail;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;

import com.mathhead200.games3d.Behavior;
import com.mathhead200.games3d.Game;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Vector;


public class Trail
{
	public static void main(String[] args) {
		Game game = new Game(650, 650);
		JFrame frame = Game.frame(game);
		frame.setTitle("Trail");
		frame.setExtendedState( JFrame.MAXIMIZED_BOTH );
		game.setBackground(Color.BLACK);
		game.setPerspectiveScaling(true);
		game.setDrawOrder(Game.ORDER_BY_PERSPECTIVE);
		// game.getCanvas().setFrameDelay(5);
		// game.getCanvas().setFrameRateTracking(false);

		// game.getCanvas().pause();
		game.add( new Behavior() {
			public void behave(GameState info) {
				final Vector vel_left = new Vector(-0.5, 0.0);
				final Vector vel_up = new Vector(0.0, -0.5);
				final Vector vel_out = new Vector(0.0, 0.0, -0.5);
				final Vector vel_right = vel_left.negate();
				final Vector vel_down = vel_up.negate();
				final Vector vel_in = vel_out.negate();

				if( info.heldKeys.contains(KeyEvent.VK_A) )
					info.game.setOrigin( info.game.getOrigin().add(vel_left.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_W) )
					info.game.setOrigin( info.game.getOrigin().add(vel_up.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_D) )
					info.game.setOrigin( info.game.getOrigin().add(vel_right.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_S) )
					info.game.setOrigin( info.game.getOrigin().add(vel_down.multiply(info.deltaTime)) );

				if( info.heldKeys.contains(KeyEvent.VK_LEFT) )
					info.game.setPerspective( info.game.getPerspective().add(vel_left.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_UP) )
					info.game.setPerspective( info.game.getPerspective().add(vel_up.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_RIGHT) )
					info.game.setPerspective( info.game.getPerspective().add(vel_right.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_DOWN) )
					info.game.setPerspective( info.game.getPerspective().add(vel_down.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_SHIFT) )
					info.game.setPerspective( info.game.getPerspective().add(vel_in.multiply(info.deltaTime)) );
				if( info.heldKeys.contains(KeyEvent.VK_CONTROL) )
					info.game.setPerspective( info.game.getPerspective().add(vel_out.multiply(info.deltaTime)) );
			}
		});

		Random rand = new Random();
		for( int i = 0; i < 1500; i++ ) {
			double speed = 0.2E-7 * rand.nextGaussian() + 1E-7;
			if( speed <= 0 )
				speed = Double.MIN_VALUE;
			KParticle p = new KParticle(speed);
			p.setPosition( new Vector(
					rand.nextInt( game.getWidth() ),
					rand.nextInt( game.getHeight() ),
					rand.nextInt(1000) - 500
			));
			game.add(p);
		}

		game.play();
		while( game.isDisplayable() ) {
			try {
				Thread.sleep(1000);
			} catch(InterruptedException e) {
				e.printStackTrace();
			}
			if( !game.getDrawExceptions().isEmpty() )
				game.getDrawExceptions().remove(0).printStackTrace();
			System.out.println( "Frame Rate = " + game.getFrameRate() + " fps" );
		}
	}
}
