package demos.stars;

import java.awt.Color;
import java.awt.event.KeyEvent;
import java.util.Random;

import javax.swing.JFrame;

import com.mathhead200.games3d.Behavior;
import com.mathhead200.games3d.Game;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Mob;
import com.mathhead200.games3d.StaticImage;
import com.mathhead200.games3d.Vector;

import demos.trail.Particle;


class Star extends Particle implements Behavior
{		
	public Star() {
		super(Color.WHITE, 16);
	}

	public void behave(GameState info) {
		if( this.getPosition().z < info.game.getPerspective().z )
			info.game.remove(this);
	}
}


public class Runner extends Mob
{
	public static final int BACK = 1000; //distance the perspective is back from Runner
	public static final int FRONT = 36000; //distance obsticals are created in front of Runner
	
	
	public Runner() {
		super(StaticImage.NULL);
	}
	
	
	public void behave(GameState info) {
		int width = info.game.getWidth();
		int height = info.game.getHeight();
		
		for( Integer k : info.heldKeys ) {
			if( k == KeyEvent.VK_LEFT )
				move( Vector.I.multiply(info.deltaTime * width / -200.0) );
			else if( k == KeyEvent.VK_UP)
				move( Vector.J.multiply(info.deltaTime * height / -200.0) );
			else if( k == KeyEvent.VK_RIGHT )
				move( Vector.I.multiply(info.deltaTime * width / 200.0) );
			else if( k == KeyEvent.VK_DOWN)
				move( Vector.J.multiply(info.deltaTime * height / 200.0) );
		}
		
		Vector c = centerOfMass();
		Vector p = getPosition();
		info.game.setOrigin( new Vector( c.x - width / 2, c.y - height / 2, p.z ) );
		info.game.setPerspective( new Vector(c.x, p.y, p.z - BACK) );
	}
	
	
	public static void main(String[] args) throws Exception {
		
		Game game = new Game(600, 400);
		JFrame frame = Game.frame(game);
		// game.getCanvas().setFrameDelay(8);
		game.setPerspectiveScaling(true);
		game.setDrawOrder(Game.ORDER_BY_PERSPECTIVE);
		game.setBackground(Color.BLACK);
		
		Runner runner = new Runner();
		game.add(runner);
		runner.setVelocity( new Vector(0, 0, FRONT / 2000.0) );
		game.play();
		
		Random rand = new Random();
		while( frame.isDisplayable() ) {
			Star obstacle = new Star();
			double x = runner.getX() + FRONT/BACK/5 * game.getWidth() * rand.nextGaussian();
			double y = runner.getY() + FRONT/BACK/5 * game.getHeight() * rand.nextGaussian();
			double z = runner.getZ() + FRONT;
			obstacle.setPosition( new Vector(x, y, z) );
			game.add(obstacle);
			
			System.out.printf( "Num. of Items: %d,  Frame Rate: %d Hz%n",
					game.getItems().size(),
					game.getFrameRate() );
			
			Thread.sleep(2);
		}
		
	}
}
