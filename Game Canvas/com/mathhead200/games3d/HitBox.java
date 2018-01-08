package com.mathhead200.games3d;


public class HitBox
{
	public final Vector topLeft;
	public final Vector bottomRight;


	public HitBox(Vector topLeft, Vector bottomRight) {
		this.topLeft = topLeft;
		this.bottomRight = bottomRight;
	}

	public HitBox(double x1, double y1, double z1, double x2, double y2, double z2) {
		this( new Vector(x1, y1, z1), new Vector(x2, y2, z2) );
	}

	public HitBox(double x1, double y1, double x2, double y2) {
		this(x1, y1, 0.0, x2, y2, 0.0);
	}

	public HitBox(double width, double height, double depth) {
		this( Vector.ZERO, new Vector(width, height, depth) );
	}

	public HitBox(double width, double height) {
		this(width, height, 0.0);
	}


	public double x1() {
		return topLeft.x;
	}

	public double y1() {
		return topLeft.y;
	}

	public double z1() {
		return topLeft.z;
	}

	public double x2() {
		return bottomRight.x;
	}

	public double y2() {
		return bottomRight.y;
	}

	public double z2() {
		return bottomRight.z;
	}

	public double width() {
		return x2() - x1();
	}

	public double height() {
		return y2() - y1();
	}

	public double depth() {
		return z2() - z1();
	}

	public Vector center() {
		return topLeft.add(bottomRight).multiply(0.5);
	}
}
