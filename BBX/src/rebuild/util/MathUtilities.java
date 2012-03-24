//#preprocessor

//---------------------------------------------------------------------------------
//
// BlackBerry Extensions
// Copyright (c) 2011-2012 Vincent Simonetti
//
// Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated 
// documentation files (the "Software"), to deal in the Software without restriction, including without limitation 
// the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and 
// to permit persons to whom the Software is furnished to do so, subject to the following conditions:
// 
// The above copyright notice and this permission notice shall be included in all copies or substantial portions of the Software.
// 
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
// INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR 
// PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE 
// LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR 
// OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
// DEALINGS IN THE SOFTWARE.
//
//---------------------------------------------------------------------------------
//
package rebuild.util;

//asin, acos, atan2 are from J4ME and use the Apache License 2.0 (which is compatible with LGPL)

/**
 * Provides some basic numeric operations.
 */
public final class MathUtilities
{
//#ifdef BlackBerrySDK4.5.0
	private static final double LOG2 = 0.69314718055994530941723212145818;
//#endif
	
	/**
	 * Returns the arc cosine of the value x.
	 * @param x the value.
	 * @return the arc cosine of the argument in radians.
	 * @since BBX 1.0.0
	 */
	public static double acos(double x)
	{
//#ifdef BlackBerrySDK4.5.0
		//return (Math.PI / 2) - asin(x);
		
		// Special case.
		if (Double.isNaN(x) || Math.abs(x) > 1.0)
		{
			return Double.NaN;
		}
		
		// Calculate the arc cosine.
		double aSquared = x * x;
		double arcCosine = atan2(Math.sqrt(1 - aSquared), x);
		return arcCosine;
//#else
		return net.rim.device.api.util.MathUtilities.acos(x);
//#endif
	}
	
	/**
	 * Returns the arc sine of the value x.
	 * @param x the value.
	 * @return the arc sine of the argument in radians.
	 * @since BBX 1.0.0
	 */
	public static double asin(double x)
	{
//#ifdef BlackBerrySDK4.5.0
		//Code from J4ME
		// Special cases.
		if (Double.isNaN(x) || Math.abs(x) > 1.0)
		{
			return Double.NaN;
		}
		
		if (x == 0.0)
		{
			return x;
		}
		
		// Calculate the arc sine.
		double aSquared = x * x;
		double arcSine = atan2(x, Math.sqrt(1 - aSquared));
		return arcSine;
//#else
		return net.rim.device.api.util.MathUtilities.asin(x);
//#endif
	}
	
//#ifdef BlackBerrySDK4.5.0
	//atan code and constants taken from cmath.tgz (http://www.netlib.org/cephes/)
	
	/*
	Cephes Math Library Release 2.8:  June, 2000
	Copyright 1984, 1995, 2000 by Stephen L. Moshier
	*/
	/* arctan(x)  = x + x^3 P(x^2)/Q(x^2)
	   0 <= x <= 0.66
	   Peak relative error = 2.6e-18  */
	private static final double[] P = {
	    -8.750608600031904122785E-1,
	    -1.615753718733365076637E1,
	    -7.500855792314704667340E1,
	    -1.228866684490136173410E2,
	    -6.485021904942025371773E1
    };
    private static final double[] Q = {
	    /* 1.000000000000000000000E0, */
	     2.485846490142306297962E1,
	     1.650270098316988542046E2,
	     4.328810604912902668951E2,
	     4.853903996359136964868E2,
	     1.945506571482613964425E2
    };
    
    /* tan( 3*pi/8 ) */
    private static final double T3P8 = 2.41421356237309504880;
    /* pi/2 = PIO2 + MOREBITS.  */
    private static final double MOREBITS = 6.123233995736765886130E-17;
    
    //polevl and p1evl taken from Java-ML 0.1.6 (Java Machine Learning Library)(http://java-ml.sourceforge.net/)
    /*
    Copyright (C) 1999 CERN - European Organization for Nuclear Research.
    Permission to use, copy, modify, distribute and sell this software and its documentation for any purpose 
    is hereby granted without fee, provided that the above copyright notice appear in all copies and 
    that both that copyright notice and this permission notice appear in supporting documentation. 
    CERN makes no representations about the suitability of this software for any purpose. 
    It is provided "as is" without expressed or implied warranty.
    */
    private static double p1evl(double x, double[] coef, int N)
    {
        double ans;
        
        ans = x + coef[0];
        
        for (int i = 1; i < N; i++)
        {
            ans = ans * x + coef[i];
        }
        
        return ans;
    }
    
    private static double polevl(double x, double[] coef, int N)
    {
        double ans;
        ans = coef[0];
        
        for (int i = 1; i <= N; i++)
        {
        	ans = ans * x + coef[i];
        }
        
        return ans;
    }
//#endif
	
	/**
	 * Returns the arc tangent of the value x.
	 * @param x the value.
	 * @return the arc tangent of the argument in radians.
	 * @since BBX 1.0.0
	 */
	public static double atan(double x)
	{
//#ifdef BlackBerrySDK4.5.0
		// Special cases.
        if (Double.isNaN(x))
        {
            return Double.NaN;
        }
        
        if(Double.isInfinite(x))
		{
			if(x < 0)
			{
				return -Math.PI / 2;
			}
			else
			{
				return Math.PI / 2;
			}
		}
        
        if (x == 0.0)
        {
            return x;
        }
        
        // Compute the arc tangent.
        double y, z;
        short sign, flag;
        
        //make argument positive and save the sign
        sign = 1;
        if (x < 0.0)
        {
            sign = -1;
            x = -x;
        }
        //range reduction
        flag = 0;
        if (x > T3P8)
        {
            y = Math.PI / 2;
            flag = 1;
            x = -(1.0 / x);
        }
        else if (x <= 0.66)
        {
            y = 0.0;
        }
        else
        {
            y = Math.PI / 4;
            flag = 2;
            x = (x - 1.0) / (x + 1.0);
        }
        z = x * x;
        z = z * polevl(z, P, 4) / p1evl(z, Q, 5);
        z = x * z + x;
        if (flag == 2)
        {
        	z += 0.5 * MOREBITS;
        }
        else if (flag == 1)
        {
        	z += MOREBITS;
        }
        y = y + z;
        if (sign < 0)
        {
        	y = -y;
        }
        return y;
//#else
		return net.rim.device.api.util.MathUtilities.atan(x);
//#endif
	}
	
	/**
	 * Converts rectangular coordinates (x,y) to polar coordinates (r,theta).
	 * This method computes the phase theta by computing the arc tangent of y/x in the range of -pi to pi.
	 * @param y the abscissa coordinate.
	 * @param x the ordinate coordinate.
	 * @return the theta component of the point (r,theta) in polar coordinates that corresponds to the point (x,y) in Cartesian coordinates.
	 * @since BBX 1.0.0
	 */
	public static double atan2(double y, double x)
	{
//#ifdef BlackBerrySDK4.5.0
		//Code from J4ME
		if(Double.isNaN(y) || Double.isNaN(x))
		{
			return Double.NaN;
		}
		else if (Double.isInfinite(y))
        {
            if (y > 0.0) // Positive infinity
            {
                if (Double.isInfinite(x))
                {
                    if (x > 0.0)
                    {
                        return Math.PI / 4;
                    }
                    else
                    {
                        return 3.0 * Math.PI / 4;
                    }
                }
                else if (x != 0.0)
                {
                    return Math.PI / 2;
                }
            }
            else  // Negative infinity
            {
                if (Double.isInfinite(x))
                {
                    if (x > 0.0)
                    {
                        return -Math.PI / 4;
                    }
                    else
                    {
                        return -3.0 * Math.PI / 4;
                    }
                }
                else if (x != 0.0)
                {
                    return -Math.PI / 2;
                }
            }
        }
        else if (y == 0.0)
        {
            if (x > 0.0)
            {
                return y;
            }
            else if (x < 0.0)
            {
                return Math.PI;
            }
        }
        else if (Double.isInfinite(x))
        {
            if (x > 0.0)  // Positive infinity
            {
                if (y > 0.0)
                {
                    return 0.0;
                }
                else if (y < 0.0)
                {
                    return -0.0;
                }
            }
            else  // Negative infinity
            {
                if (y > 0.0)
                {
                    return Math.PI;
                }
                else if (y < 0.0)
                {
                    return -Math.PI;
                }
            }
        }
        else if (x == 0.0)
        {
            if (y > 0.0)
            {
                return Math.PI / 2;
            }
            else if (y < 0.0)
            {
                return -Math.PI / 2;
            }
        }
		
		// Implementation a simple version ported from a PASCAL implementation:
        //   http://everything2.com/index.pl?node_id=1008481
		
        double arcTangent;
        
        // Use arctan() avoiding division by zero.
        if (Math.abs(x) > Math.abs(y))
        {
            arcTangent = atan(y / x);
        }
        else
        {
            arcTangent = atan(x / y); // -PI/4 <= a <= PI/4

            if (arcTangent < 0)
            {
                arcTangent = -(Math.PI / 2) - arcTangent; // a is negative, so we're adding
            }
            else
            {
                arcTangent = (Math.PI / 2) - arcTangent;
            }
        }
        
        // Adjust result to be from [-PI, PI]
        if (x < 0)
        {
            if (y < 0)
            {
                arcTangent = arcTangent - Math.PI;
            }
            else
            {
                arcTangent = arcTangent + Math.PI;
            }
        }
        
        return arcTangent;
//#else
		return net.rim.device.api.util.MathUtilities.atan2(y, x);
//#endif
	}
	
	/**
	 * Clamps provided integer value between a lower and upper bound.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to clamp.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the lower bound; if the value is higher than the higher bound, this method returns 
	 * the higher bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.0
	 */
	public static int clamp(int low, int value, int high)
	{
		return net.rim.device.api.util.MathUtilities.clamp(low, value, high);
	}
	
	/**
	 * Clamps provided long integer value between a lower and upper bound.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to clamp.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the lower bound; if the value is higher than the higher bound, this method returns 
	 * the higher bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.1
	 */
	public static long clamp(long low, long value, long high)
	{
//#ifdef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
		if(low > high)
		{
			throw new IllegalArgumentException("low > high");
		}
		return Math.max(low, Math.min(high, value));
//#else
		return net.rim.device.api.util.MathUtilities.clamp(low, value, high);
//#endif
	}
	
	/**
	 * Clamps provided float value between a lower and upper bound.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to clamp.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the lower bound; if the value is higher than the higher bound, this method returns 
	 * the higher bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.1
	 */
	public static float clamp(float low, float value, float high)
	{
//#ifdef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
		if(low > high)
		{
			throw new IllegalArgumentException("low > high");
		}
		return Math.max(low, Math.min(high, value));
//#else
		return net.rim.device.api.util.MathUtilities.clamp(low, value, high);
//#endif
	}
	
	/**
	 * Clamps provided double value between a lower and upper bound.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to clamp.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the lower bound; if the value is higher than the higher bound, this method returns 
	 * the higher bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.1
	 */
	public static double clamp(double low, double value, double high)
	{
//#ifdef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
		if(low > high)
		{
			throw new IllegalArgumentException("low > high");
		}
		return Math.max(low, Math.min(high, value));
//#else
		return net.rim.device.api.util.MathUtilities.clamp(low, value, high);
//#endif
	}
	
	/**
	 * Return the exponential (base e) of x.
	 * Interesting cases:
	 * <ul>
	 * 	<li>If the argument is negative infinity, the result is zero.</li>
	 * 	<li>If the argument is positive infinity, the result is positive infinity.</li>
	 * 	<li>If the argument is NaN, the result is NaN.</li>
	 * 	<li>If the argument is greater than 7.09e+02, the result is positive infinity.</li>
	 * 	<li>If the argument is less than -7.45e+02, the result is zero.</li>
	 * </ul>
	 * @param x the power to raise e to.
	 * @return e raised to the power x.
	 * @since BBX 1.0.0
	 */
	public static double exp(double x)
	{
		return net.rim.device.api.util.MathUtilities.exp(x);
	}
	
	/**
	 * Returns the result of multiplying x (the significand) by 2 raised to the power of exp (the exponent).
	 * @param x the significand.
	 * @param exp the exponent.
	 * @return x * 2exp.
	 * @since BBX 1.0.0
	 */
	public static double ldexp(double x, int exp)
	{
//#ifdef BlackBerrySDK4.5.0
		//"x * e^(exp * log(2.0))" AKA "x * 2^exp"
		return x * net.rim.device.api.util.MathUtilities.exp(exp * LOG2);
//#else
		return net.rim.device.api.util.MathUtilities.ldexp(x, exp);
//#endif
	}
	
	/**
	 * Return the natural logarithm (base e) of x.
	 * Interesting cases:
	 * <ul>
	 * 	<li>If the argument is less than zero (including negative infinity), the result is NaN.</li>
	 * 	<li>If the argument is zero, the result is negative infinity.</li>
	 * 	<li>If the argument is positive infinity, the result is positive infinity.</li>
	 * 	<li>If the argument is NaN, the result is NaN.</li>
	 * </ul>
	 * @param x a number greater than zero.
	 * @return the natural logarithm (base e) of the argument.
	 * @since BBX 1.0.0
	 */
	public static double log(double x)
	{
		return net.rim.device.api.util.MathUtilities.log(x);
	}
	
	/**
	 * Returns the log base 2 of the unsigned value rounded down.
	 * <p>
	 * The value zero and one both return zero.
	 * @param value The unsigned value to calculate log2 on.
	 * @return The calculation of log2(value) (range: 0-31).
	 * @since BBX 1.0.0
	 */
	public static int log2(int value)
	{
		return net.rim.device.api.util.MathUtilities.log2(value);
	}
	
	/**
	 * Returns the log base 2 of the unsigned value rounded down.
	 * <p>
	 * The value zero and one both return a log2 result of zero.
	 * @param value The unsigned value to calculate log2 on.
	 * @return The calculation of log2(value) (range: 0-63).
	 * @since BBX 1.0.0
	 */
	public static int log2(long value)
	{
		return net.rim.device.api.util.MathUtilities.log2(value);
	}
	
	/**
	 * Return x raised to the power of y.
	 * @param x the base value.
	 * @param y the exponent.
	 * @return xy.
	 * @since BBX 1.0.0
	 */
	public static double pow(double x, double y)
	{
//#ifdef BlackBerrySDK4.5.0
		//e^(y * log(x))
		return net.rim.device.api.util.MathUtilities.exp(y * net.rim.device.api.util.MathUtilities.log(x));
//#else
		return net.rim.device.api.util.MathUtilities.pow(x, y);
//#endif
	}
	
	/**
	 * Returns the closest long to the argument.
	 * The result is rounded to an integer by adding 1/2 if positive or subtracting 1/2 if negative, taking the floor of the result, and casting the result to type long.
	 * <p>
	 * Special cases:
	 * <ul>
	 * 	<li>If the argument is NaN, the result is 0.</li>
	 * 	<li>If the argument is negative infinity or any value less than or equal to the value of Long.MIN_VALUE, the result is equal to the value of Long.MIN_VALUE.</li>
	 * 	<li>If the argument is positive infinity or any value greater than or equal to the value of Long.MAX_VALUE, the result is equal to the value of Long.MAX_VALUE.</li>
	 * 	<li>If the argument is an exact negative half (-x.5), the value is rounded to the closest value to zero.</li>
	 * </ul>
	 * @param a a floating-point value to be rounded to a long.
	 * @return the value of the argument rounded to the nearest long value.
	 * @see Long#MAX_VALUE
	 * @see Long#MIN_VALUE
	 * @since BBX 1.0.0
	 */
	public static long round(double a)
	{
//#ifdef BlackBerrySDK4.5.0
		if(a == 0)
		{
			return 0;
		}
		else if(Double.isNaN(a))
		{
			return 0;
		}
		else if(Double.isInfinite(a))
		{
			if(a == Double.NEGATIVE_INFINITY)
			{
				return Long.MIN_VALUE;
			}
			else
			{
				return Long.MAX_VALUE;
			}
		}
		else if(a <= Long.MIN_VALUE)
		{
			return Long.MIN_VALUE;
		}
		else if(a >= Long.MAX_VALUE)
		{
			return Long.MAX_VALUE;
		}
		else
		{
			if(a < 0)
			{
				a -= 0.5;
			}
			else
			{
				a += 0.5;
			}
			return (long)Math.floor(a);
		}
//#else
		return net.rim.device.api.util.MathUtilities.round(a);
//#endif
	}
	
	/**
	 * Returns the closest int to the argument.
	 * The result is rounded to an integer by adding 1/2 if positive or subtracting 1/2 if negative, taking the floor of the result, and casting the result to type int.
	 * <p>
	 * Special cases:
	 * <ul>
	 * 	<li>If the argument is NaN, the result is 0.</li>
	 * 	<li>If the argument is negative infinity or any value less than or equal to the value of Integer.MIN_VALUE, the result is equal to the value of Integer.MIN_VALUE.</li>
	 * 	<li>If the argument is positive infinity or any value greater than or equal to the value of Integer.MAX_VALUE, the result is equal to the value of Integer.MAX_VALUE.</li>
	 * 	<li>If the argument is an exact negative half (-x.5), the value is rounded to the closes value to zero.</li>
	 * </ul>
	 * @param a a floating-point value to be rounded to an integer.
	 * @return the value of the argument rounded to the nearest int value.
	 * @see Integer#MAX_VALUE
	 * @see Integer#MIN_VALUE
	 * @since BBX 1.0.0
	 */
	public static int round(float a)
	{
//#ifdef BlackBerrySDK4.5.0
		if(a == 0)
		{
			return 0;
		}
		else if(Float.isNaN(a))
		{
			return 0;
		}
		else if(Float.isInfinite(a))
		{
			if(a == Float.NEGATIVE_INFINITY)
			{
				return Integer.MIN_VALUE;
			}
			else
			{
				return Integer.MAX_VALUE;
			}
		}
		else if(a <= Integer.MIN_VALUE)
		{
			return Integer.MIN_VALUE;
		}
		else if(a >= Integer.MAX_VALUE)
		{
			return Integer.MAX_VALUE;
		}
		else
		{
			if(a < 0)
			{
				a -= 0.5f;
			}
			else
			{
				a += 0.5f;
			}
			return (int)Math.floor(a);
		}
//#else
		return net.rim.device.api.util.MathUtilities.round(a);
//#endif
	}
	
	/**
	 * Wraps provided integer value around a lower and upper bound.
	 * <p>
	 * This method does the opposite of {@link MathUtilities#clamp(int, int, int)}.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to wrap.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the higher bound; if the value is higher than the higher bound, this method returns 
	 * the lower bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.0
	 */
	public static int wrap(int low, int value, int high)
	{
		return net.rim.device.api.util.MathUtilities.wrap(low, value, high);
	}
	
	/**
	 * Wraps provided long integer value around a lower and upper bound.
	 * <p>
	 * This method does the opposite of {@link MathUtilities#clamp(long, long, long)}.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to wrap.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the higher bound; if the value is higher than the higher bound, this method returns 
	 * the lower bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.1
	 */
	public static long wrap(long low, long value, long high)
	{
//#ifdef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
		if(low > high)
		{
			throw new IllegalArgumentException("low > high");
		}
		if(value < low)
		{
			return high;
		}
		else if(value > high)
		{
			return low;
		}
		return value;
//#else
		return net.rim.device.api.util.MathUtilities.wrap(low, value, high);
//#endif
	}
	
	/**
	 * Wraps provided float value around a lower and upper bound.
	 * <p>
	 * This method does the opposite of {@link MathUtilities#clamp(float, float, float)}.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to wrap.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the higher bound; if the value is higher than the higher bound, this method returns 
	 * the lower bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.1
	 */
	public static float wrap(float low, float value, float high)
	{
//#ifdef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
		if(low > high)
		{
			throw new IllegalArgumentException("low > high");
		}
		if(value < low)
		{
			return high;
		}
		else if(value > high)
		{
			return low;
		}
		return value;
//#else
		return net.rim.device.api.util.MathUtilities.wrap(low, value, high);
//#endif
	}
	
	/**
	 * Wraps provided double value around a lower and upper bound.
	 * <p>
	 * This method does the opposite of {@link MathUtilities#clamp(double, double, double)}.
	 * @param low Lower bound value; must be less than your upper bound parameter.
	 * @param value Value to wrap.
	 * @param high Upper bound value.
	 * @return If the value is lower than the lower bound, this method returns the higher bound; if the value is higher than the higher bound, this method returns 
	 * the lower bound; otherwise, this method returns the value itself.
	 * @throws IllegalArgumentException If you provide a low parameter value that is greater than your high parameter value.
	 * @since BBX 1.0.1
	 */
	public static double wrap(double low, double value, double high)
	{
//#ifdef BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1 | BlackBerrySDK5.0.0 | BlackBerrySDK6.0.0
		if(low > high)
		{
			throw new IllegalArgumentException("low > high");
		}
		if(value < low)
		{
			return high;
		}
		else if(value > high)
		{
			return low;
		}
		return value;
//#else
		return net.rim.device.api.util.MathUtilities.wrap(low, value, high);
//#endif
	}
}