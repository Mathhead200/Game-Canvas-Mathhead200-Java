package com.mathhead200.game;


/**
 * Used to program (set up instructions for) a {@link ProgrammableMob}.
 * 	Also contains many common instruction as static members.
 * 
 * @author Christopher D'Angelo
 * @version June 27, 2015
 */
public interface Instruction<T extends Behavior>
{
	/**
	 * Is invoked inside of a {@link ProgrammableMob}'s behave method. Used to carry out this
	 * 	{@link Instruction}. Will keep looping until it returns false or throws an exception.
	 * @param self - The {@link Behavior} that will run this instruction.
	 * @param info - Carries information about the current state of the game during this frame.
	 * @return <code>true</code> if another invocation of this method is needed to finish
	 * 	this instruction. Otherwise, if the instruction is done, <code>false</code>.
	 */
	public boolean follow(T self, GameState info);
	
	
	/**
	 * Moves (teleports) directly to a destination.
	 */
	public static class MoveTo implements Instruction<Mob>
	{
		private Vector dest;
		
		/** @param dest - Where to move to. */
		public MoveTo(Vector dest) {
			this.dest = dest;
		}
		
		/** @see #MoveTo(Vector) */
		public MoveTo(double x, double y, double z) {
			this( new Vector(x, y, z) );
		}
		
		public boolean follow(Mob self, GameState info) {
			self.setPosition(dest);
			return false; //done
		}	
	}
	
	
	/**
	 * Moves (teleports) directly to a destination relative to its current location.
	 */
	public static class MoveBy implements Instruction<Mob>
	{
		private Vector translation;
		
		/** @param translation - Move by how much. */
		public MoveBy(Vector translation) {
			this.translation = translation;
		}
		
		/** @see #MoveBy(Vector) */
		public MoveBy(double dx, double dy, double dz) {
			this( new Vector(dx, dy, dz) );
		}
		
		public boolean follow(Mob self, GameState info) {
			self.move(translation);
			return false; //done
		}	
	}
	
	
	/**
	 * Slides gradually to a destination.
	 */
	public static class SlideTo implements Instruction<Mob>
	{
		private Vector dest;
		private double speed;
		
		/**
		 * @param dest - Where to slide to.
		 * @param speed - How fast do we slide there (in pixels per ms).
		 */
		public SlideTo(Vector dest, double speed) {
			this.dest = dest;
			this.speed = speed;
		}
		
		/** @see #SlideTo(Vector, double) */
		public SlideTo(double x, double y, double z, double speed) {
			this( new Vector(x, y, z), speed );
		}
		
		public boolean follow(Mob self, GameState info) {
			Vector dir = dest.subtract(self.getPosition());
			double dist = speed * info.deltaTime;
			if( dist >= dir.norm() ) {
				self.setPosition(dest);
				return false; //done
			}
			self.move( dir.normalize().multiply(dist) );
			return true; //continue
		}	
	}
	
	
	/**
	 * Slides gradually by a certain amount.
	 */
	public static class SlideBy implements Instruction<Mob>
	{
		private Vector translation;
		private double speed;
		
		/**
		 * @param dest - Slide by how much.
		 * @param speed - How fast do we slide there (in pixels per ms).
		 */
		public SlideBy(Vector translation, double speed) {
			this.translation = translation;
			this.speed = speed;
		}
		
		/** @see #SlideBy(Vector, double) */
		public SlideBy(double x, double y, double z, double speed) {
			this( new Vector(x, y, z), speed );
		}
		
		public boolean follow(Mob self, GameState info) {
			double dist = speed * info.deltaTime;
			if( dist >= translation.norm() ) {
				self.move(translation);
				return false; //done
			}
			Vector delta = translation.normalize().multiply(dist);
			translation = translation.subtract(delta);
			self.move(delta);
			return true; //continue
		}	
	}
	
	
	/**
	 * Slides gradually with the given velocity indefinitely;
	 * 	only stops after {@link #stop()} is called.
	 */
	public static class Slide implements Instruction<Mob>
	{
		private Vector velocity;
		private boolean stopped = false;
		
		public Slide(Vector velocity) {
			this.velocity = velocity;
		}
		
		public Slide(double x, double y, double z) {
			this( new Vector(x, y, z) );
		}
		
		public void stop() {
			stopped = true;
		}
		
		public boolean follow(Mob self, GameState info) {
			if( stopped )
				return false; // done
			self.move( velocity.multiply(info.deltaTime) );
			return true; // continue
		}
		
	}
	
	
	/**
	 * (Buggy) Tries to intercept the target's path, i.e. go where it's headed.
	 * If the target is moving too fast, it will simply follow the target.*/
	public static class Intercept implements Instruction<Mob>
	{
		private DenseSprite target;
		private double speed;
		private Vector lastPos = null;
		private double lastTime = 0;
		
		public Intercept(DenseSprite target, double speed) {
			this.target = target;
			this.speed = speed;
		}
		
		public boolean follow(Mob self, GameState info) {
			double dist = speed * info.deltaTime; // how far we can travel in this frame
			Vector diff; // vector from us to our target
			{	Vector my_pos = self.centerOfMass();
				if( my_pos == null )
					my_pos = self.getPosition();
				Vector trg_pos = target.centerOfMass();
				if( trg_pos == null )
					trg_pos = target.getPosition();
				diff = trg_pos.subtract(my_pos);
			}
			// Can we reach the target in this frame?
			if( dist >= diff.norm() ) {
				self.move(diff);
				return false; // done
			}
			
			// first frame?
			if( lastPos == null ) {
				lastPos = target.getPosition();
				lastTime = info.elapsedTime;
				return true; // continue
			}
			
			// calculate direction (solve  a = v_a * t + a_0  and  b = v_b * t + b_0,  where a = b)
			Vector trg_vel = target.getPosition().subtract(lastPos).multiply(1 / (info.elapsedTime - lastTime));
			double a = diff.x * speed;
			double b = diff.y * speed;
			double c = diff.x * trg_vel.y - diff.y * trg_vel.x;
			//    a * sin(theta) - b * cos(theta) = c
			// -> sqrt(a^2 + b^2) * sin(theta - arctan(b/a)) = c
			double theta = Math.atan2(b, a);
			double arg = c / Math.sqrt( a*a + b*b );
			if( -1 <= arg && arg <=1 )
				theta += Math.asin(arg);
			
			// move in the calculated direction
			self.move( Vector.polarForm(dist, theta) );
			
			// have we reached our target?
			if( target.isIntersecting(self) )
				return false; // done
			
			// update data for the next frame's calculations
			lastPos = target.getPosition();
			lastTime = info.elapsedTime;
			return true; // continue
		}
	}
	
	
	/**
	 * Follows the target until {@link #stop} is called.
	 */
	public static class Follow implements Instruction<Mob>
	{
		private DenseSprite target;
		private double speed;
		private Vector offset;
		private double minimumDistance;
		private boolean followingCenterOfMass = true;
		private boolean stopped = false;
		
		public Follow(DenseSprite target, double speed, Vector offset, double minimumDistance) {
			this.target = target;
			this.speed = speed;
			this.offset = offset;
			this.minimumDistance = minimumDistance;
		}
		
		public Follow(DenseSprite target, double speed, Vector offset) {
			this(target, speed, offset, 0.0);
		}
		
		public Follow(DenseSprite target, double speed, double minimumDistance) {
			this(target, speed, Vector.ZERO, minimumDistance);
		}
		
		public Follow(DenseSprite target, double speed) {
			this(target, speed, Vector.ZERO, 0.0);
		}
		
		public boolean follow(Mob self, GameState info) {
			if( stopped )
				return false; // done
			
			double dist = speed * info.deltaTime;
			Vector diff;
			if( followingCenterOfMass ) {
				Vector my_pos = self.centerOfMass();
				if( my_pos == null )
					my_pos = self.getPosition();
				Vector trg_pos = target.centerOfMass();
				if( trg_pos == null )
					trg_pos = target.getPosition();
				trg_pos.add(offset);
				diff = trg_pos.subtract(my_pos);
			} else {
				diff = target.getPosition().add(offset).subtract(self.getPosition());
			}
			diff = Vector.polarForm(diff.norm() - minimumDistance, diff.theta());
			
			if( dist >= diff.norm() ) {
				self.move(diff);
			} else {
				self.move( Vector.polarForm(dist, diff.theta()) );
			}
			return true; // continue
		}
		
		public void stop() {
			stopped = true;
		}
		
		public DenseSprite getTarget() {
			return target;
		}
		
		public void setTarget(DenseSprite target) {
			this.target = target;
		}
		
		public double getSpeed() {
			return speed;
		}

		public void setSpeed(double speed) {
			this.speed = speed;
		}

		public Vector getOffset() {
			return offset;
		}

		public void setOffset(Vector offset) {
			this.offset = offset;
		}

		public double getMinimumDistance() {
			return minimumDistance;
		}

		public void setMinimumDistance(double minimumDistance) {
			this.minimumDistance = minimumDistance;
		}

		public boolean isFollowingCenterOfMass() {
			return followingCenterOfMass;
		}
		
		public void setFollowingCenterOfMass(boolean followingCenterOfMass) {
			this.followingCenterOfMass = followingCenterOfMass;
		}
	}
}
