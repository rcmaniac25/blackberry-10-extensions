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
 * Reference Integer is the same as {@link Integer} but allows you to set the int without creating a new {@link RefInteger}.
 */
public final class RefInteger extends RefNumber
{
	/**
	 * The largest value of type int.
	 */
	public static final int MAX_VALUE = Integer.MAX_VALUE;
	/**
	 * The smallest value of type int.
	 */
	public static final int MIN_VALUE = Integer.MIN_VALUE;
	
	int _val;
	
	/**
	 * Create a new {@link RefInteger} set to the default of 0.
	 */
	public RefInteger()
	{
		_val = 0;
	}
	
	/**
	 * Create a new {@link RefInteger} using a int primitive.
	 * @param value The int primitive to set this {@link RefInteger} with.
	 */
	public RefInteger(int value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefInteger} using a int primitive.
	 * @param value The int primitive to set this {@link RefInteger} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefInteger(int value, boolean fixed)
	{
		super(fixed);
		_val = value;
	}
	
	/**
	 * Create a new {@link RefInteger} using a {@link Integer}.
	 * @param value The {@link Integer} to set this {@link RefInteger} with.
	 */
	public RefInteger(Integer value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefInteger} using a {@link Integer}.
	 * @param value The {@link Integer} to set this {@link RefInteger} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefInteger(Integer value, boolean fixed)
	{
		this(value.intValue(), fixed);
	}
	
	/**
	 * Returns Returns the value of this {@link RefInteger} as an int.
	 * @return The int value represented by this object.
	 */
	public int intValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefInteger} object with a int primitive.
	 * @param value The primitive int value to set this object.
	 * @return This object.
	 */
	public RefInteger setValue(int value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Set the value of this {@link RefInteger} object with a {@link Integer}.
	 * @param value The {@link Integer} value to set this object.
	 * @return This object.
	 */
	public RefInteger setValue(Integer value)
	{
		return setValue(value.intValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefInteger} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return _val;
		//return new Integer(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefInteger}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return Integer.toString(_val);
	}
	
	/**
	 * Returns the value of this {@link Integer} as a byte.
	 * @return Returns the value of this {@link Integer} as a byte.
	 */
	public byte byteValue()
	{
		return new Integer(_val).byteValue();
	}
	
	/**
	 *  Returns the value of this {@link Integer} as a double.
	 * @return The int value represented by this object is converted to type double and the result of the conversion is returned.
	 */
	public double doubleValue()
	{
		return new Integer(_val).doubleValue();
	}
	
	/**
	 * Returns the value of this {@link Integer} as a float.
	 * @return The int value represented by this object is converted to type float and the result of the conversion is returned.
	 */
	public float floatValue()
	{
		return new Integer(_val).floatValue();
	}
	
	/**
	 * Returns the value of this {@link Integer} as a long.
	 * @return The int value represented by this object that is converted to type long and the result of the conversion is returned.
	 */
	public long longValue()
	{
		return new Integer(_val).longValue();
	}
	
	/**
	 * Returns the value of this {@link Integer} as a short.
	 * @return The int value represented by this object that is converted to type short and the result of the conversion is returned.
	 */
	public short shortValue()
	{
		return new Integer(_val).shortValue();
	}
	
	/**
	 * Parses the string argument as a signed decimal integer.
	 * @param s A string.
	 * @return The integer represented by the argument in decimal.
	 * @throws NumberFormatException If the string does not contain a parsable integer.
	 */
	public static int parseInt(String s)
	{
		return Integer.parseInt(s);
	}
	
	/**
	 * Parses the string argument as a signed integer in the radix specified by the second argument.
	 * @param s The {@link String} containing the integer.
	 * @param radix The radix to be used.
	 * @return The integer represented by the string argument in the specified radix.
	 * @throws NumberFormatException If the string does not contain a parsable integer.
	 */
	public static int parseInt(String s, int radix)
	{
		return Integer.parseInt(s, radix);
	}
	
	/**
	 * Creates a string representation of the integer argument as an unsigned integer in base 2.
	 * @param i An integer.
	 * @return The string representation of the unsigned integer value represented by the argument in binary (base 2).
	 */
	public static String toBinaryString(int i)
	{
		return Integer.toBinaryString(i);
	}
	
	/**
	 * Creates a string representation of the integer argument as an unsigned integer in base 16.
	 * @param i An integer.
	 * @return The string representation of the unsigned integer value represented by the argument in hexadecimal (base 16).
	 */
	public static String toHexString(int i)
	{
		return Integer.toHexString(i);
	}
	
	/**
	 * Creates a string representation of the integer argument as an unsigned integer in base 8.
	 * @param i An integer.
	 * @return The string representation of the unsigned integer value represented by the argument in octal (base 8).
	 */
	public static String toOctalString(int i)
	{
		return Integer.toOctalString(i);
	}
	
	/**
	 * Returns A new {@link String} object representing the specified integer.
	 * @param i An integer to be converted.
	 * @return A string representation of the argument in base 10.
	 */
	public static String toString(int i)
	{
		return Integer.toString(i);
	}
	
	/**
	 * Creates a string representation of the first argument in the radix specified by the second argument.
	 * @param i An integer.
	 * @param radix The radix.
	 * @return A string representation of the argument in the specified radix.
	 */
	public static String toString(int i, int radix)
	{
		return Integer.toString(i, radix);
	}
	
	/**
	 * Returns a new {@link RefInteger} object initialized to the value of the specified {@link String}.
	 * @param s The string to be parsed.
	 * @return A newly constructed {@link RefInteger} initialized to the value represented by the string argument.
	 * @throws NumberFormatException If the string cannot be parsed as an integer.
	 */
	public static RefInteger valueOf(String s)
	{
		return new RefInteger(Integer.parseInt(s));
	}
	
	/**
	 * Returns a new {@link RefInteger} object initialized to the value of the specified {@link String}.
	 * @param s The string to be parsed.
	 * @param radix The radix of the integer represented by string s.
	 * @return A newly constructed {@link RefInteger} initialized to the value represented by the string argument in the specified radix.
	 * @throws NumberFormatException If the {@link String} cannot be parsed as an int.
	 */
	public static RefInteger valueOf(String s, int radix)
	{
		return new RefInteger(Integer.parseInt(s, radix));
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefInteger}.
	 */
	public RefInteger clone()
	{
		return new RefInteger(_val);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefInteger}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
