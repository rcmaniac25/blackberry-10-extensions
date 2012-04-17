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
 * Reference Boolean is the same as {@link Boolean} but allows you to set the boolean without creating a new {@link Boolean}.
 * @since BBX 1.1.0
 */
public final class RefBoolean
{
	/**
	 * The {@link RefBoolean} object corresponding to the primitive value false.
	 */
	public static final RefBoolean FALSE = new RefBoolean(false, true);
	/**
	 * The {@link RefBoolean} object corresponding to the primitive value true.
	 */
	public static final RefBoolean TRUE = new RefBoolean(true, true);
	
	private boolean _fixed;
	boolean _val;
	
	/**
	 * Create a new {@link RefBoolean} set to the default of false.
	 */
	public RefBoolean()
	{
		_fixed = false;
		_val = false;
	}
	
	/**
	 * Create a new {@link RefBoolean} using a boolean primitive.
	 * @param value The boolean primitive to set this {@link RefBoolean} with.
	 */
	public RefBoolean(boolean value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefBoolean} using a boolean primitive.
	 * @param value The boolean primitive to set this {@link RefBoolean} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefBoolean(boolean value, boolean fixed)
	{
		_fixed = fixed;
		_val = value;
	}
	
	/**
	 * Create a new {@link RefBoolean} using a {@link Boolean}.
	 * @param value The {@link Boolean} to set this {@link RefBoolean} with.
	 */
	public RefBoolean(Boolean value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefBoolean} using a {@link Boolean}.
	 * @param value The {@link Boolean} to set this {@link RefBoolean} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefBoolean(Boolean value, boolean fixed)
	{
		_fixed = fixed;
		_val = value.booleanValue();
	}
	
	/**
	 * If this {@link RefBoolean} is read only.
	 * @return <code>true</code> if this {@link RefBoolean} is read only, <code>false</code> if otherwise.
	 */
	public boolean isReadOnly()
	{
		return _fixed;
	}
	
	/**
	 * Returns The value of this {@link RefBoolean} object as a boolean primitive.
	 * @return The primitive boolean value of this object.
	 */
	public boolean booleanValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefBoolean} object with a boolean primitive.
	 * @param value The primitive boolean value to set this object.
	 * @return This object.
	 */
	public RefBoolean setValue(boolean value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Set the value of this {@link RefBoolean} object with a {@link Boolean}.
	 * @param value The {@link Boolean} value to set this object.
	 * @return This object.
	 */
	public RefBoolean setValue(Boolean value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value.booleanValue();
		return this;
	}
	
	/**
	 * Returns true if and only if the argument is not null and is a {@link RefBoolean} or {@link Boolean} object that represents the same boolean value as this object.
	 * @param obj The object to compare with.
	 * @return true if the {@link RefBoolean} objects represent the same value; false otherwise.
	 */
	public boolean equals(Object obj)
	{
		if((obj != null) && ((obj instanceof RefBoolean) || (obj instanceof Boolean)))
		{
			if(obj instanceof Boolean)
			{
				return ((Boolean)obj).booleanValue() == this._val;
			}
			else
			{
				return ((RefBoolean)obj)._val == this._val;
			}
		}
		return false;
	}
	
	/**
	 * Returns a hash code for this {@link RefBoolean} object.
	 * @return The integer 1231 if this object represents true; returns the integer 1237 if this object represents false.
	 */
	public int hashCode()
	{
		return _val ? 1231 : 1237;
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefBoolean}'s value.
	 * @return A string representation of this object.
	 */
	public String toString()
	{
		return _val ? "true" : "false";
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefBoolean}.
	 */
	public RefBoolean clone()
	{
		return new RefBoolean(_val);
	}
	
	/**
	 * Toggle the boolean value if not fixed.
	 */
	public void toggle()
	{
		if(!_fixed)
		{
			this._val = !this._val;
		}
	}
}
