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
package rebuild.collections;

import java.util.Enumeration;
import java.util.Vector;

/**
 * A synchronized vector.
 * @since BBX 1.2.0
 */
//#ifdef BBX_INTERNAL_ACCESS & DEBUG
public
//#endif
final class SynchronizedVector extends Vector
{
	final Vector list;
	final Object mutex;
	
	SynchronizedVector(Vector l)
	{
		if(l == null)
		{
			throw new NullPointerException();
		}
		this.list = l;
		this.mutex = this;
	}
	
	public void addElement(Object obj)
	{
		synchronized(mutex)
		{
			list.addElement(obj);
		}
	}
	
	public int capacity()
	{
		synchronized(mutex)
		{
			return list.capacity();
		}
	}
	
	public boolean contains(Object elem)
	{
		synchronized(mutex)
		{
			return list.contains(elem);
		}
	}
	
	public void copyInto(Object[] anArray)
	{
		synchronized(mutex)
		{
			list.copyInto(anArray);
		}
	}
	
	public Object elementAt(int index)
	{
		synchronized(mutex)
		{
			return list.elementAt(index);
		}
	}
	
	public Enumeration elements()
	{
		return list.elements();
	}
	
	public void ensureCapacity(int minCapacity)
	{
		synchronized(mutex)
		{
			list.ensureCapacity(minCapacity);
		}
	}
	
	public boolean equals(Object obj)
	{
		synchronized(mutex)
		{
			return list.equals(obj);
		}
	}
	
	public Object firstElement()
	{
		synchronized(mutex)
		{
			return list.firstElement();
		}
	}
	
	public int hashCode()
	{
		synchronized(mutex)
		{
			return list.hashCode();
		}
	}
	
	public int indexOf(Object elem)
	{
		synchronized(mutex)
		{
			return list.indexOf(elem);
		}
	}
	
	public int indexOf(Object elem, int index)
	{
		synchronized(mutex)
		{
			return list.indexOf(elem, index);
		}
	}
	
	public void insertElementAt(Object obj, int index)
	{
		synchronized(mutex)
		{
			list.insertElementAt(obj, index);
		}
	}
	
	public boolean isEmpty()
	{
		synchronized(mutex)
		{
			return list.isEmpty();
		}
	}
	
	public Object lastElement()
	{
		synchronized(mutex)
		{
			return list.lastElement();
		}
	}
	
	public int lastIndexOf(Object elem)
	{
		synchronized(mutex)
		{
			return list.lastIndexOf(elem);
		}
	}
	
	public int lastIndexOf(Object elem, int index)
	{
		synchronized(mutex)
		{
			return list.lastIndexOf(elem, index);
		}
	}
	
	public void removeAllElements()
	{
		synchronized(mutex)
		{
			list.removeAllElements();
		}
	}
	
	public boolean removeElement(Object obj)
	{
		synchronized(mutex)
		{
			return list.removeElement(obj);
		}
	}
	
	public void removeElementAt(int index)
	{
		synchronized(mutex)
		{
			list.removeElementAt(index);
		}
	}
	
	public void setElementAt(Object obj, int index)
	{
		synchronized(mutex)
		{
			list.setElementAt(obj, index);
		}
	}
	
	public void setSize(int newSize)
	{
		synchronized(mutex)
		{
			list.setSize(newSize);
		}
	}
	
	public int size()
	{
		synchronized(mutex)
		{
			return list.size();
		}
	}
	
	public String toString()
	{
		synchronized(mutex)
		{
			return list.toString();
		}
	}
	
	public void trimToSize()
	{
		synchronized(mutex)
		{
			list.trimToSize();
		}
	}
}
