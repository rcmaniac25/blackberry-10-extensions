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

import rebuild.BBXResource;
import rebuild.Resources;
import rebuild.util.text.StringUtilities;

/**
 * Reference UInteger is the same as {@link Integer} but is unsigned and allows you to set the int without creating a new {@link Integer}.
 * @since BBX 1.1.0
 */
public final class RefUInteger extends RefNumber
{
	/**
	 * The maximum value a {@link RefUInteger} can have. This is stored as a signed number for simplicity.
	 */
	public static final int MAX_VALUE_INT = 0xFFFFFFFF;
	/**
	 * The minimum value a {@link RefUInteger} can have. This is stored as a signed number for simplicity.
	 */
	public static final int MIN_VALUE_INT = 0x00000000;
	
	/**
	 * The maximum value a {@link RefUInteger} can have.
	 */
	public static final RefUInteger MAX_VALUE = new RefUInteger(MAX_VALUE_INT, true);
	/**
	 * The minimum value a {@link RefUInteger} can have.
	 */
	public static final RefUInteger MIN_VALUE = new RefUInteger(MIN_VALUE_INT, true);
	
	long _val;
	
	/**
	 * Create a new {@link RefUInteger} set to the default of 0.
	 */
	public RefUInteger()
	{
		_val = 0;
	}
	
	/**
	 * Create a new {@link RefUInteger} using a int primitive.
	 * @param value The int primitive to set this {@link RefUInteger} with.
	 */
	public RefUInteger(int value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefUInteger} using a int primitive.
	 * @param value The int primitive to set this {@link RefUInteger} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefUInteger(int value, boolean fixed)
	{
		super(fixed);
		if (value != 0)
        {
            if (value < 0)
            {
                this._val = value & 0xFFFFFFFF;
            }
            else
            {
                this._val = value;
            }
        }
	}
	
	/**
	 * Returns The value of this {@link RefUInteger} object as a int primitive.
	 * @return The primitive int value of this object.
	 */
	public int intValue()
	{
		int value = (int)(_val & 0x7FFFFFFF);
        return ((_val & 0x80000000) == 0x80000000) ? (Integer.MIN_VALUE + value) : value;
	}
	
	/**
	 * Returns The value of this {@link RefUInteger} object as a long primitive.
	 * @return The primitive long value of this object. This is the only way to return the value unsigned.
	 */
	public long uintValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefUInteger} object with a int primitive.
	 * @param value The primitive int value to set this object.
	 * @return This object.
	 */
	public RefUInteger setValue(int value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		if (value < 0)
        {
            this._val = value & 0xFFFFFFFF;
        }
        else
        {
            this._val = value;
        }
		return this;
	}
	
	/**
	 * Set the value of this {@link RefUInteger} object with a {@link Integer}.
	 * @param value The {@link Integer} value to set this object.
	 * @return This object.
	 */
	public RefUInteger setValue(Integer value)
	{
		return setValue(value.intValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefUInteger} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return new Long(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefUInteger}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return new Long(_val).toString();
	}
	
	/**
	 * Assuming the specified {@link String} represents a int, returns that int's value.
	 * @param s The {@link String} containing the int.
	 * @return The parsed value of the int.
	 * @throws NumberFormatException If the string does not contain a parsable int.
	 */
	public static RefUInteger parseUInteger(String s)
	{
		return parseUInteger(s, 10);
	}
	
	/**
	 * Assuming the specified {@link String} represents a int, returns that int's value.
	 * @param s The {@link String} containing the int.
	 * @param radix The radix to be used.
	 * @return The parsed value of the int.
	 * @throws NumberFormatException If the string does not contain a parsable int.
	 */
	public static RefUInteger parseUInteger(String s, int radix)
	{
		long sh = Long.parseLong(s, radix);
		if(sh < 0 || sh > 0xFFFFFFFF)
		{
			throw new NumberFormatException(StringUtilities.format_java(Resources.getString(BBXResource.UNSIGNED_NUMBER_UNPARSEABLE), "int"));
		}
		RefUInteger clone = new RefUInteger();
		clone._val = sh;
		return clone;
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefUInteger}.
	 */
	public RefUInteger clone()
	{
		RefUInteger clone = new RefUInteger();
		clone._val = this._val;
		return clone;
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefUInteger}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
