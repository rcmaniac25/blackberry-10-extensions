//#preprocessor

//#implicit BlackBerrySDK4.5.0 | BlackBerrySDK4.6.0 | BlackBerrySDK4.6.1 | BlackBerrySDK4.7.0 | BlackBerrySDK4.7.1

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
//Taken from PDF Renderer for BlackBerry
package rebuild.comp.java.nio;

/**
 * A container for data of a specific primitive type. Based off J2SE java.nio.Buffer class but no source code used for it.
 * @since BBX 1.2.0
 */
public abstract class Buffer
{
	int mark, position, limit, capacity;
	
	Buffer()
	{
		this.mark = -1;
	}
	
	/**
	 * Returns this buffer's limit.
	 * @return The limit of this buffer.
	 */
	public final int limit()
	{
		return this.limit;
	}
	
	/**
	 * Returns this buffer's position.
	 * @return The position of this buffer.
	 */
	public final int position()
	{
		return this.position;
	}
	
	/**
	 * Sets this buffer's position.
	 * @param newPosition The new position value; must be non-negative and no larger than the current limit.
	 * @return This buffer.
	 */
	public final Buffer position(int newPosition)
	{
		if(newPosition < 0 || newPosition > this.limit)
		{
			throw new IllegalArgumentException();
		}
		if(this.mark != -1 && this.mark > newPosition)
		{
			this.mark = -1;
		}
		this.position = newPosition;
		return this;
	}
	
	/**
	 * Returns the number of elements between the current position and the limit.
	 * @return The number of elements remaining in this buffer.
	 */
	public final int remaining()
	{
		return this.limit - this.position;
	}
	
	/**
	 * Flips this buffer.
	 * @return this buffer.
	 */
	public final Buffer flip()
	{
		this.limit = this.position;
		return rewind();
	}
	
	/**
	 * Returns this buffer's capacity.
	 * @return The capacity of this buffer.
	 */
	public final int capacity()
	{
		return this.capacity;
	}
	
	/**
	 * Sets this buffer's limit.
	 * @param newLimit the new limit value.
	 * @return this buffer.
	 */
	public final Buffer limit(int newLimit)
	{
		if(newLimit < 0 || newLimit > this.capacity)
		{
			throw new IllegalArgumentException();
		}
		if(this.position > newLimit)
		{
			this.position = newLimit;
		}
		if(this.mark != -1 && this.mark > newLimit)
		{
			this.mark = -1;
		}
		this.limit = newLimit;
		return this;
	}
	
	/**
	 * Rewinds this buffer.
	 * @return this buffer.
	 */
	public final Buffer rewind()
	{
		this.position = 0;
		this.mark = -1;
		return this;
	}
	
	/**
	 * Tells whether there are any elements between the current position and the limit.
	 * @return true if, and only if, there is at least one element remaining in this buffer.
	 */
	public final boolean hasRemaining()
	{
		return remaining() >= 1;
	}
	
	/**
	 * Clears this buffer.
	 * @return this buffer.
	 */
	public final Buffer clear()
	{
		this.limit = this.capacity;
		return rewind();
	}
}
