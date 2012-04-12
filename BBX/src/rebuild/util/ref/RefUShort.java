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
 * Reference UShort is the same as {@link Short} but is unsigned and allows you to set the short without creating a new {@link Short}.
 * @since BBX 1.1.0
 */
public final class RefUShort extends RefNumber
{
	/**
	 * The maximum value a {@link RefUShort} can have. This is stored as a signed number for simplicity.
	 */
	public static final short MAX_VALUE_SHORT = (short)0xFFFF;
	/**
	 * The minimum value a {@link RefUShort} can have. This is stored as a signed number for simplicity.
	 */
	public static final short MIN_VALUE_SHORT = 0x0000;
	
	/**
	 * The maximum value a {@link RefUShort} can have.
	 */
	public static final RefUShort MAX_VALUE = new RefUShort(MAX_VALUE_SHORT, true);
	/**
	 * The minimum value a {@link RefUShort} can have.
	 */
	public static final RefUShort MIN_VALUE = new RefUShort(MIN_VALUE_SHORT, true);
	
	int _val;
	
	/**
	 * Create a new {@link RefUShort} set to the default of 0.
	 */
	public RefUShort()
	{
		_val = 0;
	}
	
	/**
	 * Create a new {@link RefUShort} using a short primitive.
	 * @param value The short primitive to set this {@link RefUShort} with.
	 */
	public RefUShort(short value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefUShort} using a short primitive.
	 * @param value The short primitive to set this {@link RefUShort} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefUShort(short value, boolean fixed)
	{
		super(fixed);
		if (value != 0)
        {
            if (value < 0)
            {
                this._val = value & 0xFFFF;
            }
            else
            {
                this._val = value;
            }
        }
	}
	
	/**
	 * Returns The value of this {@link RefUShort} object as a short primitive.
	 * @return The primitive short value of this object.
	 */
	public short shortValue()
	{
		short value = (short)(_val & 0x7FFF);
        return ((_val & 0x8000) == 0x8000) ? ((short)(Short.MIN_VALUE + value)) : value;
	}
	
	/**
	 * Returns The value of this {@link RefUShort} object as a int primitive.
	 * @return The primitive int value of this object. This is the only way to return the value unsigned.
	 */
	public int ushortValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefUShort} object with a short primitive.
	 * @param value The primitive short value to set this object.
	 * @return This object.
	 */
	public RefUShort setValue(short value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		if (value < 0)
        {
            this._val = value & 0xFFFF;
        }
        else
        {
            this._val = value;
        }
		return this;
	}
	
	/**
	 * Set the value of this {@link RefUShort} object with a {@link Short}.
	 * @param value The {@link Short} value to set this object.
	 * @return This object.
	 */
	public RefUShort setValue(Short value)
	{
		return setValue(value.shortValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefUShort} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return new Integer(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefUShort}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return new Integer(_val).toString();
	}
	
	/**
	 * Assuming the specified {@link String} represents a short, returns that short's value.
	 * @param s The {@link String} containing the short.
	 * @return The parsed value of the short.
	 * @throws NumberFormatException If the string does not contain a parsable short.
	 */
	public static RefUShort parseUShort(String s)
	{
		return parseUShort(s, 10);
	}
	
	/**
	 * Assuming the specified {@link String} represents a short, returns that short's value.
	 * @param s The {@link String} containing the short.
	 * @param radix The radix to be used.
	 * @return The parsed value of the short.
	 * @throws NumberFormatException If the string does not contain a parsable short.
	 */
	public static RefUShort parseUShort(String s, int radix)
	{
		int sh = Integer.parseInt(s, radix);
		if(sh < 0 || sh > 0xFFFF)
		{
			throw new NumberFormatException(StringUtilities.format_java(Resources.getString(BBXResource.UNSIGNED_NUMBER_UNPARSEABLE), "short"));
		}
		RefUShort clone = new RefUShort();
		clone._val = sh;
		return clone;
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefUShort}.
	 */
	public RefUShort clone()
	{
		RefUShort clone = new RefUShort();
		clone._val = this._val;
		return clone;
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefUShort}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
