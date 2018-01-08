package demos.snake;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import com.mathhead200.games3d.DenseSprite;
import com.mathhead200.games3d.Game;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Mob;
import com.mathhead200.games3d.StaticImage;
import com.mathhead200.games3d.Vector;


public class Snake extends Mob
{
	private static final double size = 16;
	private static final double eatDist = 4 * size / 5;
	private static double startSpeed = size * 0.01;
	private static final BufferedImage image =
		new BufferedImage( (int)size, (int)size, BufferedImage.TYPE_INT_ARGB );
	static {
		Graphics2D g = image.createGraphics();
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR) );
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER) );
		g.setColor(Color.BLUE);
		g.fillOval( 0, 0, image.getWidth(), image.getHeight() );
		g.dispose();
	}


	private class Part extends DenseSprite
	{
		Part() {
			super( new StaticImage(image) );
			DenseSprite tail = (body.size() > 0 ? body.get(body.size() - 1) : Snake.this);
			setPosition( tail.getPosition().add(
					tail.getVelocity().negate().normalize().multiply(size) ) );
			System.out.println( getPosition() );
		}
	}


	private Game game;
	private List<Part> body = new ArrayList<Part>();
	private Random random = new Random();
	private List<Food> food = new ArrayList<Food>(3);


	private void nextFood() {
		food.add( new Food( random.nextInt(game.getWidth()),
		                    random.nextInt(game.getHeight()) ) );
		game.add( food.get(food.size() - 1) );
	}


	public Snake(Game game) {
		this.game = game;
		{	BufferedImage image = new BufferedImage( (int)size, (int)size, BufferedImage.TYPE_INT_ARGB );
			Graphics2D g = image.createGraphics();
			g.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR) );
			g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
			g.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER) );
			g.setColor(Color.RED);
			g.fillOval( 0, 0, image.getWidth(), image.getHeight() );
			g.dispose();
			mapState( null, new StaticImage(image) );
		}
		setVelocity( Vector.J.multiply(startSpeed) );
		setPosition(Vector.ZERO);
		for( int i = 0; i < 3; i++ ) {
			body.add( new Part() );
			game.add( body.get(i) );
		}
		nextFood();
		nextFood();
		nextFood();
	}


	public void behave(GameState info) {
		//Check: Eating Self
		for( Part part : body )
			if( part.getPosition().distance(getPosition()) <= eatDist ) {
				System.out.println("TODO: Eatting Self");
				break;
			}
		System.out.println( getVelocity().norm() );
		//Check: Eating Food
		Iterator<Food> iter = food.iterator();
		int eaten = 0;
		while( iter.hasNext() ) {
			Food f = iter.next();
			if( f.getPosition().distance(getPosition()) <= eatDist ) {
				game.remove(f);
				iter.remove();
				body.add( new Part() );
				game.add( body.get(body.size() - 1) );
				eaten++;
				setVelocity( getVelocity().add( getVelocity().normalize().multiply(0.05 * startSpeed) ) );
			}
		}
		while( eaten --> 0 )
			nextFood();
		//Handle Controls
		if( info.heldKeys.contains(KeyEvent.VK_LEFT) )
			setVelocity( getVelocity().rotate(
					info.deltaTime * -2 * Math.PI * getVelocity().norm() / (size * (body.size() + 1)) ) );
		if( info.heldKeys.contains(KeyEvent.VK_RIGHT) )
			setVelocity( getVelocity().rotate(
					info.deltaTime * 2 * Math.PI * getVelocity().norm() / (size * (body.size() + 1)) ) );
		//Handle Body Movement
		Vector dest = getPosition();
		for( Part part : body ) {
			if( part.getPosition().distance(dest) <= size )
				part.setVelocity( Vector.ZERO );
			else
				part.setVelocity(
						dest.subtract(part.getPosition()).normalize().multiply(getVelocity().norm()) );
			dest = part.getPosition();
		}
	}
}
