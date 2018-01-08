package demos.three;

import javax.swing.JFrame;

import com.mathhead200.games3d.Game;
import com.mathhead200.games3d.Vector;

import demos.platformer.Tree;

public class Three
{
	public static void main(String[] args) throws Exception {
		Game game = new Game();
		JFrame frame = Game.frame(game);
		frame.setTitle("3D - test");
		game.setPerspectiveScaling(true);

		game.add( new Camera() );
		
		Me me = new Me();
		game.add(me);
		me.setPosition( new Vector(200, 300) );
		
		game.add( new Tree(0, 0, 200), Game.BACKGROUND );
		
		game.play();
		while( frame.isDisplayable() ) {
			Thread.sleep(1000);
			System.out.println( "pos: " + me.getPosition() + ", per: " + game.getPerspective()
					+ ", ori: " + game.getOrigin() );
		}
	}
}
