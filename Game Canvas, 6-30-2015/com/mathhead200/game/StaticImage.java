package com.mathhead200.game;

import java.awt.Image;
import java.util.Iterator;

public class StaticImage implements Iterable<Image>
{
	private class MyIterator implements Iterator<Image>
	{
		public boolean hasNext() {
			return true;
		}

		public Image next() {
			return image;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}


	public final Image image;

	public static final StaticImage NULL = new StaticImage(null);


	public StaticImage(Image image) {
		this.image = image;
	}


	public Iterator<Image> iterator() {
		return new MyIterator();
	}
}
