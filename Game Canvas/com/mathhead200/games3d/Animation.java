package com.mathhead200.games3d;

import java.awt.Image;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * An <code>Animation</code> is a helper class that allows animations
 * 	to be bulit easily. It also provides a static method for looping
 * 	an existent animation.
 * 
 * @author Christopher D'Angelo
 * @version March 4, 2014
 */
public class Animation implements Iterable<Image>
{
	private static class Step
	{
		final Image image;
		final int count;

		Step(Image image, int count) {
			this.image = image;
			this.count = count;
		}
	}

	private class MyIterator implements Iterator<Image>
	{
		private Iterator<Step> iter = steps.iterator();
		private Step step = null;
		private int count = 0;

		public boolean hasNext() {
			return iter.hasNext() || (step != null && count > 0);
		}

		public Image next() {
			if( step == null || count <= 0 ) {
				step = iter.next();
				count = step.count;
			}
			count--;
			return step.image;
		}

		public void remove() {
			throw new UnsupportedOperationException();
		}
	}


	private List<Step> steps = new ArrayList<Step>();


	public Animation() {
	}


	public void addFrames(Image image, int count) {
		if( count <= 0 )
			throw new IllegalArgumentException("count must be positive");
		steps.add( new Step(image, count) );
	}
	
	/**  @return The number of frames currently in this animation.*/
	public int size() {
		int n = 0;
		for( Step step : steps )
			n += step.count;
		return n;
	}

	public Iterator<Image> iterator() {
		return new MyIterator();
	}


	public static Iterable<Image> loop(final Iterable<Image> animation) {
		return new Iterable<Image>() {
			public Iterator<Image> iterator() {
				return new Iterator<Image>()
				{
					Iterator<Image> iter = animation.iterator();

					public boolean hasNext() {
						return true;
					}

					public Image next() {
						if( iter.hasNext() )
							return iter.next();
						iter = animation.iterator();
						if( iter.hasNext() )
							return iter.next();
						return null;
					}

					public void remove() {
						throw new UnsupportedOperationException();
					}
				};
			}
		};
	}
}
