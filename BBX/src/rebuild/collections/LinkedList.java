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
package rebuild.collections;

import java.util.NoSuchElementException;
import java.util.Vector;

/**
 * Simplistic linked list based off a Vector.
 * @since BBX 1.2.0
 */
public class LinkedList extends Vector
{
	/**
	 * Adds the specified object at the beginning of this {@code LinkedList}.
	 * @param object The object to add.
	 */
	public void addFirst(Object object)
	{
		if(this.elementCount == 0)
		{
			this.addElement(object);
		}
		else
		{
			this.insertElementAt(object, 0);
		}
	}
	
	/**
	 * Removes the last object from this {@code LinkedList}.
	 * @return the removed object.
	 * @throws NoSuchElementException if this {@code LinkedList} is empty.
	 */
	public Object removeLast()
	{
		if(this.elementCount > 0)
		{
			Object ret = this.lastElement();
			this.removeElementAt(this.elementCount - 1);
			return ret;
		}
		throw new NoSuchElementException();
	}
	
	/**
	 * Removes the first object from this {@code LinkedList}.
	 * @return the removed object.
	 * @throws NoSuchElementException if this {@code LinkedList} is empty.
	 */
	public Object removeFirst()
	{
		if(this.elementCount > 0)
		{
			Object ret = this.firstElement();
			this.removeElementAt(0);
			return ret;
		}
		throw new NoSuchElementException();
	}
}
