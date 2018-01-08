package demos.platformer;

import java.awt.image.BufferedImage;

import com.mathhead200.games3d.Resources;
import com.mathhead200.games3d.Scenery;


public class Tree extends Scenery
{
	private static BufferedImage IMAGE = Resources.loadImageFromFile("rsc/tree.dmi");

	public static final int WIDTH = IMAGE.getWidth();
	public static final int HEIGHT = IMAGE.getHeight();


	public Tree(double x, double y, double z) {
		super(IMAGE, x, y, z);
	}
}
