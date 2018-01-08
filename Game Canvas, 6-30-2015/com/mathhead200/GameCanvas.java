package com.mathhead200;

import java.awt.Canvas;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferStrategy;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;

import javax.swing.Timer;


/**
 * @author Christopher D'Angelo
 * @version 3/4/2014
 */
@SuppressWarnings("serial")
public class GameCanvas extends Canvas
{
	/**
	 * Thrown within the draw or game step to signify something exceptional occurred.
	 * 	For example, the draw step couldn't complete normally.
	 * @author Christopher D'Angelo
	 */
	public class LoopException extends Exception
	{
		private static final long serialVersionUID = 1447545154071916239L;

		/** The loop ID when this exception occurred. */
		public final long loopID;

		public LoopException() {
			super();
			loopID = getLoopID();
		}

		public LoopException(String message) {
			super(message);
			loopID = getLoopID();
		}

		public LoopException(String message, Throwable cause) {
			super(message, cause);
			loopID = getLoopID();
		}

		public LoopException(Throwable cause) {
			super(cause);
			loopID = getLoopID();
		}
	}


	private class GameLoop implements ActionListener
	{
		private void handleException(LoopException ex) {
			loopExceptions.add(ex);
			if( pauseOnException )
				pause();
		}

		public void actionPerformed(ActionEvent e) {
			long now = System.nanoTime();
			deltaNanoTime = now - lastNanoTime; //track delta time
			elapsedNanoTime += deltaNanoTime; //track elapsed time
			loopID++;

			Graphics2D g = null;
			try {
				//calls gameLoop to update the games state
				gameStep();

				//calls drawLoop to paint this frame (using the current buffer strategy)
				BufferStrategy strategy = getBufferStrategy();
				if( strategy == null )
					throw new LoopException("null BufferStrategy");
				g = (Graphics2D) strategy.getDrawGraphics();
				if( g == null )
					throw new LoopException("null Graphics2D");
				drawStep(g);
				strategy.show();
				Toolkit.getDefaultToolkit().sync();
			} catch(LoopException ex) {
				handleException(ex);
			} /* catch(RuntimeException ex) {
				handleException( new LoopException(ex) );
			} */ finally {
				if( g != null )
					g.dispose();
			}

			//keep track of frame rate
			if( trackFrameRate )
				synchronized(frameNanoTimes) {
					while( !frameNanoTimes.isEmpty() ) {
						if( now - frameNanoTimes.peek() <= 1000000000L )
							break;
						frameNanoTimes.remove();
					}
					frameNanoTimes.add(now);
				}
			
			//adjust frame delay to approximate the desired frame rate
			if( desiredFrameDelay > 0 ) {
				double cpuTime = (System.nanoTime() - now) / 1E6;
				int delay = (int) (desiredFrameDelay - cpuTime);
				setFrameDelay( delay > 0 ? delay : 0 );
			}
			
			lastNanoTime = now;
		}
	}

	private Timer mainLoop;
	private long lastNanoTime = 0L;
	private long deltaNanoTime = 0L;
	private long elapsedNanoTime = 0L;
	private long loopID = 0L;
	private Queue<Long> frameNanoTimes = new ArrayDeque<Long>();
	private List<LoopException> loopExceptions = Collections.synchronizedList( new ArrayList<LoopException>(0) );
	private boolean pauseOnException = false;
	private boolean trackFrameRate = true;
	private double desiredFrameDelay = 0;


	/** @see #setFrameDelay(int) */
	public GameCanvas(int delay) {
		setIgnoreRepaint(true);
		mainLoop = new Timer( delay, new GameLoop() );
		mainLoop.setInitialDelay(0);
	}

	/** No delay, maximum frame rate. */
	public GameCanvas() {
		this(0);
	}


	/** Start executing the game and draw loops of this canvas. */
	public void play() {
		lastNanoTime = System.nanoTime();
		mainLoop.start();
	}

	/**
	 * Stop executing the game and draw loops of this canvas.
	 * The <code>loopID</code> and <code>elapsedTime</code> values will be persistent.
	 */
	public void pause() {
		mainLoop.stop();
		deltaNanoTime = 0L;
	}

	/**
	 * @return <code>true</code> only if {@link #play()} was called, but
	 * 	<code>false</code> if {@link #pause()} has been called more recently.
	 */
	public boolean isRunning() {
		return mainLoop.isRunning();
	}


	/**
	 * Called once for each frame. Meant to be overridden by subclasses.
	 * 	Default behavior is to simply callback to {@link Canvas#paint(Graphics)}.
	 * @param g - Gaphics2D object to draw on.
	 * @throws LoopException - If something exceptional occurred
	 * 	and the draw step couldn't complete normally
	 */
	protected void drawStep(Graphics2D g) throws LoopException {
		paint(g);
	}

	/**
	 * Called once for each frame. Meant to be overridden by subclasses.
	 * 	Default behavior is to do nothing.
	 */
	protected void gameStep() throws LoopException {
	}


	/**
	 * Can be used in {@link #gameStep()} and {@link #drawStep(Graphics2D)}.
	 * @return Time that passed between the last loop and this one.
	 * 	(Measured in milliseconds, but with nanosecond precision.)
	 * 	<code>0L</code> if the Game is paused.
	 */
	protected double getDeltaTime() {
		return deltaNanoTime / 1.0E6;
	}

	/**
	 * Can be used in {@link #gameStep()} and {@link #drawStep(Graphics2D)}.
	 * @return Time that has passed while this canvas has been running.
	 * 	(Measured in milliseconds, but with nanosecond precision.)
	 * 	Time is recalculated before the current frame loops start.
	 */
	public double getElapsedTime() {
		return elapsedNanoTime / 1.0E6;
	}

	/**
	 * Can be used in {@link #gameStep()} and {@link #drawStep(Graphics2D)}.
	 * @return <code>1L</code> for the first loop, and increments for each successive loop.
	 */
	public long getLoopID() {
		return loopID;
	}


	/** @see #setFrameDelay(int) */
	public int getFrameDelay() {
		return mainLoop.getDelay();
	}

	/**
	 * Sets a minimum delay between each frame (in milliseconds). This effectively sets a maximum
	 * 	frame rate equal to 1000 / delay (in Hertz). Use to avoid over taxing your CPU
	 * 	by having a frame rate higher then you display supports. For example
	 * 	16 ms = 62.5 Hz, and 8 ms = 125 Hz.
	 * @param delay - The minimum delay (in milliseconds) between each frame.
	 */
	public void setFrameDelay(int delay) {
		mainLoop.setDelay(delay);
	}

	/**
	 * @return The number of loop IDs iterated over within the last (realtime) second.
	 * @see #setFrameRateTracking(boolean)
	 */
	public int getFrameRate() {
		synchronized(frameNanoTimes) {
			return frameNanoTimes.size();
		}
	}

	/** @see #setFrameRateTracking(boolean) */
	public boolean isTrackingFrameRate() {
		return trackFrameRate;
	}

	/**
	 * Used to turn on/off frame rate tracking, usually for performance reasons.
	 * 	Frame rate tracking is on by default.
	 * @param trackFrameRate - <code>true</code> to track the frame rate; <code>false</code> to stop.
	 */
	public void setFrameRateTracking(boolean trackFrameRate) {
		this.trackFrameRate = trackFrameRate;
	}

	/**
	 * @return The {@link LoopException}s (in chronological order) that have been thrown so far.
	 * 	This methods returns a synchronized List.
	 */
	public List<LoopException> getDrawExceptions() {
		return loopExceptions;
	}

	/** @see #setPauseOnException(boolean) */
	public boolean gePauseOnException() {
		return pauseOnException;
	}

	/**
	 * If set to <code>true</code> then {@link #pause()} will be called when
	 * 	an exception occurs within the game loop.
	 * @param pauseOnException - A boolean.
	 */
	public void setPauseOnException(boolean pauseOnException) {
		this.pauseOnException = pauseOnException;
	}
	
	/** @see #setDesiredFrameRate */
	public double getDesiredFrameRate() {
		return 1000.0 / desiredFrameDelay;
	}
	
	/**
	 * <p>
	 * 	Set the approximate maximum frame rate, or in other words, a desired frame rate.
	 * 	This will cause the GameCanvas to automatically change the frame delay
	 * 	depending on the speed of the system. Set the desired frame rate to a
	 * 	non-positive number (e.g. <code>0</code>) to turn off this feature.
	 * </p><p>
	 * 	This is like a frame rate "cap" because slow systems may not be able
	 * 	to reach the desired frame rate. It, however, is not a true "cap"
	 * 	because the frame rate may be above the desired rate.
	 * </p>
	 * <p>
	 * 	Note: frame rate tracking must be on for this feature to work correctly!
	 * </p>
	 * @param desiredFrameRate - The desired frame rate.
	 * @see #setFrameDelay(int)
	 * @see #setFrameRateTracking(boolean)
	 */
	public void setDesiredFrameRate(double desiredFrameRate) {
		this.desiredFrameDelay = (desiredFrameRate > 0 ? 1000 / desiredFrameRate : 0);
	}
}
