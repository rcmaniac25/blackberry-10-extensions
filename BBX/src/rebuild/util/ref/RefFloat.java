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
 * Reference Float is the same as {@link Float} but allows you to set the float without creating a new {@link RefFloat}.
 * @since BBX 1.1.0
 */
public final class RefFloat extends RefNumber
{
	/**
	 * The largest positive finite value of type float.
	 */
	public static final float MAX_VALUE = Float.MAX_VALUE;
	/**
	 * The smallest positive value of type float.
	 */
	public static final float MIN_VALUE = Float.MIN_VALUE;
	/**
	 * A Not-a-Number (NaN) value of type float.
	 */
	public static final float NaN = Float.NaN;
	/**
	 * The negative infinity of type float.
	 */
	public static final float NEGATIVE_INFINITY = Float.NEGATIVE_INFINITY;
	/**
	 * The positive infinity of type float.
	 */
	public static final float POSITIVE_INFINITY = Float.POSITIVE_INFINITY;
	
	float _val;
	
	/**
	 * Create a new {@link RefFloat} set to the default of 0f.
	 */
	public RefFloat()
	{
		_val = 0f;
	}
	
	/**
	 * Create a new {@link RefFloat} using a float primitive.
	 * @param value The float primitive to set this {@link RefFloat} with.
	 */
	public RefFloat(float value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefFloat} using a float primitive.
	 * @param value The float primitive to set this {@link RefFloat} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefFloat(float value, boolean fixed)
	{
		super(fixed);
		_val = value;
	}
	
	/**
	 * Create a new {@link RefFloat} using a {@link Float}.
	 * @param value The {@link Float} to set this {@link RefFloat} with.
	 */
	public RefFloat(Float value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefFloat} using a {@link Float}.
	 * @param value The {@link Float} to set this {@link RefFloat} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefFloat(Float value, boolean fixed)
	{
		this(value.floatValue(), fixed);
	}
	
	/**
	 * Returns The value of this {@link RefFloat} object as a double primitive.
	 * @return The float value represented by this object is converted to type double and the result of the conversion is returned.
	 */
	public double doubleValue()
	{
		return new Float(_val).doubleValue();
	}
	
	/**
	 * Set the value of this {@link RefFloat} object with a float primitive.
	 * @param value The primitive float value to set this object.
	 * @return This object.
	 */
	public RefFloat setValue(float value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Set the value of this {@link RefFloat} object with a {@link Float}.
	 * @param value The {@link Float} value to set this object.
	 * @return This object.
	 */
	public RefFloat setValue(Float value)
	{
		return setValue(value.floatValue());
	}
	
	/**
	 * Returns a hash code for this {@link RefFloat} object.
	 * @return A hash code value for this object.
	 */
	public int hashCode()
	{
		return Float.floatToIntBits(_val);
		//return new Float(_val).hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefFloat}'s value.
	 * @return A string representation of the object.
	 */
	public String toString()
	{
		return Float.toString(_val);
	}
	
	/**
	 * Returns the value of this {@link RefFloat} as a byte (by casting to a byte).
	 * @return This {@link RefFloat} as a byte.
	 */
	public byte byteValue()
	{
		return new Float(_val).byteValue();
	}
	
	/**
	 * Returns the bit representation of a single-float value.
	 * @param value A floating-point number.
	 * @return The bits that represent the floating-point number.
	 */
	public static int floatToIntBits(float value)
	{
		return Float.floatToIntBits(value);
	}
	
	/**
	 * Returns the float value of this {@link RefFloat}.
	 * @return The float value represented by this object.
	 */
	public float floatValue()
	{
		return _val;
	}
	
	/**
	 * Returns the integer value of this {@link RefFloat} (by casting to an int).
	 * @return The float value represented by this object is converted to type int and the result of the conversion is returned.
	 */
	public int intValue()
	{
		return new Float(_val).intValue();
	}
	
	/**
	 * Returns true if this {@link RefFloat} value is infinitely large in magnitude.
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
	public static boolean isInfinite(float v)
	{
		return Float.isInfinite(v);
	}
	
	/**
	 * Returns true if this {@link RefFloat} value is the special Not-a-Number ({@link NaN}) value.
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
	public static boolean isNaN(float v)
	{
		return Float.isNaN(v);
	}
	
	/**
	 * Returns the single-float corresponding to a given bit representation.
	 * @param bits An integer.
	 * @return The single-format floating-point value with the same bit pattern.
	 */
	public static float intBitsToFloat(int bits)
	{
		return Float.intBitsToFloat(bits);
	}
	
	/**
	 * Returns the long value of this {@link RefFloat} (by casting to a long).
	 * @return The float value represented by this object is converted to type long and the result of the conversion is returned.
	 */
	public long longValue()
	{
		return new Float(_val).longValue();
	}
	
	/**
	 * Returns a new float initialized to the value represented by the specified {@link String}.
	 * @param s The string to be parsed.
	 * @return The float value represented by the string argument.
	 * @throws NumberFormatException If the string does not contain a parsable float.
	 */
	public static float parseFloat(String s)
	{
		return Float.parseFloat(s);
	}
	
	/**
	 * Returns the value of this {@link RefFloat} as a short (by casting to a short).
	 * @return The {@link RefFloat} cast to a short.
	 */
	public short shortValue()
	{
		return new Float(_val).shortValue();
	}
	
	/**
	 * Returns a {@link String} representation for the specified float value.
	 * @param d The float to be converted.
	 * @return A string representation of the argument.
	 */
	public static String toString(float d)
	{
		return Float.toString(d);
	}
	
	/**
	 * Returns the floating point value represented by the specified {@link String}.
	 * @param s The string to be parsed.
	 * @return A newly constructed {@link RefFloat} initialized to the value represented by the {@link String} argument.
	 * @throws NumberFormatException If the string does not contain a parsable number.
	 */
	public static RefFloat valueOf(String s)
	{
		return new RefFloat(Float.parseFloat(s));
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefFloat}.
	 */
	public RefFloat clone()
	{
		return new RefFloat(_val);
	}

	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefFloat}.
	 */
	public RefNumber cloneNumber()
	{
		return clone();
	}
}
