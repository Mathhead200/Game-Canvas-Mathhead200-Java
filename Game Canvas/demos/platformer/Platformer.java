package demos.platformer;

import java.awt.Color;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import com.mathhead200.games3d.DenseSprite;
import com.mathhead200.games3d.Game;
import com.mathhead200.games3d.Vector;


public class Platformer
{
	public static void main(String[] args) {
		Game game = new Game(800, 216);
		JFrame frame = Game.frame(game);
		game.setBackground( new Color(0xCCEEFF) );
		// game.setFrameDelay(0);
		// game.setDesiredFrameRate(120);
		// game.getCanvas().setFrameRateTracking(false);
		int height = game.getHeight();
		Random rand = new Random();

		/*
		{	final int y = height - Tree.WIDTH;
			final int W = game.getCanvas().getWidth();
			for( int i = 0; i < 16; i++ ) {
				int x = -W / 3 + rand.nextInt( 5 * W / 3 - Tree.HEIGHT );
				double z = 1000 * rand.nextDouble();
				System.out.println(z);
				game.add( new Tree(x, y, z), Game.BACKGROUND );
			}
		}
		*/

		Protagonist main = new Protagonist(height);
		game.add( main, Game.FOREGROUND );
		// for( int delay = 15; delay <= 90; delay += 15 )
		// 	game.add( new Shadow(main, delay), Game.MIDGROUND );
		game.add( main.createHUD(Vector.ZERO), Game.OVERLAY );
		game.play();

		while( game.isDisplayable() && main.isAlive() ) {
			try {
				Thread.sleep( Math.round(
						1200 * Math.pow(.75, game.getElapsedTime() / 30000) ) );
			} catch(InterruptedException e) {
				e.printStackTrace();
				break;
			}

			DenseSprite sprite;
			double x = rand.nextDouble();
			if( x < 0.90 ) {
				sprite = new Bullet(main);
				sprite.setPosition( new Vector(
						game.getWidth() - sprite.getHitBox().x2(),
						rand.nextDouble() * (height - sprite.getHitBox().y2())
				));
			} else if( x < 0.95 ) {
				sprite = new Heart(main);
				sprite.setPosition( new Vector(
						rand.nextDouble() * (game.getWidth() - sprite.getHitBox().x2()),
						rand.nextDouble() * (height - sprite.getHitBox().y2())
				));
			} else {
				sprite = new Potion(main);
				sprite.setPosition( new Vector(
						rand.nextDouble() * (game.getWidth() - sprite.getHitBox().x2()),
						rand.nextDouble() * (height - sprite.getHitBox().y2())
				));
			}
			game.add( sprite, Game.MIDGROUND );

			if( !game.getDrawExceptions().isEmpty() )
				game.getDrawExceptions().remove(0).printStackTrace();

			System.out.print( "Frame Rate = " + game.getFrameRate() + " fps, " );
			System.out.println( "Frame Delay = " + game.getFrameDelay() + " ms" );
		}
		
		game.pause();
		JOptionPane.showMessageDialog( frame,
				"Time: " + (int)(game.getElapsedTime() / 1000) + " second.\n"
				+ "Score: " + main.getPoints() + " points." );
	}
}
