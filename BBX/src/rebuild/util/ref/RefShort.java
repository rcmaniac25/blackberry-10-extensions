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
 * Reference Short is the same as {@link Short} but allows you to set the short without creating a new {@link RefShort}.
 */
public final class RefShort extends RefNumber
{
	/**
	 * The largest value of type short.
	 */
	public static final short MAX_VALUE = Short.MAX_VALUE;
	/**
	 * The smallest value of type short.
	 */
	public static final short MIN_VALUE = Short.MIN_VALUE;
	
	short _val;
	
	/**
	 * Create a new {@link RefShort} set to the default of 0.
	 */
	public RefShort()
	{
		_val = 0;
	}
	
	/**
	 * Create a new {@link RefShort} using a short primitive.
	 * @param value The short primitive to set this {@link RefShort} with.
	 */
	public RefShort(short value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefShort} using a short primitive.
	 * @param value The short primitive to set this {@link RefShort} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefShort(short value, boolean fixed)
	{
		super(fixed);
		_val = value;
	}
	
	/**
	 * Create a new {@link RefShort} using a {@link Short}.
	 * @param value The {@link Short} to set this {@link RefShort} with.
	 */
	public RefShort(Short value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefShort} using a {@link Short}.
	 * @param value The {@link Short} to set this {@link RefShort} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefShort(Short value, boolean fixed)
	{
		this(value.shortValue(), fixed);
	}
	
	/**
	 * Returns Returns the value of this {@link RefShort} as an short.
	 * @return The short value represented by this object.
	 */
	public short shortValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefShort} object with a short primitive.
	 * @param value The primitive short value to set this object.
	 * @return This object.
	 */
	public RefShort setValue(short value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Set the value of this {@link RefShort} object with a {@link Short}.
	 * @param value The {@link Short} value to set this object.
	 * @return This object.
	 */
	public RefShort setValue(Short value)
	{
		return setValue(value.shortValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefShort} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return new Short(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefShort}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return new Short(_val).toString();
	}
	
	/**
	 * Assuming the specified {@link String} represents a short, returns that short's value.
	 * @param s The {@link String} containing the short.
	 * @return short the value represented by the specified string.
	 * @throws NumberFormatException If the string does not contain a parsable short.
	 */
	public static short parseShort(String s)
	{
		return Short.parseShort(s);
	}
	
	/**
	 * Assuming the specified String represents a short, returns that short's value.
	 * @param s The {@link String} containing the short.
	 * @param radix The radix to be used.
	 * @return The short value represented by the specified string in the specified radix.
	 * @throws NumberFormatException If the String does not contain a parsable short.
	 */
	public static short parseShort(String s, int radix)
	{
		return Short.parseShort(s, radix);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefShort}.
	 */
	public RefShort clone()
	{
		return new RefShort(_val);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefShort}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
