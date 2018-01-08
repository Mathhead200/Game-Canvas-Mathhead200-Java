package demos.trail;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.Iterator;
import java.util.Random;

import com.mathhead200.games3d.DenseSprite;
import com.mathhead200.games3d.HitBox;


public class Particle extends DenseSprite
{
	public Particle(final Color initialColor, final int radius) {
		super( new Iterable<Image>() {
			public Iterator<Image> iterator() {
				return new Iterator<Image>() {
					Color color = initialColor;
					BufferedImage image = new BufferedImage(2 * radius, 2 * radius, BufferedImage.TYPE_INT_ARGB);
					Random rand = new Random();

					public boolean hasNext() {
						return true;
					}

					public Image next() {
						Graphics2D g = image.createGraphics();

						g.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR) );
						g.fillRect( 0, 0, image.getWidth(), image.getHeight() );

						g.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER) );
						int red = color.getRed();
						int green = color.getGreen();
						int blue = color.getBlue();
						int n = rand.nextInt(6);
						if( n == 0 && red > 0 )
							red--;
						else if( n == 1 && red < 0xFF )
							red++;
						else if( n == 2 && green > 0 )
							green--;
						else if( n == 3 && green < 0xFF )
							green++;
						else if( n == 4 && blue > 0 )
							blue--;
						else if( blue < 0xFF )
							blue++;
						g.setColor( color = new Color(red, green, blue) );
						g.fillOval( 0, 0, image.getWidth(), image.getHeight() );
						g.dispose();

						return image;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		});
		HitBox hb = getHitBox();
		setHitBox( new HitBox(hb.x1(), hb.y1(), -radius, hb.x2(), hb.y2(), radius) );
	}
}
