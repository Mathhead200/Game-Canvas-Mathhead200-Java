
import com.mathhead200.game.Vector;


public class VectorTest
{
	public static void main(String[] args) {
		Vector v = new Vector(1, 1, 1);
		System.out.println(v);
		System.out.printf( "%.3f @ (%.3f * PI), (%.3f * PI)%n",
				v.norm(), v.theta() / Math.PI, v.phi() / Math.PI );

		Vector w = Vector.sphericalForm( v.norm(), v.theta(), v.phi() );
		System.out.println(w);

		Vector x = w.normalize();
		System.out.println( "norm( " + x + " ) = " + x.norm() );
	}
}
