package demos.snake;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import com.mathhead200.games3d.DenseSprite;
import com.mathhead200.games3d.StaticImage;
import com.mathhead200.games3d.Vector;


public class Food extends DenseSprite
{
	private static final BufferedImage image =
		new BufferedImage( 8, 8, BufferedImage.TYPE_INT_ARGB );
	static {
		Graphics2D g = image.createGraphics();
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR) );
		g.fillRect( 0, 0, image.getWidth(), image.getHeight() );
		g.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER) );
		g.setColor(Color.GREEN);
		g.fillOval( 0, 0, image.getWidth(), image.getHeight() );
	}


	public Food(double x, double y) {
		super( new StaticImage(image) );
		setPosition( new Vector(x, y) );
	}
}
