
import java.math.BigInteger;
import java.util.Arrays;

import com.mathhead200.BigRational;


public class RationalTest
{
	public static void main(String[] args) {
		BigRational x = new BigRational(415, 93);
		BigRational y = new BigRational("-5/3");
		System.out.println( "x: " + x.continuedFraction() );
		System.out.println( "y: " + y.continuedFraction() );
		System.out.println( "0: " + BigRational.ZERO.continuedFraction() );

		BigRational a = BigRational.valueOf( Arrays.asList(
				BigInteger.valueOf(4), BigInteger.valueOf(5), BigInteger.valueOf(2) ) );
		System.out.println( "a: " + a.toMixedString() );

		BigRational q = new BigRational("5.2");
		BigRational r = new BigRational("-10.e", 16);
		System.out.println(q.toBigDecimal(20));
		System.out.println(r.toBigDecimal(5));

		System.out.println("Computing...");
		long time = System.nanoTime();
		BigRational e = BigRational.computeE(1500);
		time = System.nanoTime() - time;
		System.out.println( "e = " + e.toBigDecimal(2872) );
		System.out.println( "e = " + e.toPointString() );
		System.out.printf( "Time: %.1f ms%n", time / 1000000.0 );
	}
}
