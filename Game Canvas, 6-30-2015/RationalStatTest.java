import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.mathhead200.BigRational;



public class RationalStatTest
{
	public static void main(String[] args) throws IOException {
		BufferedReader stdin = new BufferedReader( new InputStreamReader(System.in) );
		List<BigRational> seq = new ArrayList<BigRational>();
		while(true) {
			System.out.print("> ");
			String line = stdin.readLine();
			if( line == null || line.length() == 0 )
				break;
			try {
				seq.add( new BigRational(line) );
			} catch(NumberFormatException e) {
				System.out.println( e.getMessage() );
			}
		}
		long time = System.nanoTime();
		System.out.println("seq: " + seq);
		System.out.println();
		System.out.println("Sum: " + BigRational.sum(seq));
		System.out.println("Product: " + BigRational.product(seq));
		System.out.println();
		System.out.println("Minimum: " + BigRational.min(seq));
		System.out.println("Maximum: " + BigRational.max(seq));
		System.out.println("Arithmetic Mean: " + BigRational.arithmeticMean(seq));
		try {
			System.out.println("Harmonic Mean: " + BigRational.harmonicMean(seq));
		} catch(ArithmeticException e) {
			System.out.println("Harmonic Mean: undefined (" + e.getMessage() + ")");
		}
		System.out.println("Median: " + BigRational.median(seq));
		System.out.println("Mode: " + BigRational.mode(seq));
		System.out.println();
		System.out.println("Absolute Deviation: " + BigRational.absoluteDeviation(seq));
		System.out.println("Varience: " + BigRational.variance(seq));
		time = System.nanoTime() - time;
		System.out.println();
		System.out.println("Time: " + (time / 1000000.0) + " ms");
	}
}
