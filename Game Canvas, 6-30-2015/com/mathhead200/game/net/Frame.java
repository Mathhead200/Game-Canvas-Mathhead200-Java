package com.mathhead200.game.net;

import java.awt.image.BufferedImage;
import java.io.Serializable;


/**
 * Used to serialize a {@link BufferedImage} so it can be sent via an object stream.
 * 
 * @author Christopher D'Angelo
 * @version Jul 26, 2014
 */
public class Frame implements Serializable
{
	private static final long serialVersionUID = 8587542835521025956L;
	
	private int width;
	private int[] pixels;
	
	private Frame(int width, int height) {
		this.width = width;
		this.pixels = new int[width * height];
	}
	
	/**
	 * Use this to get the width of the image in this frame without reconstructing it.
	 * @return The width of the image
	 */
	public int getWidth() {
		return width;
	}
	
	/**
	 * Use this to get the height of the image in this frame without reconstructing it.
	 * @return The height of the image
	 */
	public int getHeight() {
		return pixels.length / width;
	}
	
	/**
	 * The only was to construct a frame.
	 * 	Translates the image's data into an array of <code>int</code>s so that is can be serialized.
	 * @param image - The <code>BufferedImage</code> to deconstruct
	 * @return A frame containing the information of the given image
	 * @see #reconstruct()
	 */
	public static Frame deconstruct(BufferedImage image) {
		Frame frame = new Frame( image.getWidth(), image.getHeight() );
		image.getRGB( 0, 0, image.getWidth(), image.getHeight(), frame.pixels, 0, image.getWidth() );
		return frame;
	}
	
	/**
	 * Used to reconstruct the original <code>BufferedImage</code> after it has been deserialized.
	 * @return The original image whos information is contained in the frame.
	 * @see #deconstruct(BufferedImage)
	 */
	public BufferedImage reconstruct() {
		BufferedImage image = new BufferedImage( getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB );
		image.setRGB( 0, 0, image.getWidth(), image.getHeight(), pixels, 0, image.getWidth() );
		return image;
	}
}
