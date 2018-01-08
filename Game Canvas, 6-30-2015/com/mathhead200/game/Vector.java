package com.mathhead200.game;


public class Vector
{
	public final double x;
	public final double y;
	public final double z;
	private Double cachedNorm = null;
	private Double cachedTheta = null;
	private Double cachedPhi = null;


	public static final Vector ZERO = new Vector(0, 0);
	public static final Vector I = new Vector(1, 0, 0);
	public static final Vector J = new Vector(0, 1, 0);
	public static final Vector K = new Vector(0, 0, 1);
	static {
		ZERO.cachedNorm = 0.0;
		ZERO.cachedTheta = 0.0;
		ZERO.cachedPhi = 0.0;
		I.cachedNorm = 1.0;
		I.cachedTheta = 0.0;
		I.cachedPhi = 0.0;
		J.cachedNorm = 1.0;
		J.cachedTheta = Math.PI / 2;
		J.cachedPhi = 0.0;
		K.cachedNorm = 1.0;
		K.cachedTheta = 0.0;
		K.cachedPhi = Math.PI / 2;
	}


	/** Rotate the angle PI radians; stays in [PI, -PI). */
	private static double turn(double angle) {
		return angle + (angle <= 0 ? Math.PI : -Math.PI);
	}


	public Vector(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector(double x, double y) {
		this(x, y, 0.0);
		cachedPhi = 0.0;
	}


	public static Vector cylindricalForm(double r, double theta, double z) {
		double x = r * Math.cos(theta);
		double y = r * Math.sin(theta);
		Vector v = new Vector(x, y, z);
		if( r < 0 )
			theta += Math.PI;
		theta /= 2 * Math.PI;
		theta -= Math.floor(theta);
		if( theta > 0.5 )
			theta--;
		v.cachedTheta = 2 * Math.PI * theta;
		return v;
	}

	public static Vector polarForm(double r, double theta) {
		Vector v = cylindricalForm(r, theta, 0.0);
		v.cachedNorm = Math.abs(r);
		v.cachedPhi = 0.0;
		return v;
	}

	public static Vector sphericalForm(double rho, double theta, double phi) {
		double r = rho * Math.cos(phi);
		double z = rho * Math.sin(phi);
		Vector v = cylindricalForm(r, theta, z);
		if( rho < 0 )
			phi = -phi;
		phi /= 2 * Math.PI;
		phi -= Math.floor(phi);
		if( phi >= 0.75 ) {
			phi--;
		} else if( phi > 0.25 ) {
			phi -= 0.5;
			v.cachedTheta = turn(v.cachedTheta);
		}
		v.cachedPhi = 2 * Math.PI * phi;
		v.cachedNorm = Math.abs(rho);
		return v;
	}


	public double norm() {
		if( cachedNorm == null )
			cachedNorm = Math.sqrt( x * x + y * y + z * z );
		return cachedNorm;
	}

	public double theta() {
		if( cachedTheta == null )
			cachedTheta = Math.atan2(y, x);
		return cachedTheta;
	}

	public double phi() {
		if( cachedPhi == null ) {
			double r = Math.sqrt( x * x + y * y );
			if( r == 0 ) {
				if( z > 0 )
					cachedPhi = Math.PI / 2;
				else if( z < 0 )
					cachedPhi = -Math.PI / 2;
				else //z == 0
					cachedPhi = 0.0;
			} else
				cachedPhi = Math.atan( z / r );
		}
		return cachedPhi;
	}

	public Vector add(Vector v) {
		return new Vector( x + v.x, y + v.y, z + v.z );
	}

	public Vector negate() {
		Vector v = new Vector( -x, -y, -z );
		if( cachedNorm != null )
			v.cachedNorm = cachedNorm;
		if( cachedTheta != null )
			v.cachedTheta = turn(cachedTheta);
		if( cachedPhi != null )
			v.cachedPhi = -cachedPhi;
		return v;
	}

	public Vector subtract(Vector v) {
		return new Vector( x - v.x, y - v.y, z - v.z );
	}

	public Vector multiply(double k) {
		Vector v = new Vector( k * x, k * y, k * z );
		if( cachedNorm != null )
			v.cachedNorm = Math.abs(k * cachedNorm);
		if( cachedTheta != null )
			v.cachedTheta = (k >= 0 ? cachedTheta : turn(cachedTheta));
		if( cachedPhi != null )
			v.cachedPhi = Math.signum(k) * cachedPhi;
		return v;
	}

	/** @return The square of the distance between two vectors. */
	public double distanceSq(Vector v) {
		double dx = x - v.x;
		double dy = y - v.y;
		double dz = z - v.z;
		return dx * dx + dy * dy + dz * dz;
	}

	public double distance(Vector v) {
		return Math.sqrt( distanceSq(v) );
	}

	public double dotProduct(Vector v) {
		return x * v.x + y * v.y + z * v.z;
	}

	public double angleBetween(Vector v) {
		return Math.acos( dotProduct(v) / (norm() * v.norm()) );
	}

	public Vector crossProduct(Vector v) {
		return new Vector( y * v.z - z * v.y, z * v.x - x * v.z, x * v.y - y * v.x );
	}

	public Vector rotate(double theta, double phi) {
		return sphericalForm( norm(), theta() + theta, phi() + phi );
	}

	public Vector rotate(double theta) {
		return rotate(theta, 0.0);
	}

	public Vector normalize() {
		return sphericalForm( 1.0, theta(), phi() );
	}


	public String toString() {
		return "<" + x + ", " + y + ", " + z + ">";
	}

	public boolean equals(Object obj) {
		if( this == obj )
			return true;
		if( !(obj instanceof Vector) )
			return false;
		Vector v = (Vector)obj;
		return x == v.x && y == v.y && z == v.z;
	}

	public int hashCode() {
		int hash = Double.valueOf(z).hashCode();
		hash *= 31;
		hash ^= Double.valueOf(y).hashCode();
		hash *= 31;
		hash ^= Double.valueOf(x).hashCode();
		return hash;
	}
}
