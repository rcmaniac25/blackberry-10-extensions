//---------------------------------------------------------------------------------
//
// BlackBerry Extensions
// Copyright (c) 2008-2012 Vincent Simonetti
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
// Created 2009
package rebuild.util.ref;

/**
 * Reference Double is the same as {@link Double} but allows you to set the double without creating a new {@link Double}.
 * @since BBX 1.1.0
 */
public final class RefDouble extends RefNumber
{
	/**
	 * The largest positive finite value of type double.
	 */
	public static final double MAX_VALUE = Double.MAX_VALUE;
	/**
	 * The smallest positive value of type double.
	 */
	public static final double MIN_VALUE = Double.MIN_VALUE;
	/**
	 * A Not-a-Number (NaN) value of type double.
	 */
	public static final double NaN = Double.NaN;
	/**
	 * The negative infinity of type double.
	 */
	public static final double NEGATIVE_INFINITY = Double.NEGATIVE_INFINITY;
	/**
	 * The positive infinity of type double.
	 */
	public static final double POSITIVE_INFINITY = Double.POSITIVE_INFINITY;
	
	double _val;
	
	/**
	 * Create a new {@link RefDouble} set to the default of 0.0.
	 */
	public RefDouble()
	{
		_val = 0.0;
	}
	
	/**
	 * Create a new {@link RefDouble} using a double primitive.
	 * @param value The double primitive to set this {@link RefDouble} with.
	 */
	public RefDouble(double value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefDouble} using a double primitive.
	 * @param value The double primitive to set this {@link RefDouble} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefDouble(double value, boolean fixed)
	{
		super(fixed);
		_val = value;
	}
	
	/**
	 * Create a new {@link RefDouble} using a {@link Double}.
	 * @param value The {@link Double} to set this {@link RefDouble} with.
	 */
	public RefDouble(Double value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefDouble} using a {@link Double}.
	 * @param value The {@link Double} to set this {@link RefDouble} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefDouble(Double value, boolean fixed)
	{
		this(value.doubleValue(), fixed);
	}
	
	/**
	 * Returns The value of this {@link RefDouble} object as a double primitive.
	 * @return The primitive double value of this object.
	 */
	public double doubleValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefDouble} object with a double primitive.
	 * @param value The primitive double value to set this object.
	 * @return This object.
	 */
	public RefDouble setValue(double value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Set the value of this {@link RefDouble} object with a {@link Double}.
	 * @param value The {@link Double} value to set this object.
	 * @return This object.
	 */
	public RefDouble setValue(Double value)
	{
		return setValue(value.doubleValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefDouble} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		long v = Double.doubleToLongBits(_val);
		return (int)(v ^ (v >>> 32));
		//return new Double(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefDouble}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return Double.toString(_val);
	}
	
	/**
	 * Returns the value of this {@link RefDouble} as a byte (by casting to a byte).
	 * @return This {@link RefDouble} as a byte.
	 */
	public byte byteValue()
	{
		return new Double(_val).byteValue();
	}
	
	/**
	 * Returns a representation of the specified floating-point value according to the IEEE 754 floating-point "double format" bit layout.
	 * @param value A double precision floating-point number.
	 * @return The bits that represent the floating-point number.
	 */
	public static long doubleToLongBits(double value)
	{
		return Double.doubleToLongBits(value);
	}
	
	/**
	 * Returns the float value of this {@link RefDouble}.
	 * @return The double value represented by this object is converted to type float and the result of the conversion is returned.
	 */
	public float floatValue()
	{
		return new Double(_val).floatValue();
	}
	
	/**
	 * Returns the integer value of this {@link RefDouble} (by casting to an int).
	 * @return The double value represented by this object is converted to type int and the result of the conversion is returned.
	 */
	public int intValue()
	{
		return new Double(_val).intValue();
	}
	
	/**
	 * Returns true if this {@link RefDouble} value is infinitely large in magnitude.
	 * @return true if the value represented by this object is positive infinity or negative infinity; false otherwise.
	 */
	public boolean isInfinite()
	{
		return isInfinite(_val);
	}
	
	/**
	 * Returns true if the specified number is infinitely large in magnitude.
	 * @param v The value to be tested.
	 * @return true if the value of the argument is positive infinity or negative infinity; false otherwise.
	 */
	public static boolean isInfinite(double v)
	{
		return Double.isInfinite(v);
	}
	
	/**
	 * Returns true if this {@link RefDouble} value is the special Not-a-Number ({@link NaN}) value.
	 * @return true if the value represented by this object is {@link NaN}; false otherwise.
	 */
	public boolean isNaN()
	{
		return isNaN(_val);
	}
	
	/**
	 * Returns true if the specified number is the special Not-a-Number ({@link NaN}) value.
	 * @param v The value to be tested.
	 * @return true if the value of the argument is {@link NaN}; false otherwise.
	 */
	public static boolean isNaN(double v)
	{
		return Double.isNaN(v);
	}
	
	/**
	 * Returns the double-float corresponding to a given bit representation.
	 * @param bits Any long integer.
	 * @return The double floating-point value with the same bit pattern.
	 */
	public static double longBitsToDouble(long bits)
	{
		return Double.longBitsToDouble(bits);
	}
	
	/**
	 * Returns the long value of this {@link RefDouble} (by casting to a long).
	 * @return The double value represented by this object is converted to type long and the result of the conversion is returned.
	 */
	public long longValue()
	{
		return new Double(_val).longValue();
	}
	
	/**
	 * Returns a new double initialized to the value represented by the specified {@link String}, as performed by the {@link valueOf} method of class {@link RefDouble}.
	 * @param s The string to be parsed.
	 * @return The double value represented by the string argument.
	 * @throws NumberFormatException If the string does not contain a parsable double.
	 */
	public static double parseDouble(String s)
	{
		return Double.parseDouble(s);
	}
	
	/**
	 * Returns the value of this {@link RefDouble} as a short (by casting to a short).
	 * @return The {@link RefDouble} cast to a short.
	 */
	public short shortValue()
	{
		return new Double(_val).shortValue();
	}
	
	/**
	 * Creates a string representation of the double argument.
	 * @param d The double to be converted.
	 * @return A string representation of the argument.
	 */
	public static String toString(double d)
	{
		return Double.toString(d);
	}
	
	/**
	 * Returns a new {@link RefDouble} object initialized to the value represented by the specified string.
	 * @param s The string to be parsed.
	 * @return A newly constructed {@link RefDouble} initialized to the value represented by the string argument.
	 * @throws NumberFormatException If the string does not contain a parsable number.
	 */
	public static RefDouble valueOf(String s)
	{
		return new RefDouble(Double.parseDouble(s));
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefDouble}.
	 */
	public RefDouble clone()
	{
		return new RefDouble(_val);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefDouble}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
