package com.mathhead200.game;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


/**
 * Provides helper methods for loading common resources.
 * 	Instances also act like a resource map, mapping labels to resources.
 * 
 * @author Christopher D'Angelo
 * @version Mar 4, 2014
 */
public final class Resources
{
	private Map<String, Object> resourceMap = new HashMap<String, Object>();


	public static final Resources global = new Resources();


	// Load Image --------------------------------------------------------------
	public static BufferedImage loadImageFromFile(String file) {
		try {
			return ImageIO.read( new File(file) );
		} catch(IOException e) {
			return null;
		}
	}

	public BufferedImage loadImageFromFile(String name, String file) {
		BufferedImage image = loadImageFromFile(file);
		if( image == null )
			return null;
		resourceMap.put(name, image);
		return image;
	}

	public static BufferedImage loadImageFromURL(String url) {
		try {
			return ImageIO.read( new URL(url) );
		} catch(IOException e) {
			return null;
		}
	}

	public BufferedImage loadImageFromURL(String name, String url) {
		BufferedImage image = loadImageFromURL(url);
		if( image == null )
			return null;
		resourceMap.put(name, image);
		return image;
	}


	// Load StaticImage -------------------------------------------------------
	public static StaticImage loadStaticImageFromFile(String file) {
		BufferedImage image = loadImageFromFile(file);
		if( image == null )
			return null;
		return new StaticImage(image);
	}

	public StaticImage loadStaticImageFromFile(String name, String file) {
		StaticImage image = loadStaticImageFromFile(file);
		if( image == null )
			return null;
		resourceMap.put(name, image);
		return image;
	}

	public static StaticImage loadStaticImageFromURL(String url) {
		BufferedImage image = loadImageFromURL(url);
		if( image == null )
			return null;
		return new StaticImage(image);
	}

	public StaticImage loadStaticImageFromURL(String name, String url) {
		StaticImage image = loadStaticImageFromURL(url);
		if( image == null )
			return null;
		resourceMap.put(name, image);
		return image;
	}

	// Load Animation ----------------------------------------------------------
	private static Animation loadGIFAnimation(InputStream in, boolean useGIFDelay, int frameCount) throws IOException {
		//code from http://stackoverflow.com/questions/8933893/convert-animated-gif-frames-to-separate-bufferedimages-java/17269591
		class ImageFrame {
		    final int delay;
		    final BufferedImage image;
		    final String disposal;
		    final int width, height;

		    public ImageFrame(BufferedImage image, int delay, String disposal, int width, int height) {
		        this.image = image;
		        this.delay = delay;
		        this.disposal = disposal;
		        this.width = width;
		        this.height = height;
		    }
		}
		
		List<ImageFrame> frames = new ArrayList<ImageFrame>();

		ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
		reader.setInput( ImageIO.createImageInputStream(in) );

		int lastx = 0;
		int lasty = 0;

		int width = -1;
		int height = -1;

		IIOMetadata metadata = reader.getStreamMetadata();

		Color backgroundColor = null;

		if( metadata != null ) {
			IIOMetadataNode globalRoot = (IIOMetadataNode) metadata.getAsTree(metadata.getNativeMetadataFormatName());

			NodeList globalColorTable = globalRoot.getElementsByTagName("GlobalColorTable");
			NodeList globalScreeDescriptor = globalRoot.getElementsByTagName("LogicalScreenDescriptor");

			if( globalScreeDescriptor != null && globalScreeDescriptor.getLength() > 0 ) {
				IIOMetadataNode screenDescriptor = (IIOMetadataNode) globalScreeDescriptor.item(0);

				if(screenDescriptor != null) {
					width = Integer.parseInt( screenDescriptor.getAttribute("logicalScreenWidth") );
					height = Integer.parseInt( screenDescriptor.getAttribute("logicalScreenHeight") );
				}
			}

			if( globalColorTable != null && globalColorTable.getLength() > 0 ) {
				IIOMetadataNode colorTable = (IIOMetadataNode) globalColorTable.item(0);

				if( colorTable != null ) {
					String bgIndex = colorTable.getAttribute("backgroundColorIndex");

					IIOMetadataNode colorEntry = (IIOMetadataNode) colorTable.getFirstChild();
					while( colorEntry != null ) {
						if( colorEntry.getAttribute("index").equals(bgIndex) ) {
							int red = Integer.parseInt( colorEntry.getAttribute("red") );
							int green = Integer.parseInt( colorEntry.getAttribute("green") );
							int blue = Integer.parseInt( colorEntry.getAttribute("blue") );

							backgroundColor = new Color(red, green, blue);
							break;
						}

						colorEntry = (IIOMetadataNode) colorEntry.getNextSibling();
					}
				}
			}
		}

		BufferedImage master = null;
		boolean hasBackround = false;

		for( int frameIndex = 0; true; frameIndex++ ) {
			BufferedImage image;
			try {
				image = reader.read(frameIndex);
			} catch (IndexOutOfBoundsException e) {
				break;
			}

			if( width == -1 || height == -1 ) {
				width = image.getWidth();
				height = image.getHeight();
			}

			IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(frameIndex).getAsTree("javax_imageio_gif_image_1.0");
			IIOMetadataNode gce = (IIOMetadataNode) root.getElementsByTagName("GraphicControlExtension").item(0);
			NodeList children = root.getChildNodes();

			int delay = Integer.valueOf( gce.getAttribute("delayTime") );

			String disposal = gce.getAttribute("disposalMethod");

			if( master == null ) {
				master = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = master.createGraphics();
				g.setColor(backgroundColor);
				g.fillRect( 0, 0, master.getWidth(), master.getHeight() );
				
				hasBackround = (image.getWidth() == width && image.getHeight() == height);
				
				g.drawImage(image, 0, 0, null);
				g.dispose();
			} else {
				int x = 0;
				int y = 0;

				for( int nodeIndex = 0; nodeIndex < children.getLength(); nodeIndex++ ) {
					Node nodeItem = children.item(nodeIndex);

					if( nodeItem.getNodeName().equals("ImageDescriptor") ) {
						NamedNodeMap map = nodeItem.getAttributes();

						x = Integer.parseInt( map.getNamedItem("imageLeftPosition").getNodeValue() );
						y = Integer.parseInt( map.getNamedItem("imageTopPosition").getNodeValue() );
					}
				}

				if( disposal.equals("restoreToPrevious") ) {
					BufferedImage from = null;
					for( int i = frameIndex - 1; i >= 0; i-- ) {
						if( !frames.get(i).disposal.equals("restoreToPrevious") || frameIndex == 0 ) {
							from = frames.get(i).image;
							break;
						}
					}

					{	ColorModel model = from.getColorModel();
						boolean alpha = from.isAlphaPremultiplied();
						WritableRaster raster = from.copyData(null);
						master = new BufferedImage(model, raster, alpha, null);
					}
				} else if( disposal.equals("restoreToBackgroundColor") && backgroundColor != null ) {
					if( !hasBackround || frameIndex > 1 ) {
						master.createGraphics().fillRect( lastx, lasty, frames.get(frameIndex - 1).width, frames.get(frameIndex - 1).height );
					}
				}
				master.createGraphics().drawImage(image, x, y, null);

				lastx = x;
				lasty = y;
			}

			{
				BufferedImage copy;

				{	ColorModel model = master.getColorModel();
					boolean alpha = master.isAlphaPremultiplied();
					WritableRaster raster = master.copyData(null);
					copy = new BufferedImage(model, raster, alpha, null);
				}
				frames.add( new ImageFrame(copy, delay, disposal, image.getWidth(), image.getHeight()) );
			}

			master.flush();
		}
		reader.dispose();

		//now my code
		Animation animation = new Animation();
		for( ImageFrame frame : frames )
			animation.addFrames( frame.image, useGIFDelay ? frame.delay * frameCount : frameCount );
		
		return animation;
	}
	
	public static Animation loadGIFAnimationFromFile(String file, boolean useGIFDelay, int frameCount) {
		try (InputStream in = new FileInputStream(file)) {
			return loadGIFAnimation(in, useGIFDelay, frameCount);
		} catch(IOException e) {
			return null;
		}
	}
	
	public Animation loadGIFAnimationFromFile(String name, String file, boolean useGIFDelay, int frameCount) {
		Animation animation = loadGIFAnimationFromFile(file, useGIFDelay, frameCount);
		if( animation == null )
			return null;
		resourceMap.put(name, animation);
		return animation;
	}
	
	public static Animation loadGIFAnimationFromURL(String url, boolean useGIFDelay, int frameCount) {
		try (InputStream in = new URL(url).openStream()) {
			return loadGIFAnimation(in, useGIFDelay, frameCount);
		} catch(IOException e) {
			e.printStackTrace(); //TODO: DEBUG
			return null;
		}
	}
	
	public Animation loadGIFAnimationFromURL(String name, String url, boolean useGIFDelay, int frameCount) {
		Animation animation = loadGIFAnimationFromURL(url, useGIFDelay, frameCount);
		if( animation == null )
			return null;
		resourceMap.put(name, animation);
		return animation;
	}
	
	public static Animation loadAnimationFromFile(String file, int width, int height, int frameCount) {
		try {
			BufferedImage spriteSheet = ImageIO.read( new File(file) );
			Animation animation = new Animation();
			for( int y = 0; y < spriteSheet.getHeight(); y += height )
				for( int x = 0; x < spriteSheet.getWidth(); x += width )
					animation.addFrames( spriteSheet.getSubimage(x, y, width, height), frameCount );
			return animation;
		} catch(IOException e) {
			return null;
		}
	}

	public Animation loadAnimationFromFile(String name, String file, int width, int height, int frameCount) {
		Animation animation = loadAnimationFromFile(file, width, height, frameCount);
		if( animation == null )
			return null;
		resourceMap.put(name, animation);
		return animation;
	}

	public static Animation loadAnimationFromURL(String url, int width, int height, int frameCount) {
		try {
			BufferedImage spriteSheet = ImageIO.read( new URL(url) );
			Animation animation = new Animation();
			for( int y = 0; y < spriteSheet.getHeight(); y += height )
				for( int x = 0; x < spriteSheet.getWidth(); x += width )
					animation.addFrames( spriteSheet.getSubimage(x, y, width, height), frameCount );
			return animation;
		} catch(IOException e) {
			return null;
		}
	}

	public Animation loadAnimationFromURL(String name, String url, int width, int height, int frameCount) {
		Animation animation = loadAnimationFromURL(url, width, height, frameCount);
		if( animation == null )
			return null;
		resourceMap.put(name, animation);
		return animation;
	}

	// Load AudioClip ----------------------------------------------------------
	public static Clip loadAudioClipFromFile(String file) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open( AudioSystem.getAudioInputStream( new File(file) ) );
			return clip;
		} catch (LineUnavailableException e) {
			return null;
		} catch(UnsupportedAudioFileException e) {
			return null;
		} catch(IOException e) {
			return null;
		}
	}

	public Clip loadAudioClipFromFile(String name, String file) {
		Clip clip = loadAudioClipFromFile(file);
		if( clip == null )
			return null;
		resourceMap.put(name, clip);
		return clip;
	}

	public static Clip loadAudioClipFromURL(String url) {
		try {
			Clip clip = AudioSystem.getClip();
			clip.open( AudioSystem.getAudioInputStream( new URL(url) ) );
			return clip;
		} catch (LineUnavailableException e) {
			return null;
		} catch(UnsupportedAudioFileException e) {
			return null;
		} catch(IOException e) {
			return null;
		}
	}

	public Clip loadAudioClipFromURL(String name, String url) {
		Clip clip = loadAudioClipFromURL(url);
		if( clip == null )
			return null;
		resourceMap.put(name, clip);
		return clip;
	}

	// -------------------------------------------------------------------------
	public boolean hasLoaded(String name) {
		return resourceMap.containsKey(name);
	}

	public Object get(String name) {
		return resourceMap.get(name);
	}

	public <T> T get(String name, Class<T> type) {
		Object obj = resourceMap.get(name);
		if( type.isInstance(obj) )
			return type.cast(obj);
		return null;
	}

	public BufferedImage getImage(String name) {
		return get(name, BufferedImage.class);
	}

	public StaticImage getStaticImage(String name) {
		return get(name, StaticImage.class);
	}

	public Animation getAnimation(String name) {
		return get(name, Animation.class);
	}

	public Clip getAudioClip(String name) {
		return get(name, Clip.class);
	}

	public void free(String name) {
		Object obj = resourceMap.remove(name);
		if( obj instanceof Clip )
			((Clip)obj).close();
	}
}
