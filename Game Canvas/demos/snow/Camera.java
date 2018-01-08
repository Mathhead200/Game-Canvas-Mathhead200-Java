package demos.snow;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

import javax.swing.JFrame;

import com.mathhead200.games3d.Game;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Mob;
import com.mathhead200.games3d.StaticImage;
import com.mathhead200.games3d.Vector;


class Snow extends Mob
{
	private static final Vector g = new Vector(0, 0, 0.00981); 
	private static final BufferedImage image = new BufferedImage(8, 8, BufferedImage.TYPE_INT_ARGB);
	static {
		Graphics2D g = image.createGraphics();
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR) );
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER) );
		g.setColor(Color.WHITE);
		g.fillOval( 0, 0, image.getWidth(), image.getHeight() );
		g.dispose();
	}
	
	public Snow() {
		super( new StaticImage(image) );
	}

	public void behave(GameState info) {
		Vector p = getPosition();
		if( p.z < 0 )
			applyImpulse(g);
		else {
			setPosition( new Vector(p.x, p.y, 0) );
			setVelocity(Vector.ZERO);
		}
	}
}


public class Camera
{
	public static final int GROUND = 5000; //distance snow lands in front of Camera
	
	
	public static void main(String[] args) throws Exception {
		
		Game game = new Game(600, 400);
		JFrame frame = Game.frame(game);
		// game.getCanvas().setFrameDelay(8);
		game.setPerspectiveScaling(true);
		game.setDrawOrder(Game.ORDER_BY_PERSPECTIVE);
		game.setBackground( new Color(0xCCEEFF) );
		
		game.setPerspective( new Vector(game.getWidth() / 2, game.getHeight() / 2, -GROUND) );
		game.play();
		
		Random rand = new Random();
		while( frame.isDisplayable() ) {
			Snow flake = new Snow();
			double x = game.getPerspective().x + 0.25 * game.getWidth() * rand.nextGaussian();
			double y = game.getPerspective().y + 0.25 * game.getHeight() * rand.nextGaussian();
			double z = game.getPerspective().z;
			flake.setPosition( new Vector(x, y, z) );
			game.add(flake);
			
			System.out.printf( "Num. of Items: %d,  Frame Rate: %d Hz%n",
					game.getItems().size(),
					game.getFrameRate() );
			
			Thread.sleep(3);
		}
		
	}
}
