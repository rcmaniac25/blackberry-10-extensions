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
 * Reference Long is the same as {@link Long} but allows you to set the long without creating a new {@link RefLong}.
 * @since BBX 1.1.0
 */
public final class RefLong extends RefNumber
{
	/**
	 * The largest value of type long.
	 */
	public static final long MAX_VALUE = Long.MAX_VALUE;
	/**
	 * The smallest value of type long.
	 */
	public static final long MIN_VALUE = Long.MIN_VALUE;
	
	long _val;
	
	/**
	 * Create a new {@link RefLong} set to the default of 0L.
	 */
	public RefLong()
	{
		_val = 0L;
	}
	
	/**
	 * Create a new {@link RefLong} using a long primitive.
	 * @param value The long primitive to set this {@link RefLong} with.
	 */
	public RefLong(long value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefLong} using a long primitive.
	 * @param value The long primitive to set this {@link RefLong} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefLong(long value, boolean fixed)
	{
		super(fixed);
		_val = value;
	}
	
	/**
	 * Create a new {@link RefLong} using a {@link Long}.
	 * @param value The {@link Long} to set this {@link RefLong} with.
	 */
	public RefLong(Long value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefLong} using a {@link Long}.
	 * @param value The {@link Long} to set this {@link RefLong} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefLong(Long value, boolean fixed)
	{
		this(value.longValue(), fixed);
	}
	
	/**
	 * Returns Returns the value of this {@link RefLong} as an long.
	 * @return The long value represented by this object.
	 */
	public long longValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefLong} object with a long primitive.
	 * @param value The primitive long value to set this object.
	 * @return This object.
	 */
	public RefLong setValue(long value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Set the value of this {@link RefLong} object with a {@link Long}.
	 * @param value The {@link Long} value to set this object.
	 * @return This object.
	 */
	public RefLong setValue(Long value)
	{
		return setValue(value.longValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefLong} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return (int)(_val ^ (_val >>> 32));
		//return new Long(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefLong}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return Long.toString(_val);
	}
	
	/**
	 * Returns the value of this {@link RefLong} as a double.
	 * @return The long value represented by this object that is converted to type double and the result of the conversion is returned.
	 */
	public double doubleValue()
	{
		return new Long(_val).doubleValue();
	}
	
	/**
	 * Returns the value of this {@link RefLong} as a float.
	 * @return The long value represented by this object is converted to type float and the result of the conversion is returned.
	 */
	public float floatValue()
	{
		return new Long(_val).floatValue();
	}
	
	/**
	 * Parses the string argument as a signed decimal long.
	 * @param s A string.
	 * @return The long represented by the argument in decimal.
	 * @throws NumberFormatException If the string does not contain a parsable long.
	 */
	public static long parseLong(String s)
	{
		return Long.parseLong(s);
	}
	
	/**
	 * Parses the string argument as a signed long in the radix specified by the second argument.
	 * @param s The {@link String} containing the long.
	 * @param radix The radix to be used.
	 * @return The long represented by the string argument in the specified radix.
	 * @throws NumberFormatException If the string does not contain a parsable integer.
	 */
	public static long parseLong(String s, int radix)
	{
		return Long.parseLong(s, radix);
	}
	
	/**
	 * Returns a new {@link String} object representing the specified integer.
	 * @param i A long to be converted.
	 * @return A string representation of the argument in base 10.
	 */
	public static String toString(long i)
	{
		return Long.toString(i);
	}
	
	/**
	 * Creates a string representation of the first argument in the radix specified by the second argument.
	 * @param i A long.
	 * @param radix The radix.
	 * @return A string representation of the argument in the specified radix.
	 */
	public static String toString(long i, int radix)
	{
		return Long.toString(i, radix);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefLong}.
	 */
	public RefLong clone()
	{
		return new RefLong(_val);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefLong}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
