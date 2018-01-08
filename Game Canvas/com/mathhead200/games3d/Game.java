package com.mathhead200.games3d;

import java.awt.Canvas;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import com.mathhead200.BigRational;
import com.mathhead200.GameCanvas;


/**
 * @author Christopher D'Angelo
 * @version Feb 19, 2014
 */
@SuppressWarnings("serial")
public class Game extends GameCanvas
{
	private static class MyItem
	{
		final Object object;
		final Drawable drawable;
		final Behavior behavior;
		final DenseSprite denseSprite;
		final BigRational level;

		MyItem(Object obj, BigRational lvl) {
			object = obj;
			drawable = (obj instanceof Drawable ? (Drawable)obj : null);
			behavior = (obj instanceof Behavior ? (Behavior)obj : null);
			denseSprite = (obj instanceof DenseSprite ? (DenseSprite)obj : null);
			level = lvl;
		}
	}


	private Vector origin = Vector.ZERO;
	private Vector perspective = Vector.K.multiply(-1000.0);
	private Comparator<MyItem> drawOrder = ORDER_BY_LAYER;
	private boolean perspectiveScaling = false;
	private Set<Integer> pressedKeys = new HashSet<Integer>(4);
	private Set<Integer> heldKeys = new HashSet<Integer>(8); //Used as LOCK
	private Set<Integer> releasedKeys = new HashSet<Integer>(4);
	private Set<Integer> pressedButtons = new HashSet<Integer>(3);
	private Set<Integer> heldButtons = new HashSet<Integer>(3); //Used as LOCK
	private Set<Integer> releasedButtons = new HashSet<Integer>(3);
	private MouseEvent mouseEntered = null;
	private MouseEvent mouseExited = null;
	private LinkedList<MyItem> addQueue = new LinkedList<MyItem>();
	private LinkedList<Object> removeQueue = new LinkedList<Object>(); //Used as LOCK
	private Set<Object> itemsCopy = new HashSet<Object>();
	private boolean queueLayerResort = false;

	private List<MyItem> items = new ArrayList<MyItem>(128) {
		public boolean add(MyItem item) {
			if( drawOrder == ORDER_BY_LAYER ) {
				//binary search - add in place
				int low = 0;
				int high = size();
				while(low < high) {
					int mid = (low + high) / 2;
					int cmp = ORDER_BY_LAYER.compare( get(mid), item );
					if( cmp < 0 )
						low = mid + 1;
					else if( cmp > 0 )
						high = mid;
					else {
						low = mid;
						break;
					}
				}
				add(low, item);
			} else
				super.add(item);
			return true;
		}
	};

	private KeyListener keyListener = new KeyListener() {
		public void keyPressed(KeyEvent e) {
			synchronized(heldKeys) {
				if( !heldKeys.contains(e.getKeyCode()) ) {
					pressedKeys.add( e.getKeyCode() );
					heldKeys.add( e.getKeyCode() );
				}
			}
		}

		public void keyReleased(KeyEvent e) {
			synchronized(heldKeys) {
				heldKeys.remove( e.getKeyCode() );
				releasedKeys.add( e.getKeyCode() );
			}
		}

		public void keyTyped(KeyEvent e) {}
	};

	private MouseListener mouseListener = new MouseListener() {
		public void mouseEntered(MouseEvent e) {
			mouseEntered = e;
		}

		public void mouseExited(MouseEvent e) {
			mouseExited = e;
		}

		public void mousePressed(MouseEvent e) {
			synchronized(heldButtons) {
				if( !heldButtons.contains(e.getButton()) ) {
					pressedButtons.add( e.getButton() );
					heldButtons.add( e.getButton() );
				}
			}
		}

		public void mouseReleased(MouseEvent e) {
			synchronized(heldButtons) {
				heldButtons.remove( e.getButton() );
				releasedButtons.add( e.getButton() );
			}
		}

		public void mouseClicked(MouseEvent e) {}
	};

	public static final Comparator<MyItem> ORDER_BY_LAYER = new Comparator<MyItem>() {
		public int compare(MyItem a, MyItem b) {
			return b.level.compareTo(a.level);
		}
	};

	public static final Comparator<MyItem> ORDER_BY_PERSPECTIVE = new Comparator<MyItem>() {
		public int compare(MyItem a, MyItem b) {
			if( a.drawable == null )
				return b.drawable == null ? 0 : -1;
			if( b.drawable == null )
				return 1;
			return Double.compare( b.drawable.getPosition().z, a.drawable.getPosition().z );
		}
	};

	
	/** A layer behind {@link BACKGROUND}. Equal to <code>2</code>. */
	public static final BigRational UNDERLAY = new BigRational(2);
	
	/** A layer behind {@link MIDGROUND}. Equal to <code>1</code>.
	 *  The default layer for {@link Scenery}. */
	public static final BigRational BACKGROUND = new BigRational(1);
	
	/** The default layer for {@link Drawbale}. Equal to <code>0</code>. */
	public static final BigRational MIDGROUND = BigRational.ZERO;
	
	/** A layer a front of {@link MIDGROUND}. Equal to <code>-1</code>.
	 *  The default layer for {@link @Mob}. */
	public static final BigRational FOREGROUND = new BigRational(-1);
	
	/** A layer in front of {@link FOREGROUND}. Equal to <code>-2</code>. */
	public static final BigRational OVERLAY = new BigRational(-2);


	/**
	 * Creates a game. This is just a {@link Canvas};
	 * it is not visible until placed in a visible container,
	 * it will not run until {@link #play()} is invoked, and
	 * {@link #createBufferStrategy(int)} should be invoked before that.
	 * 
	 * @param width - The width in pixels of the {@link GameCanvas}.
	 * @param height - The height in pixels of the {@link GameCanvas}.
	 * @see GameCanvas
	 */
	public Game(int width, int height) {
		setSize(width, height);
		addKeyListener(keyListener);
		addMouseListener(mouseListener);
	}

	/**
	 * Default constructor. Has size 600 x 400.
	 * @see #Game(int, int)
	 */
	public Game() {
		this(600, 400);
	}
	
	
	/**
	 * Constructs and returns a {@link JFrame} to display this Game on.
	 * The game is automatically added to the new frame,
	 * the frame is made visible, and a buffer strategy (double buffer)
	 * is created for the game. The game will not be started.
	 * 
	 * @param game - The game to frame.
	 * @return The {@link JFrame} that was created to show this game.
	 */
	public static JFrame frame(Game game) {
		JFrame frame = new JFrame();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(game);
		// frame.addKeyListener(keyListener);
		frame.pack();
		frame.setLocationRelativeTo(null);
		game.requestFocusInWindow();
		frame.setVisible(true);
		game.createBufferStrategy(2);
		return frame;
	}

	
	protected void gameStep() {
		//Remove then Add Queued Items
		synchronized(removeQueue) {
			Object obj;
			while( (obj = removeQueue.poll()) != null ) {
				Iterator<MyItem> iter = items.iterator();
				while( iter.hasNext() ) {
					MyItem item = iter.next();
					if( item.object.equals(obj) ) {
						iter.remove();
						break; //inner while loop, poll next element
					}
				}
			}

			MyItem item;
			while( (item = addQueue.poll()) != null )
				items.add(item);
		}

		//Get Game State
		GameState info = new GameState();
		info.game = Game.this;

		info.loopID = getLoopID();
		info.deltaTime = getDeltaTime();
		info.elapsedTime = getElapsedTime();

		synchronized(heldKeys) {
			info.pressedKeys = new HashSet<Integer>( (int)Math.ceil(pressedKeys.size() / 0.75) );
			info.heldKeys = new HashSet<Integer>( (int)Math.ceil(heldKeys.size() / 0.75) );
			info.releasedKeys = new HashSet<Integer>( (int)Math.ceil(releasedKeys.size() / 0.75) );
			info.pressedKeys.addAll(pressedKeys);
			info.heldKeys.addAll(heldKeys);
			info.releasedKeys.addAll(releasedKeys);
			pressedKeys.clear();
			releasedKeys.clear();
		}

		synchronized(heldButtons) {
			info.pressedButtons = new HashSet<Integer>( (int)Math.ceil(pressedButtons.size() / .75) );
			info.heldButtons = new HashSet<Integer>( (int)Math.ceil(heldButtons.size() / .75) );
			info.releasedButtons = new HashSet<Integer>( (int)Math.ceil(releasedButtons.size() / .75) );
			info.pressedButtons.addAll(pressedButtons);
			info.heldButtons.addAll(heldButtons);
			info.releasedButtons.addAll(releasedButtons);
			pressedButtons.clear();
			releasedButtons.clear();
		}
		info.mouseEntered = mouseEntered;
		info.mouseExited = mouseExited;
		mouseEntered = null;
		mouseExited = null;
		info.mouseLocation = MouseInfo.getPointerInfo().getLocation();
		Point canvasLocation = getLocationOnScreen();
		info.mouseLocation.x -= canvasLocation.x;
		info.mouseLocation.y -= canvasLocation.y;

		//Run Mob behavior loop, and DenseSprite physics
		for( MyItem item : items ) {
			if( item.behavior != null )
				item.behavior.behave(info);
			if( item.denseSprite != null )
				item.denseSprite.move( item.denseSprite.getVelocity().multiply(info.deltaTime) );
		}

		//(Optional) Sort Items
		if( drawOrder == ORDER_BY_PERSPECTIVE || queueLayerResort )
			Collections.sort(items, drawOrder);
	}

	protected void drawStep(Graphics2D g) {
		g.clearRect( 0, 0, getWidth(), getHeight() );
		for( MyItem item : items )
			if(item.drawable != null) {
				Image image = item.drawable.getImage();
				if( image != null )
					if( item.drawable.isHUD() ) {
						Vector pos = item.drawable.getPosition();
						g.drawImage( item.drawable.getImage(), (int)pos.x, (int)pos.y, null );
					} else {
						Vector a = item.drawable.getPosition().subtract(perspective);
						Vector c = origin.subtract(perspective);
						double k = c.z / a.z;
						int x = (int)( (a.x * k - c.x) ); //... - c.x = ... + perspective.x - origin.x
						int y = (int)( (a.y * k - c.y) ); //... - c.y = ... + perspectove.y - origin.y
						if( perspectiveScaling ) {
							if( k > 0 ) {
								int width = (int)( image.getWidth(null) * k );
								int height = (int)( image.getHeight(null) * k );
								g.drawImage( image, x, y, width, height, null);
							}
						} else
							g.drawImage( image, x, y, null );
					}
			}
	}
	

	/**
	 * Adds either a {@link Drawable} or {@link Behavoir} to the Game.
	 * This method returns immediately, but the object isn't drawn until the next frame.
	 * @param obj - The object to add.
	 * @param level - The level to add it on.
	 * 	Useful when the draw order is set to {@link ORDER_BY_LAYER}.
	 */
	public void add(Item obj, BigRational level) {
		synchronized(removeQueue) {
			Iterator<Object> iter = removeQueue.iterator();
			while( iter.hasNext() )
				if( obj.equals(iter.next()) ) {
					iter.remove();
					return;
				}
			addQueue.add( new MyItem(obj, level) );
			itemsCopy.add(obj);
		}
	}

	/**
	 * Adds either a {@link Drawable} or {@link Behavoir} to the Game.
	 * 	The layer is determined by the type of object being added.
	 * 	{@link Mob}s are placed in the {@link FORGROUND},
	 * 	{@link Scenery} in the {@link BACKGROUND},
	 * 	and everything else in the {@link MIDGROUND}.
	 * 	This method returns immediately, but the object isn't drawn until the next frame.
	 * @param obj - The object to add.
	 */
	public void add(Item obj) {
		if( obj instanceof Mob )
			add(obj, FOREGROUND);
		else if( obj instanceof Scenery )
			add(obj, BACKGROUND);
		else
			add(obj, MIDGROUND);
	}

	/**
	 * Removes an object from the Game. This method returns immediately,
	 * 	but the object will persist until the next frame.
	 * 
	 * @param obj - The object to remove.
	 * @return <code>true</code> if the object was removed.
	 * 	<code>false</code> if no such object was in the Game.
	 */
	public boolean remove(Item obj) {
		synchronized(removeQueue) {
			Iterator<MyItem> iter = addQueue.iterator();
			while( iter.hasNext() )
				if( obj.equals(iter.next().object) ) {
					iter.remove();
					return true;
				}
			if( itemsCopy.remove(obj) ) {
				removeQueue.add(obj);
				return true;
			}
		}
		return false;
	}
	
	/** @return All the items in the Game. These items will be draw
	 *  	in each subsequent frame, until removed. */
	public Set<Object> getItems() {
		return Collections.unmodifiableSet(itemsCopy);
	}

	public Vector getOrigin() {
		return origin;
	}

	public void setOrigin(Vector origin) {
		 this.origin = origin;
	}

	public Vector getPerspective() {
		return perspective;
	}

	public void setPerspective(Vector perspective) {
		this.perspective = perspective;
	}

	public void setPerspectiveScaling(boolean perspectiveScaling) {
		this.perspectiveScaling = perspectiveScaling;
	}

	public Comparator<MyItem> getDrawOrder() {
		return drawOrder;
	}

	public boolean isPerspectiveScaling() {
		return perspectiveScaling;
	}

	public void setDrawOrder(Comparator<MyItem> drawOrder) {
		if( drawOrder == ORDER_BY_LAYER && this.drawOrder != ORDER_BY_LAYER )
			queueLayerResort = true;
		this.drawOrder = drawOrder;
	}
}
