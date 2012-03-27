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
 * Reference Object is the same as {@link Object} but allows you to set the object without creating a new {@link Object}.
 */
public final class RefObject
{
	private boolean _fixed;
	Object _val;
	
	/**
	 * Create a new {@link RefObject} set to the default of null.
	 */
	public RefObject()
	{
		_fixed = false;
		_val = null;
	}
	
	/**
	 * Create a new {@link RefObject} using a object.
	 * @param value The object to set this {@link RefObject} with.
	 */
	public RefObject(Object value)
	{
		this(value, false);
	}
	
	/**
	 * Create a new {@link RefObject} using a object.
	 * @param value The object to set this {@link RefObject} with.
	 * @param fixed If this item is read only and cannot be modified.
	 */
	public RefObject(Object value, boolean fixed)
	{
		_fixed = fixed;
		_val = value;
	}
	
	/**
	 * Returns The value of this {@link RefObject} object.
	 * @return The value of this object.
	 */
	public Object objectValue()
	{
		return _val;
	}
	
	/**
	 * Set the value of this {@link RefObject} object with a object.
	 * @param value The object value to set this object.
	 * @return This object.
	 */
	public RefObject setValue(Object value)
	{
		if(_fixed)
		{
			return this.clone().setValue(value);
		}
		_val = value;
		return this;
	}
	
	/**
	 * Returns true if and only if the argument is not null and is a {@link RefObject} object that represents the same value as this object.
	 * @param obj The object to compare with.
	 * @return true if the {@link RefObject} objects represent the same value; false otherwise.
	 */
	public boolean equals(Object obj)
	{
		if((obj != null) && (obj instanceof RefObject))
		{
			boolean b = ((RefObject)obj)._val == this._val;
			if(!b)
			{
				b = ((RefObject)obj)._val.equals(this._val);
			}
			return b;
		}
		return false;
	}
	
	/**
	 * Returns a hash code for this {@link RefObject} object.
	 * @return The hashcode this object represents.
	 */
	public int hashCode()
	{
		return _val == null ? 0 : _val.hashCode();
	}
	
	/**
	 * Returns a {@link String} object representing this {@link RefObject}'s value.
	 * @return A string representation of this object.
	 */
	public String toString()
	{
		return _val == null ? "null" : _val.toString();
	}
	
	/**
	 * Create a clone of the current object.
	 * @return A clone of the current {@link RefObject}.
	 */
	public RefObject clone()
	{
		return new RefObject(_val);
	}
}
