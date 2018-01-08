import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.Timer;

import com.mathhead200.GameCanvas;
import com.mathhead200.game.Animation;
import com.mathhead200.game.DenseSprite;
import com.mathhead200.game.Game;
import com.mathhead200.game.GameState;
import com.mathhead200.game.Mob;
import com.mathhead200.game.Resources;
import com.mathhead200.game.Vector;


public class Game2DTest
{
	public static void main(String[] args) throws Exception {
		final Game game = new Game();
		final JFrame frame = Game.frame(game);
		game.setFrameDelay(8);
		game.setBackground(Color.GRAY);
		frame.setTitle("Game - Test");

		final DenseSprite tree = new DenseSprite( Resources.loadStaticImageFromFile("rsc/tree.dmi") );
		tree.setPosition( new Vector(600, 600) ); //300, 200
		// final Mob dog = new Mob( Resources.loadStaticImageFromFile("rsc/dog.dmi") ) {
		final Mob dog = new Mob( Resources.loadGIFAnimationFromFile("rsc/walker.gif", true, 1) ) {
			Vector lastPos = getPosition();
			double speed = 8 * 0.032;

			public void behave(GameState info) {
				double up = (info.pressedKeys.contains(KeyEvent.VK_UP) ? 1.0 : 0.0)
						+ (info.releasedKeys.contains(KeyEvent.VK_DOWN) ? 1.0 : 0.0);
				double down = (info.pressedKeys.contains(KeyEvent.VK_DOWN) ? 1.0 : 0.0)
						+ (info.releasedKeys.contains(KeyEvent.VK_UP) ? 1.0 : 0.0);
				double left = (info.pressedKeys.contains(KeyEvent.VK_LEFT) ? 1.0 : 0.0)
						+ (info.releasedKeys.contains(KeyEvent.VK_RIGHT) ? 1.0 : 0.0);
				double right = (info.pressedKeys.contains(KeyEvent.VK_RIGHT) ? 1.0 : 0.0)
						+ (info.releasedKeys.contains(KeyEvent.VK_LEFT) ? 1.0 : 0.0);

				setVelocity( getVelocity().add( Vector.J.negate().multiply(speed).multiply(up) ) );
				setVelocity( getVelocity().add( Vector.J.multiply(speed).multiply(down) ) );
				setVelocity( getVelocity().add( Vector.I.negate().multiply(speed).multiply(left) ) );
				setVelocity( getVelocity().add( Vector.I.multiply(speed).multiply(right) ) );

				if( isIntersecting(tree) )
					setPosition(lastPos);
				lastPos = getPosition();
			}
		};
		game.add(tree);
		game.add(dog);
		game.play();

		Timer exTimer = new Timer( 500, new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println( "Frame rate: " + game.getFrameRate() );

				List<GameCanvas.LoopException> exList = game.getDrawExceptions();
				if( !exList.isEmpty() ) {
					System.err.println();
					GameCanvas.LoopException ex = exList.remove(0);
					System.err.println( "loopID = " + ex.loopID + ": " );
					ex.printStackTrace(System.err);
					System.err.println();
				}
			}
		});
		exTimer.start();
	}
}
