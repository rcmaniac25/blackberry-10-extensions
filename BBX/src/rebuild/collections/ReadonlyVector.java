//#preprocessor

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

import java.util.Enumeration;
import java.util.Vector;

/**
 * A readonly vector.
 * @since BBX 1.2.0
 */
//#ifdef BBX_INTERNAL_ACCESS & DEBUG
public
//#endif
final class ReadonlyVector extends Vector
{
	private Vector v;
	
	ReadonlyVector(Vector vector)
	{
		if(vector == null)
		{
			throw new NullPointerException();
		}
		this.v = vector;
	}
	
	public synchronized void addElement(Object obj)
	{
		throw new UnsupportedOperationException();
	}
	
	public int capacity()
	{
		return v.capacity();
	}
	
	public boolean contains(Object elem)
	{
		return v.contains(elem);
	}
	
	public synchronized void copyInto(Object[] anArray)
	{
		v.copyInto(anArray);
	}
	
	public synchronized Object elementAt(int index)
	{
		return v.elementAt(index);
	}
	
	public synchronized Enumeration elements()
	{
		return v.elements();
	}
	
	public synchronized void ensureCapacity(int minCapacity)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean equals(Object obj)
	{
		return v.equals(obj);
	}
	
	public synchronized Object firstElement()
	{
		return v.firstElement();
	}
	
	public int hashCode()
	{
		return v.hashCode();
	}
	
	public int indexOf(Object elem)
	{
		return v.indexOf(elem);
	}
	
	public synchronized int indexOf(Object elem, int index)
	{
		return v.indexOf(elem, index);
	}
	
	public synchronized void insertElementAt(Object obj, int index)
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean isEmpty()
	{
		return v.isEmpty();
	}
	
	public synchronized Object lastElement()
	{
		return v.lastElement();
	}
	
	public int lastIndexOf(Object elem)
	{
		return v.lastIndexOf(elem);
	}
	
	public synchronized int lastIndexOf(Object elem, int index)
	{
		return v.lastIndexOf(elem, index);
	}
	
	public synchronized void removeAllElements()
	{
		throw new UnsupportedOperationException();
	}
	
	public synchronized boolean removeElement(Object obj)
	{
		throw new UnsupportedOperationException();
	}
	
	public synchronized void removeElementAt(int index)
	{
		throw new UnsupportedOperationException();
	}
	
	public synchronized void setElementAt(Object obj, int index)
	{
		throw new UnsupportedOperationException();
	}
	
	public synchronized void setSize(int newSize)
	{
		throw new UnsupportedOperationException();
	}
	
	public int size()
	{
		return v.size();
	}
	
	public synchronized String toString()
	{
		return v.toString();
	}
	
	public synchronized void trimToSize()
	{
		throw new UnsupportedOperationException();
	}
}
