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
 * Reference Byte is the same as {@link Byte} but allows you to set the byte without creating a new {@link Byte}.
 */
public final class RefByte extends RefNumber
{
	/**
	 * The maximum value a {@link RefByte} can have.
	 */
	public static final byte MAX_VALUE = Byte.MAX_VALUE;
	/**
	 * The minimum value a {@link RefByte} can have.
	 */
	public static final byte MIN_VALUE = Byte.MIN_VALUE;
	
	byte _val;
	
	/**
	 * Create a new {@link RefByte} set to the default of 0.
	 */
	public RefByte()
	{
		_val = 0;
	}
	
	/**
	 * Create a new {@link RefByte} using a byte primitive.
	 * @param value The byte primitive to set this {@link RefByte} with.
	 */
	public RefByte(byte value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefByte} using a byte primitive.
	 * @param value The byte primitive to set this {@link RefByte} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefByte(byte value, boolean fixed)
	{
		super(fixed);
		_val = value;
	}
	
	/**
	 * Create a new {@link RefByte} using a {@link Byte}.
	 * @param value The {@link Byte} to set this {@link RefByte} with.
	 */
	public RefByte(Byte value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefByte} using a {@link Byte}.
	 * @param value The {@link Byte} to set this {@link RefByte} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefByte(Byte value, boolean fixed)
	{
		this(value.byteValue(), fixed);
	}
	
	/**
	 * Returns The value of this {@link RefByte} object as a byte primitive.
	 * @return The primitive byte value of this object.
	 */
	public byte byteValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefByte} object with a byte primitive.
	 * @param value The primitive byte value to set this object.
	 * @return This object.
	 */
	public RefByte setValue(byte value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Set the value of this {@link RefByte} object with a {@link Byte}.
	 * @param value The {@link Byte} value to set this object.
	 * @return This object.
	 */
	public RefByte setValue(Byte value)
	{
		return setValue(value.byteValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefByte} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return new Byte(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefByte}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return new Byte(_val).toString();
	}
	
	/**
	 * Assuming the specified {@link String} represents a byte, returns that byte's value.
	 * @param s The {@link String} containing the byte.
	 * @return The parsed value of the byte.
	 * @throws NumberFormatException If the string does not contain a parsable byte.
	 */
	public static byte parseByte(String s)
	{
		return Byte.parseByte(s);
	}
	
	/**
	 * Assuming the specified {@link String} represents a byte, returns that byte's value.
	 * @param s The {@link String} containing the byte.
	 * @param radix The radix to be used.
	 * @return The parsed value of the byte.
	 * @throws NumberFormatException If the string does not contain a parsable byte.
	 */
	public static byte parseByte(String s, int radix)
	{
		return Byte.parseByte(s, radix);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefByte}.
	 */
	public RefByte clone()
	{
		return new RefByte(_val);
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefByte}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
