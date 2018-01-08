package com.mathhead200;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 * Represents immutable rational numbers, (i.e. fractions of integers.)
 * 	The numerator and denominator of each rational number is
 * 	represented by a {@link java.math.BigInteger}, effectively allowing
 * 	the rational number to reach arbitrary magnitudes. These rational
 * 	numbers are reduce to and represented in lowest terms.
 * @author Christopher D'Angelo
 * @version 6/3/2013
 */
public class BigRational extends Number implements Comparable<BigRational>
{
	private BigInteger numerator;
	private BigInteger denominator;

	private static final long serialVersionUID = -2224876438957308489L;

	/** The <code>BigRational</code> constant zero, the additive identity. */
	public static final BigRational ZERO = new BigRational(0);

	/** The <code>BigRational</code> constant one, the multiplicative identity. */
	public static final BigRational ONE = new BigRational(1);

	/** The <code>BigRational</code> constant ten, the standard radix. */
	public static final BigRational TEN = new BigRational(10);

	/**
	 * The mathematical constant <code>e</code> (Euler's Number.) Specifically
	 * 	a rational approximation of <code>e</code>, which when converted to a double using
	 * 	{@link #doubleValue()} will be equal to {@link Math#E}.
	 */
	public static final BigRational E = computeE(49);


	private void reduce() {
		if( numerator.signum() == 0 ) {
			denominator = BigInteger.ONE;
			return;
		}
		if( denominator.signum() < 0 ) {
			numerator = numerator.negate();
			denominator = denominator.negate();
		}
		final BigInteger gcd = numerator.gcd(denominator);
		numerator = numerator.divide(gcd);
		denominator = denominator.divide(gcd);
	}


	/**
	 * Parses a <code>String</code> representation of a rational number
	 * 	in the given number base. This can be a simple whole number (which would be
	 * 	parsed as a {@link java.math.BigInteger}), can be a (finite) decimal number,
	 * 	or can use a '/' character to delineate between the numerator and denominator in
	 * 	a fractional number. E.g. <code>"42"</code>, <code>"1.41"</code>, or <code>"22/7"</code>.
	 * @param str - A <code>String</code> representation of a rational number.
	 * @param radix - A number base
	 * @throws NumberFormatException - If <code>str</code> is not a valid representation of a
	 * 	<code>BigRational</code> in the specified radix, or radix is outside the range
	 * 	from {@link Character#MIN_RADIX} to {@link Character#MAX_RADIX}, inclusive.
	 * @throws NullPointerException - If <code>str</code> is <code>null</code>.
	 */
	public BigRational(String str, int radix) {
		if( str == null )
			throw new NullPointerException("can not have any null arguments.");
		int i = str.indexOf('/');
		if( i >= 0 ) {
			//fraction
			numerator = new BigInteger( str.substring(0, i), radix );
			denominator = new BigInteger( str.substring(i + 1), radix );
			if( denominator.equals(BigInteger.ZERO) )
				throw new NumberFormatException("can not divide by zero.");
		} else {
			i = str.indexOf('.');
			if( i >= 0 ) {
				//decimal
				String head = str.substring(0, i);
				String tail = str.substring(i + 1);
				numerator = new BigInteger( head + tail, radix );
				denominator = BigInteger.valueOf(radix).pow( tail.length() );
			} else {
				//whole number
				numerator = new BigInteger(str, radix);
				denominator = BigInteger.ONE;
			}
		}
		reduce();
	}

	/**
	 * Parses a <code>String</code> representation of a rational number.
	 * 	This can be a simple whole number (which would be parsed as a {@link java.math.BigInteger}),
	 * 	can be a (finite) decimal number, or can use a '/' character
	 * 	to delineate between the numerator and denominator in a fractional number.
	 *  E.g. <code>"42"</code>, <code>"1.41"</code>, or <code>"22/7"</code>.
	 * @param str - A <code>String</code> representation of a rational number.
	 * @throws NumberFormatException - If <code>str</code> is not a valid representation of a
	 * 	<code>BigRational</code>.
	 * @throws NullPointerException - If <code>str</code> is <code>null</code>.
	 */
	public BigRational(String str) {
		this(str, 10);
	}

	/**
	 * Constructs a rational number with the given numerator and denominator.
	 * @param numerator - The numerator of a <code>Rational</code> number.
	 * @param denominator - The denominator of a <code>Rational</code> number.
	 * @throws ArithmeticException - If <code>denominator<code> is {@link BigInteger#ZERO}.
	 * @throws NullPointerException - If any argument is <code>null</code>.
	 */
	public BigRational(BigInteger numerator, BigInteger denominator) {
		if( numerator == null || denominator == null )
			throw new NullPointerException("can not have any null arguments.");
		if( denominator.equals(BigInteger.ZERO) )
			throw new ArithmeticException("can not divide by zero.");
		this.numerator = numerator;
		this.denominator = denominator;
		reduce();
	}

	/**
	 * Constructs a rational number representation of the given integer.
	 * @param n - An <code>BigInteger</code>.
	 * @throws NullPointerException - If <code>n</code> is <code>null</code>.
	 */
	public BigRational(BigInteger n) {
		this(n, BigInteger.ONE);
	}

	/**
	 * Constructs a rational number with the given numerator and denominator.
	 * @param numerator - The numerator of a <code>Rational</code> number.
	 * @param denominator - The denominator of a <code>Rational</code> number.
	 * @throws ArithmeticException - If <code>denominator</code> is <code>0</code>.
	 */
	public BigRational(long numerator, long denominator) {
		this( BigInteger.valueOf(numerator), BigInteger.valueOf(denominator) );
	}

	/**
	 * Constructs the rational number representation of the given integer.
	 * @param n - A <code>long</code>.
	 */
	public BigRational(long n) {
		this(n, 1);
	}

	/**
	 * Constructs the rational number form of the given decimal number.
	 * @param x - A {@link BigDecimal}.
	 */
	public BigRational(BigDecimal x) {
		this( x.toPlainString() );
	}

	/**
	 * Constructs the rational number form of the given decimal number.
	 * @param x - A <code>double</code>.
	 * @throws NumberFormatException - If <code>x</code> satisfies either
	 * 	{@link Double#isNaN(double)} or {@link Double#isInfinite(double)}.
	 */
	public BigRational(double x) {
		this( new BigDecimal(Double.toString(x)) );
	}


	/**
	 * Convert a continued fraction (represented as a {@link java.util.List})
	 * 	into a rational number.
	 * @param continuedFraction - The sequence representing a continued fraction.
	 * @return The rational number represented by the given
	 * 	continued fraction.
	 * @throws IllegalArgumentException - If the given sequence is empty.
	 * @throw NullPointerException - If the argument is <code>null</code>,
	 * 	or contains any <code>null</code> elements.
	 */
	public static BigRational valueOf(List<BigInteger> continuedFraction) {
		if( continuedFraction == null )
			throw new NullPointerException("can not have any null arguments.");
		if( continuedFraction.size() == 0 )
			throw new IllegalArgumentException("continued fraction sequence can not be empty.");
		BigRational q = new BigRational( continuedFraction.get(continuedFraction.size() - 1) );
		for( int i = continuedFraction.size() - 2; i >= 0; i-- ) {
			q = q.reciprocal();
			q = q.add( new BigRational(continuedFraction.get(i)) );
		}
		return q;
	}


	/**
	 * Compute an approximation of the mathematical constant e (Euler's Number) to arbitrary precision.
	 * Uses the (truncated) continued fraction form of e: <br>
	 * 	<code>[2; 1, <i>2</i>, 1, 1, <i>4</i>, 1, 1, <i>6</i>, 1, ..., 1, <i>2k</i>, 1, ...]</code>.
	 * @param n - The number of terms to carry out e's continued fraction.
	 * 	For example <code>n == 49</code> is the minimum <code>n</code> such that {@link #doubleValue()}
	 * 	returns a double equal to {@link Math#E}; where as <code>n == 1500</code> computes e
	 * 	accurately to 2872 decimal places.
	 * @return A rational approximation of e (Euler's Number).
	 * @see #E
	 */
	public static BigRational computeE(int n) {
		if( n <= 0 )
			throw new IllegalArgumentException("continued fraction sequence can not be empty.");
		List<BigInteger> contFrac = new ArrayList<BigInteger>(n);
		contFrac.add( BigInteger.valueOf(2) ); //first term in continued fraction
		for( int i = 1; i < n; i++ )
			contFrac.add( i % 3 == 2 ? BigInteger.valueOf((i+1)/3 * 2) : BigInteger.ONE );
		return valueOf(contFrac);
	}


	/** @return The numerator of this <code>BigRational<code>. */
	public BigInteger numerator() {
		return numerator;
	}

	/** @return The denominator of this <code>BigRational</code>. */
	public BigInteger denominator() {
		return denominator;
	}

	/** @return -1, 0, or 1 as the value of this <code>BigRational</code> is
		negative, zero, or positive (respectively.) */
	public int signum() {
		return numerator.signum();
	}


	/**
	 * Computes the sum of this <code>BigRational</code> plus the given <code>BigRational</code>.
	 * @param x - The addend.
	 * @return <code>this + q</this>
	 */
	public BigRational add(BigRational x) {
		return new BigRational(
			numerator.multiply(x.denominator).add( x.numerator.multiply(denominator) ), //new numerator
			denominator.multiply( x.denominator ) //new denominator
		);
	}

	/**
	 * Computes the negative (additive inverse) of this <code>BigRational</code>.
	 * @return <code>-this</code>
	 */
	public BigRational negate() {
		return new BigRational( numerator.negate(), denominator );
	}

	/**
	 * Computes the difference of this <code>BigRational</code> minus the given <code>BigRational</code>.
	 * @param x - The subtrahend.
	 * @return <code>this - q</code>
	 */
	public BigRational subtract(BigRational x) {
		return add( x.negate() );
	}

	/**
	 * Computes the product of this <code>BigRational</code> times the given <code>BigRational</code>.
	 * @param x - The multiplicand.
	 * @return <code>this * q</code>
	 */
	public BigRational multiply(BigRational x) {
		return new BigRational(
			numerator.multiply( x.numerator ), //new numerator
			denominator.multiply( x.denominator ) //new denominator
		);
	}

	/**
	 * Computes the reciprocal (multiplicative inverse) of this <code>BigRational</code>.
	 * @return <code>1 / this</code>
	 */
	public BigRational reciprocal() {
		if( denominator.equals(BigInteger.ZERO) )
			throw new ArithmeticException("can not divide by zero.");
		return new BigRational(denominator, numerator);
	}

	/**
	 * Computes the quotient of this <code>BigRational</code> divided by a given <code>BigRational</code>.
	 * @param x - The divisor (denominator.)
	 * @return <code>this / q</code>
	 */
	public BigRational divide(BigRational x) {
		return multiply( x.reciprocal() );
	}

	/**
	 * Computes this <code>BigRational</code> raised to the power of a given integer.
	 * 	[Note that unlike {@link java.math.BigInteger#pow(int)},
	 * 	the exponent here <i>can</i> be negative.]
	 * @param n - The exponent.
	 * @return <code>this<sup>n</sup></code>
	 */
	public BigRational pow(int n) {
		if( n < 0 )
			return reciprocal().pow(-n);
		return new BigRational( numerator.pow(n), denominator.pow(n) );
	}

	/**
	 * Computes the absolute value of this <code>Rational</code>.
	 * 	This will always be a non-negative value.
	 * @return <code>|this|</code>
	 */
	public BigRational abs() {
		if( signum() < 0 )
			return negate();
		return this;
	}


	/**
	 * Computes the mediant of two rational numbers, taking into account their being expressed
	 * 	in lowest terms. The mediant is the sum of the numerators over the sum of the denominators.
	 * @param x - An operand.
	 * @return <code>(n<sub>1</sub> + n<sub>2</sub>) / (d<sub>1</sub> + d<sub>2</sub>)</code>
	 */
	public BigRational mediant(BigRational x) {
		return new BigRational(
				numerator.add(x.numerator), //new numerator
				denominator.add(x.denominator) //new denominator
		);
	}


	/**
	 * @param x - Another <code>BigRational</code> to compare to.
	 * @return The greater rational number (or <code>this</code> if they are equal.)
	 */
	public BigRational max(BigRational x) {
		return compareTo(x) >= 0 ? this : x;
	}

	/**
	 * @param x - Another <code>BigRational</code> to compare to.
	 * @return The lesser rational number (or <code>this</code> if they are equal.)
	 */
	public BigRational min(BigRational x) {
		return compareTo(x) <= 0 ? this : x;
	}


	/**
	 * @return The integer part of this <code>BigRational</code>, after it's converted to a mixed number.
	 * 	Is negative if this rational number is less than or equal to -1,
	 * 	0 if between -1 and 1 (exclusively), and positive if greater than or equal to 1.
	 */
	public BigRational integerPart() {
		return new BigRational( numerator.divide(denominator) );
	}

	/**
	 * @return The fractional part of this <code>BigRational</code>, after it's converted to a mixed
	 * 	number. Is non-positive if this rational number is negative,
	 * 	and non-negative if this rational number is positive.
	 */
	public BigRational fractionalPart() {
		return new BigRational( numerator.remainder(denominator), denominator );
	}

	/**
	 * Generates a continued fraction representation of this rational number.
	 * 	The continued fraction's sequence of augends is returned as a List of {@link BigInteger}s.
	 * 	[Note that because this is a rational number, its continued fraction is guaranteed
	 * 	to be finite. Also a particular continued fraction representation (in general)
	 * 	is not unique.]
	 * @return A continued fraction representation of this rational number.
	 * @see #valueOf(List)
	 */
	public List<BigInteger> continuedFraction() {
		List<BigInteger> tuple = new ContinuedFraction();
		BigRational r = this;
		while(true) {
			tuple.add( r.integerPart().numerator() );
			r = r.fractionalPart();
			if( r.signum() == 0 )
				break;
			r = r.reciprocal();
		}
		return Collections.unmodifiableList(tuple);
	}

	/** @return The largest integer less than or equal to <code>this</code>. */
	public BigRational floor() {
		BigRational floor = integerPart();
		if( signum() < 0 && fractionalPart().signum() != 0 )
			floor = floor.subtract(BigRational.ONE);
		return floor;
	}

	/** @return The smallest integer greater than or equal to <code>this</code>. */
	public BigRational ceil() {
		BigRational ceil = integerPart();
		if( signum() >= 0 && fractionalPart().signum() > 0 )
			ceil = ceil.add(BigRational.ONE);
		return ceil;
	}


	/**
	 * Calculates the sum of all the <code>BigRational</code>s in a set.
	 * @param nums - Some <code>BigRational</code>s to add.
	 * @return The sum of all the rational numbers given,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational sum(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		BigRational sum = iter.next();
		while( iter.hasNext() )
			sum = sum.add( iter.next() );
		return sum;
	}

	/**
	 * Calculates the product of all the <code>BigRational</code>s in a set.
	 * @param nums - Some <code>BigRational</code>s to multiply.
	 * @return The product of all the rational numbers given,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational product(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		BigRational product = iter.next();
		while( iter.hasNext() )
			product = product.multiply( iter.next() );
		return product;
	}

	/**
	 * Calculates the maximum in a collection of <code>BigRational</code>s.
	 * @param nums - Some <code>BigRational</code>s to compare.
	 * @return The greatest rational number of the ones given,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational max(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		BigRational high = iter.next();
		while( iter.hasNext() )
			high = high.max( iter.next() );
		return high;
	}

	/**
	 * Calculates the minimum in a collection of <code>BigRational</code>s.
	 * @param nums - Some <code>BigRational</code>s to compare.
	 * @return The least rational number of the ones given,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational min(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		BigRational low = iter.next();
		while( iter.hasNext() )
			low = low.min( iter.next() );
		return low;
	}

	/**
	 * Calculates the arithmetic mean of a collection of <code>BigRational</code>s.
	 * @param nums - Some <code>BigRational</code>s to average.
	 * @return The arithmetic mean of the rational numbers given,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational arithmeticMean(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		BigRational sum = iter.next();
		int count = 1;
		while( iter.hasNext() ) {
			sum = sum.add( iter.next() );
			count++;
		}
		return sum.divide( new BigRational(count) );
	}

	/**
	 * Calculates the harmonic mean of a collection of <code>BigRational</code>s.
	 * @param nums - Some <code>BigRational</code>s to average.
	 * @return The harmonic mean of the rational given,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws ArithmeticException - If any of the <code>nums</code> are {@link #ZERO},
	 * 	or if the reciprocals sum to 0.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational harmonicMean(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		BigRational invSum = iter.next().reciprocal();
		int count = 1;
		while( iter.hasNext() ) {
			invSum = invSum.add( iter.next().reciprocal() );
			count++;
		}
		return new BigRational(count).divide(invSum);
	}

	/**
	 * Calculates the median of a collection of <code>BigRational</code>s.
	 * 	The collection is ordered, then if its size is odd, the middle element is returned,
	 * 	otherwise the (arithmetic) mean of the two middle elements are returned.
	 * @param nums - Some <code>BigRational</code>s.
	 * @return The median of a given set of rational numbers,
	 * 	or <code>null</code> if no numbers were given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational median(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		List<BigRational> seq = new ArrayList<BigRational>();
		do {
			BigRational x = iter.next();
			int low = 0;
			int high = seq.size();
			while( low < high ) {
				int mid = (high + low) / 2;
				int cmp = x.compareTo( seq.get(mid) );
				if( cmp < 0 )
					high = mid;
				else {
					low = mid + 1;
					if( cmp == 0 )
						break;
				}
			}
			seq.add(low, x);
		} while( iter.hasNext() );
		int n = seq.size();
		int mid = n / 2;
		if( n % 2 == 1 )
			return seq.get(mid);
		return seq.get(mid - 1).add( seq.get(mid) ).divide( new BigRational(2) );
	}

	/**
	 * Calculates the mode (the most commonly occurring elements)
	 * 	of a collection of <code>BigRational</code>s. The return value is
	 * 	a set, because there can be multiple modes of a set.
	 * @param nums - Some <code>BigRational</code>s.
	 * @return The mode (as a {@link Set}) of the given set of rational numbers,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static Set<BigRational> mode(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		Map<BigRational, Integer> counts = new HashMap<BigRational, Integer>();
		do {
			BigRational x = iter.next();
			Integer n = counts.get(x);
			counts.put( x, (n == null ? 1 : n + 1) );
		} while( iter.hasNext() );
		Set<BigRational> mode = new HashSet<BigRational>();
		int maxN = 1;
		for( Map.Entry<BigRational, Integer> entry : counts.entrySet() ) {
			if( entry.getValue() > maxN ) {
				maxN = entry.getValue();
				mode.clear();
			}
			if( entry.getValue() == maxN )
				mode.add( entry.getKey() );
		}
		return mode;
	}

	/**
	 * Calculates the variance of a collection of <code>BigRational</code>s.
	 * 	The variance is better know by its square root, standard deviation, a measure
	 * 	of central tendency in a set; however, the standard deviation of a set of rational numbers
	 * 	may not be rational itself.
	 * @param nums - Some <code>BigRational</code>s.
	 * @return The variance of the the given set of rational numbers,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational variance(Iterable<BigRational> nums) {
		Iterator<BigRational> iter = nums.iterator();
		final BigRational avg = arithmeticMean(nums);
		if( avg == null )
			return null;
		BigRational var = iter.next().subtract(avg).pow(2);
		int count = 1;
		while( iter.hasNext() ) {
			var = var.add( iter.next().subtract(avg).pow(2) );
			count++;
		}
		return var.divide( new BigRational(count) );
	}

	/**
	 * Calculates the absolute deviation of a collection of <code>BigRational</code>s
	 * 	from a given number. The absolute deviation, like the standard deviation,
	 * 	can be a measure of central tendency in a set.
	 * @param nums - Some <code>BigRational</code>s.
	 * @param center - A rational number to test tendency towards.
	 * @return The absolute deviation of the the given set of rational numbers from a given number,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>center</code>, <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational absoluteDeviation(Iterable<BigRational> nums, BigRational center) {
		Iterator<BigRational> iter = nums.iterator();
		if( !iter.hasNext() )
			return null;
		BigRational dev = iter.next().subtract(center).abs();
		int count = 1;
		while( iter.hasNext() ) {
			dev = dev.add( iter.next().subtract(center).abs() );
			count++;
		}
		return dev.divide( new BigRational(count) );
	}

	/**
	 * Calculates the absolute deviation of a collection of <code>BigRational</code>s.
	 * 	The absolute deviation, like the standard deviation, is a measure of central tendency in a set.
	 * @param nums - Some <code>BigRational</code>s.
	 * @return The absolute deviation of the the given set of rational numbers,
	 * 	or <code>null</code> if no numbers are given.
	 * @throws NullPointerException - If <code>nums</code> itself, or
	 * 	any of the rational numbers in <code>nums</code> are <code>null</code>.
	 */
	public static BigRational absoluteDeviation(Iterable<BigRational> nums) {
		return absoluteDeviation( nums, arithmeticMean(nums) );
	}


	/**
	 * Converts this <code>BigRational</code> to a {@link BigDecimal} with some given precision;
	 * 	which is in case this rational number can not be exactly expressed in decimal notation.
	 * 	(For example, the rational number 1/3 = 0.3333...)
	 * @param scale - The number of digits after the decimal point
	 * 	to round this <code>Rational</code> to.
	 * @return A {@link BigDecimal} version of this <code>Rational</code>,
	 * 	expressed with the given precision.
	 */
	public BigDecimal toBigDecimal(int scale) {
		if( scale < 0 )
			throw new IllegalArgumentException(scale + " < 0");
		BigDecimal n = new BigDecimal( numerator() ).setScale(scale);
		BigDecimal d = new BigDecimal( denominator() );
		return n.divide( d, RoundingMode.HALF_EVEN );
	}


	/**
	 * Converts this <code>BigRational</code> to a <code>String</code>,
	 * 	but represented in the given number base.
	 * @param radix - A number base.
	 * @return A <code>String</code> representation of this <code>BigRational</code>,
	 * 	in the given number base.
	 * @see #toMixedString(int)
	 * @see #toPointString(int)
	 */
	public String toString(int radix) {
		if( denominator.equals(BigInteger.ONE) )
			return numerator.toString(radix);
		return numerator.toString(radix) + "/" + denominator.toString(radix);
	}

	/**
	 * Similar to {@link #toString()}, but converts improper fractions to mixed numbers instead,
	 * 	also represented in the given number base.
	 * @param radix - A number base.
	 * @return A <code>String</code> representation of this <code>BigRational</code>, converting it to
	 * 	a mixed number if need be, and in a given number base.
	 * @see #toString(int)
	 * @see #toPointString(int)
	 */
	public String toMixedString(int radix) {
		BigRational whole = integerPart();
		BigRational frac = fractionalPart();
		if( frac.signum() == 0 ) //integer
			return whole.toString(radix);
		else if( whole.signum() == 0 ) //proper fraction
			return frac.toString(radix);
		else if( whole.signum() > 0 ) //positive mixed number
			return whole.toString(radix) + " " + frac.toString(radix);
		else //negative mixed number
			return "-" + whole.negate().toString(radix) + " " + frac.negate().toString(radix);
	}

	/**
	 * Similar to {@link #toString()}, but converts improper fractions to mixed numbers instead.
	 * @return A <code>String</code> representation of this <code>BigRational</code>, converting it to
	 * 	a mixed number if need be.
	 * @see #toString()
	 * @see #toPointString()
	 */
	public String toMixedString() {
		return toMixedString(10);
	}

	/**
	 * Converts this <code>BigRational</code> to a <code>String</code>, in the given
	 * 	number base, using conventional decimal point notation. If the rational can only be
	 * 	expressed with an infinite, but repeating pattern, the expansion
	 * 	will truncate after the first repetition, and signify which digits repeat.
	 * 	For example, <code>97/30</code> would be converted to <code>3.2&lt;3&gt;...</code>
	 * @param radix - A number base.
	 * @return A <code>String</code> representation of this <code>BigRational</code>,
	 * 	using conventional decimal point notation, but in the given number base.
	 * @see #toString(int)
	 * @see #toMixedString(int)
	 */
	public String toPointString(int radix) {
		if( radix < Character.MIN_RADIX || radix > Character.MAX_RADIX )
			radix = 10;
		if( signum() < 0 )
			return "-" + negate().toPointString(radix);
		Map<BigRational, Integer> remainderIndices = new HashMap<BigRational, Integer>();
		String head = integerPart().toString(radix);
		StringBuilder tail = new StringBuilder();
		BigRational rem = fractionalPart();
		final BigRational base = new BigRational(radix);
		while( rem.signum() != 0 ) {
			if( remainderIndices.containsKey(rem) ) {
				tail.insert( remainderIndices.get(rem), "<" );
				tail.append(">...");
				break;
			}
			remainderIndices.put( rem, tail.length() );
			rem = rem.multiply(base);
			tail.append( rem.integerPart().toString(radix) );
			rem = rem.fractionalPart();
		}
		return tail.length() == 0 ? head : head + "." + tail;
	}

	/**
	 * Converts this <code>BigRational</code> to a <code>String</code>,
	 * 	using conventional decimal point notation. If the rational can only be
	 * 	expressed with an infinite, but repeating pattern, the expansion
	 * 	will truncate after the first repetition, and signify which digits repeat.
	 * 	For example, <code>97/30</code> would be converted to <code>3.2&lt;3&gt;...</code>
	 * @return A <code>String</code> representation of this <code>BigRational</code>,
	 * 	using conventional decimal point notation.
	 */
	public String toPointString() {
		return toPointString(10);
	}


	/**
	 * Converts this <code>BigRational</code> to a <code>String</code>
	 * @return A <code>String</code> representation of this <code>BigRational</code>.
	 * @see #toMixedString()
	 * @see #toPointString()
	 */
	public String toString() {
		return toString(10);
	}

	public boolean equals(Object obj) {
		if( obj == this )
			return true;
		if( !(obj instanceof BigRational) )
			return false;
		BigRational that = (BigRational)obj;
		return numerator.equals(that.numerator) && denominator.equals(that.denominator);
	}

	public int hashCode() {
		return numerator.hashCode() ^ (denominator.hashCode() * 31);
	}


	public int compareTo(BigRational that) {
		return numerator.multiply(that.denominator).subtract(
				that.numerator.multiply(denominator) ).signum();
	}


	/**
     * Returns the value of the specified number as a <code>double</code>. This may involve rounding.
     * @return The numeric value represented by this object after conversion to type <code>double</code>.
     * @see #toBigDecimal(int)
     */
	public double doubleValue() {
		return numerator.doubleValue() / denominator.doubleValue();
	}

	/**
     * Returns the value of the specified number as a <code>float</code>. This may involve rounding.
     * @return The numeric value represented by this object after conversion to type <code>float</code>.
     * @see #toBigDecimal(int)
     */
	public float floatValue() {
		return numerator.floatValue() / denominator.floatValue();
	}

	/**
     * Returns the value of the specified number as an <code>int</code>.
     * This may involve rounding or truncation.
     * @return The numeric value represented by this object after conversion to type <code>int</code>.
     * @see #integerPart()
     */
	public int intValue() {
		return integerPart().intValue();
	}

	/**
     * Returns the value of the specified number as a <code>long</code>.
     * This may involve rounding or truncation.
     * @return The numeric value represented by this object after conversion to type <code>long</code>.
     * @see #integerPart()
     */
	public long longValue() {
		return integerPart().longValue();
	}


	/**
	 * Generates the Farey Sequence, F<sub>n</sub>.
	 * 	<p> In mathematics, the Farey sequence of order n is the sequence of completely reduced fractions
	 * 	between 0 and 1 which, when in lowest terms, have denominators less than or equal to n,
	 * 	arranged in order of increasing size.
	 * 	Each Farey sequence starts with {@link #ZERO}, and ends with {@link #ONE}. </p>
	 * @param n - An index (order), to designate which Farey sequence should be generated.
	 * @return The Farey Sequence of order n, F<sub>n</sub>.
	 */
	public static List<BigRational> fareySequence(int n) {
		if( n <= 0 )
			throw new IllegalArgumentException("n must be positive.");
		List<BigRational> seq;
		{	//approximate the size of the sequence
			double low = 3 * n * n / (Math.PI * Math.PI); //asymptotic behaviour
			int approx = (int)Math.ceil( low + n * (1205.0 / 2000) );
			seq = new ArrayList<BigRational>(approx);
		}
		BigRational p = BigRational.ZERO; //preceding previous
		BigRational q = new BigRational(1, n); //previous
		seq.add(p);
		seq.add(q);
		while( !q.equals(BigRational.ONE) ) {
			BigInteger k = BigInteger.valueOf(n).add(p.denominator()).divide(q.denominator());
			BigRational r = new BigRational(
					k.multiply(q.numerator()).subtract(p.numerator()),
					k.multiply(q.denominator()).subtract(p.denominator())
			);
			p = q;
			q = r;
			seq.add(r);
		}
		return seq;
	}
}


class ContinuedFraction extends ArrayList<BigInteger> {
	private static final long serialVersionUID = -3410302548870294448L;

	public String toString() {
		Iterator<BigInteger> iter = iterator();
		StringBuilder str = new StringBuilder();
		str.append('[').append( iter.next() );
		if( iter.hasNext() )
			str.append(';');
		else {
			str.append(']');
			return str.toString();
		}
		while(true) {
			BigInteger n = iter.next();
			str.append(n);
			if( !iter.hasNext() )
				return str.append(']').toString();
			str.append(',');
		}
	}
}
