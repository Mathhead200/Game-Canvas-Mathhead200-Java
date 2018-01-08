package com.mathhead200.game;

import java.awt.Image;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * An implementation of {@link Drawable}.
 * 	A sprite's image(s) are animations stored in the form of an
 * 	<code>{@link Iterable}&lt;Image&gt;</code> instance.
 * 	It can have many animations mapped to different "states."
 * 	A sprite has a mutable position (and is not an HUD element.)
 * 
 * @author Christopher D'Angelo
 * @version Nov 4, 2013
 */
public class Sprite implements Drawable
{
	private Map<String, Iterable<Image>> stateMap = new HashMap<String, Iterable<Image>>();
	private String state = null;
	private Iterator<Image> iter;
	private Vector pos = Vector.ZERO;
	private Image cachedImage = null;


	/**
	 * Construct a sprite with the given default animation.
	 * @param aniamtion - The sprite's default animation.
	 */
	public Sprite(Iterable<Image> animation) {
		if( animation == null )
			throw new IllegalArgumentException("a sprite's default animation can not be null");
		this.stateMap.put(null, animation);
		this.iter = animation.iterator();
	}

	/**
	 * Constructs a sprite with no default animation.
	 */
	public Sprite() {
		this( StaticImage.NULL );
	}


	/**
	 * @return The current state of the sprite.
	 * @see #setState(String)
	 */
	public String getState() {
		return state;
	}

	/**
	 * Used to check if a particular state has been mapped to this sprite.
	 * @param state - A state to check for.
	 * @return <code>true</code> if and only if the given state is mapped.
	 * @see #mapState(String, Iterable)
	 */
	public boolean hasState(String state) {
		return stateMap.containsKey(state);
	}

	/**
	 * Set the current state of this sprite. This may effect how the sprite looks
	 * 	depending on what animations are mapped to it.
	 * @param state - The state to change to.
	 * @return <code>true</code> if and only if the given state is mapped; thus
	 * 	<code>false</code> implies the state didn't change.
	 * @see #mapState(String, Iterable)
	 */
	public boolean setState(String state) {
		if( !hasState(state) )
			return false;
		this.state = state;
		iter = stateMap.get(state).iterator();
		return true;
	}

	/**
	 * Use to map a new state to an animation, or to map an existing state to a new animation.
	 * 	Mapping a state to <code>null</code> will unmap that state, making it no longer valid.
	 * @param state - Key: the state to map.
	 * @param animation - Value: the animation to be mapped to the given state.
	 * @throws IllegalArgumentException - If <code>state</code> and <code>animation</code>
	 * 	are both <code>null</code>
	 */
	public void mapState(String state, Iterable<Image> animation) {
		if( animation == null ) {
			if( state == null )
				throw new IllegalArgumentException("can not removing null as a key from the state map");
			stateMap.remove(state);
			if( state.equals(this.state) ) {
				this.state = null;
				this.iter = stateMap.get(null).iterator();
			}
		} else {
			stateMap.put(state, animation);
			if( (this.state == state) || (this.state != null && this.state.equals(state)) )
				iter = animation.iterator();
		}
	}

	public Image getImage() {
		if( iter.hasNext() )
			return iter.next();
		iter = stateMap.get(null).iterator();
		if( iter.hasNext() )
			return iter.next();
		return null;
	}

	/** @return The sprite's position vector. */
	public Vector getPosition() {
		return pos;
	}

	public boolean isHUD() {
		return false;
	}

	public Image getCachedImage() {
		return cachedImage;
	}
	
	/** @return The x coordinate of the sprite's position. */
	public double getX() {
		return pos.x;
	}

	/** @return The y coordinate of the sprote's position. */
	public double getY() {
		return pos.y;
	}

	/** @return The Z coordinate of the sprite's position. */
	public double getZ() {
		return pos.z;
	}

	/**
	 * Jump the sprite to a new absolute position.
	 * @param pos - The sprite's new position vector.
	 */
	public void setPosition(Vector pos) {
		this.pos = pos;
	}

	/**
	 * Jump the sprite to a new relative position, based on the given movement vector.
	 * @param path - A move for this sprite to take.
	 */
	public void move(Vector movement) {
		pos = pos.add(movement);
	}
}
