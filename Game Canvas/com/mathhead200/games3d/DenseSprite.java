package com.mathhead200.games3d;

import java.awt.Image;


/**
 * A subclass of {@link Sprite} that adds to it
 * 	a {@link HitBox} and a velocity.
 * 
 * @author Christopher D'Angelo
 * @version Nov 4, 2013
 */
public class DenseSprite extends Sprite
{
	private Vector vel = Vector.ZERO;
	private HitBox hitBox;


	/**
	 * Construct a sprite with the given default animation
	 * 	and a {@link HitBox} that fits the dimensions of the first frame
	 * 	of that animation. It has no initial velocity.
	 * @param aniamtion - The sprite's default animation.
	 */
	public DenseSprite(Iterable<Image> animation) {
		super(animation);
		Image image = getImage();
		this.hitBox = new HitBox( Vector.ZERO, (image != null ?
				new Vector(image.getWidth(null), image.getHeight(null)) : Vector.ZERO) );
	}

	/**
	 * Constructs a sprite with no default animation,
	 * 	no {@link HitBox}, and no initial velocity.
	 */
	public DenseSprite() {
		super();
	}


	/**
	 * @return The current velocity of this sprite.
	 */
	public Vector getVelocity() {
		return vel;
	}

	/**
	 * Set the current velocity of this sprite.
	 * 
	 * @param vel - The sprite's new velocity.
	 */
	public void setVelocity(Vector vel) {
		this.vel = vel;
	}

	/**
	 * Instantly change the sprite's current velocity
	 * 	by the given {@link Vector}.
	 * 
	 * @param impulse - The magnitude and direction at which
	 * 	to change the sprite's current velocity.
	 */
	public void applyImpulse(Vector impulse) {
		this.vel = vel.add(impulse);
	}

	/**
	 * @return The sprite's hit box.
	 */
	public HitBox getHitBox() {
		return hitBox;
	}

	/**
	 * Set the hit box of this sprite.
	 * 
	 * @param hitBox - The sprite's new hit box.
	 */
	public void setHitBox(HitBox hitBox) {
		this.hitBox = hitBox;
	}

	/**
	 * Set the {@link HitBox} of this sprite to a hit box
	 * 	with the given dimensions.
	 * @see HitBox#HitBox(double, double)
	 * 
	 * @param width - The width of the new hit box.
	 * @param height - The height of the new hit box.
	 */
	public void setHitBox(double width, double height) {
		this.hitBox = new HitBox(width, height);
	}

	/**
	 * Used to check if two sprites' hit boxes are intersecting.
	 * 
	 * @param sprite - Another sprite.
	 * @return <code>true</code> if any part of the sprites' associated
	 * 	hit boxes are overlapping; <code>false</code> if not,
	 * 	or if either hit box is <code>null</code>.
	 */
	public boolean isIntersecting(DenseSprite sprite) {
		if( hitBox == null || sprite.hitBox == null )
			return false;
		Vector v2 = hitBox.bottomRight.add( getPosition() );
		Vector w1 = sprite.hitBox.topLeft.add( sprite.getPosition() );
		if( v2.x < w1.x || v2.y < w1.y || v2.z < w1.z )
			return false;
		Vector v1 = hitBox.topLeft.add( getPosition() );
		Vector w2 = sprite.hitBox.bottomRight.add( sprite.getPosition() );
		return !(v1.x > w2.x || v1.y > w2.y || v1.z > w2.z);
	}

	/**
	 * Checks if the given position vector is inside of this sprite's hit box.
	 * @param pos - A position vector.
	 * @return <code>true</code> if the given position is within this sprite's hit box;
	 * 	<code>false</code> if not, or if either the position or hit box is <code>null</code>.
	 */
	public boolean contains(Vector pos) {
		if( hitBox == null || pos == null )
			return false;
		Vector v1 = hitBox.topLeft.add( getPosition() );
		if( pos.x < v1.x || pos.y < v1.y || pos.z < v1.z )
			return false;
		Vector v2 = hitBox.bottomRight.add( getPosition() );
		return !(pos.x > v2.x || pos.y > v2.y || pos.z > v1.z);
	}

	/**
	 * @return The middle point inside this sprite's hit box,
	 * 	or <code>null</code> if that hit box is <code>null</code>.
	 */
	public Vector centerOfMass() {
		if( getHitBox() == null )
			return null;
		return getHitBox().center().add( getPosition() );
	}
}
