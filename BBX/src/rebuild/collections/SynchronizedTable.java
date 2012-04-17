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
import java.util.Hashtable;

/**
 * A synchronized hashtable.
 * @since BBX 1.2.0
 */
//#ifdef BBX_INTERNAL_ACCESS & DEBUG
public
//#endif
final class SynchronizedTable extends Hashtable
{
	final Hashtable t; // Backing Map
	final Object mutex; // Object on which to synchronize
	
	SynchronizedTable(Hashtable table)
	{
		if(table == null)
		{
			throw new NullPointerException();
		}
		this.t = table;
		this.mutex = this;
	}
	
	SynchronizedTable(Hashtable table, Object mutex)
	{
		if(table == null)
		{
			throw new NullPointerException();
		}
		this.t = table;
		this.mutex = mutex;
	}
	
	//We don't care about any synchronized functions as we do our own synchronization (though the methods would be a little faster)
	
	public int size()
	{
		synchronized(this.mutex)
		{
			return this.t.size();
		}
	}
	
	public boolean isEmpty()
	{
		synchronized(this.mutex)
		{
			return this.t.isEmpty();
		}
	}
	
	public boolean containsKey(Object key)
	{
		synchronized(this.mutex)
		{
			return this.t.containsKey(key);
		}
	}
	
	public boolean contains(Object value)
	{
		synchronized(this.mutex)
		{
			return this.t.contains(value);
		}
	}
	
	public Object get(Object key)
	{
		synchronized(this.mutex)
		{
			return this.t.get(key);
		}
	}
	
	public Object put(Object key, Object value)
	{
		synchronized(this.mutex)
		{
			return this.t.put(key, value);
		}
	}
	
	public Object remove(Object key)
	{
		synchronized(this.mutex)
		{
			return this.t.remove(key);
		}
	}
	
	public void clear()
	{
		synchronized(this.mutex)
		{
			this.t.clear();
		}
	}
	
	public Enumeration elements()
	{
		return this.t.elements();
	}
	
	public Enumeration keys()
	{
		return this.t.keys();
	}
	
	public boolean equals(Object obj)
	{
		synchronized(this.mutex)
		{
			return this.t.equals(obj);
		}
	}
	
	public int hashCode()
	{
		synchronized(this.mutex)
		{
			return this.t.hashCode();
		}
	}
	
	public String toString()
	{
		synchronized(this.mutex)
		{
			return this.t.toString();
		}
	}
}
