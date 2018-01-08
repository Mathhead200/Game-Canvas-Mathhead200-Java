package com.mathhead200.msd;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.math.BigInteger;
import java.util.Random;

import javax.swing.JFrame;

import com.mathhead200.BigRational;
import com.mathhead200.games3d.Behavior;
import com.mathhead200.games3d.Drawable;
import com.mathhead200.games3d.Game;
import com.mathhead200.games3d.GameState;
import com.mathhead200.games3d.Vector;


public class MSD
{
	//Parameters
	private static final int width = 11;
	private static final int height = 10;
	private static final int depth = 10;
	private static final int msdPosL = 5;
	private static final int msdPosR = 5;

	private static double kT = 1.0;
	private static Vector B = Vector.ZERO;
	private static double J = 1.0;

	private static boolean paused = false;
	private static BigInteger counter = BigInteger.ZERO;
	private static double lastU = Double.NaN;
	private static Vector lastM = new Vector(Double.NaN, Double.NaN, Double.NaN);

	private static final Atom[][][] atoms = new Atom[depth][height][width];
	private static final char[][][] mats = new char[depth][height][width];

	private static Vector calcM() {
		Vector m = Vector.ZERO;
		for( int z = 0; z < depth; z++ )
			for( int y = 0; y < height; y++ )
				for( int x = 0; x < width; x++ )
					if( atoms[z][y][x] != null )
						m = m.add( atoms[z][y][x].getSpin() );
		return m;
	}

	private static double calcU() {
		double u = 0;
		for( int z = 0; z < depth; z++ )
			for( int y = 0; y < height; y++ )
				for( int x = 0; x < width; x++ ) {
					Atom a = atoms[z][y][x];
					if( a == null )
						continue;
					Atom n1 = (x != width - 1 ? atoms[z][y][x + 1] : null);
					Atom n2 = (y != height - 1 ? atoms[z][y + 1][x] : null);
					Atom n3 = (z != depth - 1 ? atoms[z + 1][y][x] : null);
					if( n1 != null )
						u += -J * a.getSpin().dotProduct( n1.getSpin() );
					if( n2 != null )
						u += -J * a.getSpin().dotProduct( n2.getSpin() );
					if( n3 != null )
						u += -J * a.getSpin().dotProduct( n3.getSpin() );
				}
		return u - B.dotProduct( calcM() );
	}

	private static class HUD implements Drawable, Behavior
	{
		BufferedImage image = new BufferedImage(600, 100, BufferedImage.TYPE_INT_ARGB);
		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 12);

		public Image getImage() {
			Graphics2D g = image.createGraphics();

			g.setComposite( AlphaComposite.getInstance(AlphaComposite.CLEAR) );
			g.fillRect( 0, 0, image.getWidth(), image.getHeight() );

			g.setComposite( AlphaComposite.getInstance(AlphaComposite.SRC_OVER) );
			g.setColor(Color.BLACK);
			g.setFont(font);

			g.drawString("kT = " + kT, 4, 12);
			g.drawString("B  = " + B, 4, 28);
			g.drawString("M  = " + lastM, 4, 44);
			g.drawString("U  = " + lastU, 4, 60);
			g.drawString( (paused ? "PAUSED " : "RUNNING ") + "[" + counter + "]", 4, 76 );

			g.dispose();
			return image;
		}

		public Vector getPosition() {
			return Vector.ZERO;
		}

		public boolean isHUD() {
			return true;
		}

		public void behave(GameState info) {
			if( info.pressedKeys.contains(KeyEvent.VK_P) )
				paused = !paused;

			if( info.pressedKeys.contains(KeyEvent.VK_M) )
				kT += 0.25;
			if( info.pressedKeys.contains(KeyEvent.VK_N) )
				kT = (kT > 0.25 ? kT - 0.25 : Double.MIN_VALUE);

			if( info.pressedKeys.contains(KeyEvent.VK_I) )
				B = B.add(Vector.J.multiply(-0.25));
			if( info.pressedKeys.contains(KeyEvent.VK_K) )
				B = B.add(Vector.J.multiply(0.25));
			if( info.pressedKeys.contains(KeyEvent.VK_J) )
				B = B.add(Vector.I.multiply(-0.25));
			if( info.pressedKeys.contains(KeyEvent.VK_L) )
				B = B.add(Vector.I.multiply(0.25));
			if( info.pressedKeys.contains(KeyEvent.VK_U) )
				B = B.add(Vector.K.multiply(-0.25));
			if( info.pressedKeys.contains(KeyEvent.VK_O) )
				B = B.add(Vector.K.multiply(0.25));
		}

	}


	public static void main(String[] args) throws InterruptedException {
		//Set up GUI
		Game game = new Game(800, 600);
		JFrame frame = Game.frame(game);
		game.setPerspectiveScaling(true);
		game.setFrameRateTracking(false);
		frame.setTitle("MSD - Test");
		game.setBackground(Color.WHITE);
		game.setOrigin( new Vector(-250, -100, 250) );
		game.setPerspective( new Vector(-240, -240, -1000) );
		game.add( new Camera() );
		game.add( new HUD() );

		//Create Atoms
		int n = 0;
		for( int z = 0; z < depth; z++ )
			for( int y = 0; y < height; y++ )
				for( int x = 0; x < width; x++ ) {
					if( x < msdPosL)
						mats[z][y][x] = 'L';
					else if( x > msdPosR )
						mats[z][y][x] = 'R';
					else if( y == 0 || z == 0 || y == height - 1 || z == depth - 1 )
						mats[z][y][x] = 'm';
					else
						mats[z][y][x] = '\0';

					if( mats[z][y][x] != '\0' ) {
						atoms[z][y][x] = new Atom();
						atoms[z][y][x].setPosition( new Vector(x, y, z).multiply(32) );
						game.add( atoms[z][y][x], new BigRational(z) );
						n++;
					} else
						atoms[z][y][x] = null;
				}
		game.play();

		//Simulate
		BigInteger period = BigInteger.valueOf(5000);
		Random rand = new Random();
		double u = calcU();
		while( frame.isDisplayable() ) {
			if( paused ) {
				Thread.sleep(100);
				continue;
			}
			//count iterations and report status
			lastU = u;
			if( counter.remainder(period).equals(BigInteger.ZERO) )
				lastM = calcM();
			counter = counter.add(BigInteger.ONE);
			//pick random atom
			int x = rand.nextInt(width);
			int y = rand.nextInt(height);
			int z = rand.nextInt(depth);
			Atom a = atoms[z][y][x];
			if( a == null )
				continue;
			//change atom's spin
			Vector s = a.getSpin();
			// double theta = rand.nextDouble() * 2 * Math.PI;
			// double phi = rand.nextDouble() * Math.PI;
			// a.setSpin( Vector.sphericalForm(1.0, theta, phi) );
			a.setSpin( a.getSpin().negate() );
			//calculate new energy
			double u2 = calcU();
			//compare difference energy states
			if( u2 <= u || rand.nextDouble() < Math.exp((u - u2) / kT) )
				u = u2;
			else
				a.setSpin(s);
		}
	}
}
