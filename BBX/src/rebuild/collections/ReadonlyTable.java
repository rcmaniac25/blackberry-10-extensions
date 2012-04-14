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
import java.util.Hashtable;

/**
 * A readonly table.
 * @since BBX 1.2.0
 */
//#ifdef BBX_INTERNAL_ACCESS & DEBUG
public
//#endif
final class ReadonlyTable extends Hashtable
{
	private Hashtable t;
	
	ReadonlyTable(Hashtable table)
	{
		if(table == null)
		{
			throw new NullPointerException();
		}
		this.t = table;
	}
	
	public void clear()
	{
		throw new UnsupportedOperationException();
	}
	
	public boolean contains(Object value)
	{
		return this.t.contains(value);
	}
	
	public boolean containsKey(Object key)
	{
		return this.t.containsKey(key);
	}
	
	public Enumeration elements()
	{
		return this.t.elements();
	}
	
	public boolean equals(Object obj)
	{
		return this.t.equals(obj);
	}
	
	public Object get(Object key)
	{
		return this.t.get(key);
	}
	
	public int hashCode()
	{
		return this.t.hashCode();
	}
	
	public boolean isEmpty()
	{
		return this.t.isEmpty();
	}
	
	public Enumeration keys()
	{
		return this.t.keys();
	}
	
	public Object put(Object key, Object value)
	{
		throw new UnsupportedOperationException();
	}
	
	protected void rehash()
	{
		throw new UnsupportedOperationException();
	}
	
	public Object remove(Object key)
	{
		throw new UnsupportedOperationException();
	}
	
	public int size()
	{
		return this.t.size();
	}
	
	public String toString()
	{
		return this.t.toString();
	}
}
