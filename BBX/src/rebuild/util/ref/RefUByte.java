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
import rebuild.util.StringUtilities;

/**
 * Reference UByte is the same as {@link Byte} but is unsigned and allows you to set the byte without creating a new {@link Byte}.
 */
public final class RefUByte extends RefNumber
{
	/**
	 * The maximum value a {@link RefUByte} can have. This is stored as a signed number for simplicity.
	 */
	public static final byte MAX_VALUE_BYTE = (byte)0xFF;
	/**
	 * The minimum value a {@link RefUByte} can have. This is stored as a signed number for simplicity.
	 */
	public static final byte MIN_VALUE_BYTE = 0x00;
	
	/**
	 * The maximum value a {@link RefUByte} can have.
	 */
	public static final RefUByte MAX_VALUE = new RefUByte(MAX_VALUE_BYTE, true);
	/**
	 * The minimum value a {@link RefUByte} can have.
	 */
	public static final RefUByte MIN_VALUE = new RefUByte(MIN_VALUE_BYTE, true);
	
	short _val;
	
	/**
	 * Create a new {@link RefByte} set to the default of 0.
	 */
	public RefUByte()
	{
		_val = 0;
	}
	
	/**
	 * Create a new {@link RefUByte} using a byte primitive.
	 * @param value The byte primitive to set this {@link RefUByte} with.
	 */
	public RefUByte(byte value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefUByte} using a byte primitive.
	 * @param value The byte primitive to set this {@link RefUByte} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefUByte(byte value, boolean fixed)
	{
		super(fixed);
		if (value != 0)
        {
            if (value < 0)
            {
                this._val = (short)(value & 0xFF);
            }
            else
            {
                this._val = value;
            }
        }
	}
	
	/**
	 * Returns The value of this {@link RefUByte} object as a byte primitive.
	 * @return The primitive byte value of this object.
	 */
	public byte byteValue()
	{
		byte value = (byte)(_val & 0x7F);
        return ((_val & 0x80) == 0x80) ? ((byte)(Byte.MIN_VALUE + value)) : value;
	}
	
	/**
	 * Returns The value of this {@link RefUByte} object as a short primitive.
	 * @return The primitive short value of this object. This is the only way to return the value unsigned.
	 */
	public short ubyteValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefUByte} object with a byte primitive.
	 * @param value The primitive byte value to set this object.
	 * @return This object.
	 */
	public RefUByte setValue(byte value)
	{
		if (value < 0)
        {
            this._val = (short)(value & 0xFF);
        }
        else
        {
            this._val = value;
        }
		return this;
	}
	
	/**
	 * Set the value of this {@link RefUByte} object with a {@link Byte}.
	 * @param value The {@link Byte} value to set this object.
	 * @return This object.
	 */
	public RefUByte setValue(Byte value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		return setValue(value.byteValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefUByte} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return new Short(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefUByte}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return new Short(_val).toString();
	}
	
	/**
	 * Assuming the specified {@link String} represents a byte, returns that byte's value.
	 * @param s The {@link String} containing the byte.
	 * @return The parsed value of the byte.
	 * @throws NumberFormatException If the string does not contain a parsable byte.
	 */
	public static RefUByte parseUByte(String s)
	{
		return parseUByte(s, 10);
	}
	
	/**
	 * Assuming the specified {@link String} represents a byte, returns that byte's value.
	 * @param s The {@link String} containing the byte.
	 * @param radix The radix to be used.
	 * @return The parsed value of the byte.
	 * @throws NumberFormatException If the string does not contain a parsable byte.
	 */
	public static RefUByte parseUByte(String s, int radix)
	{
		short sh = Short.parseShort(s, radix);
		if(sh < 0 || sh > 0xFF)
		{
			throw new NumberFormatException(StringUtilities.format(Resources.getString(BBXResource.UNSIGNED_NUMBER_UNPARSEABLE), "byte"));
		}
		RefUByte clone = new RefUByte();
		clone._val = sh;
		return clone;
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefUByte}.
	 */
	public RefUByte clone()
	{
		RefUByte clone = new RefUByte();
		clone._val = this._val;
		return clone;
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefUByte}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
